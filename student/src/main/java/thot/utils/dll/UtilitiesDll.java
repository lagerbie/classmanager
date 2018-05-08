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

import java.util.HashMap;
import java.util.Map;

import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIFunctionMapper;
import com.sun.jna.win32.W32APITypeMapper;

/**
 * Utilisation d'une dll Windows pour la batterie et la fermeture de Windows et de la session utilisateur Windows.
 *
 * @author Fabrice Alleau
 * @version 1.8.4
 */
public interface UtilitiesDll extends StdCallLibrary {

    Map<String, Object> UNICODE_OPTIONS = new HashMap<String, Object>() {
        private static final long serialVersionUID = 67L;

        {
            put(OPTION_TYPE_MAPPER, W32APITypeMapper.UNICODE);
            put(OPTION_FUNCTION_MAPPER, W32APIFunctionMapper.UNICODE);
        }
    };

    Map<String, Object> ASCII_OPTIONS = new HashMap<String, Object>() {
        private static final long serialVersionUID = 67L;

        {
            put(OPTION_TYPE_MAPPER, W32APITypeMapper.ASCII);
            put(OPTION_FUNCTION_MAPPER, W32APIFunctionMapper.ASCII);
        }
    };

    Map<String, Object> DEFAULT_OPTIONS = Boolean.getBoolean("w32.ascii") ? ASCII_OPTIONS : UNICODE_OPTIONS;

    /**
     * Donne le niveau de batterie en poucentage.
     *
     * @return le niveau de batterie en poucentage.
     */
    int RecupererBatterie();

    /**
     * Ferme la session utilisateur de Windows.
     *
     * @return 1 si réussite.
     */
    long FermetureWindows();

    /**
     * Eteint l'ordinateur.
     *
     * @return 1 si réussite.
     */
    long FermetureSessions();
}
