package eestudio.audio;

import java.nio.ByteBuffer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

/*
 * v0.95: supp de private Core core;
 * v0.95: modif de BUFFER_SIZE = 1024 en 1024*8
 * v0.95: modif de Recorder(Core core, ByteBuffer recordBuffer, AudioFormat
 *        audioFormat) en Recorder(ByteBuffer recordBuffer, AudioFormat audioFormat)
 *        throws LineUnavailableException, IllegalArgumentException
 * v0.95: private void fireTimeChanged(long time) géré dans Processing
 * v0.95: private void fireEndProcess(boolean running) géré dans Processing
 *
 * v0.96: modif de run() [remplacement de data.length par BUFFER_SIZE]
 *
 * v0.97: public static final int BUFFER_SIZE géré dans AudioProcessing
 * v0.97: private byte data[] géré dans AudioProcessing
 * v0.97: private AudioFormat audioFormat géré dans AudioProcessing
 * v0.97: private ByteBuffer buffer géré dans AudioProcessing
 * v0.97: private long initTime géré dans AudioProcessing
 * v0.97: private long stopTime géré dans AudioProcessing
 * v0.97: public void setRecordBuffer(ByteBuffer buffer) géré dans AudioProcessing
 * v0.97: public void start(long initTime, long stopTime) géré dans AudioProcessing
 * v0.97: supp de @Override public void start()
 * v0.97: modif de Recorder(...) [use super(...)]
 * v0.97: modif de run() [supp utilisation variable locale initTime]
 */

/**
 * Classe pour capturer des données.
 *
 * @author Fabrice Alleau
 * @since version 0.94
 * @version 0.97
 */
public class Recorder extends AudioProcessing {
    /* Ligne directe sur le microphone */
    private TargetDataLine targetDataLine;

    /**
     * Initialisation avec un format audio et une référence sur le buffer où
     * seront enregistrées les données.
     *
     * @param buffer le buffer de stockage.
     * @param audioFormat le format audio.
     * @throws LineUnavailableException
     * @throws IllegalArgumentException
     * @since version 0.94 - version 0.97
     */
    public Recorder(ByteBuffer buffer, AudioFormat audioFormat) 
            throws LineUnavailableException, IllegalArgumentException {
        super(buffer, audioFormat);

        //Recherche de la configuration pour la capture des données.
        targetDataLine = AudioSystem.getTargetDataLine(audioFormat);

        //ouverture et démarage de la ligne de capture aver une taille
        //de buffer la plus petite possible.
        targetDataLine.open(audioFormat, BUFFER_SIZE);
        targetDataLine.start();
    }

    /**
     * Fermeture de la ligne de capture.
     *
     * @since version 0.94
     */
    public void close() {
        targetDataLine.stop();
        targetDataLine.close();
    }

    /**
     * Traitement de du son.
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
        int cnt = 0;
        //on boucle tant que l'état du module audio n'a pas été arrêté.
        while(run && cnt != -1) {
            cnt = targetDataLine.read(data, 0, read);

            if(cnt < 0)
                break;
            if(cnt == 0)
                continue;

            //Sauvegarde les données dans un ByteBuffer.
            buffer.put(data, 0, cnt);

            //mise à jour du temps courant
            time = (long) (buffer.position()
                    / audioFormat.getFrameSize()
                    / audioFormat.getSampleRate() * 1000.0f);

            if(time - initTime > 500) {
                initTime = time;
                fireTimeChanged(time);
            }//end if

            nbBytes -= cnt;
            if(nbBytes < audioFormat.getFrameSize())
                //si inférieur à la taille d'une frame on arrête
                break;
            else
                //mise à jour du nombres de bytes à lire
                read = Math.min(nbBytes, BUFFER_SIZE);
        }//end while

        targetDataLine.flush();

        fireTimeChanged(time);
        fireEndProcess(run);
    }//end run()

}//end
