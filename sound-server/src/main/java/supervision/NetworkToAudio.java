package supervision;

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

/**
 * Classe pour recevoir des données audio en mode serveur.
 *
 * @author Fabrice Alleau
 * @version 1.8.4
 */
public class NetworkToAudio implements Runnable {

    /**
     * Instance de log.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(NetworkToAudio.class);

    /**
     * Taille par défaut du buffer.
     */
    private static final int BUFFER_SIZE = 8 * 1024;
    /**
     * Buffer pour la réception des données.
     */
    private byte[] data = new byte[BUFFER_SIZE];

    /**
     * Format Audio pour la capture du microphone.
     */
    private AudioFormat audioFormat;
    /**
     * Ligne pour le rendu du son.
     */
    private SourceDataLine sourceDataLine;

    /**
     * Port d'écoute du serveur.
     */
    private int port;
    /**
     * Serveur pour la réception des données.
     */
    private ServerSocket serverSocket = null;

    /**
     * Pour savoir s'il faut recevoir les données.
     */
    private boolean receive;

    /**
     * Initialisation avec un numéro de port.
     *
     * @param audioFormat le format de la ligne microphone.
     * @param port le port du receveur.
     */
    public NetworkToAudio(AudioFormat audioFormat, int port) {
        this.audioFormat = audioFormat;
        this.port = port;
    }

    /**
     * Ouverture de la ligne pour écouter les données.
     *
     * @param audioFormat le format de la ligne microphone.
     */
    private void openLine(AudioFormat audioFormat) throws LineUnavailableException {
        LOGGER.info("Initalisation de la ligne de rendu audio au format {}", audioFormat);
        sourceDataLine = AudioSystem.getSourceDataLine(audioFormat);
        sourceDataLine.open(audioFormat, BUFFER_SIZE);
        sourceDataLine.start();
    }

    /**
     * Démarre la réception des données et leur lecture.
     */
    public void start() throws LineUnavailableException, IOException {
        LOGGER.info("Démarrage du serveur de réception des données audio");
        serverSocket = new ServerSocket(port);
        //Ouverture de la ligne pour écouter les données
        openLine(audioFormat);

        receive = true;
        Thread thread = new Thread(this, this.getClass().getName());
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.start();
        thread.setPriority(Thread.MAX_PRIORITY);
    }

    /**
     * Stoppe le serveur.
     */
    public void stop() {
        LOGGER.info("Arrêt du serveur de réception des données audio");
        receive = false;
    }

    @Override
    public void run() {
        int cnt;
        while (receive) {
            String host = null;
            // attente de la connection
            try (Socket socket = serverSocket.accept()) {
                host = socket.getInetAddress().getHostName();
                LOGGER.info("Connexion du client {}", host);
                socket.setTcpNoDelay(true);
                socket.setSoTimeout(10000);
                InputStream inputStream = socket.getInputStream();

                //tant qu'il faut lire les données
                do {
                    cnt = inputStream.read(data);
                    if (cnt >= 0) {
                        sourceDataLine.write(data, 0, cnt);
                    }
                } while (receive && cnt >= 0);
            } catch (IOException e) {
                LOGGER.error("Impossible de recevoir des données du client {}, déconnexion du client", e, host);
            }

            sourceDataLine.flush();
        }

        sourceDataLine.close();
        try {
            serverSocket.close();
        } catch (IOException e) {
            LOGGER.error("Impossible de fermer le serveur", e);
        }
    }

}
