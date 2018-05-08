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
package thot.utils.dll;

import com.sun.jna.win32.StdCallLibrary;

/**
 * @author Fabrice Alleau
 * @version 1.8.4
 */
public interface User32Dll extends StdCallLibrary {

    /**
     * The BlockInput function blocks keyboard and mouse input events from reaching applications.
     *
     * @param bvalid Specifies the function's purpose. If this parameter is TRUE, keyboard and mouse input
     *         events are blocked. If this parameter is FALSE, keyboard and mouse events are unblocked. Note that only
     *         the thread that blocked input can successfully unblock input.
     *
     * @return If the function succeeds, the return value is nonzero. If input is already blocked, the return value is
     *         zero.
     */
    boolean BlockInput(boolean bvalid);
}
