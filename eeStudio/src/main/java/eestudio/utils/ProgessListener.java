package eestudio.utils;

import java.util.EventListener;

/*
 * v0.98: modif de processTitleChanged(String title) en
 *        processTitleChanged(Object source, String title);
 * v0.98: modif de processMessageChanged(String message) en
 *        processMessageChanged(Object source, String message);
 * v0.98: modif de processDeterminatedChanged(boolean determinated) en
 *        processDeterminatedChanged(Object source, boolean determinated)
 * v0.98: modif de processBegin(boolean determinated) en
 *        processBegin(Object source, boolean determinated)
 * v0.98: modif de processEnded(int exit) en
 *        processEnded(Object source, int exit)
 * v0.98: modif de percentChanged(int percent) en
 *        percentChanged(Object source, int percent)
 * 
 * v0.99: supp de void processTitleChanged(Object source, String title);
 * v0.99: supp de void processMessageChanged(Object source, String message);
 * v0.99: supp de void processDeterminatedChanged(Object source, boolean determinated);
 */

/**
 * Listener pour la progression d'un traitement.
 * 
 * @author Fabrice Alleau
 * @since version 0.95
 * @version 0.99
 */
public interface ProgessListener extends EventListener {

//    /**
//     * Notification du changement du mode déterminé de la progression.
//     *
//     * @param source la source de l'évènement.
//     * @param determinated le mode déterminé (<code>true</code> si un pourcentage
//     *        de progression peut être affiché.
//     * @since version 0.95 - version 0.98
//     */
//    void processDeterminatedChanged(Object source, boolean determinated);

    /**
     * Notifiction du démmarrage du processus.
     *
     * @param source la source de l'évènement.
     * @param determinated le mode déterminé (<code>true</code> si un pourcentage
     *        de progression peut être affiché.
     * @since version 0.95 - version 0.98
     */
    void processBegin(Object source, boolean determinated);

    /**
     * Notification de l'arrêt du processus.
     *
     * @param source la source de l'évènement.
     * @param exit la valeur de sortie (par convention 0 équvaut à une sortie normale)
     * @since version 0.95 - version 0.98
     */
    void processEnded(Object source, int exit);

    /**
     * Notification du changement du pourcentage de progression.
     *
     * @param source la source de l'évènement.
     * @param percent le pourcentage de progression.
     * @since version 0.95 - version 0.98
     */
    void percentChanged(Object source, int percent);

}//end
