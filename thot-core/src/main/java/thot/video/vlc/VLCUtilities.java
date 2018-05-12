package thot.video.vlc;

import java.io.File;

import thot.exception.ThotException;
import thot.utils.Utilities;

/**
 * Outils autour de VLC.
 *
 * @author Fabrice Alleau
 */
public class VLCUtilities {

    /**
     * Donne le chemin complet de l'exécutable de VLC.
     *
     * @return le chemin de VLC.
     */
    public static File getVLC() throws ThotException {
        File file = null;
        if (Utilities.WINDOWS_PLATFORM) {
            file = getVLConWindows();
        } else if (Utilities.LINUX_PLATFORM) {
            file = getVLConLinux();
        } else if (Utilities.MAC_PLATFORM) {
            file = getVLConMac();
        }
        return file;
    }

    /**
     * Retourne le chemin de l'exécutable de VLC sous Windows.
     *
     * @return le chemin de VLC.
     */
    private static File getVLConWindows() throws ThotException {
        String path = getVLCpathOnWindows();
        return (path == null) ? null : new File(path, "vlc.exe");
    }

    /**
     * Retourne le chemin du répertoire de VLC sous Windows.
     *
     * @return le chemin du répertoire de VLC.
     */
    public static String getVLCpathOnWindows() throws ThotException {
        String path = null;
        String command = "reg query HKLM\\SOFTWARE\\VideoLAN\\VLC /v InstallDir";
        StringBuilder result = new StringBuilder(1024);
        StringBuilder error = new StringBuilder(1024);

        Utilities.executeCommand("reg query", result, error, command);

        String[] splitResult = result.toString().split("REG_SZ");

        if (splitResult.length == 1) {
            command = "reg query HKLM\\SOFTWARE\\Wow6432Node\\VideoLAN\\VLC /v InstallDir";
            result = new StringBuilder(1024);
            error = new StringBuilder(1024);

            Utilities.executeCommand("reg query", result, error, command);

            splitResult = result.toString().split("REG_SZ");
        }

        if (splitResult.length > 1) {
            path = splitResult[splitResult.length - 1].trim();
        }

        return path;
    }


    /**
     * Retourne le chemin de l'exécutable de VLC sous Linux.
     *
     * @return le chemin de VLC.
     */
    private static File getVLConLinux() throws ThotException {
        return Utilities.getApplicationPathOnLinux("vlc");
    }

    /**
     * Retourne le chemin de l'exécutable de VLC sous Mac.
     *
     * @return le chemin de VLC.
     */
    private static File getVLConMac() {
        return new File(getVLCpathOnMac(), "VLC");
    }

    /**
     * Retourne le chemin du répertoire de VLC sous Mac.
     *
     * @return le chemin du répertoire de VLC.
     */
    public static String getVLCpathOnMac() {
        return "/Applications/VLC.app/Contents/MacOS";
    }
}
