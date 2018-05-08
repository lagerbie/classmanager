package thot.utils.dll;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static String dllName = "utilities";
    /**
     * Références des fonctions de la dll.
     */
    private static UtilitiesDll utilitiesDll;
    /**
     * Références des fonctions de la dll user32.dll.
     */
    private static User32Dll user32Dll;

    static {
        loadDll();
    }

    /**
     * Initialise les dll.
     */
    private static void loadDll() {
        addSearchPathFor(dllName);

        try {
            utilitiesDll = Native.loadLibrary(dllName, UtilitiesDll.class, UtilitiesDll.DEFAULT_OPTIONS);
        } catch (UnsatisfiedLinkError e) {
            LOGGER.error("", e);
            utilitiesDll = null;
        }

        user32Dll = Native.loadLibrary("User32", User32Dll.class);
    }

    /**
     * Ajout des chemins de recherche pour les librairies embarquées.
     *
     * @param libraryName le nom de la librairie.
     */
    private static void addSearchPathFor(String libraryName) {
        String fileProtocol = "file:/";
        String jarFileProtocol = "jar:file:/";
        URL url = WindowsUtilities.class.getResource(libraryName + ".dll");
        //file:/C:/..../utilities.dll (si le fichier .jar n'existe pas)
        //jar:file:/C:/.../student.jar!/supervision/dll/utilities.dll

        String ressource = null;
        if (url != null) {//fichier jar
            try {
                ressource = URLDecoder.decode(url.toString(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                LOGGER.error("", e);
                return;
            }
        }

        if (ressource == null) {
            ressource = "bin/lib/" + libraryName + ".dll";
        }

        String path = null;
        if (ressource.startsWith(fileProtocol)) {
            path = ressource.substring(fileProtocol.length());
        } else if (ressource.startsWith(jarFileProtocol)) {
            int begin = jarFileProtocol.length();
            int end = ressource.lastIndexOf('!');
            String jarFilePath = ressource.substring(begin, end);
            String fileClassPath = ressource.substring(end + 2);

            path = System.getProperty("java.io.tmpdir");
            String separator = System.getProperty("file.separator");
            if (!path.endsWith(separator)) {
                path += separator;
            }

            path = path + dllName + separator;

            FileOutputStream outputStream = null;
            InputStream inputStream = null;
            try {
                JarFile jarFile = new JarFile(jarFilePath);
                JarEntry entry = jarFile.getJarEntry(fileClassPath);

                File fileDest = new File(path + libraryName + ".dll");
                fileDest.getParentFile().mkdirs();// important de créer les répertoires
                fileDest.createNewFile();

                outputStream = new FileOutputStream(fileDest);
                inputStream = jarFile.getInputStream(entry);

                byte[] data = new byte[1024];

                int read = inputStream.read(data);
                while (read > 0) {
                    outputStream.write(data, 0, read);
                    read = inputStream.read(data);
                }
            } catch (IOException e) {
                LOGGER.error("", e);
                return;
            } finally {
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        LOGGER.error("", e);
                    }
                }

                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        LOGGER.error("", e);
                    }
                }
            }
        }

        NativeLibrary.addSearchPath(libraryName, path);
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
