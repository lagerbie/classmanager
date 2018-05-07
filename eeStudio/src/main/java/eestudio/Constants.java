package eestudio;

/*
 * v0.92: modif de videoExtension pour avoir que des fichiers vidéo
 * v0.93: modif des types pour les fichiers
 *
 * v0.94: ajout de  String LRC_extension = ".lrc";
 * v0.94: ajout de LRC_extension dans subtitleExtension
 * v0.94: ajout de LRC_extension dans textExtension
 * v0.94: ajout de RTF_extension, HTML_extension et ".htm" dans textExtension
 * v0.94: modif de la valeur textDefaultExtension pour HTML_extension
 *
 * v0.95: modif de  int PROJECT en PROJECT_FILE
 * v0.95: modif de  int INDEX en INDEX_FILE
 * v0.95: modif de  int AUDIO en AUDIO_FILE
 * v0.95: modif de  int VIDEO en VIDEO_FILE
 * v0.95: modif de  int IMAGE en IMAGE_FILE
 * v0.95: modif de  int TEXT en TEXT_FILE
 * v0.95: modif de  int SUBTITLE en SUBTITLE_FILE
 * v0.95: modif de  int DIAPORAMA en DIAPORAMA_FILE
 * v0.95: modif de audioExtension [ajout de "flac"]
 * v0.95.11: modif de  String edu4Extension (".edu4" -> ".ees")
 * v0.95.12: ajout de  String WAV_extension = ".wav";
 * v0.95.12: ajout de  String COMMON_SOFT = "common";
 * v0.95.12: ajout de  String EASYLAB = "easyLab";
 * 
 * v0.96: ajout de  String AVI_extension = ".avi";
 * 
 * v0.97: ajout de  int RECORDING_INSERT = 3;
 * v0.97: supp de  int UNLOAD = 0;
 * v0.97: supp de  int AUDIO_FILE = 11;
 * v0.97: supp de  int VIDEO_FILE = 12;
 * v0.97: supp de  int DIAPORAMA_FILE = 21;
 * v0.97: supp de  String diaporamaExtension = ".diapo";
 * 
 * v0.98: ajout de  String SIMPLE_EXPORT = "simple";
 * v0.98: ajout de  String UTF8_CHARSET = "UTF-8";
 * v0.98: ajout de  String DOS_CHARSET = "IBM850";
 * v0.98: ajout de  String WINDOWS_CHARSET = "windows-1252";
 * 
 * v1.00: ajout de  String tagExtension = ".tag";
 * v1.00: supp de  int EDU4_FILE = 1;
 * v1.00: supp de  int PROJECT_FILE = 2;
 * v1.00: supp de  int INDEX_FILE = 3;
 * v1.00: supp de  int IMAGE_FILE = 13;
 * v1.00: supp de  int TEXT_FILE = 14;
 * v1.00: supp de  int SUBTITLE_FILE = 15;
 * 
 * v1.01: ajout de  String FLV_extension = ".flv";
 */

/**
 * Constantes.
 *
 * @author Fabrice Alleau
 * @since version 0.94
 * @version 1.01
 */
@Deprecated
public interface Constants {
    /** Module en pause */
     int PAUSE = 0;
    /** Module en lecture */
     int PLAYING = 1;
    /** Module en enregistrement */
     int RECORDING = 2;
    /** Module en insertion de voix */
     int RECORDING_INSERT = 3;//

    /** Logiciel standard (Vocalab 3, eeVision 2) */
     String COMMON_SOFT = "common";
    /** Logiciel standard (Vocalab 3, eeVision 2) */
     String SIMPLE_EXPORT = "simple";
    /** Logiciel EasyLab */
     String EASYLAB = "easyLab";

    /** Nom du charset pour le format UTF-8 */
     String UTF8_CHARSET = "UTF-8";
    /** Nom du charset pour le format des fenêtres DOS */
     String DOS_CHARSET = "IBM850";
    /** Nom du charset pour le format par défaut de Windows */
     String WINDOWS_CHARSET = "windows-1252";

    /** Vérification si la plateforme est Linux */
     boolean LINUX_PLATFORM
            = System.getProperty("os.name").toLowerCase().contains("linux");
    /** Vérification si la plateforme est Windows */
     boolean WINDOWS_PLATFORM
            = System.getProperty("os.name").toLowerCase().contains("windows");

    /** Nom de l'extension d'un projet edu4 */
     String edu4Extension = ".ees";
    /** Nom de l'extension d'un projet */
     String projectExtension = ".project";
    /** Nom de l'extension d'un fichier d'index */
     String indexesExtension = ".index";
    /** Nom de l'extension d'un fichier de tags */
     String tagExtension = ".tag";
    /** Nom de l'extension par le format wav */
     String WAV_extension = ".wav";
    /** Nom de l'extension par défaut pour l'enregistrement du fichier audio */
     String audioDefaultExtension = ".mp3";
    /** Nom de l'extension par le format avi */
     String AVI_extension = ".avi";
    /** Nom de l'extension par le format flv */
     String FLV_extension = ".flv";
    /** Nom de l'extension par défaut pour l'enregistrement du fichier audio */
     String videoDefaultExtension = ".mp4";
    /** Nom de l'extension d'un fichier texte brut */
     String TXT_extension = ".txt";
    /** Nom de l'extension d'un fichier texte au format RTF */
     String RTF_extension = ".rtf";
    /** Nom de l'extension d'un fichier texte au format HTML */
     String HTML_extension = ".html";
    /** Nom de l'extension par défaut pour l'enregistrement de fichiers texte */
     String textDefaultExtension = HTML_extension;
    /** Nom de l'extension d'un fichier de sous-titres au format SubRip */
     String SRT_extension = ".srt";
    /** Nom de l'extension d'un fichier de sous-titres au format SubWiever */
     String SUB_extension = ".sub";
    /** Nom de l'extension d'un fichier de sous-titres au format LyRiCs */
     String LRC_extension = ".lrc";

    /** Extensions possibles pour les fichiers texte */
     String[] subtitleExtension = {
        SRT_extension, SUB_extension, LRC_extension
    };

    /** Extensions possibles pour les fichiers texte */
     String[] textExtension = {
        TXT_extension,
        RTF_extension, HTML_extension, ".htm",
        SRT_extension, SUB_extension, LRC_extension
    };

    /** Extensions possibles pour les fichiers texte avec gestion de styles */
     String[] textStyledExtension = {
        RTF_extension, HTML_extension, ".htm"
    };

    /** Extensions possibles pour les fichiers audio */
     String[] audioExtension = {
        "wav", "mp3", "flac", "ogg", "wma"
    };

    /** Extensions possibles pour les fichiers video */
     String[] videoExtension = {
        "avi", "mpg", "mpeg", "mp4", "wmv", "flv", "mkv"
    };

    /** Extensions possibles pour les fichiers image */
     String[] imageExtension = {
        "png", "jpg", "jpeg", "gif", "bmp"
    };

}
