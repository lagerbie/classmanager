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

import com.sun.jna.Pointer;

import thot.vlc.MediaPlayer;
import thot.vlc.internal.libvlc_callback_t;
import thot.vlc.internal.libvlc_event_e;
import thot.vlc.internal.libvlc_event_t;

/**
 * Callback pour les évènements de la librairie VLC.
 *
 * @author Fabrice Alleau
 * @version 1.1.0 (VLC 0.9.x à 2.1.x)
 */
public class MediaPlayerCallback implements libvlc_callback_t {

    /**
     * Lecteur multimédia.
     */
    private MediaPlayer mediaPlayer;
    /**
     * Référence du listener.
     */
    private MediaPlayerListener listener;
    /**
     * Offset sur les évènement suivant les versions.
     */
    private int eventOffset;
    /**
     * Offset pour les évènements des versions antérieures (VLC >= 1.0.x).
     */
    private static final int OLD_OFFSET
            = libvlc_event_e.libvlc_MediaPlayerNothingSpecial.intValue() - 6;

    /**
     * Initialisation du callback.
     *
     * @param mediaPlayer le lecteur multimédia.
     * @param listener le listener.
     * @version 1.1.0
     */
    public MediaPlayerCallback(MediaPlayer mediaPlayer, MediaPlayerListener listener) {
        this.mediaPlayer = mediaPlayer;
        this.listener = listener;

        String version = MediaPlayer.version;
        int first = version.charAt(0) - '0';
        int second = version.charAt(2) - '0';

        this.eventOffset = (first < 1 || (first == 1 && second == 0)) ? OLD_OFFSET : 0;
    }

    /**
     * Retourne l'offset sur les id des évènements.
     *
     * @return l'offset pour les évènement.
     * @since version 1.1.0
     */
    public int getEventOffset() {
        return eventOffset;
    }

    /**
     * Retourne le listener du callback.
     *
     * @return le listener du callback.
     * @since version 0.9.0
     */
    public MediaPlayerListener getListener() {
        return listener;
    }

    /**
     * Appel du callback.
     *
     * @param libvlc_event l'évènement.
     * @param userData les données de l'évènement.
     * @version 1.1.0
     */
    @Override
    public void callback(libvlc_event_t libvlc_event, Pointer userData) {
        int type = libvlc_event.type;
        MediaPlayerEvent event = new MediaPlayerEvent(mediaPlayer, type);

        switch (libvlc_event_e.event(type + eventOffset)) {
            case libvlc_MediaPlayerEndReached:
                listener.endReached(event);
                break;
            case libvlc_MediaPlayerEncounteredError:
                listener.encounteredError(event);
                break;
        }
    }
}
