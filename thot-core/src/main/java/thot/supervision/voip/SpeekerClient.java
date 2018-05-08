package thot.supervision.voip;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.Socket;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thot.utils.Constants;

/**
 * Classe pour envoyer les données audio du microphone à plusieurs destinataire.
 *
 * @author Fabrice Alleau
 * @version 1.8.4
 */
public class SpeekerClient implements Runnable {

    /**
     * Instance de log.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SpeekerClient.class);

    /**
     * Ligne de capture pour le microphone.
     */
    private TargetDataLine targetDataLine;

    /**
     * Socket pour les différents destinataires.
     */
    private Socket[] sockets;
    /**
     * Flux de sortie des différents destinataires pour l'envoi des données.
     */
    private OutputStream[] outputStreams;

    /**
     * Nombre courant de ligne.
     */
    private int cntLine = 0;
    /**
     * Nombre maximum de ligne pouvant être ouverte.
     */
    private int lineMax = 64;

    /**
     * Thread de transfert des données.
     */
    private Thread thread;
    /**
     * Pour savoir s'il faut envoyer les données.
     */
    private boolean send = false;

    /**
     * Taille par défaut du buffer.
     */
    private static final int BUFFER_SIZE = 8 * 1024;
    /**
     * Buffer pour la réception des données.
     */
    private byte[] data = new byte[BUFFER_SIZE];

    /**
     * Mode de connection à la ligne microphone.
     */
    private boolean microphone;
    /**
     * Socket pour le mode déporté.
     */
    private DatagramSocket socketMicrophone;
    /**
     * Paquet de réception par défaut.
     */
    private DatagramPacket paquet;

    /**
     * Initialisation du client pour envoi des données du microphone. Voie d'accès directe au microphone
     *
     * @param audioFormat le format de la ligne microphone.
     */
    public SpeekerClient(AudioFormat audioFormat) {
        this.microphone = true;
        openLine(audioFormat);
        sockets = new Socket[lineMax];
        outputStreams = new OutputStream[lineMax];
    }

    /**
     * Initialisation du client pour envoi des données du microphone.
     *
     * @param microphoneServerPort le port du serveur de gestion du microphone.
     * @param microphonePort le port où seront envoyées les données microphone.
     */
    public SpeekerClient(int microphoneServerPort, int microphonePort) {
        this.microphone = false;
        connectMicrophoneServer(microphoneServerPort, microphonePort);
        sockets = new Socket[lineMax];
        outputStreams = new OutputStream[lineMax];
    }

    /**
     * Indique si on est en direct sur le microphone.
     *
     * @return si on est en direct sur le microphone.
     */
    public boolean isMicrophone() {
        return microphone;
    }

    /**
     * Connection au serveur à l'adresse et au port donnés.
     *
     * @param addressIP l'adresse IP du serveur d'écoute.
     * @param port le port du serveur d'écoute.
     *
     * @return le numéro de la ligne ou -1 si échec de la connection.
     */
    public int connect(String addressIP, int port) {
        //recherche de la première ligne non occupée.
        while (sockets[cntLine] != null) {
            cntLine++;
            if (cntLine == lineMax) {
                cntLine = 0;
            }
        }

        try {
            InetSocketAddress socketAddress = new InetSocketAddress(addressIP, port);
            Socket socket = new Socket();
            socket.connect(socketAddress, Constants.TIME_MAX_FOR_CONNEXION);
            socket.setTcpNoDelay(true);
            outputStreams[cntLine] = socket.getOutputStream();
            sockets[cntLine] = socket;
            LOGGER.info("Speeker connected to {} : {}", addressIP, port);
        } catch (IOException e) {
            LOGGER.error("Impossible de se connecter à {}:{}", e, addressIP, port);
            return -1;
        }

        return cntLine;
    }

    /**
     * Démarre l'écoute du microphone et l'envoi des données.
     */
    public void start() {
        send = true;
        if (thread != null && !thread.isAlive()) {
            thread = null;
        }

        if (thread == null) {
            thread = new Thread(this, this.getClass().getName());

            //Lancement des données
            thread.setPriority(Thread.MAX_PRIORITY);
            thread.start();
            LOGGER.debug("Speeker thread started");
        }
    }

    /**
     * Ouverture de la ligne pour écouter les données du microphone.
     *
     * @param audioFormat le format de la ligne microphone.
     */
    private void openLine(AudioFormat audioFormat) {
        if (targetDataLine == null) {
            try {
                targetDataLine = AudioSystem.getTargetDataLine(audioFormat);
                targetDataLine.open(audioFormat, BUFFER_SIZE);
                LOGGER.info("Speeker line opened with: {}", audioFormat.toString());
            } catch (LineUnavailableException | IllegalArgumentException e) {
                //pas de rendu du son
                LOGGER.error("Impossible d'ouvrir un flux audio", e);
                return;
            }
        }
        targetDataLine.start();
    }

    /**
     * Retourne si il y a encor une ligne active.
     *
     * @return {@code true} si il y a une ligne active.
     */
    public boolean hasLine() {
        for (Socket socket : sockets) {
            if (socket != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * Indique si la ligne est ouverte. C'est à dire si le système a réservé les ressources et si elle est
     * opérationnelle.
     *
     * @return {@code true} si la ligne est ouverte.
     */
    public boolean isLineOpen() {
        if (targetDataLine == null) {
            return false;
        } else {
            return targetDataLine.isOpen();
        }
    }

    /**
     * Indique si la socket est ouverte, c'est à dire si le système a réservé les ressources et si elle est
     * opérationnelle.
     *
     * @return {@code true} si la ligne est ouverte.
     */
    public boolean isSocketOpen() {
        return socketMicrophone != null;
    }

    /**
     * Déconnecte toutes les lignes actives.
     */
    public void disconnectAll() {
        send = false;
        for (int i = 0; i < lineMax; i++) {
            if (sockets[i] != null) {
                try {
                    sockets[i].close();
                } catch (IOException e) {
                    LOGGER.error("Impossible de fermer la connexion", e);
                }
            }

            sockets[i] = null;
            outputStreams[i] = null;
        }
    }

    /**
     * Déconnecte toutes les lignes actives exceptée la ligne indiquée.
     *
     * @param line le numéro de la ligne qui ne sera pas déconnectée.
     */
    public void disconnectAllWithoutLine(int line) {
        if (line < 0) {
            disconnectAll();
            return;
        }

        for (int i = 0; i < lineMax; i++) {
            if (i != line) {
                if (sockets[i] != null) {
                    try {
                        sockets[i].close();
                    } catch (IOException e) {
                        LOGGER.error("Impossible de fermer la connexion", e);
                    }
                }

                sockets[i] = null;
                outputStreams[i] = null;
            }
        }

        if (sockets[line] == null) {
            send = false;
        }
    }

    /**
     * Connecte à un serveur de son pour la prise du microphone.
     *
     * @param microphoneServerPort le port du serveur de gestion du microphone.
     * @param microphonePort le port où seront envoyées les données microphone.
     */
    private void connectMicrophoneServer(int microphoneServerPort, int microphonePort) {
        String xml = "<connection><address>127.0.0.1</address>"
                + "<port>" + String.valueOf(microphonePort) + "</port></connection>";

        try (Socket sendMicro = new Socket("127.0.0.1", microphoneServerPort)) {
            DataOutputStream outputStream = new DataOutputStream(sendMicro.getOutputStream());
            outputStream.writeUTF(xml);
            outputStream.flush();

            socketMicrophone = new DatagramSocket(microphonePort);
            paquet = new DatagramPacket(data, BUFFER_SIZE);
        } catch (IOException e) {
            LOGGER.error("Impossible de se connecter au server du microphone sur les ports {} et {}", e,
                    microphoneServerPort, microphonePort);
        }
    }

    @Override
    public void run() {
        int cnt = 0;

        long pass = 0;
        long initTime;
        double transfert = 0;

        while (send) {
            if (microphone) {
                try {
                    cnt = targetDataLine.read(data, 0, BUFFER_SIZE);
                } catch (Exception e) {
                    LOGGER.error("", e);
                }
            } else {
                long timeTampon = System.currentTimeMillis();
                try {
                    socketMicrophone.receive(paquet);
                    cnt = paquet.getLength();
                } catch (IOException e) {
                    LOGGER.error("", e);
                }
                //1024 bits -> 64ms
                //mémoire tampon accès en 1 ou 2 ms
                //on passe les données non acquises réellement
                if (System.currentTimeMillis() - timeTampon < 10) {
                    continue;
                }
            }

            if (cnt > 0) {
                for (int i = 0; i < lineMax; i++) {
                    if (sockets[i] != null) {
                        try {
                            pass++;
                            initTime = System.nanoTime();
                            outputStreams[i].write(data, 0, cnt);
//                            outputStreams[i].flush();
                            transfert += (System.nanoTime() - initTime);
                        } catch (IOException e) {
                            LOGGER.error("IOException {} in {}", e, e.getMessage(), this.getClass());
                            try {
                                sockets[i].close();
                            } catch (IOException ioe) {
                                LOGGER.error("IOException on close {} in {}", e, e.getMessage(), this.getClass());
                            }
                            sockets[i] = null;
                            outputStreams[i] = null;
                        }
                    }
                }
            } else {
                send = false;
            }
        }

        if (pass > 0) {
            LOGGER.info("Listener stop stats transfert: {}", transfert / 1000000 / pass);
        }

        disconnectAll();
    }
}
