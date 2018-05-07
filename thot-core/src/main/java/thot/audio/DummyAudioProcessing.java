package thot.audio;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Classe abstraite pour le non traitement de données audio.
 *
 * @author Fabrice Alleau
 * @version 1.8.4
 */
public abstract class DummyAudioProcessing extends AbstractAudioProcessing {

    /**
     * Instance de log.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DummyAudioProcessing.class);

    /**
     * Temps d'attente (en ms).
     */
    private static final int WAITING_TIME = 10;

    /**
     * Initialisation.
     */
    DummyAudioProcessing() {
        super(null, null);
    }

    @Override
    protected void endProcess() {

    }

    @Override
    protected int process(ByteBuffer recordBuffer, byte[] data, int offset, int length) {
        return 0;
    }

    @Override
    public void run() {
        long duration = getEndTime() - getStartTime();
        long initTime = System.currentTimeMillis();

        long timePassed = 0;
        long currentTime = getStartTime();

        while (isRunning() && duration > 0) {
            try {
                Thread.sleep(WAITING_TIME);
            } catch (InterruptedException e) {
                LOGGER.error("Interruption de l'attente sur le dummy audio processing", e);
            }

            timePassed = System.currentTimeMillis() - initTime;
            if (timePassed > NOTIFICATION_MINIMUN_TIME) {
                fireTimeChanged(currentTime, currentTime + timePassed);
                currentTime += timePassed;
                duration -= timePassed;
                timePassed = 0;
                initTime = System.currentTimeMillis();
            }
        }

        fireTimeChanged(currentTime, currentTime + timePassed);
        fireEndProcess(isRunning());
    }
}
