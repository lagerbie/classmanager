package thot.audio;

/**
 * Interface pour la lecture et l'enrigistrement de données audio.
 *
 * @author Fabrice Alleau
 * @version 1.8.4
 */
public interface AudioProcessing extends Runnable {

    /**
     * Temps minimun entre 2 notifications (en ms).
     */
    int NOTIFICATION_MINIMUN_TIME = 500;

    /**
     * Démarre l'enregistrement des données.
     *
     * @param startTime le temps de départ.
     * @param stopTime le temps de fin.
     */
    void start(long startTime, long stopTime);

    /**
     * Arrête le processus.
     */
    void stop();

    /**
     * Indique si le processus est actif.
     *
     * @return {@code true} si le processus est actif.
     */
    boolean isAlive();

    /**
     * Ajoute un listener.
     *
     * @param listener le listener à ajouter.
     */
    void addListener(TimeProcessingListener listener);

    /**
     * Supprime un listener.
     *
     * @param listener le listener à supprimer.
     */
    void removeListener(TimeProcessingListener listener);

    /**
     * Notification du changement de temps.
     *
     * @param oldValue l'ancien temps.
     * @param newValue le nouveau temps.
     */
    void fireTimeChanged(long oldValue, long newValue);

    /**
     * Appel lors de la fin du traitement.
     *
     * @param running indique si le mode est toujours actif.
     */
    void fireEndProcess(boolean running);
}
