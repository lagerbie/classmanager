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
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thot.exception.ThotCodeException;
import thot.exception.ThotException;

/**
 * Classe pour lire les données audio en mode direct.
 *
 * @author Fabrice Alleau
 * @version 1.8.4
 */
public class DirectAudioPlayer extends AbstractAudioProcessing implements AudioPlayer {

    /**
     * Instance de log.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DirectAudioPlayer.class);

    /**
     * Ligne audio de rendu.
     */
    private SourceDataLine sourceDataLine;
    /**
     * Control du volume de sortie.
     */
    private FloatControl gainControl = null;

    /**
     * Initialisation avec un format audio et une référence sur le buffer où sont enregistrées les données.
     *
     * @param recordBuffer le buffer de stockage.
     * @param audioFormat le format audio.
     */
    public DirectAudioPlayer(ByteBuffer recordBuffer, AudioFormat audioFormat) {
        super(recordBuffer, audioFormat);
    }

    /**
     * Initialise le flux audio.
     *
     * @throws ThotException si l'ouverture de la ligne échoue.
     */
    public void initAudioLine() throws ThotException {
        LOGGER.info("Initialisation du flux audio pour le format {}", getAudioFormat());

        try {
            //Recherche de la configuration pour la lecture de données.
            sourceDataLine = AudioSystem.getSourceDataLine(getAudioFormat());

            //Ouverture de la ligne avec une taille de buffer la plus petite possible.
            sourceDataLine.open(getAudioFormat(), BUFFER_SIZE);
            sourceDataLine.start();

            //control pour le volume
            LOGGER.info("Initialisation du controller du volume audio pour le format");
            if (sourceDataLine.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                gainControl = (FloatControl) sourceDataLine.getControl(FloatControl.Type.MASTER_GAIN);
            } else if (sourceDataLine.isControlSupported(FloatControl.Type.VOLUME)) {
                gainControl = (FloatControl) sourceDataLine.getControl(FloatControl.Type.VOLUME);
            }
        } catch (LineUnavailableException | IllegalArgumentException e) {
            throw new ThotException(ThotCodeException.AUDIO, "Impossible d'ouvrir une ligne avec le format {}", e,
                    getAudioFormat());
        }
    }

    /**
     * Modifie le volume.
     * <p>
     * Le gain effectif n'est que de 75% du gain réel pour avoir un décroisement du volume moins rapide.
     *
     * @param value la valeur de volume en poucentage (entre 0 et 100).
     */
    @Override
    public void setVolume(int value) {
        LOGGER.debug("Initialisation du flux audio pour le format {}", getAudioFormat());
        float max = gainControl.getMaximum();
        float min = gainControl.getMinimum();
        float gain = (max - min) * value / 100.0f + min;
        gainControl.setValue(gain * 0.75f);
    }

    @Override
    public void close() {
        LOGGER.info("Fermeture de la ligne audio");
        sourceDataLine.stop();
        sourceDataLine.close();
    }

    /**
     * Indique si la ligne est ouverte.
     * <p>
     * Vérifie si la est opérationnelle.
     *
     * @return {@code true} si la ligne est ouverte.
     */
    public boolean isLineOpen() {
        return sourceDataLine != null && sourceDataLine.isOpen();
    }

    /**
     * Traitement spécifique des données pour la lecture des données.
     *
     * @param recordBuffer le buffer contenant les données.
     * @param data le tableau des données.
     * @param offset l'offset de départ.
     * @param length le nombre de bytes à traiter.
     *
     * @return le nombre de bytes traités.
     */
    @Override
    protected int process(ByteBuffer recordBuffer, byte[] data, int offset, int length) {
        recordBuffer.get(data, offset, length);
        return sourceDataLine.write(data, offset, length);
    }

    @Override
    protected void endProcess() {
        LOGGER.info("Drain sur la ligne audio");
        sourceDataLine.drain();
    }
}
