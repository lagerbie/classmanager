package thot.supervision.voip;

import javax.sound.sampled.AudioFormat;

import lombok.Getter;
import thot.exception.ThotException;

/**
 * Module de communication avec 2 écoutes actives permanentes.
 *
 * @author Fabrice Alleau
 * @version 1.8.4
 */
public class Voip {

    public static final AudioFormat DEFAULT_AUDIO_FORMAT = new AudioFormat(11025.0f, 16, 1, true, false);

    /**
     * Client pour l'envoi du microphone.
     */
    private SpeekerClient speekerClient;
    /**
     * Serveur de réception de flux audio principal.
     */
    private ListenerServer listenerServer;
    /**
     * Serveur de réception de flux audio pour le pairing.
     */
    private ListenerServer listenerPairingServer;
    /**
     * Numéro de ligne occupée par le pairing.
     */
    private int pairingLine = -1;
    /**
     * Port audio principal.
     */
    @Getter
    private int audioPort;
    /**
     * Port audio secondaire.
     */
    @Getter
    private int audioPairingPort;

    /**
     * Initialisation du module du communication avec prise directe sur le microphone.
     *
     * @param port port d'écoute pour la réception de flux audio principal.
     * @param pairingPort port d'écoute pour la réception de flux audio du pairing.
     */
    public Voip(int port, int pairingPort) {
        this.audioPort = port;
        this.audioPairingPort = pairingPort;

        listenerServer = new ListenerServer(DEFAULT_AUDIO_FORMAT, port);
        listenerPairingServer = new ListenerServer(DEFAULT_AUDIO_FORMAT, pairingPort);

        speekerClient = new SpeekerClient();
    }

    /**
     * Initialisation du module du communication avec prise directe sur le microphone.
     */
    public void initDirectMode() throws ThotException {
        listenerServer.start();
        listenerPairingServer.start();

        speekerClient.init(DEFAULT_AUDIO_FORMAT);
    }

    /**
     * Initialisation du module du communication avec un serveur de gestion du microphone.
     *
     * @param microphoneServerPort le port du serveur de gestion du microphone.
     * @param microphonePort le port où seront envoyées les données microphone.
     */
    public void initIndirectMode(int microphoneServerPort, int microphonePort) throws ThotException {
        listenerServer.start();
        listenerPairingServer.start();

        speekerClient.init(microphoneServerPort, microphonePort);
    }

    /**
     * Connecte et démarre le client d'envoi du microphone à l'adresse donnée.
     *
     * @param addressIP l'adresse IP pour l'envoi du microphone.
     * @param port le port pour l'envoi du microphone.
     *
     * @return le numéro de ligne occupée si la connexion à réussie ou sinon -1.
     */
    public int connect(String addressIP, int port) {
        int line = -1;
        if (speekerClient != null) {
            line = speekerClient.connect(addressIP, port);
            speekerClient.start();
        }
        return line;
    }

    /**
     * Connecte et démarre le client d'envoi du microphone en tant que connexion du pairing à l'adresse donnée.
     *
     * @param addressIP l'adresse IP pour l'envoi du microphone.
     * @param port le port pour l'envoi du microphone.
     *
     * @return le numéro de ligne occupée si la connexion à réussie ou sinon -1.
     */
    public int connectPairing(String addressIP, int port) {
        this.pairingLine = connect(addressIP, port);
        return pairingLine;
    }

    /**
     * Déconnecte toutes les lignes du microphone.
     */
    public void disconnectAll() {
        if (speekerClient != null) {
            speekerClient.disconnectAll();
        }
        pairingLine = -1;
    }

    /**
     * Déconnecte toutes les lignes du microphone sauf la ligne du pairing.
     */
    public void disconnectAllWithoutPairing() {
        if (speekerClient != null) {
            speekerClient.disconnectAllWithoutLine(pairingLine);
        }
    }

    /**
     * Retourne si le socket d'envoi du microphone à des connexions.
     *
     * @return si le socket d'envoi du microphone à des connexions.
     */
    public boolean speekerhasLine() {
        if (speekerClient == null) {
            return false;
        } else {
            return speekerClient.hasLine();
        }
    }
}
