package thot.labo;

import lombok.Data;

/**
 * Cette classe regroupant les fichiers du projet.
 *
 * @author Fabrice Alleau
 */
@Data
public class ProjectFiles {

    /**
     * Nom du logiciel de l'exportation
     */
    private ProjectTarget soft = ProjectTarget.COMMON_SOFT;
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
     * Réinitialise les fichiers.
     */
    public void clear() {
        this.soft = ProjectTarget.COMMON_SOFT;
        this.audioFile = null;
        this.videoFile = null;
        this.indexesFile = null;
        this.subtitleFile = null;
        this.textFile = null;
        this.tagFile = null;
        this.videoOriginalFile = null;
    }

    /**
     * Indique si le projet est vide.
     *
     * @return {@code true} si le projet est vide.
     */
    public boolean isEmptyProject() {
        return audioFile == null && videoFile == null && indexesFile == null && subtitleFile == null
                && textFile == null;
    }

    /**
     * Retourne si le project contient uniquement un fichier audio.
     *
     * @return si on uniquement un fichier audio.
     */
    public boolean isAudioProject() {
        return audioFile != null && videoFile == null && textFile == null && indexesFile == null
                && subtitleFile == null;
    }

    /**
     * Retourne si le project contient uniquement un fichier vidéo.
     *
     * @return si on uniquement un fichier vidéo.
     */
    public boolean isVideoProject() {
        return audioFile == null && videoFile != null && textFile == null && indexesFile == null
                && subtitleFile == null;
    }

    /**
     * Retourne si le project contient uniquement un fichier texte.
     *
     * @return si on uniquement un fichier texte.
     */
    public boolean isTextProject() {
        return audioFile == null && videoFile == null && textFile != null && indexesFile == null
                && subtitleFile == null;
    }

    /**
     * Retourne si le project contient uniquement un fichier d'index.
     *
     * @return si on uniquement un fichier d'index.
     */
    public boolean isIndexesProject() {
        return audioFile == null && videoFile == null && textFile == null && indexesFile != null;
    }
}
