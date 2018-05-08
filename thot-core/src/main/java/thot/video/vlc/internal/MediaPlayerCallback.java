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
package thot.video.vlc.internal;

import com.sun.jna.Pointer;
import thot.video.event.MediaPlayerEvent;
import thot.video.event.MediaPlayerListener;
import thot.video.vlc.VLCMediaPlayer;

/**
 * Callback pour les évènements de la librairie VLC.
 *
 * @author Fabrice Alleau
 * @version 1.8.4 (VLC 1.1.0 à 3.0.x et compatible JET)
 */
public class MediaPlayerCallback implements libvlc_callback_t {

    /**
     * Lecteur multimédia.
     */
    private VLCMediaPlayer mediaPlayer;
    /**
     * Référence du listener.
     */
    private MediaPlayerListener listener;

    /**
     * Initialisation du callback.
     *
     * @param mediaPlayer le lecteur multimédia.
     * @param listener le listener.
     */
    public MediaPlayerCallback(VLCMediaPlayer mediaPlayer, MediaPlayerListener listener) {
        this.mediaPlayer = mediaPlayer;
        this.listener = listener;
    }

    /**
     * Retourne le listener du callback.
     *
     * @return le listener du callback.
     */
    public MediaPlayerListener getListener() {
        return listener;
    }

    /**
     * Appel du callback.
     *
     * @param libvlc_event l'évènement.
     * @param userData les données de l'évènement.
     */
    @Override
    public void callback(libvlc_event_t libvlc_event, Pointer userData) {
        int type = libvlc_event.type;
        MediaPlayerEvent event = new MediaPlayerEvent(mediaPlayer);

        switch (libvlc_event_e.event(type)) {
            case libvlc_MediaPlayerEndReached:
                listener.endReached(event);
                break;
            case libvlc_MediaPlayerEncounteredError:
                listener.encounteredError(event);
                break;
        }
    }
}
