package thot.supervision.gui;

import java.util.ResourceBundle;

/**
 * @author Fabrice Alleau
 * @version 1.8.4
 */
public interface GuiConstants {

    /**
     * Chemin interne des images.
     */
    String imagesPath = "thot/gui/images/";
    /**
     * Réfrérences sur les images selon leur type.
     */
    ResourceBundle images = ResourceBundle.getBundle("supervision.gui.images");

    /**
     * identifiant pour la notification d'aide.
     */
    String callMessage = "callMessage";
    /**
     * identifiant pour la notification de réception de fichier.
     */
    String receiveFile = "receiveFile";
    /**
     * identifiant pour modifier la langue.
     */
    String language = "language";
    /**
     * identifiant pour modifier l'aide.
     */
    String help = "help";
    /**
     * identifiant pour minimiser.
     */
    String mini = "mini";
    /**
     * identifiant pour fermer.
     */
    String close = "close";
    /**
     * identifiant pour la sélection d'un élève.
     */
    String student = "student";

    //Boutons pour le suivi
    /**
     * identifiant pour l'écoute discrète.
     */
    String listening = "listening";
    /**
     * identifiant pour la prise en main.
     */
    String studentControl = "studentControl";
    /**
     * identifiant pour la scrutation automatique.
     */
    String scanning = "scanning";
    /**
     * identifiant pour la mosaïque.
     */
    String mosaique = "mosaique";

    //Boutons pour la diffusion
    /**
     * identifiant pour l'envoi de fichiers.
     */
    String sendFile = "sendFile";
    /**
     * identifiant pour l'envoi d'un écran noir.
     */
    String blackScreen = "blackScreen";
    /**
     * identifiant pour l'envoi de l'écran professeur.
     */
    String masterScreen = "masterScreen";
    /**
     * identifiant pour l'envoi de l'écran professeur et de sa voix.
     */
    String masterScreenVoice = "masterScreenVoice";
    /**
     * identifiant pour l'envoi de la voix du professeur.
     */
    String masterVoice = "masterVoice";
    /**
     * identifiant pour l'envoi de l'écran d'un élève.
     */
    String studentScreen = "studentScreen";
    /**
     * identifiant pour l'envoi d'un message texte.
     */
    String sendMessage = "sendTextMessage";

    //Boutons pour le pairing
    /**
     * identifiant pour le pairing.
     */
    String pairing = "pairing";
    /**
     * identifiant pour la validation du pairing.
     */
    String pairingValid = "pairingValid";

    //Boutons pour la gestion
    /**
     * identifiant pour la création de groupes.
     */
    String groupCreation = "groupCreation";
    /**
     * identifiant pour l'extinction sur les élèves.
     */
    String studentClose = "studentClose";
    /**
     * identifiant pour la réinitialisation du login.
     */
    String loginSession = "loginSession";
    /**
     * identifiant pour la fermeture de la session de l'ordinateur.
     */
    String osSession = "osSession";
    /**
     * identifiant pour éteintre l'ordinateur.
     */
    String computerPower = "computerPower";

    //Boutons pour les groupes
    /**
     * identifiant de base pour les groupes.
     */
    String group = "group";
    /**
     * identifiant pour le groupe A.
     */
    String groupA = group + "A";
    /**
     * identifiant pour le groupe B.
     */
    String groupB = group + "B";
    /**
     * identifiant pour le groupe C.
     */
    String groupC = group + "C";
    /**
     * identifiant pour le groupe D.
     */
    String groupD = group + "D";
    /**
     * identifiant pour le groupe E.
     */
    String groupE = group + "E";
    /**
     * identifiant pour le groupe F.
     */
    String groupF = group + "F";
    /**
     * identifiant pour le groupe G.
     */
    String groupG = group + "G";
    /**
     * identifiant pour le groupe H.
     */
    String groupH = group + "H";
    /**
     * identifiant pour la flèche des groupes supplémentaires.
     */
    String arrow = "arrow";
    /**
     * identifiant de base pour les groupes lors de la création.
     */
    String creationGroup = "creationGroup";


    String jclicReports = "mcJClicReports";
    String jclic = "mcJClic";

    String bloqueClavier = "mcBlocageClavier";
    String bloqueInternet = "mcBlocageInternet";
    String interdireApplication = "mcBlocageApplication";
    String deleteDocument = "mcDeleteDocument";

    String changeLanguage = "mcLanguage";
    String freeze = "mcBlock";
    String timeMax = "mcTimeMax";
    String rapatriate = "mcRapatriate";
    String rapatriateAudio = "Audio";
    String rapatriateText = "Text";
    String rapatriateAll = "All";
    String message = "mcMessage";
    String mediaLoad = "mcMediaLoad";
    String mediaSend = "mcMediaSend";
    String mediaUnload = "mcMediaErase";
    String mediaVolume = "mcVolumeProf";
    String mediaDiffuse = "mcDiffuse";
    String fullScreen = "mcFullScreen";
    String back = "mcBack";
    String play = "mcPlay";
    String stop = "mcPause";
    String record = "mcRecord";
    String time = "mcTime";
    String audioSend = "mcAudioSend";
    String audioErase = "mcAudioErase";
    String audioSave = "mcAudioSave";
    String audioVolume = "mcVolumeEleve";
    String masterTextLoad = "mcTextLoad";
    String masterTextSave = "mcMasterTextSave";
    String masterTextErase = "mcMasterTextErase";
    String textSave = "mcTextSave";
    String textErase = "mcTextErase";
    String textSpeed = "mcSpeed";
    String textSend = "mcTextSend";
    String lanceLabo = "mcLanceLabo";
    String closeLabo = "mcCloseLabo";

    String unknown = "unknown";
    String playing = "playing";
    String recording = "recording";
    String pause = "pause";
    String runningState = "runningState";
    String unload = "unload";
    String image = "image";
    String audio = "audio";
    String video = "video";
    String mediaType = "mediaType";
    String trackTimeMax = "timeMax";
    String timePosition = "timePosition";
    String text = "text";
    String removeIndexes = "removeIndexes";
    String secure = "secure";
}
