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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Classe pour lire le ByteBuffer.
 *
 * @author Fabrice Alleau
 * @version 1.90
 */
public class DummyAudioPlayer extends AudioPlayer {

    /**
     * Instance de log.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DummyAudioPlayer.class);

    /**
     * Temps courant dans la piste.
     */
    private long currentTime;
    /**
     * Temps fin de lecture dans la piste.
     */
    private long stopTime;

    /**
     * Initialisation.
     */
    public DummyAudioPlayer() {
        super(null, null);
    }

    @Override
    public void start(long initTime, long stopTime) {
        this.currentTime = initTime;
        this.stopTime = stopTime;
        super.start(initTime, stopTime);
    }

    @Override
    public void setVolume(int value) {
    }

    @Override
    public void close() {
    }

    @Override
    public void run() {
        long duration = stopTime - currentTime;
        long initTime = System.currentTimeMillis();

        long timePassed = 0;

        while (isRun() && duration > 0) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                LOGGER.error("", e);
            }

            timePassed = System.currentTimeMillis() - initTime;
            if (timePassed > 500) {
                duration -= timePassed;
                initTime = System.currentTimeMillis();
                currentTime += timePassed;
                fireTimeChanged(currentTime);
                timePassed = 0;
            }
        }

        fireTimeChanged(currentTime + timePassed);
        fireEndProcess(isRun());
    }
}
