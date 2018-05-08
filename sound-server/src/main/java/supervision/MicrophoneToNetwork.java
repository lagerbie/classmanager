package supervision;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.ArrayList;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Gestionnaire d'envoi des données au microphone sur le réseau en UDP.
 *
 * @author Fabrice Alleau
 * @version 1.8.4
 */
public class MicrophoneToNetwork implements Runnable {

    /**
     * Instance de log.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MicrophoneToNetwork.class);

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
     * Ligne de capture du microphone.
     */
    private TargetDataLine targetDataLine;

    /**
     * Socket UDP d'envoi de données.
     */
    private DatagramSocket socket;
    /**
     * Liste des clients.
     */
    private ArrayList<InetSocketAddress> clients;

    /**
     * Etat du serveur.
     */
    private boolean send;

    /**
     * Initialisation du gestionnaire du microphone.
     *
     * @param audioFormat le format de capture.
     */
    public MicrophoneToNetwork(AudioFormat audioFormat) {
        clients = new ArrayList<>(4);
        this.audioFormat = audioFormat;
    }

    /**
     * Ouverture de la ligne pour écouter les données du microphone.
     * <p>
     * Le format par défaut (44100 Hz, 16 bits, mono, signed, little-endian).
     *
     * @throws LineUnavailableException si pas d'ouverture de ligne possible.
     */
    private void openLine(AudioFormat audioFormat) throws LineUnavailableException {
        LOGGER.info("Initalisation de la ligne de capture audio au format {}", audioFormat);
        targetDataLine = AudioSystem.getTargetDataLine(audioFormat);
        targetDataLine.open(audioFormat, BUFFER_SIZE);
        targetDataLine.start();
    }

    /**
     * Démarre le serveur.
     */
    public void start() throws LineUnavailableException, SocketException {
        LOGGER.info("Démarrage du serveur d'envoi des données du microphone");
        socket = new DatagramSocket();
        openLine(audioFormat);

        send = true;
        Thread thread = new Thread(this, this.getClass().getName());
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.start();
        thread.setPriority(Thread.MAX_PRIORITY);
    }

    /**
     * Stoppe le serveur.
     */
    public void stop() {
        LOGGER.info("Arrêt du serveur d'envoi des données du microphone");
        send = false;
    }

    /**
     * Connection au serveur à l'adresse et au port donnés.
     *
     * @param addressIP l'adresse IP du serveur d'écoute.
     * @param port le port du serveur d'écoute.
     *
     * @return si la connection est établie.
     */
    public boolean connect(String addressIP, int port) {
        LOGGER.info("Ajout du client {}:{}", addressIP, port);
        InetSocketAddress socketAddress = new InetSocketAddress(addressIP, port);

        // prévention pour éviter d'envoyer deux fois à la même addresse
        if (clients.stream().anyMatch(client -> client.equals(socketAddress))) {
            LOGGER.info("Le client est déjà connecté {}:{}", addressIP, port);
            return true;
        }

        return clients.add(socketAddress);
    }

    /**
     * Déconnecte une ligne.
     *
     * @param addressIP l'adresse IP du serveur d'écoute.
     * @param port le port du serveur d'écoute.
     */
    public void disconnect(String addressIP, int port) {
        InetSocketAddress socketAddress = new InetSocketAddress(addressIP, port);
        disconnect(socketAddress);
    }

    /**
     * Déconnecte une ligne.
     *
     * @param socketAddress l'adresse du client.
     */
    private void disconnect(InetSocketAddress socketAddress) {
        LOGGER.info("Suppression du client {}:{}", socketAddress.getHostName(), socketAddress.getPort());
        clients.remove(socketAddress);
    }

    @Override
    public void run() {
        int cnt;

        while (send) {
            cnt = targetDataLine.read(data, 0, BUFFER_SIZE);
            if (cnt <= 0) {
                continue;
            }

            for (InetSocketAddress client : clients) {
                DatagramPacket paquet = new DatagramPacket(data, cnt, client);
                try {
                    socket.send(paquet);
                } catch (IOException e) {
                    LOGGER.error("Impossible d'envoyer le packet au client {}, déconnexion du client", e, client);
                    disconnect(client);
                }
            }
        }

        targetDataLine.close();
        socket.close();
    }
}
