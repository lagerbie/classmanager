package eevision;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

/**
 * Classe abstraite pour recevoir des données audio.
 *
 * @author fabrice
 * @version 0.50
 */
public abstract class Listener implements Runnable {

    /**
     * Ligne pour écouter
     */
    protected SourceDataLine sourceDataLine;

    /**
     * Port sur lequel on écoute
     */
    protected int port;

    /**
     * Pour savoir s'il faut recevoir les données
     */
    protected boolean receive;

    /**
     * Buffer pour la réception des données
     */
    protected byte[] data = new byte[4 * 1024];

    private Thread thread;

    /**
     * Initialisation avec un numéro de port.
     *
     * @param port le port du receveur.
     */
    public Listener(int port) {
        this.port = port;
    }

    /**
     * Démarre la réception des données et leur lecture.
     */
    public void start() {
        receive = true;
        thread = new Thread(this);
        //Ouverture de la ligne pour écouter les données
        openLine();
        //Lancement des données
        thread.start();
    }

    /**
     * Arrete la réception des données et leur lecture.
     */
    public void stop() {
        receive = false;
        try {//attendre que la thread soit terminée
            while (thread.isAlive()) {
                wait(10);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }//try

        //fermeture de ligne d'écoute
        closeLine();
    }

    /**
     * Ouverture de la ligne pour écouter les données. Le format par défaut (8000 Hz, 16 bits, mono, signed,
     * little-endian).
     */
    private void openLine() {
        AudioFormat audioFormat = new AudioFormat(8000.0f, 16, 1, true, false);
        try {
            sourceDataLine = AudioSystem.getSourceDataLine(audioFormat);
            sourceDataLine.open(audioFormat, data.length);
            sourceDataLine.start();
        } catch (LineUnavailableException e) {
            //pas de rendu du son
            e.printStackTrace();
        }//end try
    }

    /**
     * Ferme une ligne d'écoute.
     */
    private void closeLine() {
        sourceDataLine.stop();
        sourceDataLine.close();
    }

}
