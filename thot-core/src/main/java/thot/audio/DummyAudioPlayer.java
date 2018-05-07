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

/**
 * Classe pour la non lecture de données audio.
 *
 * @author Fabrice Alleau
 * @version 1.8.4
 */
public class DummyAudioPlayer extends DummyAudioProcessing implements AudioPlayer {

    /**
     * Initialisation.
     */
    public DummyAudioPlayer() {
    }

    @Override
    public void setVolume(int value) {

    }

    @Override
    public void close() {

    }

}
