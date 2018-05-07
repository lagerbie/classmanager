package eestudio.audio;

import java.nio.ByteBuffer;

import javax.sound.sampled.AudioFormat;
import javax.swing.event.EventListenerList;

/**
 * Traitement général d'un buffer audio.
 * Cette classe définit les éléments communs à la lecture et l'enregistrement
 * d'un buffer audio.
 *
 * @author Fabrice Alleau
 * @see Player
 * @see Recorder
 * @since version 0.94
 * @version 0.97
 */
public abstract class AudioProcessing implements Runnable {
    /** Taille des buffers utilisés */
    public static final int BUFFER_SIZE = 1024*8;//x8 pour XP 2 canaux à 44100Hz
    /** Buffer d'échange */
    protected final byte[] data = new byte[BUFFER_SIZE];
    /** Format Audio pour la capture ou la lecture du buffer audio */
    protected final AudioFormat audioFormat;
    /** Stockage des données audio */
    protected ByteBuffer buffer;

    /** Temps de départ dans le buffer */
    protected long initTime;
    /** Temps d'arrêt dans le buffer */
    protected long stopTime;

    /** Mode actif */
    protected boolean run;
    /** Thread du procesus */
    private Thread thread;

    /** Gestionnaire des observateurs */
    private final EventListenerList listenerList = new EventListenerList();

    /**
     * Initialisation avec un format audio et une référence sur le buffer où
     * sont les données.
     *
     * @param buffer le buffer de stockage des données audio.
     * @param audioFormat le format audio.
     * @throws IllegalArgumentException si {@code audioFormat} est
     *         {@code null}.
     * @since version 0.97
     */
    public AudioProcessing(ByteBuffer buffer, AudioFormat audioFormat)
            throws IllegalArgumentException {
        if(audioFormat == null)
            throw new IllegalArgumentException("AudioFormat must be not null");

        this.buffer = buffer;
        this.audioFormat = audioFormat;
    }

    /**
     * Modifie la référence de buffer où sont enregistrées les données.
     *
     * @param buffer le nouveau buffer.
     * @since version 0.97
     */
    public void setBuffer(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    /**
     * Ajoute un obsevateur.
     *
     * @param listener l'obsevateur à ajouter.
     * @see ProcessingListener
     * @since version 0.95
     */
    public void addListener(ProcessingListener listener) {
        listenerList.add(ProcessingListener.class, listener);
    }

    /**
     * Supprime un obsevateur.
     *
     * @param listener l'obsevateur à supprimer.
     * @see ProcessingListener
     * @since version 0.95
     */
    public void removeListener(ProcessingListener listener) {
        listenerList.remove(ProcessingListener.class, listener);
    }

    /**
     * Démarre l'enregistrement des données.
     *
     * @param initTime le temps de départ dans le buffer (en ms).
     * @param stopTime le temps de fin dans le buffer (en ms).
     * @since version 0.97
     */
    public void start(long initTime, long stopTime) {
        this.initTime = initTime;
        this.stopTime = stopTime;

        run = true;
        thread = new Thread(this);
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.start();
        thread.setPriority(Thread.MAX_PRIORITY);
    }

    /**
     * Arrête le processus.
     * 
     * @since version 0.94
     */
    public void stop() {
        run = false;
    }

    /**
     * Notification du changement de temps pour les obsevateurs.
     *
     * @param time le nouveau temps.
     * @see ProcessingListener
     * @since version 0.95
     */
    protected void fireTimeChanged(long time) {
        for(ProcessingListener listener : listenerList.getListeners(ProcessingListener.class)) {
            listener.timeChanged(time);
        }
    }

    /**
     * Notification de la fin du traitement pour les obsevateurs.
     *
     * @param running indique si le mode est toujours actif.
     * @see ProcessingListener
     * @since version 0.95
     */
    protected void fireEndProcess(boolean running) {
        for(ProcessingListener listener : listenerList.getListeners(ProcessingListener.class)) {
            listener.endProcess(running);
        }
    }

    /**
     * Retourne si le processus est actif.
     *
     * @return si le processus est actif.
     * @since version 0.94
     */
    public boolean isAlive() {
        if(thread == null)
            return false;
        else
            return thread.isAlive();
    }

}//end
