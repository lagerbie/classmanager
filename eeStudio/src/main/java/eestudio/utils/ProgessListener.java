package eestudio.utils;

import java.util.EventListener;

/**
 * Listener pour la progression d'un traitement.
 *
 * @author Fabrice Alleau
 * @version 0.99
 * @since version 0.95
 */
@Deprecated
public interface ProgessListener extends EventListener {

//    /**
//     * Notification du changement du mode déterminé de la progression.
//     *
//     * @param source la source de l'évènement.
//     * @param determinated le mode déterminé ({@code true} si un pourcentage
//     *        de progression peut être affiché.
//     * @since version 0.95 - version 0.98
//     */
//    void processDeterminatedChanged(Object source, boolean determinated);

    /**
     * Notifiction du démmarrage du processus.
     *
     * @param source la source de l'évènement.
     * @param determinated le mode déterminé ({@code true} si un pourcentage de progression peut être affiché.
     *
     * @since version 0.95 - version 0.98
     */
    void processBegin(Object source, boolean determinated);

    /**
     * Notification de l'arrêt du processus.
     *
     * @param source la source de l'évènement.
     * @param exit la valeur de sortie (par convention 0 équvaut à une sortie normale)
     *
     * @since version 0.95 - version 0.98
     */
    void processEnded(Object source, int exit);

    /**
     * Notification du changement du pourcentage de progression.
     *
     * @param source la source de l'évènement.
     * @param percent le pourcentage de progression.
     *
     * @since version 0.95 - version 0.98
     */
    void percentChanged(Object source, int percent);

}
