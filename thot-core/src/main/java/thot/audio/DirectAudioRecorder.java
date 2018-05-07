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
import javax.sound.sampled.TargetDataLine;

/**
 * Classe pour capturer les données issues du microphone. Configuer pour être en
 * lecture directe du microphone.
 *
 * @author Fabrice Alleau
 * @version 1.90
 */
public class DirectAudioRecorder extends AudioRecorder {

    /**
     * Ligne directe sur le microphone.
     */
    private TargetDataLine targetDataLine;

    /**
     * Initialisation avec une ligne de capture ouverte (directement relié au
     * microphone).
     * Equivalant à <code>AudioRecorder(core, null, targetDataLine)</code>.
     *
     * @param targetDataLine la ligne de capture.
     */
    public DirectAudioRecorder(TargetDataLine targetDataLine) {
        this(null, targetDataLine);
    }

    /**
     * Initialisation avec une ligne de capture ouverte et une référence sur le
     * buffer où seront enregistrées les données (directement relié au
     * microphone).
     *
     * @param recordBuffer le buffer de stockage.
     * @param targetDataLine la ligne de capture.
     */
    public DirectAudioRecorder(ByteBuffer recordBuffer, TargetDataLine targetDataLine) {
        super(recordBuffer, targetDataLine.getFormat());
        this.targetDataLine = targetDataLine;
    }

    @Override
    public void close() {
        targetDataLine.stop();
        targetDataLine.close();
    }

    @Override
    protected int read(byte data[], int offset, int read) {
        return targetDataLine.read(data, 0, read);
    }

    @Override
    protected void flush() {
        targetDataLine.flush();
    }
}
