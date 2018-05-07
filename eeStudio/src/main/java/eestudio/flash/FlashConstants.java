package eestudio.flash;

/*
 * v0.95: ajout de public static final String indexAfter = "mcAfterIndex";
 * v0.95: ajout de public static final String catalan = "mcCatalan";
 * v0.95: ajout de public static final String basque = "mcBasque";
 * 
 * v0.95.10: supp de public static final String soft = "mcSoft";
 * v0.95.10: supp de public static final String normalRate = "mcNormal";
 * v0.95.10: supp de public static final String maximize = "maximize";
 * v0.95.10: supp de public static final String minimize = "minimize";
 * v0.95.10: supp de public static final String move = "move";
 * v0.95.10: supp de public static final String textSave = "mcSaveText";
 * 
 * v0.97: ajout de public static final String insert = "insert";
 * 
 * v0.99: ajout de public static final int flashToCorePort = 7240;
 * v0.99: ajout de public static final int coreToFlashPort = 7241;
 * v0.99: ajout de public static final String insertBlank = "mcDetect";
 * 
 * v1.00: supp de public static final String french = "mcFrench";
 * v1.00: supp de public static final String english = "mcEnglish";
 * v1.00: supp de public static final String spanish = "mcSpanish";
 * v1.00: supp de public static final String german = "mcGerman";
 * v1.00: supp de public static final String italian = "mcItalian";
 * v1.00: supp de public static final String catalan = "mcCatalan";
 * v1.00: supp de public static final String basque = "mcBasque";
 * 
 * v1.03: ajout de public static final String indexBlankAfter = "mcBlankIndex";
 * v1.03: supp de public static final String pause = "pause";
 * v1.03: supp de public static final String playing = "playing";
 * v1.03: supp de public static final String recording = "recording";
 * v1.03: supp de public static final String insert = "insert";
 */

/**
 * Noms des boutons du Flash
 * 
 * @author Fabrice Alleau
 * @since version 0.94
 * @version 1.03
 */
public interface FlashConstants {
    /** Numéro du port de communication du flash vers le java */
    public static final int flashToCorePort = 7240;
    /** Numéro du port de communication du java vers le flash */
    public static final int coreToFlashPort = 7241;

    /** Bouton de chargement */
    public static final String load = "mcLoad";
    /** Bouton de sauvegarde */
    public static final String save = "mcSave";

    /** Bouton d'édition de tous les index */
    public static final String edit = "mcEdit";
    /** Bouton de suppression d'élément */
    public static final String erase = "mcErase";
    /** Bouton d'édition des tags */
    public static final String tag = "mcTag";
    /** Bouton d'édition du texte associé */
    public static final String text = "mcText";
    /** Bouton de détection automatique de silence ou phrase */
    public static final String detect = "mcDetect";
    /** Bouton d'insertion de silence par lot */
    public static final String insertBlank = "mcDetect";//"mcInsertBlank";

    /** Bouton de lecture */
    public static final String play = "mcPlay";
    /** Bouton de retour en début de bande */
    public static final String back = "mcBack";
    /** Bouton de déplacement sur la bande de temps */
    public static final String time = "mcTime";
    /** Bouton de changement de volume */
    public static final String volume = "mcVolume";
    /** Bouton mute du volume */
    public static final String mute = "mcMute";

    /** Bouton de fermeture */
    public static final String close = "close";

    /** Bouton de création d'un soustitre */
    public static final String indexSubtitle = "mcSubtitle";
    /** Bouton de création d'un silence */
    public static final String indexBlank = "mcBlank";
    /** Bouton d'insertion d'un fichier */
    public static final String indexFile = "mcFile";
    /** Bouton d'insertion de la voix */
    public static final String indexVoice = "mcVoice";
    /** Bouton de modification de la vitesse */
    public static final String indexSpeed = "mcSpeed";

    /** Bouton d'édition d'un index */
    public static final String indexEdit = "mcEditIndex";
    /** Bouton d'ajout d'un index après un autre index */
    public static final String indexAfter = "mcAfterIndex";
    /** Bouton de répétition d'un index */
    public static final String indexRepeat = "mcRepeatIndex";
    /** Bouton d'insertion d'un silence après un index */
    public static final String indexBlankAfter = "mcBlankIndex";
    /** Bouton de déplacement en début d'un index */
    public static final String indexBegin = "mcBeginIndex";
    /** Bouton de déplacement en fin d'un index */
    public static final String indexEnd = "mcEndIndex";
    /** Bouton de lecture d'un index */
    public static final String indexPlay = "mcPlayIndex";
    /** Bouton d'enregistrement de la voix sur un index */
    public static final String indexRecord = "mcRecordIndex";
    /** Bouton d'effacement des données audio d'un index */
    public static final String indexErase = "mcEraseIndex";
    /** Bouton de suppression d'un index */
    public static final String indexDelete = "mcDeleteIndex";

    /** Bouton de changement de langue */
    public static final String language = "mcLanguage";

}//end
