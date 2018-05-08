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

/**
 * Note the order of libvlc_state_t enum must match exactly the order of \see mediacontrol_PlayerStatus, \see
 * input_state_e enums, and VideoLAN.LibVLC.State (at bindings/cil/src/media.cs).
 *
 * @author Fabrice Alleau
 */
public enum libvlc_state_t {

    libvlc_NothingSpecial(0),
    libvlc_Opening(1),
    libvlc_Buffering(2),
    libvlc_Playing(3),
    libvlc_Paused(4),
    libvlc_Stopped(5),
    libvlc_Ended(6),
    libvlc_Error(7);

    private int intValue;

    libvlc_state_t(int intValue) {
        this.intValue = intValue;
    }

    public int intValue() {
        return intValue;
    }
}
