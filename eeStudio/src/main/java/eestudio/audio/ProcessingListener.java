package eestudio.audio;

import java.util.EventListener;

/**
 * Observateur pour les processus de traitement de lecture ou écriture de
 * données audio.
 * 
 * @author Fabrice Alleau
 * @since version 0.95
 */
public interface ProcessingListener extends EventListener {

    /**
     * Notification du changement de temps.
     *
     * @param newTime le nouveau temps.
     * @since version 0.95
     */
    void timeChanged(long newTime);

    /**
     * Notification de la fin du processus.
     *
     * @param running indique si le processus s'est arrêté de lui même.
     * @since version 0.95
     */
    void endProcess(boolean running);

}// end
