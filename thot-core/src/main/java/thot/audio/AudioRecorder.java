package thot.audio;

import java.nio.ByteBuffer;

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

    /**
     * Met à jour le buffer pour les données audio.
     *
     * @param audioBuffer le nouveau buffer.
     */
    void setAudioBuffer(ByteBuffer audioBuffer);
}
