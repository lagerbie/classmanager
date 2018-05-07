package thot.audio;

/**
 * Interface pour l'enrigistrement de données audio.
 *
 * @author Fabrice Alleau
 * @version 1.8.4
 */
public interface AudioRecorder extends AudioProcessing {

    /**
     * Fermeture de la ligne.
     */
    void close();
}
