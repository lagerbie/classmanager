package thot.labo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thot.audio.DummyAudioPlayer;
import thot.audio.DummyAudioRecorder;
import thot.exception.ThotException;
import thot.gui.Resources;
import thot.supervision.Command;
import thot.supervision.CommandAction;
import thot.supervision.CommandParamater;
import thot.supervision.CommandType;
import thot.supervision.CommandXMLUtilities;
import thot.supervision.MasterCore;
import thot.supervision.Student;
import thot.supervision.StudentClass;
import thot.supervision.gui.GuiConstants;
import thot.utils.Constants;
import thot.utils.Server;
import thot.utils.ThotPort;
import thot.utils.Utilities;
import thot.video.Converter;

/**
 * Noyau pour le contôle des laboratoire élèves.
 *
 * @author Fabrice alleau
 * @version 1.8.4
 */
public class LaboModule extends LaboCore {

    /**
     * Instance de log.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);

    /**
     * Noyau de supervision.
     */
    private MasterCore core;
    /**
     * Liste des apprenants.
     */
    private StudentClass studentClass;
    /**
     * Répertoire de l'utilisateur.
     */
    private final File userHome;
    /**
     * Sauvegarde du paramètre envoyé (nécessaire pour les fichiers).
     */
    private File fileSend;
    /**
     * Sauvegarde de l'élève courant.
     */
    private String currentStudentName = null;
    /**
     * Sauvegarde de l'état de diffusion.
     */
    private boolean diffuse = false;
    /**
     * Ressources pour les textes à afficher.
     */
    private Resources resources;
    /**
     * Timeout.
     */
    private int timeout = 1000;
    /**
     * Nombre de client pour les processus de transfert.
     */
    private int nbClient = -1;
    /**
     * Indice du client pour les processus de transfert.
     */
    private int indiceClient = -1;

    /**
     * Initialisation avec le classeLabo relais avec le noyau de supervision.
     *
     * @param resources les resources textuelles.
     * @param encoder l'encodeur pour les fichiers audio et vidéo.
     * @param userHome le répertoire de travail.
     * @param core référence du noyau supervision.
     */
    public LaboModule(Resources resources, Converter encoder, File userHome, MasterCore core) {
        super(encoder);
        this.resources = resources;
        this.core = core;
        this.studentClass = core.getStudentClass();
        this.userHome = userHome;

        initRecorderAndPlayer(new DummyAudioRecorder(), new DummyAudioPlayer());

        StudentServer studentServer = new StudentServer();
        studentServer.start();
    }

    @Override
    public void eraseIndexRecord(double position) {
    }

    /**
     * Envoie d'une commande avec parametre.
     *
     * @param command la commande.
     */
    private void sendCommand(Command command) {
        String xml = CommandXMLUtilities.getXML(command);
        //pour toutes les adresses
        for (Iterator<Student> it = studentClass.iterator(); it.hasNext(); ) {
            Student student = it.next();
            if (studentClass.isSelectionnedForDiffuse(student)) {
                if (!sendCommand(xml, student.getAddressIP())) {
                    //TODO Commande traitement des échecs
                }
            }
        }
    }

    /**
     * Envoi la commande à l'adresse indiquée.
     *
     * @param address l'adresse IP de l'élève.
     * @param xml la commande à envoyer.
     *
     * @return {@code true} si la commande a bien été exécuter.
     */
    private boolean sendCommand(String xml, String address) {
        try {
            Utilities.sendMessage(xml, address, ThotPort.masterToStudentPort);
            return true;
        } catch (ThotException e) {
            LOGGER.error("Echec de l'envoi de la commande {} à l'élève {}:{}", xml, address,
                    ThotPort.masterToStudentPort);
            return false;
        }
    }

    /**
     * Envoie la commande d'ouverture du labo à tous les élèves sélectionnés.
     */
    private void sendOpenLabo() {
        core.sendOpenLabo();
    }

    /**
     * Exécute une commande en fonction du bouton et du paramètre associé.
     *
     * @param button le nom du bouton de la commande.
     * @param parameter le paramètre associé.
     */
    public void executeCommand(String button, String parameter) {
        LOGGER.info("button: " + button + " parameter: " + parameter);
        Command command = new Command(CommandType.TYPE_LABORATORY, CommandAction.getCommandAction(button));

        int cnt;
        long time;
        String fileName;
        File file;
        FileInputStream fileInputStream;

        switch (button) {
            case GuiConstants.freeze:
                command.putParameter(CommandParamater.PARAMETER, parameter);
                sendCommand(command);
                break;
            case GuiConstants.fullScreen:
                command.putParameter(CommandParamater.PARAMETER, parameter);
                sendCommand(command);
                break;
            case GuiConstants.mediaDiffuse:
                if (diffuse) {
                    if (getRunningState() != Constants.PAUSE) {
                        audioPause();
                    }
                    command.putParameter(CommandParamater.PARAMETER, !diffuse);
                    sendCommand(command);
                    diffuse = false;
                } else {
                    //affiche la boite de dialogue et récupère le nom du fichier si l'action à été validée
                    file = new File(parameter);
                    if (file.exists()) {
                        command.putParameter(CommandParamater.RECEIVE_FILE, true);
                        command.putParameter(CommandParamater.FILE, file.getName());
                        try {
                            fileInputStream = new FileInputStream(fileSend);
                            //envoi la taille du fichier
                            cnt = fileInputStream.available();
                            command.putParameter(CommandParamater.SIZE, Integer.toString(cnt));
                        } catch (IOException e) {
                            LOGGER.error("", e);
                        }

                        sendCommand(command);
//                        loadMedia(file);
                        audioPlay();
                        command.putParameter(CommandParamater.RECEIVE_FILE, false);
                        sendCommand(command);
                        diffuse = true;
                    }
                }
                break;
            case GuiConstants.time:
                int running = getRunningState();
                if (running != Constants.PAUSE) {
                    audioPause();
                }
                time = Utilities.parseStringAsLong(parameter);
                if (time >= 0) {
                    setTime(time);
                    command.putParameter(CommandParamater.PARAMETER, parameter);
                    sendCommand(command);
                    if (running == Constants.PLAYING) {
                        audioPlay();
                    } else if (running == Constants.RECORDING) {
                        audioRecord();
                    }
                }
                break;
            case GuiConstants.timeMax:
                time = Utilities.parseStringAsLong(parameter);
                if (time > 0) {
                    setRecordTimeMax(time);
                    command.putParameter(CommandParamater.PARAMETER, time);
                    sendCommand(command);
                    setTime(0);
                }
                break;
            case GuiConstants.rapatriate:
                command.putParameter(CommandParamater.PARAMETER, parameter);
                sendCommand(command);
                break;
            case GuiConstants.message:
                command.putParameter(CommandParamater.PARAMETER, parameter);
                sendCommand(command);
                break;
            case GuiConstants.mediaLoad:
                //affiche la boite de dialogue et récupère le nom du fichier
                //si l'action à été validée
                file = new File(parameter);
                if (file.exists()) {
                    command.putParameter(CommandParamater.FILE, file.getName());
                    try {
                        fileInputStream = new FileInputStream(fileSend);
                        //envoi la taille du fichier
                        cnt = fileInputStream.available();
                        command.putParameter(CommandParamater.SIZE, Integer.toString(cnt));
                    } catch (IOException e) {
                        LOGGER.error("", e);
                    }
                    String name = Utilities.getNameWithoutExtension(file);
                    File fileProject = null;
                    if (Utilities.isProjectFile(file)) {

                    }

//                    if (fileProject != null) {
//                        AssociatedFiles.showDialog(playerFrame, this, resources, fileProject);
//                    } else {
//                        success = loadMedia(file);
//                    }
                    setTime(0);
                }
                break;
            case GuiConstants.mediaSend:
                //            flashSendCommand("secure", "true");
                command.putParameter(CommandParamater.PARAMETER, getProjectFiles().getVideoFile());
                sendCommand(command);
                //si il y a un fichier d'index, on l'envoi
                if (getProjectFiles().getIndexesFile() != null) {
//                    sendStudentCommand(Command.MEDIA_LOAD_INDEXES, getProjectFiles().getIndexesFile());
                }
                //            flashSendCommand("secure", "false");
                break;
            case GuiConstants.mediaUnload:
//                sendStudentCommand(Command.MEDIA_UNLOAD, null);
//                mediaUnload();
                break;
            case GuiConstants.back:
                command.putParameter(CommandParamater.PARAMETER, 0);
                sendCommand(command);
                setTimeToZero();
                break;
            case GuiConstants.play:
                if (parameter != null && !parameter.trim().isEmpty()) {
                    time = Utilities.parseStringAsLong(parameter);
                    if (time >= 0) {
                        setTime(time);
                    }
                }

                audioPlay();
                command.putParameter(CommandParamater.PARAMETER, getCurrentTime());
                sendCommand(command);
                break;
            case GuiConstants.stop:
                if (getRunningState() != Constants.PAUSE) {
                    audioPause();
                }
                sendCommand(command);
                break;
            case GuiConstants.record:
                if (parameter != null && !parameter.trim().isEmpty()) {
                    time = Utilities.parseStringAsLong(parameter);
                    if (time >= 0) {
                        setTime(time);
                    }
                }

                audioRecord();
                command.putParameter(CommandParamater.PARAMETER, getCurrentTime());
                sendCommand(command);
                break;
            case GuiConstants.audioErase:
//                sendStudentCommand(Command.AUDIO_ERASE, null);
                break;
            case GuiConstants.audioSave:
//                fileName = gui.showInputDialog(resources.getString("saveAudio"), null);
//                if (fileName != null) {
//                    sendStudentCommand(Command.AUDIO_SAVE, fileName);
//                }
                break;
            case GuiConstants.masterTextErase:
//                textErase();
                break;
            case GuiConstants.textSend:
                File textFile = new File(tempPath, defaultFileName + Constants.RTF_extension);
//                saveText(textFile);
//                sendStudentCommand(Command.TEXT_LOAD, textFile.getAbsolutePath());
                break;
            case GuiConstants.closeLabo:
                sendCommand(command);
                break;
            case GuiConstants.lanceLabo:
                sendOpenLabo();
                break;
        }
    }

    /**
     * Exécute la commande demandée par l'élève.
     *
     * @param studentIP l'adresse IP de l'élève.
     * @param command la commande à interpréter.
     *
     * @return la validité de la commande.
     */
    private boolean executeCommand(String studentIP, Command command) {
//        if(!isStudentPresent(studentIP))
//            return false;

        boolean isExecute = false;

        switch (command.getAction()) {

        }

        return isExecute;
    }

    //========================================================================//

    /**
     * Thread de gestion des commandes envoyer par les laboratoires élèves.
     *
     * @version 0.94
     */
    private class StudentServer extends Server {

        private StudentServer() {
            super(ThotPort.studentToMasterLaboPort);
        }

        @Override
        protected void process(Socket socket) throws IOException {
            String studentIP = socket.getInetAddress().getHostAddress();

            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());

            String xml = inputStream.readUTF();
            LOGGER.info("xml commande de pilotage: " + xml);
            List<Command> commands = CommandXMLUtilities.parseCommand(xml);

            boolean isOK = false;
            if (!commands.isEmpty()) {
                Command command = commands.get(0);
                isOK = executeCommand(studentIP, command);
                commands.clear();
            }
            //envoi de la façon dont s'est exécutée la commande
            outputStream.writeBoolean(isOK);
            outputStream.flush();
        }
    }
}
