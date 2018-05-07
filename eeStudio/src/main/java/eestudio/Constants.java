package eestudio;

/*
 * v0.92: modif de videoExtension pour avoir que des fichiers vidéo
 * v0.93: modif des types pour les fichiers
 *
 * v0.94: ajout de public static final String LRC_extension = ".lrc";
 * v0.94: ajout de LRC_extension dans subtitleExtension
 * v0.94: ajout de LRC_extension dans textExtension
 * v0.94: ajout de RTF_extension, HTML_extension et ".htm" dans textExtension
 * v0.94: modif de la valeur textDefaultExtension pour HTML_extension
 *
 * v0.95: modif de public static final int PROJECT en PROJECT_FILE
 * v0.95: modif de public static final int INDEX en INDEX_FILE
 * v0.95: modif de public static final int AUDIO en AUDIO_FILE
 * v0.95: modif de public static final int VIDEO en VIDEO_FILE
 * v0.95: modif de public static final int IMAGE en IMAGE_FILE
 * v0.95: modif de public static final int TEXT en TEXT_FILE
 * v0.95: modif de public static final int SUBTITLE en SUBTITLE_FILE
 * v0.95: modif de public static final int DIAPORAMA en DIAPORAMA_FILE
 * v0.95: modif de audioExtension [ajout de "flac"]
 * v0.95.11: modif de public static final String edu4Extension (".edu4" -> ".ees")
 * v0.95.12: ajout de public static final String WAV_extension = ".wav";
 * v0.95.12: ajout de public static final String COMMON_SOFT = "common";
 * v0.95.12: ajout de public static final String EASYLAB = "easyLab";
 * 
 * v0.96: ajout de public static final String AVI_extension = ".avi";
 * 
 * v0.97: ajout de public static final int RECORDING_INSERT = 3;
 * v0.97: supp de public static final int UNLOAD = 0;
 * v0.97: supp de public static final int AUDIO_FILE = 11;
 * v0.97: supp de public static final int VIDEO_FILE = 12;
 * v0.97: supp de public static final int DIAPORAMA_FILE = 21;
 * v0.97: supp de public static final String diaporamaExtension = ".diapo";
 * 
 * v0.98: ajout de public static final String SIMPLE_EXPORT = "simple";
 * v0.98: ajout de public static final String UTF8_CHARSET = "UTF-8";
 * v0.98: ajout de public static final String DOS_CHARSET = "IBM850";
 * v0.98: ajout de public static final String WINDOWS_CHARSET = "windows-1252";
 * 
 * v1.00: ajout de public static final String tagExtension = ".tag";
 * v1.00: supp de public static final int EDU4_FILE = 1;
 * v1.00: supp de public static final int PROJECT_FILE = 2;
 * v1.00: supp de public static final int INDEX_FILE = 3;
 * v1.00: supp de public static final int IMAGE_FILE = 13;
 * v1.00: supp de public static final int TEXT_FILE = 14;
 * v1.00: supp de public static final int SUBTITLE_FILE = 15;
 * 
 * v1.01: ajout de public static final String FLV_extension = ".flv";
 */

/**
 * Constantes.
 *
 * @author Fabrice Alleau
 * @since version 0.94
 * @version 1.01
 */
public interface Constants {
    /** Module en pause */
    public static final int PAUSE = 0;
    /** Module en lecture */
    public static final int PLAYING = 1;
    /** Module en enregistrement */
    public static final int RECORDING = 2;
    /** Module en insertion de voix */
    public static final int RECORDING_INSERT = 3;//

    /** Logiciel standard (Vocalab 3, eeVision 2) */
    public static final String COMMON_SOFT = "common";
    /** Logiciel standard (Vocalab 3, eeVision 2) */
    public static final String SIMPLE_EXPORT = "simple";
    /** Logiciel EasyLab */
    public static final String EASYLAB = "easyLab";

    /** Nom du charset pour le format UTF-8 */
    public static final String UTF8_CHARSET = "UTF-8";
    /** Nom du charset pour le format des fenêtres DOS */
    public static final String DOS_CHARSET = "IBM850";
    /** Nom du charset pour le format par défaut de Windows */
    public static final String WINDOWS_CHARSET = "windows-1252";

    /** Vérification si la plateforme est Linux */
    public static final boolean LINUX_PLATFORM
            = System.getProperty("os.name").toLowerCase().contains("linux");
    /** Vérification si la plateforme est Windows */
    public static final boolean WINDOWS_PLATFORM
            = System.getProperty("os.name").toLowerCase().contains("windows");
    /** Vérification si la plateforme est Machintoch */
    public static final boolean MAC_PLATFORM
            = System.getProperty("os.name").toLowerCase().contains("mac");

    /** Nom de l'extension d'un projet edu4 */
    public static final String edu4Extension = ".ees";
    /** Nom de l'extension d'un projet */
    public static final String projectExtension = ".project";
    /** Nom de l'extension d'un fichier d'index */
    public static final String indexesExtension = ".index";
    /** Nom de l'extension d'un fichier de tags */
    public static final String tagExtension = ".tag";
    /** Nom de l'extension par le format wav */
    public static final String WAV_extension = ".wav";
    /** Nom de l'extension par défaut pour l'enregistrement du fichier audio */
    public static final String audioDefaultExtension = ".mp3";
    /** Nom de l'extension par le format avi */
    public static final String AVI_extension = ".avi";
    /** Nom de l'extension par le format flv */
    public static final String FLV_extension = ".flv";
    /** Nom de l'extension par défaut pour l'enregistrement du fichier audio */
    public static final String videoDefaultExtension = ".mp4";
    /** Nom de l'extension d'un fichier texte brut */
    public static final String TXT_extension = ".txt";
    /** Nom de l'extension d'un fichier texte au format RTF */
    public static final String RTF_extension = ".rtf";
    /** Nom de l'extension d'un fichier texte au format HTML */
    public static final String HTML_extension = ".html";
    /** Nom de l'extension par défaut pour l'enregistrement de fichiers texte */
    public static final String textDefaultExtension = HTML_extension;
    /** Nom de l'extension d'un fichier de sous-titres au format SubRip */
    public static final String SRT_extension = ".srt";
    /** Nom de l'extension d'un fichier de sous-titres au format SubWiever */
    public static final String SUB_extension = ".sub";
    /** Nom de l'extension d'un fichier de sous-titres au format LyRiCs */
    public static final String LRC_extension = ".lrc";

    /** Extensions possibles pour les fichiers texte */
    public static final String[] subtitleExtension = {
        SRT_extension, SUB_extension, LRC_extension
    };

    /** Extensions possibles pour les fichiers texte */
    public static final String[] textExtension = {
        TXT_extension,
        RTF_extension, HTML_extension, ".htm",
        SRT_extension, SUB_extension, LRC_extension
    };

    /** Extensions possibles pour les fichiers texte avec gestion de styles */
    public static final String[] textStyledExtension = {
        RTF_extension, HTML_extension, ".htm"
    };

    /** Extensions possibles pour les fichiers audio */
    public static final String[] audioExtension = {
        "wav", "mp3", "flac", "ogg", "wma"
    };

    /** Extensions possibles pour les fichiers video */
    public static final String[] videoExtension = {
        "avi", "mpg", "mpeg", "mp4", "wmv", "flv", "mkv"
    };

    /** Extensions possibles pour les fichiers image */
    public static final String[] imageExtension = {
        "png", "jpg", "jpeg", "gif", "bmp"
    };

}//end
