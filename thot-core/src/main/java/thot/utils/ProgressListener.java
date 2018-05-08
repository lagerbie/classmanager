package thot.utils;

import java.util.EventListener;

/**
 * Listener pour la progression d'un traitement.
 *
 * @author Fabrice Alleau
 * @version 1.8.4
 */
public interface ProgressListener extends EventListener {

    /**
     * Notifiction du démmarrage du processus.
     *
     * @param source la source de l'évènement.
     * @param determinated le mode déterminé ({@code true} si un pourcentage de progression peut être affiché.
     */
    void processBegin(Object source, boolean determinated);

    /**
     * Notification de l'arrêt du processus.
     *
     * @param source la source de l'évènement.
     * @param exit la valeur de sortie (par convention 0 équvaut à une sortie normale)
     */
    void processEnded(Object source, int exit);

    /**
     * Notification du changement du pourcentage de progression.
     *
     * @param source la source de l'évènement.
     * @param percent le pourcentage de progression.
     */
    void percentChanged(Object source, int percent);
}
