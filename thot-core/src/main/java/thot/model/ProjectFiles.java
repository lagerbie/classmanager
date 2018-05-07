/*
 * ClassManager - Supervision de classes et Laboratoire de langue
 * Copyright (C) 2013 Fabrice Alleau <fabrice.alleau@siclic.fr>
 *
 * This file is part of ClassManager.
 *
 * ClassManager is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * ClassManager is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with ClassManager.  If not, see <http://www.gnu.org/licenses/>.
 */
package thot.model;

/**
 * Cette classe regroupant les fichiers du projet.
 *
 * @author Fabrice Alleau
 * @version 1.90
 */
public class ProjectFiles {

    /**
     * Nom du fichier audio.
     */
    private String audioFile;
    /**
     * Nom du fichier video.
     */
    private String videoFile;
    /**
     * Nom du fichier d'index.
     */
    private String indexesFile;
    /**
     * Nom du fichier de sous titres.
     */
    private String subtitleFile;
    /**
     * Nom du fichier texte.
     */
    private String textFile;
    /**
     * Nom du fichier video originel (sans sous-titre incrustés).
     */
    private String videoOriginalFile;

    /**
     * Initialisation de la gestion des fichiers du projet.
     */
    public ProjectFiles() {
        this.audioFile = null;
        this.videoFile = null;
        this.indexesFile = null;
        this.subtitleFile = null;
        this.textFile = null;
        this.videoOriginalFile = null;
    }

    /**
     * Retourne le nom du fichier audio.
     *
     * @return le nom du fichier audio.
     */
    public String getAudioFile() {
        return audioFile;
    }

    /**
     * Retourne le nom du fichier vidéo.
     *
     * @return le nom du fichier vidéo.
     */
    public String getVideoFile() {
        return videoFile;
    }

    /**
     * Retourne le nom du fichier d'index.
     *
     * @return le nom du fichier d'index.
     */
    public String getIndexesFile() {
        return indexesFile;
    }

    /**
     * Retourne le nom du fichier de soustitres.
     *
     * @return le nom du fichier de soustitres.
     */
    public String getSubtitleFile() {
        return subtitleFile;
    }

    /**
     * Retourne le nom du fichier texte.
     *
     * @return le nom du fichier texte.
     */
    public String getTextFile() {
        return textFile;
    }

    /**
     * Retourne le nom du fichier vidéo originale (sans les sous-titres
     * intégrés).
     *
     * @return le nom du fichier vidéo originale.
     */
    public String getVideoOriginalFile() {
        return videoOriginalFile;
    }

    /**
     * Modifie le nom du fichier audio.
     *
     * @param audioFile le nom du nouveau fichier audio.
     */
    public void setAudioFile(String audioFile) {
        this.audioFile = audioFile;
    }

    /**
     * Modifie le nom du fichier vidéo.
     *
     * @param videoFile le nom du nouveau fichier vidéo.
     */
    public void setVideoFile(String videoFile) {
        this.videoFile = videoFile;
    }

    /**
     * Modifie le nom du fichier d'index.
     *
     * @param indexesFile le nom du nouveau fichier d'index.
     */
    public void setIndexesFile(String indexesFile) {
        this.indexesFile = indexesFile;
    }

    /**
     * Modifie le nom du fichier de soustitres.
     *
     * @param subtitleFile le nom du nouveau fichier de soustitres.
     */
    public void setSubtitleFile(String subtitleFile) {
        this.subtitleFile = subtitleFile;
    }

    /**
     * Modifie le nom du fichier texte.
     *
     * @param textFile le nom du nouveau fichier texte.
     */
    public void setTextFile(String textFile) {
        this.textFile = textFile;
    }

    /**
     * Modifie le nom du fichier vidéo originale (sans les sous-titres
     * intégrés).
     *
     * @param videoOriginalFile le nom du fichier vidéo originale.
     */
    public void setVideoOriginalFile(String videoOriginalFile) {
        this.videoOriginalFile = videoOriginalFile;
    }

    /**
     * Réinitialise les fichiers.
     */
    public void clear() {
        setAudioFile(null);
        setVideoFile(null);
        setTextFile(null);
        setIndexesFile(null);
        setSubtitleFile(null);
        setVideoOriginalFile(null);
    }

    /**
     * Indique si le projet est vide.
     *
     * @return <code>true</code> si le projet est vide.
     */
    public boolean isEmptyProject() {
        return audioFile == null && videoFile == null && indexesFile == null
                && subtitleFile == null && textFile == null;
    }

    /**
     * Retourne si le project contient uniquement un fichier audio.
     *
     * @return si on uniquement un fichier audio.
     */
    public boolean isAudioProject() {
        return audioFile != null && videoFile == null && textFile == null
                && indexesFile == null && subtitleFile == null;
    }

    /**
     * Retourne si le project contient uniquement un fichier vidéo.
     *
     * @return si on uniquement un fichier vidéo.
     */
    public boolean isVideoProject() {
        return audioFile == null && videoFile != null && textFile == null
                && indexesFile == null && subtitleFile == null;
    }

    /**
     * Retourne si le project contient uniquement un fichier texte.
     *
     * @return si on uniquement un fichier texte.
     */
    public boolean isTextProject() {
        return audioFile == null && videoFile == null && textFile != null
                && indexesFile == null && subtitleFile == null;
    }

    /**
     * Retourne si le project contient uniquement un fichier d'index.
     *
     * @return si on uniquement un fichier d'index.
     */
    public boolean isIndexesProject() {
        return audioFile == null && videoFile == null && textFile == null
                && indexesFile != null;
    }
}
