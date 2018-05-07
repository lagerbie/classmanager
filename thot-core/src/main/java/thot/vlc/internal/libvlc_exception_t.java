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

import com.sun.jna.Structure;

/**
 * LibVLC Exceptions handling
 *
 * @author Fabrice Alleau
 * @since version 0.9.0 (VLC 0.9.x à 1.0.x et compatible JET)
 * deprecated depuis VLC 1.1.0
 */
//@Deprecated
public class libvlc_exception_t extends Structure {

    public int b_raised;
    public int i_code;
    public String psz_message;

    @Override
    protected List<String> getFieldOrder() {
        return Arrays.asList(new String[] {"b_raised", "i_code", "psz_message"});
    }
}
