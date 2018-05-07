package eestudio;

/**
 * Constantes.
 *
 * @author Fabrice Alleau
 * @version 1.01
 * @since version 0.94
 */
@Deprecated
public interface Constants {
    /**
     * Module en pause
     */
    int PAUSE = 0;
    /**
     * Module en lecture
     */
    int PLAYING = 1;
    /**
     * Module en enregistrement
     */
    int RECORDING = 2;
    /**
     * Module en insertion de voix
     */
    int RECORDING_INSERT = 3;//

    /**
     * Nom du charset pour le format UTF-8
     */
    String UTF8_CHARSET = "UTF-8";
    /**
     * Nom du charset pour le format des fenêtres DOS
     */
    String DOS_CHARSET = "IBM850";
    /**
     * Nom du charset pour le format par défaut de Windows
     */
    String WINDOWS_CHARSET = "windows-1252";

    /**
     * Vérification si la plateforme est Linux
     */
    boolean LINUX_PLATFORM = System.getProperty("os.name").toLowerCase().contains("linux");
    /**
     * Vérification si la plateforme est Windows
     */
    boolean WINDOWS_PLATFORM = System.getProperty("os.name").toLowerCase().contains("windows");

    /**
     * Nom de l'extension d'un projet edu4
     */
    String edu4Extension = ".ees";
    /**
     * Nom de l'extension d'un projet
     */
    String projectExtension = ".project";
    /**
     * Nom de l'extension d'un fichier d'index
     */
    String indexesExtension = ".index";
    /**
     * Nom de l'extension d'un fichier de tags
     */
    String tagExtension = ".tag";
    /**
     * Nom de l'extension par le format wav
     */
    String WAV_extension = ".wav";
    /**
     * Nom de l'extension par défaut pour l'enregistrement du fichier audio
     */
    String audioDefaultExtension = ".mp3";
    /**
     * Nom de l'extension par le format avi
     */
    String AVI_extension = ".avi";
    /**
     * Nom de l'extension par le format flv
     */
    String FLV_extension = ".flv";
    /**
     * Nom de l'extension par défaut pour l'enregistrement du fichier audio
     */
    String videoDefaultExtension = ".mp4";
    /**
     * Nom de l'extension d'un fichier texte brut
     */
    String TXT_extension = ".txt";
    /**
     * Nom de l'extension d'un fichier texte au format RTF
     */
    String RTF_extension = ".rtf";
    /**
     * Nom de l'extension d'un fichier texte au format HTML
     */
    String HTML_extension = ".html";
    /**
     * Nom de l'extension par défaut pour l'enregistrement de fichiers texte
     */
    String textDefaultExtension = HTML_extension;
    /**
     * Nom de l'extension d'un fichier de sous-titres au format SubRip
     */
    String SRT_extension = ".srt";
    /**
     * Nom de l'extension d'un fichier de sous-titres au format SubWiever
     */
    String SUB_extension = ".sub";
    /**
     * Nom de l'extension d'un fichier de sous-titres au format LyRiCs
     */
    String LRC_extension = ".lrc";

    /**
     * Extensions possibles pour les fichiers texte
     */
    String[] subtitleExtension = {
            SRT_extension, SUB_extension, LRC_extension
    };

    /**
     * Extensions possibles pour les fichiers texte
     */
    String[] textExtension = {
            TXT_extension,
            RTF_extension, HTML_extension, ".htm",
            SRT_extension, SUB_extension, LRC_extension
    };

    /**
     * Extensions possibles pour les fichiers texte avec gestion de styles
     */
    String[] textStyledExtension = {
            RTF_extension, HTML_extension, ".htm"
    };

    /**
     * Extensions possibles pour les fichiers audio
     */
    String[] audioExtension = {
            "wav", "mp3", "flac", "ogg", "wma"
    };

    /**
     * Extensions possibles pour les fichiers video
     */
    String[] videoExtension = {
            "avi", "mpg", "mpeg", "mp4", "wmv", "flv", "mkv"
    };

    /**
     * Extensions possibles pour les fichiers image
     */
    String[] imageExtension = {
            "png", "jpg", "jpeg", "gif", "bmp"
    };

}
