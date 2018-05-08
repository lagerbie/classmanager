package thot.utils;

/**
 * Listener pour la progression d'un traitement.
 *
 * @author Fabrice Alleau
 * @version 1.8.4
 */
public interface ProgressPercentListener extends ProgressListener {

    /**
     * Notification du changement de titre.
     *
     * @param source la source de l'évènement.
     * @param title le nouveau titre de la fenêtre.
     */
    void processTitleChanged(Object source, String title);

    /**
     * Notification du changement de message.
     *
     * @param source la source de l'évènement.
     * @param message le nouveau message.
     */
    void processMessageChanged(Object source, String message);

    /**
     * Notification du changement du mode déterminé de la progression.
     *
     * @param source la source de l'évènement.
     * @param determinated le mode déterminé ({@code true} si un pourcentage de progression peut être affiché.
     */
    void processDeterminatedChanged(Object source, boolean determinated);

    /**
     * Notification du changement du mode double de la progression.
     *
     * @param source la source de l'évènement.
     * @param doubleStatus le mode double progression ({@code true} si deux pourcentages de progression peuvent
     *         être affichés.
     */
    void processDoubleStatusChanged(Object source, boolean doubleStatus);

    /**
     * Notification du changement du pourcentage de progression.
     *
     * @param source la source de l'évènement.
     * @param total la nouvelle valeur de progression totale en pourcentage.
     * @param subTotal la nouvelle valeur de progression intermédiaire en pourcentage.
     */
    void percentChanged(Object source, int total, int subTotal);
}
