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

/**
 * Classe pour capturer des données.
 *
 * @author Fabrice Alleau
 * @version 1.90
 */
public abstract class AudioRecorder extends AudioProcessing {

    /**
     * Initialisation avec un format audio et une référence sur le buffer où seront enregistrées les données pour un
     * mode indirect.
     *
     * @param recordBuffer le buffer de stockage.
     * @param audioFormat le format audio.
     */
    public AudioRecorder(ByteBuffer recordBuffer, AudioFormat audioFormat) {
        super(recordBuffer, audioFormat);
    }

    /**
     * Lit et enregistre les données du microphone dans le buffer data[] à partir de offset et un nombre de bytes
     * donnés.
     *
     * @param data le tableau où seront les données.
     * @param offset l'offset de départ.
     * @param read le nombre de bytes à lire.
     *
     * @return le nombre de bytes réellement lus.
     */
    protected abstract int read(byte data[], int offset, int read);

    /**
     * Flush des données.
     */
    protected abstract void flush();

    @Override
    protected int process(ByteBuffer recordBuffer, byte[] data, int offset, int length) {
        int read = read(data, offset, length);
        if (read > 0) {
            recordBuffer.put(data, 0, read);
        }
        return read;
    }

    @Override
    protected void endProcess() {
        flush();
    }
}
