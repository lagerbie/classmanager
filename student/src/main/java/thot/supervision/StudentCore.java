package thot.supervision;

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
import thot.exception.ThotException;
import thot.gui.GuiUtilities;
import thot.gui.Resources;
import thot.supervision.gui.Login;
import thot.supervision.screen.BlackWindow;
import thot.supervision.screen.CaptureScreen;
import thot.supervision.screen.DeskTopWindow;
import thot.supervision.screen.ScreenWindow;
import thot.supervision.voip.Voip;
import thot.utils.Battery;
import thot.utils.Constants;
import thot.utils.FileTransfert;
import thot.utils.ProgressListener;
import thot.utils.ThotPort;
import thot.utils.Utilities;
import thot.utils.XMLUtilities;
import thot.utils.dll.WindowsUtilities;

/**
 * Coeur de l'application de supervision élève.
 *
 * @author Fabrice Alleau
 * @version 1.8.4
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

//                    if(!screenWindow.isRun() && !captureScreen.isRun()) return; //v0.94:
                }

                if (exit < 0) {
                    try {
                        sendEndedError();
                    } catch (ThotException e) {
                        LOGGER.error("Impossible d'envoyer le signal d'erreur", e);
                    }
                }

                chatVoip.disconnectAllWithoutPairing();
                screenWindow.stop();

                if (Utilities.WINDOWS_PLATFORM) {
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

            @Override
            public void percentChanged(Object source, int percent) {

            }
        };

        screenWindow.addListener(listener);
        captureScreen.addListener(listener);
        masterSearch.addListener(listener);

        try {
            Battery.getBatteryLevel();
        } catch (ThotException e) {
            LOGGER.error("Impossible de récupérer l'état de la baterrie", e);
        }
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
    private void closeSessionOnLinux() throws ThotException {
        Utilities.killApplication("x-session-manager");
    }

    /**
     * Fermeture des applications interdites.
     */
    private void killApplication() throws ThotException {
        for (String application : applicationsForbiden) {
            LOGGER.info("kill application: " + application);
            Utilities.killApplication(application);
        }
    }

    /**
     * Blocage du port 80 (Internet) sous Linux.
     */
    private void iptablesOnLinux() throws ThotException {
        String command;
        StringBuilder out = new StringBuilder(1024);
        StringBuilder err = new StringBuilder(1024);
        if (internetBlocked) {
            command = "sudo iptables -A OUTPUT -p tcp -o wlan0 --dport 80 -j DROP";
            Utilities.executeCommand("iptables", out, err, command);
            command = "sudo iptables -A OUTPUT -p tcp -o eth0 --dport 80 -j DROP";
            Utilities.executeCommand("iptables", out, err, command);
        } else {
            command = "sudo iptables -D OUTPUT -p tcp -o wlan0 --dport 80 -j DROP";
            Utilities.executeCommand("iptables", out, err, command);
            command = "sudo iptables -D OUTPUT -p tcp -o eth0 --dport 80 -j DROP";
            Utilities.executeCommand("iptables", out, err, command);
        }
    }

    /**
     * Tue les navigateurs internet.
     */
    private void killWebNavigators() throws ThotException {
        if (Utilities.WINDOWS_PLATFORM) {
            killWebNavigatorsOnWindows();
        } else if (Utilities.LINUX_PLATFORM) {
            killWebNavigatorsOnLinux();
        }
    }

    /**
     * Fermeture de FireFox sous Linux.
     */
    private void killWebNavigatorsOnLinux() throws ThotException {
        LOGGER.info("pkill -f firefox");
        Utilities.killApplication("firefox");
    }

    /**
     * Fermeture des navigateurs FireFox et Internet Explorer sous Windows.
     */
    private void killWebNavigatorsOnWindows() throws ThotException {
        LOGGER.info("taskkill /F firefox iexplore chrome");
        Utilities.killApplication("iexplore.exe");
        Utilities.killApplication("firefox.exe");
        Utilities.killApplication("chrome.exe");
    }

    /**
     * Autoriser ou interdire l'accès à Internet.
     *
     * @param block {@code true} pour bloquer.
     */
    private void setWebEnable(boolean block) throws ThotException {
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
     */
    private void sendSupervisionCommand(Command command) throws ThotException {
        Utilities.sendMessage(CommandXMLUtilities.getXML(command), masterIP, ThotPort.studentToMasterPort);
    }

    /**
     * Envoi un appel d'aide au professeur.
     */
    public void sendHelpDemand() throws ThotException {
        Command command = new Command(CommandModule.SUPERVISION, CommandAction.HELP_CALL);
        sendSupervisionCommand(command);
    }

    /**
     * Transfert un fichier au professeur.
     *
     * @param file le fichier.
     */
    public void sendFile(File file) throws ThotException {
        Command command = new Command(CommandModule.SUPERVISION, CommandAction.RECEIVE_FILE);
        command.putParameter(CommandParamater.PORT, ThotPort.fileTransfertPortBase);
        command.putParameter(CommandParamater.FILE, file.getName());
        command.putParameter(CommandParamater.SIZE, file.length());

        sendSupervisionCommand(command);

        fileTransfert.sendFile(file, ThotPort.fileTransfertPortBase, 1);
    }

    /**
     * Transfert un fichier au professeur.
     */
    private void sendEndedError() throws ThotException {
        Command command = new Command(CommandModule.SUPERVISION, CommandAction.END_ERROR);
        sendSupervisionCommand(command);
    }

    /**
     * Traite les commandes reçues.
     *
     * @param command la commande à traiter.
     */
    private void execute(Command command) throws ThotException {
        CommandAction action = command.getAction();

        switch (action) {
            case PING:
                killApplication();
                if (internetBlocked) {
                    killWebNavigators();
                }

                int batteryLevel = Battery.getBatteryLevel();

                String remoteHost = command.getParameter(CommandParamater.IP_ADDRESS);
                boolean passwordChecked = command.getParameterAsBoolean(CommandParamater.PASSWORD_CHECKED);

                if (remoteHost.isEmpty()) {
                    return;
                }

                Command returnCommand = new Command(CommandModule.SUPERVISION, CommandAction.PONG);
                returnCommand.putParameter(CommandParamater.BATTERY, batteryLevel);

                //pas de login élève
                if (userLogin == null) {
                    if (!login.isVisible() && !blackWindow.isVisible()) {
                        login.showLogin(true);
                    }

                    loginSended = false;
                } //login élève vérifié
                else if (passwordChecked) {
                    returnCommand.putParameter(CommandParamater.NAME, userLogin);
                    returnCommand.putParameter(CommandParamater.PASSWORD, userPassword);
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
                        returnCommand.putParameter(CommandParamater.NAME, userLogin);
                        returnCommand.putParameter(CommandParamater.PASSWORD, userPassword);
                        loginSended = true;
                    }
                }

                Utilities.sendMessage(CommandXMLUtilities.getXML(returnCommand), remoteHost,
                        ThotPort.studentToMasterPort);
                break;
            case SEND_SCREEN:
                int portBase = command.getParameterAsInt(CommandParamater.SCREEN_PORT);
                int nbClient = command.getParameterAsInt(CommandParamater.CLIENT_NUMBER);

                boolean control = false;
                if (command.getParameter(CommandParamater.REMOTE_HANDLING) != null) {
                    control = command.getParameterAsBoolean(CommandParamater.REMOTE_HANDLING);
                }

                if (command.getParameter(CommandParamater.FPS) != null) {
                    captureScreen.setFPS(command.getParameterAsDouble(CommandParamater.FPS));
                }
                if (command.getParameter(CommandParamater.QUALITY) != null) {
                    captureScreen.setQuality(command.getParameterAsInt(CommandParamater.QUALITY));
                }
                if (command.getParameter(CommandParamater.LINES) != null) {
                    captureScreen.setNbLines(command.getParameterAsInt(CommandParamater.LINES));
                }

                int audioPort = chatVoip.getPort();

                String list = command.getParameter(CommandParamater.LIST);
//                List<InetSocketAddress> addresses = new ArrayList<>(nbClient);
                if (list != null) {
                    List<String> ipList = XMLUtilities.parseList(list);
                    for (String ip : ipList) {
                        //addresses.add(new InetSocketAddress(ip, portBase));
                        chatVoip.connect(ip, audioPort);
                    }
                    ipList.clear();
                }

                if (command.getParameter(CommandParamater.AUDIO_PORT) != null) {
                    String hostIP = command.getParameter(CommandParamater.IP_ADDRESS);
                    chatVoip.connect(hostIP, command.getParameterAsInt(CommandParamater.AUDIO_PORT));
                    //addresses.add(new InetSocketAddress(hostIP, portBase));
                }

                captureScreen.start(portBase, nbClient, control);
                //captureScreen.start(addresses, control);
                break;
            case SEND_SCREEN_STOP:
                chatVoip.disconnectAllWithoutPairing();
                captureScreen.stop();
                break;
            case RECEIVE_SCREEN:
                String addressIP = command.getParameter(CommandParamater.IP_ADDRESS);
                int remotePort = command.getParameterAsInt(CommandParamater.SCREEN_PORT);

                boolean remoteHandling = false;
                if (command.getParameter(CommandParamater.REMOTE_HANDLING) != null) {
                    remoteHandling = command.getParameterAsBoolean(CommandParamater.REMOTE_HANDLING);
                }

                int nbLines = -1;
                if (command.getParameter(CommandParamater.LINES) != null) {
                    nbLines = command.getParameterAsInt(CommandParamater.LINES);
                }
                if (command.getParameter(CommandParamater.TIMEOUT) != null) {
                    int timeout = command.getParameterAsInt(CommandParamater.TIMEOUT);
                    screenWindow.setTimeout(timeout);
                }

                if (Utilities.WINDOWS_PLATFORM) {
                    WindowsUtilities.blockInput(true);
                }

                screenWindow.start(addressIP, remotePort, remoteHandling, nbLines);
                break;
            case RECEIVE_BLACK_SCREEN:
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
            case RECEIVE_SCREEN_STOP:
                chatVoip.disconnectAllWithoutPairing();
                screenWindow.stop();

                if (blackWindow.isVisible()) {
                    blackWindow.showWindow(false);
                }

                if (Utilities.WINDOWS_PLATFORM && !blockWindow.isVisible()) {
                    WindowsUtilities.blockInput(false);
                }
                break;
            case PAIRING_STOP:
                chatVoip.disconnectAll();
                break;
            case PAIRING:
                String pairingIP = command.getParameter(CommandParamater.IP_ADDRESS);
                int portPairing = command.getParameterAsInt(CommandParamater.AUDIO_PORT);

                chatVoip.disconnectAll();
                chatVoip.connectPairing(pairingIP, portPairing);
                break;
            case SEND_VOICE:
                if (command.getParameter(CommandParamater.IP_ADDRESS) != null) {
                    String stHost = command.getParameter(CommandParamater.IP_ADDRESS);
                    if (stHost.length() > 1) {
                        chatVoip.connect(stHost, command.getParameterAsInt(CommandParamater.AUDIO_PORT));
                    }
                }
                break;
            case RECEIVE_MESSAGE:
                String message = command.getParameter(CommandParamater.MESSAGE);
                showMessage(message);
                break;
            case RECEIVE_FILE:
                final String address = command.getParameter(CommandParamater.IP_ADDRESS);
                final int portFile = command.getParameterAsInt(CommandParamater.PORT);
                final String fileName = command.getParameter(CommandParamater.FILE);
                final int size = command.getParameterAsInt(CommandParamater.SIZE);

                final File file = new File(userPath, fileName);
                new Thread(() -> fileTransfert.loadFile(file, size, address, portFile), "receiveFile").start();
                break;
            case LAUNCH_FILE:
                File launchFile = new File(userPath, command.getParameter(CommandParamater.FILE));
                FileTransfert.launchFile(launchFile);
                break;
            case EXECUTE:
                String parameter = command.getParameter(CommandParamater.FILE);
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
                    Utilities.startProcess(binCommand, out, err, binCommand);
                }
                break;
            case RESET_LOGIN:
                userLogin = null;
                login.reset();
                break;
            case SHUTDOWN_SESSION:
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
            case SHUTDOWN:
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
                    Utilities.startProcess("shutdown", out, err, shutdownCommand);
                }

                new Thread(() -> {
                    Utilities.waitInMillisecond(500);
                    System.exit(0);
                }, "exit").start();
                break;
            case BLOCK_KEYBOARD:
                boolean block = command.getParameterAsBoolean(CommandParamater.BLOCK);
                if (Utilities.WINDOWS_PLATFORM) {
                    WindowsUtilities.blockInput(block);
                }

                blockWindow.showWindow(block);
                break;
            case RECEIVE_INTREDICTION:
                String applicationList = command.getParameter(CommandParamater.LIST);
                applicationsForbiden.clear();
                if (applicationList != null) {
                    List<String> appList = XMLUtilities.parseList(applicationList);
                    applicationsForbiden.addAll(appList);
                    appList.clear();
                }

                killApplication();
                break;
            case BLOCK_INTERNET:
                setWebEnable(command.getParameterAsBoolean(CommandParamater.BLOCK));
                break;
            case DELETE_DOCUMENT:
                Utilities.deteleFiles(userPath);
                break;
            case MASTER_CLOSED:
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

                Command command = CommandXMLUtilities.parseCommand(xml.toString());
                if (command != null) {
                    execute(command);
                }

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
