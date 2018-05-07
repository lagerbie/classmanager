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

import java.nio.ByteBuffer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thot.exception.ThotCodeException;
import thot.exception.ThotException;

/**
 * Classe pour capturer les données issues du microphone en lecture directe.
 *
 * @author Fabrice Alleau
 * @version 1.8.4
 */
public class DirectAudioRecorder extends AbstractAudioProcessing implements AudioRecorder {

    /**
     * Instance de log.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DirectAudioRecorder.class);

    /**
     * Ligne directe sur le microphone.
     */
    private TargetDataLine targetDataLine;

    /**
     * Initialisation avec un format audio et une référence sur le buffer où seront enregistrées les données.
     *
     * @param recordBuffer le buffer de stockage.
     * @param audioFormat le format audio.
     */
    public DirectAudioRecorder(ByteBuffer recordBuffer, AudioFormat audioFormat) {
        super(recordBuffer, audioFormat);
    }

    /**
     * Initialise la ligne directe sur le microphone.
     */
    public void initAudioLine() throws ThotException {
        LOGGER.info("Initialisation du flux audio pour le format {}", getAudioFormat());
        try {
            //Recherche de la configuration pour la capture des données.
            targetDataLine = AudioSystem.getTargetDataLine(getAudioFormat());

            //ouverture et démarage de la ligne de capture aver une taille de buffer la plus petite possible.
            targetDataLine.open(getAudioFormat(), DirectAudioRecorder.BUFFER_SIZE);
            targetDataLine.start();
        } catch (LineUnavailableException | IllegalArgumentException e) {
            throw new ThotException(ThotCodeException.AUDIO, "Impossible d'ouvrir une ligne avec le format {}", e,
                    getAudioFormat());
        }
    }

    @Override
    public void close() {
        LOGGER.info("Fermeture de la ligne audio");
        targetDataLine.stop();
        targetDataLine.close();
    }

    @Override
    protected void endProcess() {
        LOGGER.info("Flush sur la ligne audio");
        targetDataLine.flush();
    }

    @Override
    protected int process(ByteBuffer recordBuffer, byte[] data, int offset, int length) {
        int read = targetDataLine.read(data, 0, length);
        if (read > 0) {
            recordBuffer.put(data, 0, read);
        }
        return read;
    }

}
