package thot.supervision;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thot.utils.Constants;
import thot.utils.ProgressThread;
import thot.utils.Utilities;

/**
 * Thread pour la découverte d'un professeur.
 *
 * @author Fabrice Alleau
 * @version 1.8.4
 */
public class MasterSearch extends ProgressThread implements Runnable {

    /**
     * Instance de log.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MasterSearch.class);

    /**
     * Adresse multicast pour la recherche d'un professeur.
     */
    private String multicastIP;
    /**
     * Port de l'adresse multicast pour la recherche d'un professeur.
     */
    private int multicastPort;
    /**
     * Port pour répondre à la recherche du professeur.
     */
    private int responsePort;

    /**
     * Temps d'attente entre 2 envois quand le professeur est présent.
     */
    private final int IP_FIND = 10000;
    /**
     * Sauvegarde du temps du dernier ping (pour voir si le délai est trop long).
     */
    private long pingTime = 0;
    /**
     * Thread de transfert des données.
     */
    private Thread thread;

    /**
     * Initialisation avec remise à zéro du fichier de sauvegarde.
     *
     * @param multicastIP adresse multicast pour la recherche du professeur.
     * @param multicastPort port pour la recherche du professeur.
     * @param responsePort port pour répondre à la recherche du professeur.
     */
    public MasterSearch(String multicastIP, int multicastPort, int responsePort) {
        this.multicastIP = multicastIP;
        this.multicastPort = multicastPort;
        this.responsePort = responsePort;
    }

    /**
     * Démarrage de la thread de la recherche de professeur.
     */
    public void start() {
        thread = new Thread(this, this.getClass().getName());
        thread.start();
    }

    /**
     * Retourne si la thread tourne.
     *
     * @return si la thread tourne.
     */
    public boolean isRun() {
        if (thread == null) {
            return false;
        } else {
            return thread.isAlive();
        }
    }

    @Override
    public void run() {
        byte[] data = new byte[1024];
        DatagramPacket packet = new DatagramPacket(data, data.length);

        MulticastSocket multicastSocket = null;
        try {
            InetAddress group = InetAddress.getByName(multicastIP);
            multicastSocket = new MulticastSocket(multicastPort);
            multicastSocket.joinGroup(group);
            multicastSocket.setSoTimeout(IP_FIND);
        } catch (IOException e) {
            LOGGER.error("", e);
        }

        if (multicastSocket == null) {
            LOGGER.error("Impossible d'initialiser la thread multicast");
            return;
        }

        Command command = new Command(CommandType.TYPE_SUPERVISION, CommandAction.FIND);
        String response = CommandXMLUtilities.getXML(command);
        String xml;
        String addressIP;

        boolean reponseSended;
        while (true) {
            try {
                multicastSocket.receive(packet);
                xml = new String(data, 0, packet.getLength(), "UTF-8");
                addressIP = packet.getAddress().getHostAddress();
                LOGGER.debug("receive : " + xml + " from " + addressIP);
                if (xml.contentEquals(Constants.XML_STUDENT_SEARCH)) {
                    reponseSended = Utilities.sendXml(response, addressIP, responsePort);
                    if (reponseSended) {
                        pingTime = System.currentTimeMillis();
                    }
                }
            } catch (SocketTimeoutException e) {
                if (System.currentTimeMillis() - pingTime
                        > 3 * Constants.TIME_MAX_FOR_CONNEXION) {
                    fireProcessEnded(0);
                }
            } catch (Exception e) {
                LOGGER.error("", e);
            }
        }
    }
}
