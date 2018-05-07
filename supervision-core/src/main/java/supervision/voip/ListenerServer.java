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

import java.io.InputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;

import supervision.CommonLogger;
import supervision.Constants;

/**
 * Classe pour recevoir des données audio en mode serveur.
 *
 * @author Fabrice Alleau
 * @version 1.90
 */
public class ListenerServer implements Runnable {

    /**
     * Ligne pour le rendu du son.
     */
    private SourceDataLine sourceDataLine;
    /**
     * Format Audio pour la capture du microphone.
     */
    private AudioFormat audioFormat;

    /**
     * Port d'écoute du serveur.
     */
    private int port;
    /**
     * Serveur pour la réception des données.
     */
    private ServerSocket serverSocket = null;

    /**
     * Thread de transfert des données.
     */
    private Thread thread;
    /**
     * Pour savoir s'il faut recevoir les données.
     */
    private boolean receive;

    /**
     * Taille par défaut du buffer
     */
    private static final int BUFFER_SIZE = 8 * 1024;
    /**
     * Buffer pour la réception des données.
     */
    private byte[] data = new byte[BUFFER_SIZE];

    /**
     * Initialisation avec un numéro de port.
     *
     * @param audioFormat le format de la ligne microphone.
     * @param port le port du receveur.
     */
    public ListenerServer(AudioFormat audioFormat, int port) {
        this.audioFormat = audioFormat;
        this.port = port;
    }

    /**
     * Démarre la réception des données et leur lecture.
     */
    public void start() {
        receive = true;

        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            CommonLogger.error(e);
        }

        thread = new Thread(this, this.getClass().getName());
        //Ouverture de la ligne pour écouter les données
        openLine(audioFormat);
        //Lancement des données
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.start();
//        thread.setPriority(Thread.MAX_PRIORITY);
        CommonLogger.debug("Listener thread started");
    }

    /**
     * Redémmarage du traitement des données. Pas d'ouverture d'une nouvelle
     * ligne du rendu audio. Pas d'ouverture du serveur de réception.
     */
    private void restart() {
        receive = true;
        thread = new Thread(this, this.getClass().getName());
        //Lancement des données
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.start();
//        thread.setPriority(Thread.MAX_PRIORITY);
        CommonLogger.debug("Listener thread restarted");
    }

    /**
     * Traitements des données.
     */
    @Override
    public void run() {
        int cnt = -1;
        Socket socket = null;

        long initTime;
        double transfert = 0;
        double writeAudio = 0;
        long pass = 0;

        try {
            //attente de la connection
            socket = serverSocket.accept();
            socket.setTcpNoDelay(true);
            socket.setSoTimeout(Constants.TIME_MAX_FOR_CONNEXION);
            InputStream inputStream = socket.getInputStream();

            //tant qu'il faut lire les données
            while (receive) {
                pass++;
                initTime = System.nanoTime();
                cnt = inputStream.read(data);
                transfert += (System.nanoTime() - initTime);

                if (cnt >= 0) {
                    initTime = System.nanoTime();
                    sourceDataLine.write(data, 0, cnt);
//                    sourceDataLine.drain();
                    writeAudio += (System.nanoTime() - initTime);
                } else {
                    receive = false;
                }
            }
        } catch (IOException e) {
            CommonLogger.error("Listener IO cnt: " + cnt);
            CommonLogger.error(e);
        } catch (Exception e) {
            CommonLogger.error(e);
        }

        if (pass > 0) {
            CommonLogger.info("Listener stop stats transfert: "
                    + transfert / 1000000 / pass + " writeAudio: "
                    + writeAudio / 1000000 / pass + " pass: " + pass);
        }

        //fermeture de la connection
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                CommonLogger.error(e);
            }
        }

        sourceDataLine.flush();

        //redémarrage pour une autre connexion au serveur
        restart();
    }

    /**
     * Ouverture de la ligne pour écouter les données.
     *
     * @param audioFormat le format de la ligne microphone.
     */
    private void openLine(AudioFormat audioFormat) {
        try {
            sourceDataLine = AudioSystem.getSourceDataLine(audioFormat);
            sourceDataLine.open(audioFormat, BUFFER_SIZE);
            sourceDataLine.start();
            CommonLogger.info("Listener line opened with: " + audioFormat.toString());
        } catch (LineUnavailableException | IllegalArgumentException e) {
            //pas de rendu du son
            CommonLogger.error(e);
        }
    }

    /**
     * Indique si la ligne est ouverte, c'est à dire si le système a réservé les
     * ressources et si elle est opérationnelle.
     *
     * @return <code>true</code> si la ligne est ouverte,
     * sinon <code>false</code>.
     */
    public boolean isLineOpen() {
        if (sourceDataLine == null) {
            return false;
        } else {
            return sourceDataLine.isOpen();
        }
    }
}
