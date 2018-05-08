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

import java.io.File;

/**
 * Interface pour la gestion d'un projet.
 *
 * @author Fabrice Alleau
 * @version 1.8.4
 */
public interface ProjectManager {

    /**
     * Retourne les fichiers du projet.
     *
     * @return le projet courant.
     */
    ProjectFiles getProjectFiles();

    /**
     * Charge les fichiers contenus dans le projet. Les données précédante sont effacées.
     *
     * @param project l'ensembles des fichiers à charger.
     *
     * @return la réussite du chargement.
     */
    boolean loadProject(ProjectFiles project);

    /**
     * Sauvegarde d'un projet.
     *
     * @param file le fichier de sauvegarde.
     * @param project le projet à sauvegarder.
     *
     * @return la réussite dde la sauvegarde.
     */
    boolean saveProject(File file, ProjectFiles project);

    /**
     * Supprime les éléments du projet qu sont chargés.
     *
     * @param project le projet dont les éléments doivent être supprimer.
     */
    void removeProject(ProjectFiles project);

    String getText();

    long getRecordTimeMax();

    boolean hasAudioSrteam(File file);

    boolean hasVideoSrteam(File file);

}
