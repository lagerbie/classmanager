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
package thot.vlc.internal;

import java.util.HashMap;

/**
 * Event types.
 *
 * @author Fabrice Alleau
 * @version 1.1.0 (VLC 1.1.x à 2.1.x et compatible JET)
 */
public enum libvlc_event_e {

    libvlc_MediaPlayerNothingSpecial(0x101),
    libvlc_MediaPlayerOpening(0x102),
    libvlc_MediaPlayerBuffering(0x103),
    libvlc_MediaPlayerPlaying(0x104),
    libvlc_MediaPlayerPaused(0x105),
    libvlc_MediaPlayerStopped(0x106),
    libvlc_MediaPlayerForward(0x107),
    libvlc_MediaPlayerBackward(0x108),
    libvlc_MediaPlayerEndReached(0x109),
    libvlc_MediaPlayerEncounteredError(0x10A),
    libvlc_MediaPlayerTimeChanged(0x10B),
    libvlc_MediaPlayerPositionChanged(0x10C),
    libvlc_MediaPlayerSeekableChanged(0x10D),
    libvlc_MediaPlayerPausableChanged(0x10E);

    private static final HashMap<Integer, libvlc_event_e> INT_MAP = new HashMap<>(16);//48
    private int intValue;

    static {
        for (libvlc_event_e event : libvlc_event_e.values()) {
            INT_MAP.put(event.intValue(), event);
        }
    }

    public static libvlc_event_e event(int intValue) {
        return INT_MAP.get(intValue);
    }

    libvlc_event_e(int intValue) {
        this.intValue = intValue;
    }

    public int intValue() {
        return intValue;
    }
}
