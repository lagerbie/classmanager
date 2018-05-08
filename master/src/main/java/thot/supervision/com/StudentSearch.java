package thot.supervision.com;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thot.utils.Constants;
import thot.utils.Utilities;

/**
 * Thread pour la recherche des élèves.
 *
 * @author Fabrice Alleau
 * @version 1.8.4
 */
public class StudentSearch implements Runnable {

    /**
     * Instance de log.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(StudentSearch.class);

    /**
     * Adresse multicast pour la recherche des élèves.
     */
    private String multicastIP;
    /**
     * Port de l'adresse multicast pour la recherche des élèves.
     */
    private int multicastPort;
    /**
     * Temps d'attente entre 2 envois de trame multicast.
     */
    private final long WAIT_TIME = Constants.TIME_MAX_FOR_ORDER;

    /**
     * Initialisation avec remise à zéro du fichier de sauvegarde.
     *
     * @param multicastIP adresse multicast pour la recherche du professeur.
     * @param multicastPort port pour la recherche du professeur.
     */
    public StudentSearch(String multicastIP, int multicastPort) {
        this.multicastIP = multicastIP;
        this.multicastPort = multicastPort;
    }

    /**
     * Démarrage de la thread de la recherche de professeur.
     */
    public void start() {
        new Thread(this, this.getClass().getName()).start();
    }

    /**
     * Recherche des élèves.
     */
    @Override
    public void run() {
        InetAddress group;
        MulticastSocket multicastSocket;
        byte[] data;

        try {
            group = InetAddress.getByName(multicastIP);
            multicastSocket = new MulticastSocket(multicastPort);
            multicastSocket.setTimeToLive(3);
            multicastSocket.joinGroup(group);
            data = Constants.XML_STUDENT_SEARCH.getBytes("UTF-8");
        } catch (IOException e) {
            LOGGER.error("", e);
            return;
        }

        DatagramPacket packet = new DatagramPacket(data, data.length, group, multicastPort);

        while (true) {
            Utilities.waitInMillisecond(WAIT_TIME);
            try {
                multicastSocket.send(packet);
            } catch (IOException e) {
                LOGGER.error("", e);
            }
        }
    }
}
