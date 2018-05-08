package thot;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.List;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thot.audio.AudioRecorder;
import thot.audio.DatagramSocketAudioRecorder;
import thot.audio.DirectAudioPlayer;
import thot.audio.DirectAudioRecorder;
import thot.exception.ThotCodeException;
import thot.exception.ThotException;
import thot.gui.GuiUtilities;
import thot.gui.Resources;
import thot.labo.LaboCore;
import thot.labo.ProjectFiles;
import thot.labo.index.Index;
import thot.supervision.Command;
import thot.supervision.CommandAction;
import thot.supervision.CommandParamater;
import thot.supervision.CommandXMLUtilities;
import thot.utils.Constants;
import thot.utils.Converter;
import thot.utils.Server;
import thot.utils.ThotPort;
import thot.utils.Utilities;

/*
 * resources: soundError
 */

/**
 * Noyau du laboratoire élèvé.
 *
 * @author Fabrice Alleau
 * @version 1.90
 */
public class LaboratoryCore extends LaboCore {

    /**
     * Instance de log.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(LaboratoryCore.class);

    /**
     * Répertoire de l'utilisateur.
     */
    private final File userHome;
    /**
     * Nom du fichier de sauvegarde de l'audio temporaire.
     */
    private final File audioFileTemp;

    /**
     * Format Audio pour la capture du microphone.
     */
    private AudioFormat audioFormat;
    /**
     * Stockage des données audio à enregistrer.
     */
    private ByteBuffer audioBuffer;

    /**
     * Serveur pour écouter les requêtes du professeur.
     */
    private MasterServer masterServer;

    /**
     * Taille des buffer utilisés.
     */
    private static final int BUFFER_SIZE = 1024 * 64;

    /**
     * Initialisation avec le répertoire de travail.
     *
     * @param encoder l'encodeur pour les fichiers audio et vidéo.
     * @param userHome le répertoire de travail.
     */
    public LaboratoryCore(Converter encoder, File userHome) {
        super(encoder);
        this.userHome = userHome;
        audioFileTemp = new File(tempPath, "audioTemp.wav");
    }

    /**
     * Initialise les valeurs par défaut.
     *
     * @param resources les resources textuelles.
     * @param microphone prise directe du microphone.
     */
    public void initValues(Resources resources, boolean microphone) throws ThotException {
        //Initialisation du format audio
        setAudioFormat(microphone ? 22050 : 44100);
        //enregistrement et sauvegarde des données
        audioBuffer = null;
        setByteArrayLimit(getRecordTimeMax());

        DirectAudioPlayer audioPlayer = new DirectAudioPlayer(audioBuffer, audioFormat);
        audioPlayer.initAudioLine();

        if (!audioPlayer.isLineOpen()) {
            String message = resources.getString("soundError");
//            String imagesPath = "labo/gui/images/icone.png";
//            ImageIcon icone = new ImageIcon(getClass().getClassLoader().getResource(imagesPath));

            GuiUtilities.showMessageDialog(null, message);
            System.exit(0);
        }

        AudioRecorder audioRecorder;
        if (microphone) {
            audioRecorder = new DirectAudioRecorder(audioBuffer, audioFormat);
            ((DirectAudioRecorder) audioRecorder).initAudioLine();
        } else {
            initDatagramSocket(ThotPort.microphoneLaboPort, ThotPort.soundServerPort);
            audioRecorder = new DatagramSocketAudioRecorder(audioBuffer, audioFormat);
            ((DatagramSocketAudioRecorder) audioRecorder).initDatagramSocket(ThotPort.microphoneLaboPort);
        }

        initRecorderAndPlayer(audioRecorder, audioPlayer);

        //Crée une Thread pour écouter le port masterCommandPort
        masterServer = new MasterServer();
        masterServer.start();
    }

    /**
     * Ferme l'application.
     */
    @Override
    public void closeApplication() {
        masterServer.stop();
        super.closeApplication();
    }

    /**
     * Notification du changement de langue pour l'interface.
     *
     * @param language le code pour la langue.
     */
    private void fireLanguageChanged(String language) {
        for (LaboratoryListener listener : getListeners(LaboratoryListener.class)) {
            listener.languageChanged(language);
        }
    }

    /**
     * Notification du changement de la durée maximum d'enregistrement.
     *
     * @param recordTimeMax le temps maximum d'enregistrement.
     */
    @Override
    protected void fireRecordTimeMaxChanged(long recordTimeMax) {
        super.fireRecordTimeMaxChanged(recordTimeMax);
        setByteArrayLimit(recordTimeMax);
    }

    /**
     * Notification du changement du volume du module multimédia.
     *
     * @param volume la nouvelle valeur du volume.
     */
    private void fireMediaVolumeChanged(int volume) {
        for (LaboratoryListener listener : getListeners(LaboratoryListener.class)) {
            listener.mediaVolumeChanged(volume);
        }
    }

    /**
     * Notification du changement du volume du module audio.
     *
     * @param volume la nouvelle valeur du volume.
     */
    private void fireAudioVolumeChanged(int volume) {
        for (LaboratoryListener listener : getListeners(LaboratoryListener.class)) {
            listener.audioVolumeChanged(volume);
        }
    }

    /**
     * Notification qu'un nouveau message est prêt à être afficher.
     *
     * @param message le message à afficher.
     */
    private void fireNewMessage(String message) {
        for (LaboratoryListener listener : getListeners(LaboratoryListener.class)) {
            listener.newMessage(message);
        }
    }

    /**
     * Notification du changement d'état du gèle des commandes.
     *
     * @param freeze le nouvel état.
     */
    private void fireFreezeChanged(boolean freeze) {
        for (LaboratoryListener listener : getListeners(LaboratoryListener.class)) {
            listener.studentControlChanged(freeze);
        }
    }

    /**
     * Notification du succes ou non de l'envoi d'une demande d'aide.
     *
     * @param success le succes de la commande.
     */
    private void fireHelpDemandSuccess(boolean success) {
        for (LaboratoryListener listener : getListeners(LaboratoryListener.class)) {
            listener.helpDemandSuccess(success);
        }
    }

    @Override
    public void eraseIndexRecord(double position) {
        Index index = getRecordIndex(position);
        if (index != null) {
            //sauvegarde de ce qu'il y a avant l'index
            int sample = (int) (index.getInitialTime() / 1000.0f * audioFormat.getSampleRate());
            int bufferSize = sample * audioFormat.getFrameSize();
            audioBuffer.position(bufferSize);

            //remplisage de blanc de l'index courant
            sample = (int) ((index.getFinalTime() - index.getInitialTime())
                    / 1000.0f * audioFormat.getSampleRate());
            bufferSize = sample * audioFormat.getFrameSize();
            byte data[] = new byte[bufferSize];
            audioBuffer.put(data);
        }
    }

    /**
     * Modifie la limite du buffer des données suivant le temps maximum.
     *
     * @param time le temps maximum d'enregistrement (en ms).
     */
    private void setByteArrayLimit(long time) {
        //nombre de bytes pour un échantillon
        int bytePerSample = audioFormat.getSampleSizeInBits() / 8;

        if (audioBuffer == null) {
            int cnt = (int) Math.floor(TIME_MAX / 1000.0f
                    * audioFormat.getSampleRate()) * bytePerSample;
            //ByteBuffer.allocateDirect(cnt) peut provoquer un outOfMemory
            audioBuffer = ByteBuffer.allocate(cnt);
        }

        int cnt = (int) Math.floor(time / 1000.0f * audioFormat.getSampleRate())
                * bytePerSample;
        audioBuffer.limit(cnt);
    }

    /**
     * Retourne l'adresse IP du professeur.
     *
     * @return l'adresse IP du professeur.
     */
    private String getMasterIP() {
//        String masterIP = Utilities.getTextInFile(masterFileTemp, "UTF-8");
//        if (masterIP == null || masterIP.isEmpty()) {
        return null;
//        }
//
//        return masterIP;
    }

    /**
     * Charge un fichier audio.
     *
     * @param file le chemein du fichier a charger.
     *
     * @return {@code true} si le chargement s'est bien passé.
     */
    @Override
    public boolean audioLoad(File file) {
        if (!file.exists()) {
            return false;
        }

        eraseAllRecordIndex();
        fireIndexesChanged();

        if (file != audioFileTemp) {
            getProjectFiles().setAudioFile(file.getAbsolutePath());
        }

        //Java ne décode que le .wav
        if (!file.getName().toLowerCase().endsWith(".wav")) {
            executeConverter(file, audioFileTemp,
                    (int) audioFormat.getSampleRate(), audioFormat.getChannels());
            return audioLoad(audioFileTemp);
        }

        try (AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file)) {
            //Récupération du flux audio et du format
            AudioFormat fileAudioFormat = audioInputStream.getFormat();

            //format ne correspond pas a celui utilisé en interne
            if (!fileAudioFormat.matches(audioFormat)) {
                executeConverter(file, audioFileTemp,
                        (int) audioFormat.getSampleRate(), audioFormat.getChannels());
                return audioLoad(audioFileTemp);
            }

            byte tempBuffer[] = new byte[BUFFER_SIZE];

            audioBuffer.clear();
            //enregistrement des données audio dans le ByteBuffer.
            int cnt = audioInputStream.read(tempBuffer);
            while (cnt != -1) {
                if (cnt > 0) {
                    audioBuffer.put(tempBuffer, 0, cnt);
                }
                cnt = audioInputStream.read(tempBuffer);
            }

            audioBuffer.flip();
        } catch (IOException | UnsupportedAudioFileException e) {
            getProjectFiles().setAudioFile(null);
            LOGGER.error("", e);
            return false;
        }

        long timeMax = getRecordTimeMax();
        if (getMediaType() == Constants.UNLOAD) {
            timeMax = (long) (audioBuffer.limit() / audioFormat.getFrameSize()
                    / audioFormat.getSampleRate() * 1000);
        }
        setRecordTimeMax(timeMax);
        setTime(0);

        //pour utiliser le bouton play
//        fireMediaTypeChanged(getMediaType());

        return true;
    }

    /**
     * Sauvegarde les données enregistrées par le microphone dans un fichier.
     *
     * @param file le fichier où l'on veut enregistrer.
     *
     * @return {@code true} si la sauvegarde s'est bien passée.
     */
    @Override
    public boolean saveAudio(File file) {
        if (!Utilities.isAudioFile(file)) {
            return saveAudio(new File(file.getParentFile(), file.getName() + Constants.audioDefaultExtension));
        }

        boolean success;
        //Si le format n'est pas du .wav, on convertie le fichier wav temporaire.
        if (!file.getName().toLowerCase().endsWith(".wav")) {
            success = saveAudio(audioFileTemp);
            executeConverter(audioFileTemp, file);
        } else {
            //Initialisation du type du fichier de sortie à wav
            AudioFileFormat.Type audioFileFormatType = AudioFileFormat.Type.WAVE;
            //Création d'un flux d'entrée audio
            ByteArrayInputStream byteArrayInputStream
                    = new ByteArrayInputStream(audioBuffer.array());
            try (AudioInputStream audioInputStream
                         = new AudioInputStream(byteArrayInputStream, audioFormat,
                    audioBuffer.limit() / audioFormat.getFrameSize())) {

                //on écrit dans le fichier les données.
                AudioSystem.write(audioInputStream, audioFileFormatType, file);
                success = true;
            } catch (IOException e) {
                LOGGER.error("", e);
                success = false;
            }
        }

        if (success && file != audioFileTemp) {
            getProjectFiles().setAudioFile(file.getAbsolutePath());
        }

        return success;
    }

    /**
     * Efface la bande audio.
     */
    @Override
    protected void audioErase() {
        int NbByte = audioBuffer.limit();
        audioBuffer.rewind();

        if (NbByte > 0) {
            byte data[] = new byte[NbByte];
            audioBuffer.put(data);
        }

        super.audioErase();
    }

    /**
     * Met à jour du format audio pour la capture de sons et de la variable bytePerSample.
     *
     * @param sampleRate la fréquence d'échantillonnage. ex. format (44100 Hz, 16 bits, mono, signed,
     *         little-endian).
     */
    private void setAudioFormat(float sampleRate) {
        //Format Audio pour la capture du microphone
//        float sampleRate = 44100.0F; //8000,11025,16000,22050,44100
        int sampleSizeInBits = 16; //8,16 (8 trop petit: bruits parasites)
        int channels = 1; //1,2

        audioFormat = new AudioFormat(sampleRate, sampleSizeInBits, channels, true, false);
    }

    /**
     * Retourne s'il est nécessaire de téléchager le fichier d'une commande.
     *
     * @param command la commande à vérifier.
     *
     * @return {@code true} si il faut télécharger un fichier.
     */
    private boolean isDownloadFile(Command command) {
        boolean download = false;
        switch (command.getAction()) {
            case MEDIA_LOAD:
            case MEDIA_LOAD_INDEXES:
            case MEDIA_LOAD_SUBTITLE:
            case TEXT_LOAD:
            case AUDIO_LOAD:
                String fileName = command.getParameter(CommandParamater.FILE);
                long size = command.getParameterAsLong(CommandParamater.SIZE);
                File file = new File(userHome, fileName);
                //test si le fichier à la même taille (s'il n'existe pas taille = 0)
                download = (file.length() != size);
        }
        return download;
    }

    /**
     * Execute une commande.
     *
     * @param command la commande à exécuter.
     *
     * @return {@code true} si la commande s'est bien effectuée, sinon retourne {@code false}.
     */
    private boolean executeCommand(Command command) {
        boolean isExecute = true;
        ProjectFiles project;

        switch (command.getAction()) {
            case LANGUAGE://réinitialise le poste élève
                fireLanguageChanged(command.getParameter(CommandParamater.PARAMETER));
                break;
            case FREEZE:
                fireFreezeChanged(Utilities.parseStringAsBoolean(command.getParameter(CommandParamater.PARAMETER)));
                break;
            case CLOSE:
                new Thread(() -> {
                    Utilities.waitInMillisecond(200);
                    closeApplication();
                }, "close").start();
                break;
            case TIME_MOVE:
                //utilisation de setPosition qui est sécurisée (pas setTime)
                double position = (double) Utilities.parseStringAsLong(command.getParameter(CommandParamater.PARAMETER))
                        / getRecordTimeMax();
                setPosition(position);
                break;
            case TIME_MAX:
                if (getProjectFiles().getVideoFile() != null) {
                    project = new ProjectFiles();
                    project.setVideoFile(defaultFileName);
                    removeProject(project);
                }
                setRecordTimeMax(Utilities.parseStringAsLong(command.getParameter(CommandParamater.PARAMETER)));
                break;
            case TIME_TO_ZERO:
                timeToZero();
                break;
            case MEDIA_LOAD:
                if (getRunningState() != Constants.PAUSE) {
                    //au cas où il y a une lecture en cours
                    audioPause();
                }
                File file = new File(userHome, command.getParameter(CommandParamater.PARAMETER));
                project = Utilities.getProject(file);
                isExecute = loadProject(project);
                break;
            case MEDIA_FULL_SCREEN:
                setFullScreen(Utilities.parseStringAsBoolean(command.getParameter(CommandParamater.PARAMETER)));
                break;
            case MEDIA_UNLOAD:
                if (getRunningState() != Constants.PAUSE) {
                    //au cas où il y a une lecture en cours
                    audioPause();
                }
                project = new ProjectFiles();
                project.setVideoFile(command.getParameter(CommandParamater.MEDIA_UNLOAD));
                project.setAudioFile(command.getParameter(CommandParamater.AUDIO_ERASE));
                project.setTextFile(command.getParameter(CommandParamater.TEXT_ERASE));
                removeProject(project);
                break;
            case MEDIA_VOLUME:
                int mediaVolume = Utilities.parseStringAsInt(command.getParameter(CommandParamater.PARAMETER));
                mediaSetVolume(mediaVolume);
                fireMediaVolumeChanged(mediaVolume);
                break;
            case AUDIO_PLAY:
                if (getRunningState() != Constants.PAUSE) {
                    //au cas où il y a une lecture en cours
                    audioPause();
                }
                if (command.getParameter(CommandParamater.PARAMETER) != null) {
                    setTime(Utilities.parseStringAsLong(command.getParameter(CommandParamater.PARAMETER)));
                }
                audioPlay();
                break;
            case AUDIO_RECORD:
                if (getRunningState() != Constants.PAUSE) {
                    //au cas où il y a une lecture en cours
                    audioPause();
                }
                if (command.getParameter(CommandParamater.PARAMETER) != null) {
                    setTime(Utilities.parseStringAsLong(command.getParameter(CommandParamater.PARAMETER)));
                }
                audioRecord();
                break;
            case AUDIO_PAUSE:
                if (getRunningState() != Constants.PAUSE) {
                    //pour éviter de faire pause inutilement
                    audioPause();
                }
                break;
            case AUDIO_SAVE:
                if (getRunningState() != Constants.PAUSE) {
                    //au cas où il y a une lecture en cours
                    audioPause();
                }
                saveAudio(new File(userHome, command.getParameter(CommandParamater.PARAMETER)));
                break;
            case AUDIO_VOLUME:
                int audioVolume = Utilities.parseStringAsInt(command.getParameter(CommandParamater.PARAMETER));
                audioSetVolume(audioVolume);
                fireAudioVolumeChanged(audioVolume);
                break;
            case SEND_MESSAGE:
                fireNewMessage(command.getParameter(CommandParamater.PARAMETER));
                break;
            default:
                isExecute = false;
        }

        return isExecute;
    }

    /**
     * Envoi un appel d'aide au professeur.
     */
    public void sendHelpDemand() {
        //si pas de professeur déclaré
        String masterIP = getMasterIP();
        boolean success = false;
        if (masterIP != null) {
            Socket socket = null;
            try {
                socket = new Socket(masterIP, ThotPort.studentToMasterPort);
                socket.getOutputStream().write(0);
                byte[] response = new byte[256];
                socket.getInputStream().read(response);
                success = true;
            } catch (IOException e) {
                LOGGER.error("", e);
            } finally {
                //Fermeture
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        LOGGER.error("", e);
                    }
                }
            }
        }

        fireHelpDemandSuccess(success);
    }


    /**
     * Initialise la socket multicast pour la réception des données du microphone.
     *
     * @param portMicrophone le port de réception des données audio.
     * @param portMicrophoneServer le port du serveur du microphone.
     */
    private void initDatagramSocket(int portMicrophone, int portMicrophoneServer) throws ThotException {
        String xml = "<connection><address>127.0.0.1</address>"
                + "<port>" + String.valueOf(portMicrophone) + "</port></connection>";

        try (Socket socket = new Socket("127.0.0.1", portMicrophoneServer)) {

            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            outputStream.writeUTF(xml);
            outputStream.flush();

        } catch (IOException e) {
            throw new ThotException(ThotCodeException.SERVER,
                    "Impossible de se connecter au serveur du microphone sur le port {}", e, portMicrophoneServer);
        }

    }


    /**
     * Classe pour la gestion des commandes du professeur.
     *
     * @version 1.90
     */
    private class MasterServer extends Server {

        private MasterServer() {
            super(ThotPort.masterToStudentLaboPort);
        }

        @Override
        protected void process(Socket socket) throws IOException {
            boolean isOK = false;
//            masterIP = socket.getInetAddress().getHostAddress();

            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());

            String xml = inputStream.readUTF();
            LOGGER.info("receive command : " + xml);
            List<Command> commands = CommandXMLUtilities.parseCommand(xml);

            if (commands.isEmpty()) {
                outputStream.writeUTF(CommandAction.END.getAction());
                outputStream.flush();
                //envoi de la façon dont s'est exécutée la commande
                outputStream.writeBoolean(false);
                outputStream.flush();
                return;
            }

            Command command = commands.get(0);

            //si il y a un fichier à télécharger
            if (isDownloadFile(command)) {
                //création d'un fichier avec le même nom dans le
                //répertoire userHome
                File file = new File(userHome, command.getParameter(CommandParamater.PARAMETER));
                //ouverture du fichier d'écriture
                FileOutputStream fileOutputStream = new FileOutputStream(file);

                outputStream.writeUTF(CommandAction.FILE_GET.getAction());
                outputStream.flush();
                //récupération de la taille du fichier à télécharger
                int cntMax = inputStream.readInt();
                //récupération du fichier: obligatoirement par des read
                //byte (car se n'est pas un fichier texte)
                int cnt = 0;
                byte tempBuffer[] = new byte[BUFFER_SIZE];
                while (cnt < cntMax) {
                    int read = inputStream.read(tempBuffer);
                    if (read > 0) {
                        fileOutputStream.write(tempBuffer, 0, read);
                        cnt += read;
                    }
                }
                fileOutputStream.close();
            }

            if (command.getAction() == CommandAction.FILE_GET) {
                File file = new File(userHome, defaultFileName);
                ProjectFiles projectFiles = new ProjectFiles();
                projectFiles.setVideoFile(defaultFileName);
                projectFiles.setAudioFile(defaultFileName);
                projectFiles.setTextFile(defaultFileName);
                saveProject(file, projectFiles);
                //envoi du fichier si il existe, sinon on retourne l'échec
                if (file.exists()) {
                    outputStream.writeUTF(CommandAction.FILE_SEND.getAction());
                    outputStream.flush();
                    //envoi du nom de fichier
                    outputStream.writeUTF(file.getName());
                    outputStream.flush();

                    FileInputStream fileInputStream = new FileInputStream(file);
                    //envoi la taille du fichier
                    outputStream.writeInt(fileInputStream.available());
                    outputStream.flush();

                    byte tempBuffer[] = new byte[BUFFER_SIZE];
                    //envoi du fichier: obligatoirement par des write
                    //byte (car se n'est pas un fichier texte)
                    int cnt = fileInputStream.read(tempBuffer);
                    while (cnt != -1) {
                        outputStream.write(tempBuffer, 0, cnt);
                        cnt = fileInputStream.read(tempBuffer);
                    }
                    outputStream.flush();
                    //fermeture du fichier
                    fileInputStream.close();

                    //attente de la fin du téléchargement
                    isOK = inputStream.readBoolean();
                }
            } else {
                //exécution de la commande
                isOK = executeCommand(command);
            }

            outputStream.writeUTF(CommandAction.END.getAction());
            outputStream.flush();
            //envoi de la façon dont s'est exécutée la commande
            outputStream.writeBoolean(isOK);
            outputStream.flush();
        }
    }
}
