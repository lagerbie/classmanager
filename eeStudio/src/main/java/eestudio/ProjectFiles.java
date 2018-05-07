package eestudio;

/**
 * Cette classe regroupant les fichiers du projet.
 *
 * @author Fabrice Alleau
 * @version 1.03
 * @since version 0.94
 */
@Deprecated
public class ProjectFiles {
    /**
     * Nom du logiciel de l'exportation
     */
    private String soft;
    /**
     * Nom du fichier audio
     */
    private String audioFile;
    /**
     * Nom du fichier video
     */
    private String videoFile;
    /**
     * Nom du fichier d'index
     */
    private String indexesFile;
    /**
     * Nom du fichier de sous titres
     */
    private String subtitleFile;
    /**
     * Nom du fichier texte
     */
    private String textFile;
    /**
     * Nom du fichier des tags
     */
    private String tagFile;

    /**
     * Nom du fichier video originel (sans sous-titre incrustés)
     */
    private String videoOriginalFile;

    /**
     * Initialisation de la gestion des fichiers du projet.
     *
     * @since version 0.94 - version 1.03
     */
    public ProjectFiles() {
        this.soft = Constants.COMMON_SOFT;
        this.audioFile = null;
        this.videoFile = null;
        this.indexesFile = null;
        this.subtitleFile = null;
        this.textFile = null;
        this.tagFile = null;
        this.videoOriginalFile = null;
    }

    /**
     * Retourne le nom du logiciel de destination du projet.
     *
     * @return le nom du logiciel de destination.
     *
     * @since version 0.96
     */
    public String getSoft() {
        return soft;
    }

    /**
     * Retourne le nom du fichier audio.
     *
     * @return le nom du fichier audio.
     *
     * @since version 0.94
     */
    public String getAudioFile() {
        return audioFile;
    }

    /**
     * Retourne le nom du fichier vidéo.
     *
     * @return le nom du fichier vidéo.
     *
     * @since version 0.94
     */
    public String getVideoFile() {
        return videoFile;
    }

    /**
     * Retourne le nom du fichier d'index.
     *
     * @return le nom du fichier d'index.
     *
     * @since version 0.94
     */
    public String getIndexesFile() {
        return indexesFile;
    }

    /**
     * Retourne le nom du fichier de soustitres.
     *
     * @return le nom du fichier de soustitres.
     *
     * @since version 0.94
     */
    public String getSubtitleFile() {
        return subtitleFile;
    }

    /**
     * Retourne le nom du fichier texte.
     *
     * @return le nom du fichier texte.
     *
     * @since version 0.94
     */
    public String getTextFile() {
        return textFile;
    }

    /**
     * Retourne le nom du fichier des tags.
     *
     * @return le nom du fichier des tags.
     *
     * @since version 1.00
     */
    public String getTagFile() {
        return tagFile;
    }

    /**
     * Retourne le nom du fichier vidéo originale (sans les sous-titres intégrés).
     *
     * @return le nom du fichier vidéo originale.
     *
     * @since version 0.96
     */
    public String getVideoOriginalFile() {
        return videoOriginalFile;
    }

    /**
     * Modifie le nom du logiciel de destination du projet.
     *
     * @param soft le nom du logiciel de destination.
     *
     * @since version 0.96
     */
    public void setSoft(String soft) {
        this.soft = soft;
    }

    /**
     * Modifie le nom du fichier audio.
     *
     * @param audioFile le nom du nouveau fichier audio.
     *
     * @since version 0.94
     */
    public void setAudioFile(String audioFile) {
        this.audioFile = audioFile;
    }

    /**
     * Modifie le nom du fichier vidéo.
     *
     * @param videoFile le nom du nouveau fichier vidéo.
     *
     * @since version 0.94
     */
    public void setVideoFile(String videoFile) {
        this.videoFile = videoFile;
    }

    /**
     * Modifie le nom du fichier d'index.
     *
     * @param indexesFile le nom du nouveau fichier d'index.
     *
     * @since version 0.94
     */
    public void setIndexesFile(String indexesFile) {
        this.indexesFile = indexesFile;
    }

    /**
     * Modifie le nom du fichier de soustitres.
     *
     * @param subtitleFile le nom du nouveau fichier de soustitres.
     *
     * @since version 0.94
     */
    public void setSubtitleFile(String subtitleFile) {
        this.subtitleFile = subtitleFile;
    }

    /**
     * Modifie le nom du fichier texte.
     *
     * @param textFile le nom du nouveau fichier texte.
     *
     * @since version 0.94
     */
    public void setTextFile(String textFile) {
        this.textFile = textFile;
    }

    /**
     * Modifie le nom du fichier des tags.
     *
     * @param tagFile le nom du nouveau fichier des tags.
     *
     * @since version 1.00
     */
    public void setTagFile(String tagFile) {
        this.tagFile = tagFile;
    }

    /**
     * Modifie le nom du fichier vidéo originale (sans les sous-titres intégrés).
     *
     * @param videoOriginalFile le nom du fichier vidéo originale.
     *
     * @since version 0.96
     */
    public void setVideoOriginalFile(String videoOriginalFile) {
        this.videoOriginalFile = videoOriginalFile;
    }

    /**
     * Réinitialise les fichiers.
     *
     * @since version 0.95 - version 1.00
     */
    public void clear() {
        setAudioFile(null);
        setVideoFile(null);
        setTextFile(null);
        setIndexesFile(null);
        setSubtitleFile(null);
        setTagFile(null);
        setVideoOriginalFile(null);
    }

    /**
     * Indique si le projet est vide.
     *
     * @return <code>true</code> si le projet est vide.
     *
     * @since version 0.95
     */
    public boolean isEmptyProject() {
        return audioFile == null && videoFile == null && indexesFile == null
                && subtitleFile == null && textFile == null;
    }

    /**
     * Retourne si le project contient uniquement un fichier audio.
     *
     * @return si on uniquement un fichier audio.
     *
     * @since version 0.94
     */
    public boolean isAudioProject() {
        return audioFile != null && videoFile == null && textFile == null
                && indexesFile == null && subtitleFile == null;
    }

    /**
     * Retourne si le project contient uniquement un fichier vidéo.
     *
     * @return si on uniquement un fichier vidéo.
     *
     * @since version 0.94
     */
    public boolean isVideoProject() {
        return audioFile == null && videoFile != null && textFile == null
                && indexesFile == null && subtitleFile == null;
    }

    /**
     * Retourne si le project contient uniquement un fichier texte.
     *
     * @return si on uniquement un fichier texte.
     *
     * @since version 0.94
     */
    public boolean isTextProject() {
        return audioFile == null && videoFile == null && textFile != null
                && indexesFile == null && subtitleFile == null;
    }

    /**
     * Retourne si le project contient uniquement un fichier d'index.
     *
     * @return si on uniquement un fichier d'index.
     *
     * @since version 0.94
     */
    public boolean isIndexesProject() {
        return audioFile == null && videoFile == null && textFile == null
                && indexesFile != null;
    }

}//end
