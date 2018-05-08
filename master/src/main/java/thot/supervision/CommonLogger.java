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
