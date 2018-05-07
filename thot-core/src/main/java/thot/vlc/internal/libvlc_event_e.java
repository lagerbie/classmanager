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
 * enum libvlc_event_e
 *
 * @author Fabrice Alleau
 * @since 0.9.0
 * @version 1.1.0 (VLC 1.1.x à 2.1.x et compatible JET)
 */
public enum libvlc_event_e {
    /* Append new event types at the end of a category.
     * Do not remove, insert or re-order any entry.
     * Keep this in sync with src/control/event.c:libvlc_event_type_name(). */
//    libvlc_MediaMetaChanged             (0x000),//=0,
//    libvlc_MediaSubItemAdded            (0x001),
//    libvlc_MediaDurationChanged         (0x002),
//    libvlc_MediaParsedChanged           (0x003),
//    libvlc_MediaFreed                   (0x004),
//    libvlc_MediaStateChanged            (0x005),
//    libvlc_MediaSubItemTreeAdded        (0x006), //VLC >=2.1.0

//    libvlc_MediaPlayerMediaChanged      (0x100),//=0x100,
    libvlc_MediaPlayerNothingSpecial    (0x101),//=6 pour vlc >=1.0.6
    libvlc_MediaPlayerOpening           (0x102),
    libvlc_MediaPlayerBuffering         (0x103),
    libvlc_MediaPlayerPlaying           (0x104),
    libvlc_MediaPlayerPaused            (0x105),
    libvlc_MediaPlayerStopped           (0x106),
    libvlc_MediaPlayerForward           (0x107),
    libvlc_MediaPlayerBackward          (0x108),
    libvlc_MediaPlayerEndReached        (0x109),
    libvlc_MediaPlayerEncounteredError  (0x10A),
    libvlc_MediaPlayerTimeChanged       (0x10B),
    libvlc_MediaPlayerPositionChanged   (0x10C),
    libvlc_MediaPlayerSeekableChanged   (0x10D),
    libvlc_MediaPlayerPausableChanged   (0x10E);
//    libvlc_MediaPlayerTitleChanged      (0x10F),
//    libvlc_MediaPlayerSnapshotTaken     (0x110),
//    libvlc_MediaPlayerLengthChanged     (0x111),

//    libvlc_MediaListItemAdded           (0x200),//=0x200,
//    libvlc_MediaListWillAddItem         (0x201),
//    libvlc_MediaListItemDeleted         (0x202),
//    libvlc_MediaListWillDeleteItem      (0x203),

//    libvlc_MediaListViewItemAdded       (0x300),//=0x300,
//    libvlc_MediaListViewWillAddItem     (0x301),
//    libvlc_MediaListViewItemDeleted     (0x302),
//    libvlc_MediaListViewWillDeleteItem  (0x303),

//    libvlc_MediaListPlayerPlayed        (0x400),//=0x400,
//    libvlc_MediaListPlayerNextItemSet   (0x401),
//    libvlc_MediaListPlayerStopped       (0x402),

//    libvlc_MediaDiscovererStarted       (0x500),//=0x500,
//    libvlc_MediaDiscovererEnded         (0x501),

//    libvlc_VlmMediaAdded                (0x600),//=0x600,
//    libvlc_VlmMediaRemoved              (0x601),
//    libvlc_VlmMediaChanged              (0x602),
//    libvlc_VlmMediaInstanceStarted      (0x603),
//    libvlc_VlmMediaInstanceStopped      (0x604),
//    libvlc_VlmMediaInstanceStatusInit   (0x605),
//    libvlc_VlmMediaInstanceStatusOpening(0x606),
//    libvlc_VlmMediaInstanceStatusPlaying(0x607),
//    libvlc_VlmMediaInstanceStatusPause  (0x608),
//    libvlc_VlmMediaInstanceStatusEnd    (0x609),
//    libvlc_VlmMediaInstanceStatusError  (0x60A);

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

    private libvlc_event_e(int intValue) {
        this.intValue = intValue;
    }

    public int intValue() {
        return intValue;
    }
}
