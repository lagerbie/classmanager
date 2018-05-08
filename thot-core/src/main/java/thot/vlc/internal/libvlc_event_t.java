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

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

/**
 * A LibVLC event.
 *
 * @author Fabrice Alleau
 * @version 0.9.0 (VLC 0.9.x à 2.1.x et compatible JET)
 */
public class libvlc_event_t extends Structure {

    /**
     * Event type (see ref libvlc_event_e).
     */
    public int type;
    /**
     * Object emitting the event.
     */
    public Pointer p_obj;
    /**
     * Type-dependent event description.
     */
    public Pointer event_type_specific;

    @Override
    protected List<String> getFieldOrder() {
        return Arrays.asList("type", "p_obj", "event_type_specific");
    }

    public int getType() {
        return type;
    }
}
