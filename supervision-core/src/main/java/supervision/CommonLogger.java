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
package supervision;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

/**
 * Gestion des logs.
 *
 * @author Fabrice Alleau
 * @version 1.90
 */
public class CommonLogger {

    /**
     * Logger.
     */
    private static final Logger logger = Logger.getLogger("commonlogger");
    /**
     * Niveau de verbosité.
     */
    private static int level = 0;
    /**
     * Mode debug.
     */
    private static boolean debug = false;
    /**
     * Zone de texte.
     */
    private static JTextArea text;

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
     * Modifie le niveau de verbosité.
     *
     * @param level le niveau de verbosité.
     */
    public static void setLevel(int level) {
        if (level < 0) {
            debug = true;
            CommonLogger.level = 3;
        } else {
            CommonLogger.level = level;
        }

        switch (CommonLogger.level) {
            case 0:
                logger.setLevel(Level.SEVERE);
                break;
            case 1:
                logger.setLevel(Level.WARNING);
                break;
            case 2:
                logger.setLevel(Level.INFO);
                break;
            case 3:
                logger.setLevel(Level.FINE);
                break;
        }

        if (debug && text == null) {
            JFrame frame = new JFrame("debug");
            text = new JTextArea();

            JScrollPane scrollPane = new JScrollPane(text,
                    JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                    JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

            frame.getContentPane().add(scrollPane);
            frame.pack();
            frame.setVisible(true);
        }
    }

    /**
     * Mesage d'erreur.
     *
     * @param message le message.
     */
    public static void error(String message) {
        logger.severe(message);
        if (debug) {
            print(message);
        }
    }

    /**
     * Message d'avertisssement.
     *
     * @param message le message.
     */
    public static void warning(String message) {
        logger.warning(message);
        if (debug) {
            print(message);
        }
    }

    /**
     * Message d'information.
     *
     * @param message le message.
     */
    public static void info(String message) {
        logger.info(message);
        if (debug) {
            print(message);
        }
    }

    /**
     * Message de debbogage.
     *
     * @param message le message.
     */
    public static void debug(String message) {
        logger.fine(message);
        if (debug) {
            print(message);
        }
    }

    /**
     * Affichage de la trace de l'erreur.
     *
     * @param error l'erreur.
     */
    public static void error(Throwable error) {
        logger.log(Level.SEVERE, error.getMessage(), error);
        if (debug) {
            print(error);
        }
    }

    /**
     * Affichage d'un message texte.
     *
     * @param message le message.
     */
    private static void print(final String message) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                text.append(message + "\n");
            }
        });
    }

    /**
     * Affichage d'une erreur.
     *
     * @param error l'erreur.
     */
    private static void print(final Throwable error) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                text.append(error.toString() + "\n");
                StackTraceElement[] elements = error.getStackTrace();
                for (StackTraceElement stackTraceElement : elements) {
                    text.append("\t" + stackTraceElement.toString() + "\n");
                }
            }
        });
    }
}
