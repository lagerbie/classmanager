package supervision;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Logger de l'application.
 *
 * @author Fabrice Alleau
 * @version 1.90
 */
public class CommonLogger {

    /**
     * Logger.
     */
    private static final Logger logger = Logger.getLogger("thumblogger");
    /**
     * Niveau de verbosité.
     */
    private static int level = 0;

    /**
     * Ajoute un fichier de log.
     *
     * @param file le fichier.
     */
    public static void setLogFile(File file) {
        try {
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
     * Message d'avertisssement.
     *
     * @param message le message.
     */
    public static void warning(String message) {
        logger.warning(message);
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
     * Message de debbogage.
     *
     * @param message le message.
     */
    public static void debug(String message) {
        logger.fine(message);
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
