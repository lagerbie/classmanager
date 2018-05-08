package thot.supervision;

import thot.utils.ProgressPercentListener;

/**
 * Listener pour les évènements du noyau professeur.
 *
 * @author Fabrice Alleau
 * @version 1.8.4
 */
public interface MasterCoreListener extends ProgressPercentListener {

    /**
     * Notification d'ajout d'un élève.
     *
     * @param source la source de l'évènement.
     * @param student l'élève ajouter.
     */
    void studentAdded(Object source, Student student);

    /**
     * Notification de modification d'un élève.
     *
     * @param source la source de l'évènement.
     * @param student l'élève ajouter.
     */
    void studentChanged(Object source, Student student);

    /**
     * Notification du changement d'état d'un bouton.
     *
     * @param source la source de l'évènement.
     * @param name le nom du bouton.
     * @param state le nouvel état du bouton.
     *
     * @deprecated
     */
    @Deprecated
    void buttonStateChanged(Object source, String name, boolean state);

    /**
     * Notification du changement de sélection d'un groupe.
     *
     * @param source la source de l'évènement.
     * @param group le groupe dont l'état de sélection est modifié.
     * @param selected le nouvel état.
     */
    void groupSelectionChanged(Object source, int group, boolean selected);

    /**
     * Notification du changement de l'élève sélectionné.
     *
     * @param source la source de l'évènement.
     * @param newStudent le nouvel élève sélectionné.
     * @param oldStudent l'ancien élève sélectionné.
     */
    void studentSelectedChanged(Object source, Student newStudent, Student oldStudent);

    /**
     * Notification du changement d'état de la visualisation.
     *
     * @param source la source de l'évènement.
     * @param newState le nouvel état de la visualisation.
     * @param oldState l'ancien état de la visualisation.
     */
    void visuationStateChanged(Object source, int newState, int oldState);

    /**
     * Notification du changement d'association d'élève pour le pairing.
     *
     * @param source la source de l'évènement.
     * @param student un élève.
     * @param associated l'élève associé pour le pairing.
     */
    void pairingStudentAssociatedChanged(Object source, Student student, Student associated);

    /**
     * Notification d'un message.
     *
     * @param source la source de l'évènement.
     * @param student l'élève demandant de l'aide.
     * @param type le type message a afficher.
     * @param args les arguments pour une chaîne de caractères formatée.
     */
    void newMessage(Object source, Student student, String type, Object... args);

    /**
     * Notification d'un message.
     *
     * @param source la source de l'évènement.
     * @param messageType le type message a afficher.
     * @param args les arguments pour une chaîne de caractères formatée.
     */
    void newMessage(Object source, String messageType, Object... args);
}
