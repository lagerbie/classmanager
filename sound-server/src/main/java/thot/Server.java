package thot;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.LineUnavailableException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Serveur pour les demandes des données du microphones.
 *
 * @author Fabrice Alleau
 * @version 1.8.4
 */
public class Server implements Runnable {

    /**
     * Instance de log.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(NetworkToAudio.class);

    /*
     * Demande de connection de la forme:
     * <?xml version=\"1.0\" encoding=\"UTF-8\"?>
     *     <connection>
     *         <address> addressIP</address>
     *         <port> port </port>
     *     </connection>
     *
     * Demande de déconnection de la forme:
     * <?xml version=\"1.0\" encoding=\"UTF-8\"?>
     *     <disconnection>
     *         <address> addressIP </address>
     *         <port> port </port>
     *     </disconnection>
     */

    /**
     * Format de capture et de rendu audio: 11025 Hz, 16 bits, mono, signed, little-endian.
     */
    private static final AudioFormat AUDIO_FORMAT = new AudioFormat(11025.0f, 16, 1, true, false);

    /**
     * Port d'envoi audio principal.
     */
    private static final int AUDIO_PORT = 7220;
    /**
     * Port d'envoi audio secondaire.
     */
    private static final int AUDIO_PAIRING_PORT = 7221;
    /**
     * Port d'écoute du service.
     */
    private static final int PORT = 7206;

    /**
     * Server pour les connexions réseau.
     */
    private ServerSocket serverSocket;
    /**
     * Gestionnaire d'envoi du son.
     */
    private MicrophoneToNetwork captureServer;
    /**
     * Serveur de réception de flux audio principal.
     */
    private NetworkToAudio listenerServer;
    /**
     * Serveur de réception de flux audio pour le pairing.
     */
    private NetworkToAudio listenerPairingServer;

    /**
     * Etat du serveur.
     */
    private boolean running;

    public static void main(String[] args) {
        LOGGER.info("version: 1.8.4");

        Server server = new Server();

        try {
            server.start();
        } catch (LineUnavailableException e) {
            LOGGER.info("Impossible d'ouvrir ligne audio au format {}", e, AUDIO_FORMAT);
            server.stop();
            System.exit(-1);
        } catch (IOException e) {
            LOGGER.info("Problème de réseau", e);
            server.stop();
            System.exit(-1);
        }
    }

    /**
     * Initialisation.
     */
    private Server() {
        captureServer = new MicrophoneToNetwork(AUDIO_FORMAT);
        listenerServer = new NetworkToAudio(AUDIO_FORMAT, AUDIO_PORT);
        listenerPairingServer = new NetworkToAudio(AUDIO_FORMAT, AUDIO_PAIRING_PORT);
    }

    /**
     * Démarrage du service.
     */
    private void start() throws LineUnavailableException, IOException {
        captureServer.start();
        listenerServer.start();
        listenerPairingServer.start();
        serverSocket = new ServerSocket(PORT);

        running = true;
        new Thread(this, this.getClass().getName()).start();
    }

    /**
     * Arrêt du service.
     */
    private void stop() {
        running = false;
        captureServer.stop();
        listenerServer.stop();
        listenerPairingServer.stop();
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                LOGGER.error("Impossible de fermer le serveur", e);
            }
        }
    }

    @Override
    public void run() {
        DataInputStream inputStream;
        String xml = null;
        String[] split;

        while (running) {
            //attente de la connection
            try (Socket socket = serverSocket.accept()) {
                inputStream = new DataInputStream(socket.getInputStream());

                xml = inputStream.readUTF();
                LOGGER.info("Réception de la commande {}" + xml);

                split = xml.split("<address>|</address><port>|</port>");

                if (split.length == 4) {
                    String addressIP = split[1];
                    int portAudio = Integer.parseInt(split[2]);
                    if (xml.contains("<connection>")) {
                        boolean connected = captureServer.connect(addressIP, portAudio);
                        if (connected) {
                            LOGGER.info("Le client a été connecté {}:{}", addressIP, portAudio);
                        } else {
                            LOGGER.info("Impssible de connecter le client {}:{}", addressIP, portAudio);
                        }
                    } else if (xml.contains("<disconnection>")) {
                        captureServer.disconnect(addressIP, portAudio);
                    }
                }
            } catch (IOException | NumberFormatException e) {
                LOGGER.info("Impossible de traiter la commande {}", e, xml);
            }
        }

        stop();
    }
}
