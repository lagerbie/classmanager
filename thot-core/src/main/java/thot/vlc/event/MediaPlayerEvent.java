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
package thot.vlc.event;

import java.util.EventObject;

import thot.vlc.MediaPlayer;

/**
 * Evènement pour la librairie VLC.
 *
 * @author Fabrice Alleau
 * @version 0.9.0 (VLC 0.9.x à 2.1.x)
 */
public class MediaPlayerEvent extends EventObject {
    private static final long serialVersionUID = 90000L;

    /**
     * Type de l'évènement.
     */
    private int eventType;

    /**
     * Initiallisation de l'évenement du lectuer multimédia.
     *
     * @param source la source de l'évènement.
     * @param eventType le type de l'évènement.
     */
    public MediaPlayerEvent(MediaPlayer source, int eventType) {
        super(source);
        this.eventType = eventType;
    }

    /**
     * Retourne le type de l'évènement.
     *
     * @return le type de l'évènement.
     */
    public int getEventType() {
        return eventType;
    }
}
