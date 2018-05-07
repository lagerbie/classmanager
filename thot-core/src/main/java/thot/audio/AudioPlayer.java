package thot.audio;

/**
 * Interface pour la lecture de données audio.
 *
 * @author Fabrice Alleau
 * @version 1.8.4
 */
public interface AudioPlayer extends AudioProcessing {

    /**
     * Modifie le volume.
     *
     * @param value la valeur de volume en poucentage (entre 0 et 100).
     */
    void setVolume(int value);

    /**
     * Fermeture de la ligne.
     */
    void close();
}
