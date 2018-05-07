package eestudio.utils;

import java.io.File;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.*;

/**
 * Gestion des logs.
 *
 * @author Fabrice Alleau
 * @version 0.99
 * @since version 0.94
 */
@Deprecated
public class Edu4Logger {
    /**
     * Logger
     */
    private static final Logger logger = Logger.getLogger("edu4logger");
    /**
     * Mode debug
     */
    private static boolean debug = false;
    /**
     * Zone de texte
     */
    private static JTextArea text;

    /**
     * Ajoute un fichier de log.
     *
     * @param file le fichier.
     *
     * @since version 0.94 - version 0.99
     */
    public static void setLogFile(File file) {
        try {
            //création du répertoire parent
            file.getParentFile().mkdirs();
            FileHandler fileHandler = new FileHandler(file.getAbsolutePath());
            logger.addHandler(fileHandler);
            logger.setLevel(Level.INFO);
        } catch (Exception e) {
            error(e);
        }
    }

    /**
     * Active le mode debug.
     *
     * @since version 0.99
     */
    public static void debug() {
        debug = true;
        logger.setLevel(Level.FINE);

        JFrame frame = new JFrame("eeStudio debug");
        text = new JTextArea();

        JScrollPane scrollPane = new JScrollPane(text,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        frame.getContentPane().add(scrollPane);
        frame.pack();
        frame.setSize(400, 500);
        frame.setVisible(true);
    }

    /**
     * Mesage d'erreur.
     *
     * @param message le message.
     *
     * @since version 0.94
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
     *
     * @since version 0.94
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
     *
     * @since version 0.94
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
     *
     * @since version 0.94
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
     *
     * @since version 0.94
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
     *
     * @since version 0.94
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
     *
     * @since version 0.94 - version 0.98
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

}//end
