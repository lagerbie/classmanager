package thot.audio;

import java.nio.ByteBuffer;

import javax.sound.sampled.AudioFormat;
import javax.swing.event.EventListenerList;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Classe abstraite pour le traitement de données audio.
 *
 * @author Fabrice Alleau
 * @version 1.8.4
 */
public abstract class AbstractAudioProcessing implements AudioProcessing {

    /**
     * Instance de log.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractAudioProcessing.class);

    /**
     * Taille des buffers utilisés. x8 pour XP 2 canaux à 44100Hz
     */
    protected static final int BUFFER_SIZE = 1024 * 8;

    /**
     * Format Audio pour la lecture ou la capture audio.
     */
    @Getter
    private AudioFormat audioFormat;
    /**
     * Buffer pour les données audio.
     */
    @Setter
    private ByteBuffer audioBuffer;

    /**
     * Buffer d'échange.
     */
    private byte data[] = new byte[BUFFER_SIZE];

    /**
     * Temps de départ.
     */
    @Getter
    private long startTime;
    /**
     * Temps de fin.
     */
    @Getter
    private long endTime;
    /**
     * Indicateur que le processus à été lancé ou stoppé.
     */
    @Getter
    private boolean running;

    /**
     * Gestionnaire des Listener.
     */
    private final EventListenerList listenerList = new EventListenerList();

    /**
     * Thread du procesus.
     */
    private Thread thread;

    /**
     * Initialisation avec un format audio et le buffer où seront enregistrées les données pour un mode indirect.
     *
     * @param audioBuffer le buffer de stockage.
     * @param audioFormat le format audio.
     */
    AbstractAudioProcessing(ByteBuffer audioBuffer, AudioFormat audioFormat) {
        this.audioFormat = audioFormat;
        this.audioBuffer = audioBuffer;
    }

    /**
     * Ajoute un listener.
     *
     * @param listener le listener à ajouter.
     */
    @Override
    public void addListener(TimeProcessingListener listener) {
        LOGGER.info("Ajout du listener {}", listener);
        listenerList.add(TimeProcessingListener.class, listener);
    }

    /**
     * Supprime un listener.
     *
     * @param listener le listener à supprimer.
     */
    @Override
    public void removeListener(TimeProcessingListener listener) {
        LOGGER.info("Suppression du listener {}", listener);
        listenerList.remove(TimeProcessingListener.class, listener);
    }

    /**
     * Notification du changement de temps.
     *
     * @param oldValue l'ancien temps.
     * @param newValue le nouveau temps.
     */
    @Override
    public void fireTimeChanged(long oldValue, long newValue) {
        LOGGER.debug("Changement du temps de {} à {}", oldValue, newValue);
        for (TimeProcessingListener listener : listenerList.getListeners(TimeProcessingListener.class)) {
            listener.timeChanged(this, oldValue, newValue);
        }
    }

    /**
     * Appel lors de la fin du traitement.
     *
     * @param running indique si le mode est toujours actif.
     */
    @Override
    public void fireEndProcess(boolean running) {
        LOGGER.debug("Arrêt du processus (par lui même : {})", running);
        for (TimeProcessingListener listener : listenerList.getListeners(TimeProcessingListener.class)) {
            listener.endProcess(this, running);
        }
    }

    /**
     * Démarre l'enregistrement des données.
     *
     * @param startTime le temps de départ.
     * @param stopTime le temps de fin.
     */
    @Override
    public void start(long startTime, long stopTime) {
        LOGGER.info("Lancement du processus [startTime={}; endTime={}]", startTime, stopTime);
        this.startTime = startTime;
        this.endTime = stopTime;

        running = true;
        thread = new Thread(this, this.getClass().getName());
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.start();
        thread.setPriority(Thread.MAX_PRIORITY);
    }

    /**
     * Arrête le processus.
     */
    @Override
    public void stop() {
        LOGGER.info("Arrêt du processus de traitement de l'audio");
        running = false;
    }

    /**
     * Indique si le processus est actif.
     *
     * @return {@code true} si le processus est actif.
     */
    @Override
    public boolean isAlive() {
        return thread != null && thread.isAlive();
    }

    /**
     * Traitement spécifique des données.
     *
     * @param recordBuffer le buffer des données.
     * @param data le tableau des données.
     * @param offset l'offset de départ.
     * @param length le nombre de bytes à traiter.
     *
     * @return le nombre de bytes traités.
     */
    protected abstract int process(ByteBuffer recordBuffer, byte[] data, int offset, int length);

    /**
     * Traitement spécifique à la fin du traitement des données.
     */
    protected abstract void endProcess();

    @Override
    public void run() {
        // Repositionnement de la tête de lecture/enregistrement avant la frame courante
        int samplePosition = (int) (getStartTime() / 1000.0f * audioFormat.getSampleRate());
        int offset = samplePosition * audioFormat.getFrameSize();
        LOGGER.info(
                "Repositionnement de la tête de lecture/enregistrement avant la frame courante [startTime={}, frame={}]",
                getStartTime(), offset);
        audioBuffer.position(offset);

        // Nombres de bytes à lire
        int nbSamples = (int) ((getEndTime() - getStartTime()) / 1000.0f * audioFormat.getSampleRate());
        int nbBytes = nbSamples * audioFormat.getFrameSize();
        LOGGER.info("Nombres de bytes à lire [startTime={}, endTime={}, nbBytes={}]", getStartTime(), getEndTime(),
                nbBytes);

        // Nombres courant de bytes à lire
        int read = Math.min(nbBytes, BUFFER_SIZE);

        long initTime = getStartTime();
        long currentTime = -1;

        //on boucle tant que l'état du module audio n'a pas été arrêté.
        while (isRunning()) {
            read = process(audioBuffer, data, 0, read);

            //mise à jour du temps courant
            currentTime = (long) (audioBuffer.position() / audioFormat.getFrameSize() / audioFormat.getSampleRate()
                    * 1000.0f);

            if (currentTime - initTime > NOTIFICATION_MINIMUN_TIME) {
                // notification toutes seulement si un temps minimun est passé
                fireTimeChanged(initTime, currentTime);
                initTime = currentTime;
            }

            nbBytes -= read;
            if (nbBytes < audioFormat.getFrameSize()) {
                //si inférieur à la taille d'une frame on arrête
                LOGGER.info("Temps de fin atteint [startTime={}, endTime={}, currentTime={}]", getStartTime(),
                        getEndTime(), currentTime);
                break;
            } else {
                //mise à jour du nombres de bytes à lire
                read = Math.min(nbBytes, BUFFER_SIZE);
            }
        }

        endProcess();
        fireTimeChanged(initTime, currentTime);
        fireEndProcess(isRunning());
    }
}
