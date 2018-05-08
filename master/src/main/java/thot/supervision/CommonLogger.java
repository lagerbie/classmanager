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
package thot.supervision;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Gestion des logs.
 *
 * @author Fabrice Alleau
 * @version 1.8.4
 */
@Deprecated
public class CommonLogger {

    /**
     * Logger.
     */
    private static final Logger logger = Logger.getLogger("commonlogger");

    /**
     * Ajoute un fichier de log.
     *
     * @param file le fichier.
     */
    public static void setLogFile(File file) {
        try {
            //création du répertoire parent
            file.getParentFile().mkdirs();
            FileHandler fileHandler = new FileHandler(file.getAbsolutePath());
            logger.addHandler(fileHandler);
        } catch (IOException | SecurityException e) {
            error(e);
        }
    }

    /**
     * Mesage d'erreur.
     *
     * @param message le message.
     */
    public static void error(String message) {
        logger.severe(message);
    }

    /**
     * Message d'information.
     *
     * @param message le message.
     */
    public static void info(String message) {
        logger.info(message);
    }

    /**
     * Affichage de la trace de l'erreur.
     *
     * @param error l'erreur.
     */
    public static void error(Throwable error) {
        logger.log(Level.SEVERE, error.getMessage(), error);
    }
}
