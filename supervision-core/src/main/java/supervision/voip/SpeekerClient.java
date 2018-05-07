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
package supervision.voip;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.Socket;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.LineUnavailableException;

import supervision.CommonLogger;
import supervision.Constants;

/**
 * Classe pour envoyer les données audio du microphone à plusieurs destinataire.
 *
 * @author Fabrice Alleau
 * @version 1.90
 */
public class SpeekerClient implements Runnable {

    /**
     * Ligne de capture pour le microphone.
     */
    private TargetDataLine targetDataLine = null;

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
    private Thread thread = null;
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
     * Initialisation du client pour envoi des données du microphone.
     * Voie d'accès directe au microphone
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
            CommonLogger.info("Speeker connected to " + addressIP + ":" + port);
        } catch (IOException e) {
            CommonLogger.error(e);
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
//            thread.setPriority(Thread.MAX_PRIORITY);
            CommonLogger.debug("Speeker thread started");
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
                CommonLogger.info("Speeker line opened with: " + audioFormat.toString());
            } catch (LineUnavailableException | IllegalArgumentException e) {
                //pas de rendu du son
                CommonLogger.error(e);
                return;
            }
        }
        targetDataLine.start();
    }

    /**
     * Retourne si il y a encor une ligne active.
     *
     * @return <code>true</code> si il y a une ligne active,
     * sinon <code>false</code>.
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
     * Indique si la ligne est ouverte.
     * C'est à dire si le système a réservé les ressources et si elle est opérationnelle.
     *
     * @return <code>true</code> si la ligne est ouverte,
     * sinon <code>false</code>.
     */
    public boolean isLineOpen() {
        if (targetDataLine == null) {
            return false;
        } else {
            return targetDataLine.isOpen();
        }
    }

    /**
     * Indique si la socket est ouverte, c'est à dire si le système a réservé
     * les ressources et si elle est opérationnelle.
     *
     * @return <code>true</code> si la ligne est ouverte,
     * sinon <code>false</code>.
     */
    public boolean isSocketOpen() {
        if (socketMicrophone == null) {
            return false;
        } else {
            return true;
        }
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
                    CommonLogger.error(e);
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
                        CommonLogger.error(e);
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
            CommonLogger.error(e);
        }
    }

    /**
     * Traitements des données.
     */
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
                    CommonLogger.error(e);
                }
            } else {
                long timeTampon = System.currentTimeMillis();
                try {
                    socketMicrophone.receive(paquet);
                    cnt = paquet.getLength();
                } catch (IOException e) {
                    CommonLogger.error(e);
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
                            CommonLogger.error("IOException " + e.getMessage()
                                    + " in " + this.getClass());
                            CommonLogger.error(e);
                            try {
                                sockets[i].close();
                            } catch (IOException ioe) {
                                CommonLogger.error("IOException on close " + e.getMessage()
                                        + " in " + this.getClass());
                                CommonLogger.error(ioe);
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
            CommonLogger.info("Listener stop stats transfert: " + transfert / 1000000 / pass);
        }

        disconnectAll();
    }
}
