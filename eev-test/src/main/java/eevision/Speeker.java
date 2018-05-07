package eevision;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import lombok.Getter;
import lombok.Setter;

/**
 * Classe abstraite pour envoyer les données audio du microphone.
 *
 * @author fabrice
 * @version 0.50
 */
public abstract class Speeker implements Runnable {

    /**
     * Ligne de capture pour le microphone
     */
    protected TargetDataLine targetDataLine;

    /**
     * Adresse IP où envoyé le son
     */
    @Getter
    private String ip;
    /**
     * Port sur lequel en envoi le son
     */
    @Getter
    private int port;

    /**
     * Pour savoir s'il faut envoyer les données
     */
    @Getter
    @Setter
    private boolean send;

    @Getter
    private long duration;

    /**
     * Buffer pour l'envoi des données
     */
    protected byte[] data = new byte[4 * 1024];

    private Thread thread;

    /**
     * Initialisation avec une adresse IP et un numéro de port.
     *
     * @param ip l'adresse ip du receveur.
     * @param port le port du receveur.
     * @param duration le temps d'envoi (-1 si infini)
     */
    public Speeker(String ip, int port, long duration) {
        this.ip = ip;
        this.port = port;
        this.duration = duration;
    }

    /**
     * Démarre l'écoute du microphone et l'envoi des données.
     */
    public void start() {
        send = true;
        thread = new Thread(this);
        //Ouverture d'une ligne pour le microphone
        openLine();
        //Lancement des données
        thread.start();
    }

    /**
     * Arrete l'écoute du microphone et l'envoi des données.
     */
    public synchronized void stop() {
        send = false;
        try {
            while (thread.isAlive()) {
                wait(10);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //fermeture de la ligne de capture
        closeLine();
    }

    /**
     * Ouverture de la ligne pour écouter les données du microphone. Le format par défaut (8000 Hz, 16 bits, mono,
     * signed, little-endian).
     */
    private void openLine() {
        AudioFormat audioFormat = new AudioFormat(8000.0f, 16, 1, true, false);
        try {
            targetDataLine = AudioSystem.getTargetDataLine(audioFormat);
            targetDataLine.open(audioFormat, data.length);
            targetDataLine.start();
        } catch (LineUnavailableException e) {
            //pas de rendu du son
            e.printStackTrace();
        }
    }

    /**
     * Ferme une ligne d'écoute du microphone.
     */
    private void closeLine() {
        targetDataLine.stop();
        targetDataLine.close();
    }

}
