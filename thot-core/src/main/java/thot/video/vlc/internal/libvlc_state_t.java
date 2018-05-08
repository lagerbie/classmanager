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

import java.util.Arrays;

import lombok.Getter;
import thot.video.MediaPlayerState;

/**
 * Note the order of libvlc_state_t enum must match exactly the order of \see mediacontrol_PlayerStatus, \see
 * input_state_e enums, and VideoLAN.LibVLC.State (at bindings/cil/src/media.cs).
 *
 * @author Fabrice Alleau
 * @version 1.8.4 (VLC 1.1.0 à 3.0.x et compatible JET)
 */
public enum libvlc_state_t {

    libvlc_NothingSpecial(MediaPlayerState.IDLE_CLOSE, 0),
    libvlc_Opening(MediaPlayerState.OPENING, 1),
    libvlc_Buffering(MediaPlayerState.BUFFERING, 2),
    libvlc_Playing(MediaPlayerState.PLAYING, 3),
    libvlc_Paused(MediaPlayerState.PAUSED, 4),
    libvlc_Stopped(MediaPlayerState.STOPPING, 5),
    libvlc_Ended(MediaPlayerState.ENDED, 6),
    libvlc_Error(MediaPlayerState.ERROR, 7);

    /**
     * Retourne l'état suivant la valeur de l'état de VLC.
     *
     * @param vlcValue la valeur de létat dans VLC.
     *
     * @return l'état du media player.
     */
    public static MediaPlayerState getState(int vlcValue) {
        return Arrays.stream(libvlc_state_t.values()).filter(state -> state.getVlcValue() == vlcValue)
                .map(libvlc_state_t::getState).findFirst().orElse(null);
    }

    @Getter
    private int vlcValue;

    @Getter
    private MediaPlayerState state;

    libvlc_state_t(MediaPlayerState state, int vlcValue) {
        this.vlcValue = vlcValue;
        this.state = state;
    }

}
