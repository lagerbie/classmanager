package thot.audio;

import java.util.EventListener;

/**
 * Listener pour les processus de traitement.
 *
 * @author Fabrice Alleau
 * @version 1.8.4
 */
public interface TimeProcessingListener extends EventListener {

    /**
     * Notification du changement de temps.
     *
     * @param source the source du changement.
     * @param oldValue l'ancien temps.
     * @param newValue le nouveau temps.
     */
    void timeChanged(Object source, long oldValue, long newValue);

    /**
     * Notification de la fin du processus.
     *
     * @param source the source du changement.
     * @param selfStop indique si le processus s'est arrêté de lui même.
     */
    void endProcess(Object source, boolean selfStop);
}
