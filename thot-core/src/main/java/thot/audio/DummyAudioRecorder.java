package thot.audio;

/**
 * Classe pour la non capture de données audio.
 *
 * @author Fabrice Alleau
 * @version 1.8.4
 */
public class DummyAudioRecorder extends DummyAudioProcessing implements AudioRecorder {

    /**
     * Initialisation.
     */
    public DummyAudioRecorder() {
    }

    @Override
    public void close() {

    }
}
