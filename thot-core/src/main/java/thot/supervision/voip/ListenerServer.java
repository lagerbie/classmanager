package thot.supervision.voip;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thot.utils.Constants;

/**
 * Classe pour recevoir des données audio en mode serveur.
 *
 * @author Fabrice Alleau
 * @version 1.8.4
 */
public class ListenerServer implements Runnable {

    /**
     * Instance de log.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ListenerServer.class);

    /**
     * Ligne pour le rendu du son.
     */
    private SourceDataLine sourceDataLine;
    /**
     * Format Audio pour la capture du microphone.
     */
    private AudioFormat audioFormat;

    /**
     * Port d'écoute du serveur.
     */
    private int port;
    /**
     * Serveur pour la réception des données.
     */
    private ServerSocket serverSocket;

    /**
     * Thread de transfert des données.
     */
    private Thread thread;
    /**
     * Pour savoir s'il faut recevoir les données.
     */
    private boolean receive;

    /**
     * Taille par défaut du buffer.
     */
    private static final int BUFFER_SIZE = 8 * 1024;
    /**
     * Buffer pour la réception des données.
     */
    private byte[] data = new byte[BUFFER_SIZE];

    /**
     * Initialisation avec un numéro de port.
     *
     * @param audioFormat le format de la ligne microphone.
     * @param port le port du receveur.
     */
    public ListenerServer(AudioFormat audioFormat, int port) {
        this.audioFormat = audioFormat;
        this.port = port;
    }

    /**
     * Démarre la réception des données et leur lecture.
     */
    public void start() {
        receive = true;

        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            LOGGER.error("Impossible d'ouvrir un serveur sur le port {}", e, port);
        }

        thread = new Thread(this, this.getClass().getName());
        //Ouverture de la ligne pour écouter les données
        openLine(audioFormat);
        //Lancement des données
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.start();
        LOGGER.debug("Listener thread started");
    }

    /**
     * Redémmarage du traitement des données. Pas d'ouverture d'une nouvelle ligne du rendu audio. Pas d'ouverture du
     * serveur de réception.
     */
    private void restart() {
        receive = true;
        thread = new Thread(this, this.getClass().getName());
        //Lancement des données
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.start();
        LOGGER.debug("Listener thread restarted");
    }

    @Override
    public void run() {
        int cnt = -1;

        long initTime;
        double transfert = 0;
        double writeAudio = 0;
        long pass = 0;

        try (Socket socket = serverSocket.accept()) {
            //attente de la connection

            socket.setTcpNoDelay(true);
            socket.setSoTimeout(Constants.TIME_MAX_FOR_CONNEXION);
            InputStream inputStream = socket.getInputStream();

            //tant qu'il faut lire les données
            while (receive) {
                pass++;
                initTime = System.nanoTime();
                cnt = inputStream.read(data);
                transfert += (System.nanoTime() - initTime);

                if (cnt >= 0) {
                    initTime = System.nanoTime();
                    sourceDataLine.write(data, 0, cnt);
//                    sourceDataLine.drain();
                    writeAudio += (System.nanoTime() - initTime);
                } else {
                    receive = false;
                }
            }
        } catch (IOException e) {
            LOGGER.error("Listener IO cnt: {}", e, cnt);
        }

        if (pass > 0) {
            LOGGER.info("Listener stop stats transfert: {}; writeAudio: {}; pass: {}", transfert / 1000000 / pass,
                    writeAudio / 1000000 / pass, pass);
        }

        sourceDataLine.flush();

        //redémarrage pour une autre connexion au serveur
        restart();
    }

    /**
     * Ouverture de la ligne pour écouter les données.
     *
     * @param audioFormat le format de la ligne microphone.
     */
    private void openLine(AudioFormat audioFormat) {
        try {
            sourceDataLine = AudioSystem.getSourceDataLine(audioFormat);
            sourceDataLine.open(audioFormat, BUFFER_SIZE);
            sourceDataLine.start();
            LOGGER.info("Listener line opened with: {}", audioFormat.toString());
        } catch (LineUnavailableException | IllegalArgumentException e) {
            //pas de rendu du son
            LOGGER.error("Impossiblr d'ouvrir un flux audio", e);
        }
    }

    /**
     * Indique si la ligne est ouverte, c'est à dire si le système a réservé les ressources et si elle est
     * opérationnelle.
     *
     * @return {@code true} si la ligne est ouverte.
     */
    public boolean isLineOpen() {
        if (sourceDataLine == null) {
            return false;
        } else {
            return sourceDataLine.isOpen();
        }
    }
}
