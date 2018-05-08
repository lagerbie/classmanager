package thot.audio;

/**
 * Classe pour la non lecture de données audio.
 *
 * @author Fabrice Alleau
 * @version 1.8.4
 */
public class DummyAudioPlayer extends DummyAudioProcessing implements AudioPlayer {

    /**
     * Initialisation.
     */
    public DummyAudioPlayer() {
    }

    @Override
    public void setVolume(int value) {

    }

    @Override
    public void close() {

    }

}
