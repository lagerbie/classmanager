package thot.utils.dll;

import java.io.File;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thot.exception.ThotException;
import thot.utils.Utilities;

/**
 * Utilitaires pour les dll de Windows.
 *
 * @author Fabrice Alleau
 * @version 1.8.4
 */
public class WindowsUtilities {

    /**
     * Instance de log.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(WindowsUtilities.class);

    /**
     * Nom de la dll.
     */
    private static final String UTILITIES_DLL_NAME = "utilities";
    /**
     * Nom de la  dll User32.
     */
    private static final String USER32_DLL_NAME = "User32";

    /**
     * Références des fonctions de la dll.
     */
    private static UtilitiesDll utilitiesDll;
    /**
     * Références des fonctions de la dll user32.dll.
     */
    private static User32Dll user32Dll;

    /*
     * Initialise les dll.
     */
    static {

        try {
            addSearchPath();
            utilitiesDll = Native.loadLibrary(UTILITIES_DLL_NAME, UtilitiesDll.class, UtilitiesDll.DEFAULT_OPTIONS);
        } catch (ThotException e) {
            LOGGER.error("Impossible de charger la librairie {}", e, UTILITIES_DLL_NAME);
        }

        user32Dll = Native.loadLibrary(USER32_DLL_NAME, User32Dll.class);
    }

    /**
     * Ajout des chemins de recherche pour les librairies embarquées.
     */
    private static void addSearchPath() throws ThotException {
        String ressourcePath = "dll" + UTILITIES_DLL_NAME + ".dll";
        LOGGER.info("Recherche de l'emplacement de librairie {} ({})", UTILITIES_DLL_NAME, ressourcePath);
        File resourceFile = new File(System.getProperty("java.io.tmpdir"), ressourcePath);

        File library = Utilities.getResource(ressourcePath, resourceFile);

        NativeLibrary.addSearchPath(WindowsUtilities.UTILITIES_DLL_NAME, library.getAbsolutePath());
    }

    /**
     * Donne le niveau de batterie en poucentage.
     *
     * @return le niveau de batterie en poucentage.
     */
    public static int getBatteryLevel() {
        if (utilitiesDll == null) {
            return -1;
        }
        return utilitiesDll.RecupererBatterie();
    }

    /**
     * Ferme la session utilisateur de Windows.
     *
     * @return 1 si réussite.
     */
    public static long shutdown() {
        if (utilitiesDll == null) {
            return 0;
        }
        return utilitiesDll.FermetureWindows();
    }

    /**
     * Eteint l'ordinateur.
     *
     * @return 1 si réussite.
     */
    public static long shutdownSession() {
        if (utilitiesDll == null) {
            return 0;
        }
        return utilitiesDll.FermetureSessions();
    }

    /**
     * Bloque les évènements du clavier et de la souris.
     *
     * @param bvalid blockage ou déblocage du clavier et de la souris.
     *
     * @return If the function succeeds, the return value is nonzero. If input is already blocked, the return value is
     *         zero.
     */
    public static boolean blockInput(boolean bvalid) {
        return user32Dll.BlockInput(bvalid);
    }
}
