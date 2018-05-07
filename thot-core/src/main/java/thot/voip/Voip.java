/*
 * ClassManager - Supervision de classes et Laboratoire de langue
 * Copyright (C) 2013 Fabrice Alleau <fabrice.alleau@siclic.fr>
 *
 * This file is part of ClassManager.
 *
 * ClassManager is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * ClassManager is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with ClassManager.  If not, see <http://www.gnu.org/licenses/>.
 */
package thot.voip;

import javax.sound.sampled.AudioFormat;

/**
 * Module de communication avec 2 écoutes actives permanentes.
 *
 * @author Fabrice Alleau
 * @version 1.90
 */
public class Voip {

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
    private int audioPort;
    /**
     * Port audio secondaire.
     */
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

        //Le format par défaut (8000 Hz, 16 bits, mono, signed, little-endian).
//        AudioFormat audioFormat = new AudioFormat(22050.0f, 16, 1, true, false);
        AudioFormat audioFormat = new AudioFormat(11025.0f, 16, 1, true, false);

        listenerServer = new ListenerServer(audioFormat, port);
        listenerServer.start();

        listenerPairingServer = new ListenerServer(audioFormat, pairingPort);
        listenerPairingServer.start();

        speekerClient = new SpeekerClient(audioFormat);
    }

    /**
     * Initialisation du module du communication avec un serveur de gestion du microphone.
     *
     * @param port port d'écoute pour la réception de flux audio principal.
     * @param pairingPort port d'écoute pour la réception de flux audio du pairing.
     * @param microphoneServerPort le port du serveur de gestion du microphone.
     * @param microphonePort le port où seront envoyées les données microphone.
     */
    public Voip(int port, int pairingPort,
            int microphoneServerPort, int microphonePort) {
        this.audioPort = port;
        this.audioPairingPort = pairingPort;

        speekerClient = new SpeekerClient(microphoneServerPort, microphonePort);
    }

    /**
     * Retourne le port d'écoute pour la réception de flux audio.
     *
     * @return le port d'écoute pour la réception de flux audio.
     */
    public int getPort() {
        return audioPort;
    }

    /**
     * Retourne le port d'écoute pour la réception de flux audio du pairing.
     *
     * @return le port d'écoute pour la réception de flux audio du pairing.
     */
    public int getPairingPort() {
        return audioPairingPort;
    }

    /**
     * Indique si les lignes sont ouvertes, c'est à dire si le système a réservé les ressources et si elles sont
     * opérationnelles.
     *
     * @return <code>true</code> si les lignes sont ouverte.
     */
    public boolean isLinesOpen() {
        if (speekerClient == null
                || (speekerClient.isMicrophone() && !speekerClient.isLineOpen())
                || (!speekerClient.isMicrophone() && !speekerClient.isSocketOpen())) {
            return false;
        } else {
            boolean direct = speekerClient.isMicrophone();
            if (direct && (listenerServer == null || !listenerServer.isLineOpen())) {
                return false;
            } else if (direct
                    && (listenerPairingServer == null || !listenerPairingServer.isLineOpen())) {
                return false;
            } else {
                return true;
            }
        }
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
