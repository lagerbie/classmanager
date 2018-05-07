package eestudio.audio;

import java.nio.ByteBuffer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

/**
 * Classe pour lire les données audio sur la certe son.
 *
 * @author Fabrice Alleau
 * @since version 0.94
 * @version 0.97
 */
@Deprecated
public class Player extends AudioProcessing {
    /** Ligne audio de rendu */
    private SourceDataLine sourceDataLine;
    /** Control du volume de sortie */
    private FloatControl gainControl = null;

    /**
     * Initialisation avec un format audio et une référence sur le buffer où
     * sont enregistrées les données.
     *
     * @param buffer le buffer de stockage.
     * @param audioFormat le format audio.
     * 
     * @throws LineUnavailableException s'il n'y a pas de ligne de rendu à cause
     *         de restrictions des ressources audio.
     * @throws IllegalArgumentException si le système audio ne supporte pas au
     *         moins une ligne de rendu correspondant au format audio spécifié.
     * @see AudioFormat
     * @since version 0.94 - version 0.97
     */
    public Player(ByteBuffer buffer, AudioFormat audioFormat)
            throws LineUnavailableException, IllegalArgumentException {
        super(buffer, audioFormat);

        //Recherche de la configuration pour la lecture de données.
        sourceDataLine = AudioSystem.getSourceDataLine(audioFormat);
        //Ouverture de la ligne avec une taille de buffer la plus petite
        //possible.
        sourceDataLine.open(audioFormat, BUFFER_SIZE);
        sourceDataLine.start();

        //control pour le volume
        gainControl = (FloatControl)
                sourceDataLine.getControl(FloatControl.Type.MASTER_GAIN);
    }

    /**
     * Modifie le volume sonore de sortie.
     * Le gain effectif est proportionnel à la racine carré du pourcentage pour
     * avoir un décroisement du volume moins rapide.
     *
     * @param value la valeur du volume en poucentage (de 0 à 100).
     * @since version 0.94 - version 0.95.10
     */
    public void setVolume(int value) {
        float max = gainControl.getMaximum();
        float min = gainControl.getMinimum();
        float gain = (max-min) * (float)Math.sqrt(value/100.0) + min;
        gainControl.setValue(gain);
    }

    /**
     * Fermeture de la ligne de rendu audio.
     *
     * @since version 0.94
     */
    public void close() {
        sourceDataLine.stop();
        sourceDataLine.close();
    }

    /**
     * Traitement de lecture des données audio.
     * 
     * @since version 0.94 - version 0.97
     */
    @Override
    public void run() {
        //repositionnement de la tête d'enregistrement pour sauvegarder
        //les données avant la frame courante.
        int samplePosition = (int)
                (initTime/1000.0f * audioFormat.getSampleRate());
        int offset = samplePosition * audioFormat.getFrameSize();
        buffer.position(offset);

        //nombres de bytes à lire
        int nbSamples = (int)
                ((stopTime-initTime)/1000.0f * audioFormat.getSampleRate());
        int nbBytes = nbSamples * audioFormat.getFrameSize();

        //nombres de bytes à lire
        int read = Math.min(nbBytes, BUFFER_SIZE);

        long time = initTime;

        //on boucle tant que l'état du module audio n'a pas été arrêté.
        while(run) {
            //copie les données dans la ligne
            buffer.get(data, 0, read);
            sourceDataLine.write(data, 0, read);

            //mise à jour du temps courant
            time = (long) (buffer.position()
                    / audioFormat.getFrameSize()
                    / audioFormat.getSampleRate() * 1000.0f);

            if(time - initTime > 500) {
                initTime = time;
                fireTimeChanged(time);
            }//end if

            nbBytes -= read;
            if(nbBytes < audioFormat.getFrameSize())
                //si inférieur à la taille d'une frame on arrête
                break;
            else
                //mise à jour du nombres de bytes à lire
                read = Math.min(nbBytes, BUFFER_SIZE);
        }//end while

        sourceDataLine.drain();
        fireTimeChanged(time);
        fireEndProcess(run);
    }//end run()

}//end
