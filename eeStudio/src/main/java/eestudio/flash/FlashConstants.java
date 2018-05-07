package eestudio.flash;

/**
 * Noms des boutons du Flash
 *
 * @author Fabrice Alleau
 * @version 1.03
 * @since version 0.94
 */
public interface FlashConstants {
    /**
     * Numéro du port de communication du flash vers le java
     */
    int flashToCorePort = 7240;
    /**
     * Numéro du port de communication du java vers le flash
     */
    int coreToFlashPort = 7241;

    /**
     * Bouton de chargement
     */
    String load = "mcLoad";
    /**
     * Bouton de sauvegarde
     */
    String save = "mcSave";

    /**
     * Bouton d'édition de tous les index
     */
    String edit = "mcEdit";
    /**
     * Bouton de suppression d'élément
     */
    String erase = "mcErase";
    /**
     * Bouton d'édition des tags
     */
    String tag = "mcTag";
    /**
     * Bouton d'édition du texte associé
     */
    String text = "mcText";
    /**
     * Bouton de détection automatique de silence ou phrase
     */
    String detect = "mcDetect";
    /**
     * Bouton d'insertion de silence par lot
     */
    String insertBlank = "mcDetect";//"mcInsertBlank";

    /**
     * Bouton de lecture
     */
    String play = "mcPlay";
    /**
     * Bouton de retour en début de bande
     */
    String back = "mcBack";
    /**
     * Bouton de déplacement sur la bande de temps
     */
    String time = "mcTime";
    /**
     * Bouton de changement de volume
     */
    String volume = "mcVolume";
    /**
     * Bouton mute du volume
     */
    String mute = "mcMute";

    /**
     * Bouton de fermeture
     */
    String close = "close";

    /**
     * Bouton de création d'un soustitre
     */
    String indexSubtitle = "mcSubtitle";
    /**
     * Bouton de création d'un silence
     */
    String indexBlank = "mcBlank";
    /**
     * Bouton d'insertion d'un fichier
     */
    String indexFile = "mcFile";
    /**
     * Bouton d'insertion de la voix
     */
    String indexVoice = "mcVoice";
    /**
     * Bouton de modification de la vitesse
     */
    String indexSpeed = "mcSpeed";

    /**
     * Bouton d'édition d'un index
     */
    String indexEdit = "mcEditIndex";
    /**
     * Bouton d'ajout d'un index après un autre index
     */
    String indexAfter = "mcAfterIndex";
    /**
     * Bouton de répétition d'un index
     */
    String indexRepeat = "mcRepeatIndex";
    /**
     * Bouton d'insertion d'un silence après un index
     */
    String indexBlankAfter = "mcBlankIndex";
    /**
     * Bouton de déplacement en début d'un index
     */
    String indexBegin = "mcBeginIndex";
    /**
     * Bouton de déplacement en fin d'un index
     */
    String indexEnd = "mcEndIndex";
    /**
     * Bouton de lecture d'un index
     */
    String indexPlay = "mcPlayIndex";
    /**
     * Bouton d'enregistrement de la voix sur un index
     */
    String indexRecord = "mcRecordIndex";
    /**
     * Bouton d'effacement des données audio d'un index
     */
    String indexErase = "mcEraseIndex";
    /**
     * Bouton de suppression d'un index
     */
    String indexDelete = "mcDeleteIndex";

    /**
     * Bouton de changement de langue
     */
    String language = "mcLanguage";

}
