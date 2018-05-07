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
package thot.audio;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;

import javax.sound.sampled.AudioFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thot.exception.ThotCodeException;
import thot.exception.ThotException;

/**
 * Classe pour capturer les données issues du microphone avec un serveur qui diffuse le microphone en multicast.
 *
 * @author Fabrice Alleau
 * @version 1.8.4
 */
public class DatagramSocketAudioRecorder extends AbstractAudioProcessing implements AudioRecorder {

    /**
     * Instance de log.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DatagramSocketAudioRecorder.class);

    /**
     * Socket pour le mode déporté.
     */
    private DatagramSocket socketMicrophone;
    /**
     * Paquet pour la réception des données.
     */
    private DatagramPacket paquet;

    /**
     * Initialisation avec un format audio, une référence sur le buffer où seront enregistrées les données et la socket
     * initialisée.
     *
     * @param recordBuffer le buffer de stockage.
     * @param audioFormat le format audio.
     */
    public DatagramSocketAudioRecorder(ByteBuffer recordBuffer, AudioFormat audioFormat) {
        super(recordBuffer, audioFormat);
    }

    /**
     * Initialise la socket multicast pour la réception des données du microphone.
     *
     * @param portMicrophone le port de réception des données audio.
     */
    public void initDatagramSocket(int portMicrophone) throws ThotException {
        if (socketMicrophone != null) {
            close();
        }
        LOGGER.info("Initialisation de la socket pour la connexion au serveur du microphone");
        try {
            socketMicrophone = new DatagramSocket(portMicrophone);
        } catch (IOException e) {
            throw new ThotException(ThotCodeException.AUDIO, "Impossible d'ouvrir une socket UDP sur le port {}", e,
                    portMicrophone);
        }
    }

    @Override
    public void close() {
        LOGGER.info("Fermeture de la connexion au serveur du microphone");
        socketMicrophone.close();
    }

    @Override
    protected void endProcess() {

    }

    @Override
    protected int process(ByteBuffer recordBuffer, byte[] data, int offset, int length) {
        int read = read(data, length);
        if (read > 0) {
            recordBuffer.put(data, 0, read);
        }
        return read;
    }

    private int read(byte data[], int length) {
        if (paquet == null) {
            paquet = new DatagramPacket(data, BUFFER_SIZE);
        }
        int cnt = -1;
        long timeTampon = System.currentTimeMillis();

        paquet.setLength(length);

        try {
            socketMicrophone.receive(paquet);
            cnt = paquet.getLength();
        } catch (IOException e) {
            LOGGER.error("", e);
        }
        //1024 bits -> 64ms
        //mémoire tampon accès en 1 ou 2 ms
        //on passe les données non acquises réellement
        if (System.currentTimeMillis() - timeTampon < 5) {
            cnt = 0;
        }
        return cnt;
    }
}
