package thot.supervision;

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

/**
 * Thread de gestion d'envoi des données au microphone.
 *
 * @author Fabrice Alleau
 * @version 1.8.4
 */
public class MicrophoneServer implements Runnable {

    /**
     * Thread de transfert des données.
     */
    private Thread thread;
    /**
     * Socket UDP d'envoi de données.
     */
    private DatagramSocket socket;
    /**
     * Taille par défaut du buffer.
     */
    private static final int BUFFER_SIZE = 8 * 1024;
    /**
     * Buffer pour la réception des données.
     */
    private byte[] data = new byte[BUFFER_SIZE];
    /**
     * Ligne de capture du microphone.
     */
    private TargetDataLine targetDataLine;
    /**
     * Liste des clients.
     */
    private ArrayList<InetSocketAddress> clients;

    /**
     * Initialisation du gestionnaire du microphone.
     *
     * @param audioFormat le format de capture.
     */
    public MicrophoneServer(AudioFormat audioFormat) {
        openLine(audioFormat);
        clients = new ArrayList<>(4);

        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            CommonLogger.error(e);
        }

        start();
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
        InetSocketAddress socketAddress = new InetSocketAddress(addressIP, port);

        //prévention pour deux addresses identiques
        for (InetSocketAddress client : clients) {
            if (client.equals(socketAddress)) {
                return true;
            }
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
        clients.remove(socketAddress);
    }

    /**
     * Ouverture de la ligne pour écouter les données du microphone. Le format par défaut (44100 Hz, 16 bits, mono,
     * signed, little-endian).
     */
    private void openLine(AudioFormat audioFormat) {
        try {
            targetDataLine = AudioSystem.getTargetDataLine(audioFormat);
            targetDataLine.open(audioFormat, BUFFER_SIZE);
            targetDataLine.start();
        } catch (LineUnavailableException | IllegalArgumentException e) {
            //pas de rendu du son
            CommonLogger.error(e);
            System.exit(-1);
        }
    }

    /**
     * Démarre l'enregistrement.
     */
    private void start() {
        thread = new Thread(this, this.getClass().getName());
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.start();
        thread.setPriority(Thread.MAX_PRIORITY);
    }

    @Override
    public void run() {
        int cnt;

        while (true) {
            cnt = targetDataLine.read(data, 0, BUFFER_SIZE);
            if (cnt <= 0) {
                continue;
            }

            for (InetSocketAddress client : clients) {
                try {
                    DatagramPacket paquet = new DatagramPacket(data, cnt, client);
                    socket.send(paquet);
                } catch (IOException e) {
                    CommonLogger.error(e);
                    disconnect(client);
                }
            }
        }
    }
}
