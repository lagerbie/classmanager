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
