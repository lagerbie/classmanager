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
