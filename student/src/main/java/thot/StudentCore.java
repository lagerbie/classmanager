/*
 * ClassManager - Supervision de classes et Laboratoire de langue
 * Copyright (C) 2013 Fabrice Alleau <fabrice.alleau@siclic.fr>
 *
 * This file is part of ClassManager.
 *
 * ClassManager is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * ClassManager is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with ClassManager.  If not, see <http://www.gnu.org/licenses/>.
 */
package thot;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thot.dll.WindowsUtilities;
import thot.gui.GuiUtilities;
import thot.gui.Login;
import thot.gui.Resources;
import thot.model.Command;
import thot.model.Constants;
import thot.model.ThotPort;
import thot.screen.BlackWindow;
import thot.screen.CaptureScreen;
import thot.screen.DeskTopWindow;
import thot.screen.ScreenWindow;
import thot.utils.Utilities;
import thot.utils.XMLUtilities;
import thot.voip.Voip;

/**
 * Coeur de l'application de supervision élève.
 *
 * @author Fabrice Alleau
 * @version 1.90
 */
public class StudentCore implements Runnable {
    /*
     * Resources textes : badLogin
     */

    /**
     * Instance de log.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(StudentCore.class);

    /**
     * Chemin de l'éxécutable du laboratoire de langue.
     */
    private File laboratoryPath;
    /**
     * Répertoire de l'utilisateur.
     */
    private final File userPath;

    /**
     * Nom de l'élève (login de session de l'os).
     */
    private String userName;
    /**
     * Login de la session.
     */
    private String userLogin;
    /**
     * Mot de passe de la session.
     */
    private String userPassword;
    /**
     * Indique si le login à déjà été envoyé.
     */
    private boolean loginSended = false;

    /**
     * ServerSocket pour les commandes du professeur.
     */
    private ServerSocket serverSocket;
    /**
     * Socket pour les commandes du professeur.
     */
    private Socket socket;

    /**
     * Envoi de l'écran sur un autre poste.
     */
    private CaptureScreen captureScreen;
    /**
     * Fenêtre de Login.
     */
    private Login login;
    /**
     * Fenêtre bloquant les actions de l'élève.
     */
    private DeskTopWindow blockWindow;
    /**
     * Fenêtre noire bloquante.
     */
    private BlackWindow blackWindow;
    /**
     * Fenêtre pour l'affichage d'un écran distant.
     */
    private ScreenWindow screenWindow;
    /**
     * Gestionnaire de transfert de fichiers.
     */
    private FileTransfert fileTransfert;
    /**
     * Module de communication principal.
     */
    private Voip chatVoip;

    /**
     * Liste des applications interdite.
     */
    private List<String> applicationsForbiden = new ArrayList<>(16);
    /**
     * Etat du blocage Internet.
     */
    private boolean internetBlocked = false;

    /**
     * Thread pour la recherche d'un professeur.
     */
    private MasterSearch masterSearch;
    /**
     * Adresse IP du tuteur.
     */
    private String masterIP;

    /**
     * Initialisation.
     *
     * @param resources les resources textuelles.
     * @param multicastIP adresse multicast pour la recherche du professeur.
     * @param voip gestionnaire pour l'audio.
     */
    public StudentCore(Resources resources, String multicastIP, Voip voip) {
        userName = System.getProperty("user.name");
        userPath = new File(System.getProperty("user.home"), Constants.softNamePath);
        userPath.mkdirs();

        Utilities.getAddress();
        this.chatVoip = voip;

        captureScreen = new CaptureScreen(ThotPort.keyboardAndMousePort);
        screenWindow = new ScreenWindow(false, ThotPort.keyboardAndMousePort);

        masterSearch = new MasterSearch(multicastIP, ThotPort.multicastPort, ThotPort.studentToMasterPort);

        login = new Login(this, resources);
        blockWindow = new DeskTopWindow();
        blackWindow = new BlackWindow();

        fileTransfert = new FileTransfert();

        ProgressListener listener = new ProgressListener() {
            @Override
            public void processBegin(Object source, boolean determinated) {
                LOGGER.info("begin: " + source + " -> " + determinated);
            }

            @Override
            public void processEnded(Object source, int exit) {
                LOGGER.info("ended: " + source + " -> " + exit);
                if (source instanceof MasterSearch) {
                    login.showLogin(false);
//v0.94:
//                    if(!screenWindow.isRun()
//                            && !captureScreen.isRun())
//                        return;
                }

                if (exit < 0) {
                    sendEndedError();
                }

                chatVoip.disconnectAllWithoutPairing();
                screenWindow.stop();

                if (Utilities.WINDOWS_PLATFORM) {// && !blockWindow.isVisible()
                    WindowsUtilities.blockInput(false);
                }

                if (blackWindow.isVisible()) {
                    blackWindow.showWindow(false);
                }

                if (blockWindow.isVisible()) {
                    blockWindow.showWindow(false);
                }

                captureScreen.stop();
            }
        };

        screenWindow.addListener(listener);
        captureScreen.addListener(listener);
        masterSearch.addListener(listener);

        Battery.getBatteryLevel();
    }

    /**
     * Retourne le nom du logiciel sans espace (pour les noms des répertoires).
     *
     * @return le nom du logiciel sans espace.
     */
    public File getUserHome() {
        return userPath;
    }

    /**
     * Modifie le chemin du lanceur du laboratoire de langue.
     *
     * @param laboratoryPath le chemin du lanceur du laboratoire de langue.
     */
    public void setLaboratoryPath(File laboratoryPath) {
        this.laboratoryPath = laboratoryPath;
    }

    /**
     * Ajoute d'une écoute de type ProgressListener.
     *
     * @param listener l'écoute à ajouter.
     */
    public void addListener(ProgressListener listener) {
        fileTransfert.addListener(listener);
    }

    /**
     * Validation interne du login et du mot de passe.
     *
     * @param login le login.
     * @param password le mot de passe.
     */
    public void checkLogin(String login, String password) {
        //enlève les accents des logins
        String normalize = Normalizer.normalize(login, Normalizer.Form.NFD);
        String withoutAccent = normalize.replaceAll("[\u0300-\u036F]", "");
        //login toujours en minuscule
        userLogin = withoutAccent.toLowerCase();
        userPassword = password;
    }

    /**
     * Fermeture des communnications existentes lorsque le professeur n'est plus présent.
     */
    private void masterClosed() {
        chatVoip.disconnectAll();

        if (screenWindow.isRun()) {
            screenWindow.stop();
            if (Utilities.WINDOWS_PLATFORM) {
                WindowsUtilities.blockInput(false);
            }
        }

        if (Utilities.WINDOWS_PLATFORM && (blackWindow.isVisible() || blockWindow.isVisible())) {
            WindowsUtilities.blockInput(false);
        }

        if (blackWindow.isVisible()) {
            blackWindow.showWindow(false);
        }

        if (blockWindow.isVisible()) {
            blockWindow.showWindow(false);
        }

        if (captureScreen.isRun()) {
            captureScreen.stop();
        }
    }

    /**
     * Fermeture de la session de l'os sous Linux.
     */
    private void closeSessionOnLinux() {
        Utilities.killApplication("x-session-manager");
    }

    /**
     * Fermeture des applications interdites.
     */
    private void killApplication() {
        for (String application : applicationsForbiden) {
            LOGGER.info("kill application: " + application);
            Utilities.killApplication(application);
        }
    }

    /**
     * Blocage du port 80 (Internet) sous Linux.
     */
    private void iptablesOnLinux() {
        String command;
        StringBuilder out = new StringBuilder(1024);
        StringBuilder err = new StringBuilder(1024);
        if (internetBlocked) {
            command = "sudo iptables -A OUTPUT -p tcp -o wlan0 --dport 80 -j DROP";
            Utilities.executeCommand("iptables", command, out, err);
            command = "sudo iptables -A OUTPUT -p tcp -o eth0 --dport 80 -j DROP";
            Utilities.executeCommand("iptables", command, out, err);
        } else {
            command = "sudo iptables -D OUTPUT -p tcp -o wlan0 --dport 80 -j DROP";
            Utilities.executeCommand("iptables", command, out, err);
            command = "sudo iptables -D OUTPUT -p tcp -o eth0 --dport 80 -j DROP";
            Utilities.executeCommand("iptables", command, out, err);
        }
    }

    /**
     * Tue les navigateurs internet.
     */
    private void killWebNavigators() {
        if (Utilities.WINDOWS_PLATFORM) {
            killWebNavigatorsOnWindows();
        } else if (Utilities.LINUX_PLATFORM) {
            killWebNavigatorsOnLinux();
        }
    }

    /**
     * Fermeture de FireFox sous Linux.
     */
    private void killWebNavigatorsOnLinux() {
        LOGGER.info("pkill -f firefox");
        Utilities.killApplication("firefox");
    }

    /**
     * Fermeture des navigateurs FireFox et Internet Explorer sous Windows.
     */
    private void killWebNavigatorsOnWindows() {
        LOGGER.info("taskkill /F firefox iexplore chrome");
        Utilities.killApplication("iexplore.exe");
        Utilities.killApplication("firefox.exe");
        Utilities.killApplication("chrome.exe");
    }

    /**
     * Autoriser ou interdire l'accès à Internet.
     *
     * @param block <code>true</code> pour bloquer.
     */
    private void setWebEnable(boolean block) {
        internetBlocked = block;
        if (internetBlocked) {
            killWebNavigators();
        }
    }

    /**
     * Affiche un message sans bloquer l'élève.
     *
     * @param message le message.
     */
    private void showMessage(String message) {
        GuiUtilities.showModelessMessage(message);
    }

    /**
     * Envoi d'une commande au poste professeur.
     *
     * @param command la commande.
     *
     * @return le success de l'envoi.
     */
    private boolean sendSupervisionCommand(Command command) {
        if (masterIP == null) {
            return false;
        }

        return Utilities.sendXml(XMLUtilities.getXML(command),
                masterIP, ThotPort.studentToMasterPort);
    }

    /**
     * Envoi un appel d'aide au professeur.
     *
     * @return le success de l'envoi.
     */
    public boolean sendHelpDemand() {
        Command command = new Command(Command.TYPE_SUPERVISION, Command.HELP_CALL);
        return sendSupervisionCommand(command);
    }

    /**
     * Transfert un fichier au professeur.
     *
     * @param file le fichier.
     *
     * @return si le fichier à été transféré.
     */
    public boolean sendFile(File file) {
        Command command = new Command(Command.TYPE_SUPERVISION, Command.RECEIVE_FILE);
        command.putParameter(Command.PORT, ThotPort.fileTransfertPortBase);
        command.putParameter(Command.FILE, file.getName());
        command.putParameter(Command.SIZE, file.length());

        boolean sended = sendSupervisionCommand(command);

        if (sended) {
            fileTransfert.sendFile(file, ThotPort.fileTransfertPortBase, 1);
        }
        return sended;
    }

    /**
     * Transfert un fichier au professeur.
     *
     * @return si le fichier à été transféré.
     */
    private boolean sendEndedError() {
        Command command = new Command(Command.TYPE_SUPERVISION, Command.END_ERROR);
        return sendSupervisionCommand(command);
    }

    /**
     * Traite les commandes reçues.
     *
     * @param command la commande à traiter.
     */
    private void execute(Command command) {
        String action = command.getAction();

        switch (action) {
            case Command.PING:
                killApplication();
                if (internetBlocked) {
                    killWebNavigators();
                }

                int batteryLevel = Battery.getBatteryLevel();

                String remoteHost = command.getParameter(Command.IP_ADDRESS);
                boolean passwordChecked = command.getParameterAsBoolean(
                        Command.PASSWORD_CHECKED);

                if (remoteHost.isEmpty()) {
                    return;
                }

                Command returnCommand = new Command(
                        Command.TYPE_SUPERVISION, Command.PONG);
                returnCommand.putParameter(Command.BATTERY, batteryLevel);

                //pas de login élève
                if (userLogin == null) {
                    if (!login.isVisible() && !blackWindow.isVisible()) {
                        login.showLogin(true);
                    }

                    loginSended = false;
                } //login élève vérifié
                else if (passwordChecked) {
                    returnCommand.putParameter(Command.NAME, userLogin);
                    returnCommand.putParameter(Command.PASSWORD, userPassword);
                    loginSended = true;
                } //login élève à vérifier
                else {
                    if (loginSended) {
                        userLogin = null;
                        login.reset();

                        if (!blackWindow.isVisible()) {
                            //login.showLogin(true);
                            login.showMessage("badLogin");
                        }

                        loginSended = false;
                    } else {
                        returnCommand.putParameter(Command.NAME, userLogin);
                        returnCommand.putParameter(Command.PASSWORD, userPassword);
                        loginSended = true;
                    }
                }

                Utilities.sendXml(XMLUtilities.getXML(returnCommand),
                        remoteHost, ThotPort.studentToMasterPort);
                break;
            case Command.SEND_SCREEN:
                int portBase = command.getParameterAsInt(Command.SCREEN_PORT);
                int nbClient = command.getParameterAsInt(Command.CLIENT_NUMBER);

                boolean control = false;
                if (command.getParameter(Command.REMOTE_HANDLING) != null) {
                    control = command.getParameterAsBoolean(Command.REMOTE_HANDLING);
                }

                if (command.getParameter(Command.FPS) != null) {
                    captureScreen.setFPS(command.getParameterAsDouble(Command.FPS));
                }
                if (command.getParameter(Command.QUALITY) != null) {
                    captureScreen.setQuality(command.getParameterAsInt(Command.QUALITY));
                }
                if (command.getParameter(Command.LINES) != null) {
                    captureScreen.setNbLines(command.getParameterAsInt(Command.LINES));
                }

                int audioPort = chatVoip.getPort();

                String list = command.getParameter(Command.LIST);
//                List<InetSocketAddress> addresses = new ArrayList<>(nbClient);
                if (list != null) {
                    List<String> ipList = XMLUtilities.parseList(list);
                    for (String ip : ipList) {
                        //addresses.add(new InetSocketAddress(ip, portBase));
                        chatVoip.connect(ip, audioPort);
                    }
                    ipList.clear();
                }

                if (command.getParameter(Command.AUDIO_PORT) != null) {
                    String hostIP = command.getParameter(Command.IP_ADDRESS);
                    chatVoip.connect(hostIP, command.getParameterAsInt(Command.AUDIO_PORT));
                    //addresses.add(new InetSocketAddress(hostIP, portBase));
                }

                captureScreen.start(portBase, nbClient, control);
                //captureScreen.start(addresses, control);
                break;
            case Command.SEND_SCREEN_STOP:
                chatVoip.disconnectAllWithoutPairing();
                captureScreen.stop();
                break;
            case Command.RECEIVE_SCREEN:
                String addressIP = command.getParameter(Command.IP_ADDRESS);
                int remotePort = command.getParameterAsInt(Command.SCREEN_PORT);

                boolean remoteHandling = false;
                if (command.getParameter(Command.REMOTE_HANDLING) != null) {
                    remoteHandling = command.getParameterAsBoolean(Command.REMOTE_HANDLING);
                }

                int nbLines = -1;
                if (command.getParameter(Command.LINES) != null) {
                    nbLines = command.getParameterAsInt(Command.LINES);
                }
                if (command.getParameter(Command.TIMEOUT) != null) {
                    int timeout = command.getParameterAsInt(Command.TIMEOUT);
                    screenWindow.setTimeout(timeout);
                }

                if (Utilities.WINDOWS_PLATFORM) {
                    WindowsUtilities.blockInput(true);
                }

                screenWindow.start(addressIP, remotePort, remoteHandling, nbLines);
                break;
            case Command.RECEIVE_BLACK_SCREEN:
                if (login.isVisible()) {
                    login.showLogin(false);
                }

                if (Utilities.WINDOWS_PLATFORM) {
                    WindowsUtilities.blockInput(true);
                }

                blackWindow.showWindow(true);
                break;
//            case "RecevoirSon":
//                break;
            case Command.RECEIVE_SCREEN_STOP:
                chatVoip.disconnectAllWithoutPairing();
                screenWindow.stop();

                if (blackWindow.isVisible()) {
                    blackWindow.showWindow(false);
                }

                if (Utilities.WINDOWS_PLATFORM && !blockWindow.isVisible()) {
                    WindowsUtilities.blockInput(false);
                }
                break;
            case Command.PAIRING_STOP:
                chatVoip.disconnectAll();
                break;
            case Command.PAIRING:
                String pairingIP = command.getParameter(Command.IP_ADDRESS);
                int portPairing = command.getParameterAsInt(Command.AUDIO_PORT);

                chatVoip.disconnectAll();
                chatVoip.connectPairing(pairingIP, portPairing);
                break;
            case Command.SEND_VOICE:
                if (command.getParameter(Command.IP_ADDRESS) != null) {
                    String stHost = command.getParameter(Command.IP_ADDRESS);
                    if (stHost.length() > 1) {
                        chatVoip.connect(
                                stHost, command.getParameterAsInt(Command.AUDIO_PORT));
                    }
                }
                break;
            case Command.RECEIVE_MESSAGE:
                String message = command.getParameter(Command.MESSAGE);
                showMessage(message);
                break;
            case Command.RECEIVE_FILE:
                final String address = command.getParameter(Command.IP_ADDRESS);
                final int portFile = command.getParameterAsInt(Command.PORT);
                final String fileName = command.getParameter(Command.FILE);
                final int size = command.getParameterAsInt(Command.SIZE);

                final File file = new File(userPath, fileName);
                new Thread(() -> fileTransfert.loadFile(file, size, address, portFile), "receiveFile").start();
                break;
            case Command.LAUNCH_FILE:
                File launchFile = new File(userPath, command.getParameter(Command.FILE));
                FileTransfert.launchFile(launchFile);
                break;
            case Command.EXECUTE:
                String parameter = command.getParameter(Command.FILE);
                String binCommand = null;
                File executeFile = new File(parameter);

                if (parameter.contentEquals("labo") && laboratoryPath != null) {
                    if (Utilities.WINDOWS_PLATFORM) {
                        File path = laboratoryPath.getParentFile().getParentFile();
                        binCommand = laboratoryPath.getAbsolutePath() + " --path \""
                                + path.getAbsolutePath() + "\"";
                    } else {
                        binCommand = "padsp " + laboratoryPath.getAbsolutePath();
                    }
                    LOGGER.info("labo launch: " + command);
                } else if (parameter.contentEquals("jclic")) {
                    binCommand = "jclic";
                    if (Utilities.WINDOWS_PLATFORM) {
                        binCommand = Utilities.getJClicCommand();
                    }
                } else {
                    if (parameter.startsWith(Constants.PROGAM_FILES)) {
                        String path = parameter.substring(Constants.PROGAM_FILES.length());
                        executeFile = Utilities.pathOnWindowsProgramFiles(path);
                    }
                    if (executeFile.exists()) {
                        if (Utilities.WINDOWS_PLATFORM) {
                            binCommand = "cmd /c \"" + executeFile.getAbsolutePath() + "\"";
                        } else {
                            binCommand = "\"" + executeFile.getAbsolutePath() + "\"";
                        }
                    }
                }

                if (binCommand != null) {
                    StringBuilder out = new StringBuilder(1024);
                    StringBuilder err = new StringBuilder(1024);
                    Utilities.startProcess(binCommand, binCommand, out, err);
                }
                break;
            case Command.RESET_LOGIN:
                userLogin = null;
                login.reset();
                break;
            case Command.SHUTDOWN_SESSION:
                if (Utilities.WINDOWS_PLATFORM) {
                    new Thread(() -> {
                        Utilities.waitInMillisecond(200);
                        WindowsUtilities.shutdownSession();
                    }, "sessionShutdown").start();
                } else {
                    closeSessionOnLinux();
                }
                new Thread(() -> {
                    Utilities.waitInMillisecond(500);
                    System.exit(0);
                }, "exit").start();
                break;
            case Command.SHUTDOWN:
                /*la commande : sudo visudo
                 Pour pouvoir effectuer un shutdown sans avoir à fournir de mot de passe
                 Cmnd_Alias      SHUTDOWN = /sbin/shutdown
                 # Les membres du groupe admin peuvent effectuer un shutdown sans password
                 %admin ALL=NOPASSWD: SHUTDOWN
                 %sudo ALL=NOPASSWD: SHUTDOWN
                 */
                if (Utilities.WINDOWS_PLATFORM) {
                    new Thread(() -> {
                        Utilities.waitInMillisecond(200);
                        WindowsUtilities.shutdown();
                    }, "shutdown").start();
                } else {
                    StringBuilder out = new StringBuilder(1024);
                    StringBuilder err = new StringBuilder(1024);
                    String shutdownCommand = "sudo shutdown -P now";
                    Utilities.startProcess("shutdown", shutdownCommand, out, err);
                }

                new Thread(() -> {
                    Utilities.waitInMillisecond(500);
                    System.exit(0);
                }, "exit").start();
                break;
            case Command.BLOCK_KEYBOARD:
                boolean block = command.getParameterAsBoolean(Command.BLOCK);
                if (Utilities.WINDOWS_PLATFORM) {
                    WindowsUtilities.blockInput(block);
                }

                blockWindow.showWindow(block);
                break;
            case Command.RECEIVE_INTREDICTION:
                String applicationList = command.getParameter(Command.LIST);
                applicationsForbiden.clear();
                if (applicationList != null) {
                    List<String> appList = XMLUtilities.parseList(applicationList);
                    for (String application : appList) {
                        applicationsForbiden.add(application);
                    }
                    appList.clear();
                }

                killApplication();
                break;
            case Command.BLOCK_INTERNET:
                setWebEnable(command.getParameterAsBoolean(Command.BLOCK));
                break;
            case Command.DELETE_DOCUMENT:
                Utilities.deteleFiles(userPath);
                break;
            case Command.MASTER_CLOSED:
                masterClosed();
                setWebEnable(false);
                applicationsForbiden.clear();
                break;
        }
    }

    /**
     * Démarre l'écoute des commandes.
     */
    public void start() {
        try {
            serverSocket = new ServerSocket(ThotPort.masterToStudentPort);
        } catch (IOException e) {
            LOGGER.error("", e);
        }

        Thread noyau = new Thread(this, this.getClass().getName());
        noyau.start();
        noyau.setPriority(Thread.MAX_PRIORITY);

        if (!masterSearch.isRun()) {
            masterSearch.start();
        }
    }

    @Override
    public void run() {
        InetAddress inetAddress;
        DataInputStream inputStream;

        byte[] buffer = new byte[1024];
        StringBuilder xml;

        int cnt;
        try {
            while (true) {
                socket = serverSocket.accept();

                inputStream = new DataInputStream(socket.getInputStream());
                xml = new StringBuilder(64);
                cnt = inputStream.read(buffer);

                inetAddress = socket.getInetAddress();
                if (inetAddress != null) {
                    masterIP = inetAddress.getHostAddress();
                } else {
                    masterIP = null;
                    LOGGER.warn("InetAddress null");
                }

                while (cnt > 0) {
                    xml.append(new String(buffer, 0, cnt, "UTF-8"));
                    cnt = inputStream.read(buffer);
                }
                LOGGER.info("Recoit Xml: " + xml.toString());

                List<Command> commands = XMLUtilities.parseCommand(xml.toString());
                for (Command command : commands) {
                    execute(command);
                }
                commands.clear();

                //fermeture de la connection et reboucle sur une écoute du
                //port (si la connection n'est pas fermée, utilisation
                //inutile du cpu).
                socket.close();
                socket = null;
            }
        } catch (Exception e) {
            LOGGER.error("", e);
        }

        if (socket != null) {
            try {
                socket.close();
                socket = null;
            } catch (IOException e) {
                LOGGER.error("", e);
            }
        }

        if (serverSocket != null) {
            try {
                serverSocket.close();
                serverSocket = null;
            } catch (IOException e) {
                LOGGER.error("", e);
            }
        }

        LOGGER.warn("redémarrage noyau");
        start();
    }
}
