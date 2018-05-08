package eestudio;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import javax.swing.*;

import eestudio.flash.FlashConstants;
import eestudio.flash.FlashCore;
import eestudio.gui.GuiFlashResource;
import eestudio.gui.Resources;
import eestudio.utils.Converter;
import eestudio.utils.MEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thot.gui.GuiUtilities;
import thot.supervision.CommandXMLUtilities;
import thot.utils.Utilities;

/**
 * Initialisation et lancement de l'application.
 *
 * @author Fabrice Alleau
 */
public class EeStudioLauncher {

    /**
     * Instance de log.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(EeStudioLauncher.class);

    private static final String version = "1.03.01";
    private static Core core = null;

    /**
     * Initialisation et lancement de la'application.
     *
     * @param args les paramètres de lancement.
     *
     * @since version 0.94 - version 1.02
     */
    public static void main(String[] args) {
        ArrayList<String> versions = new ArrayList<>(2);
        versions.add("eeStudio");
        versions.add(version);

//        boolean microphone = !Constants.LINUX_PLATFORM;

        LOGGER.info("eeStudio version:" + version);

        File path = new File(System.getProperty("java.home"), "../../");
        File flash = new File("./eeStudioGui.exe");
        File exe = new File("./eeStudio.exe");
        File mencoder = new File("../MPlayer/mencoder.exe");
        File mplayer = new File("../MPlayer/mplayer.exe");
        File filever = new File("./filever.exe");
        File userHome = new File(System.getProperty("user.home"), ".eeStudio");
        userHome.mkdirs();

        if (Utilities.LINUX_PLATFORM) {
            mplayer = Utilities.getApplicationPathOnLinux("mplayer");
            mencoder = Utilities.getApplicationPathOnLinux("mencoder");
        }

        Locale language = Locale.getDefault();
        File languageFile = new File(userHome, "language.xml");
        if (languageFile.exists()) {
            String defaultLanguage = CommandXMLUtilities.getLanguage(languageFile);
            if (defaultLanguage != null) {
                language = new Locale(defaultLanguage);
                GuiUtilities.setDefaultLocale(language);
            }
        }
        Resources resources = new Resources();

        File currentFile = null;
        try {
            for (int i = 0; i < args.length; i += 2) {
                String parameter = args[i];
                if (parameter.contentEquals("--help") || parameter.contentEquals("-h")) {
                    printUsage();
                    System.exit(0);
                } else if (parameter.contentEquals("--path")) {
                    if (!new File(args[i + 1]).exists()) {
                        throw new IllegalArgumentException(
                                String.format(resources.getString("pathError"), args[i + 1]));
                    }
                    path = new File(args[i + 1]);
                    LOGGER.info("paramètre --path: " + path);
                } else {
                    if (new File(parameter).exists()) {
                        currentFile = new File(parameter);
                        LOGGER.info("paramètre file: " + currentFile);
                    }
                }
            }
        } catch (Exception e) {
            showMessage(String.format(resources.getString("parameterError"), e.getMessage()));
            printUsage();
            System.exit(0);
        }

        if (!filever.exists()) {
            filever = new File(path, filever.getPath());
            exe = new File(path, exe.getPath());
        }
        if (filever.exists()) {
            String exeVersion = getWindowsFileVersion(filever, exe);
            LOGGER.info(exe.getName() + " version:" + exeVersion);
            if (exeVersion != null) {
                versions.add(exe.getName());
                versions.add(exeVersion);
            }
        }

        if (!flash.exists()) {
            flash = new File(path, flash.getPath());
            if (!flash.exists()) {
                showMessage(String.format(resources.getString("flashError"), flash));
                printUsage();
                System.exit(0);
            }
        }

        if (!mencoder.exists()) {
            mencoder = new File(path, mencoder.getPath());
            if (!mencoder.exists()) {
                showMessage(String.format(resources.getString("mencoderError"), mencoder));
                printUsage();
                System.exit(0);
            }
        }

        if (!mplayer.exists()) {
            mplayer = new File(path, mplayer.getPath());
            if (!mplayer.exists()) {
                showMessage(String.format(resources.getString("mplayerError"), mplayer));
                printUsage();
                System.exit(0);
            }
        }

        if (Utilities.isBusyPort(FlashConstants.flashToCorePort)) {
            showMessage(resources.getString("singleInstanceError"));
            System.exit(0);
        }

        Utilities.killApplication(mencoder.getName());
        Utilities.killApplication(mplayer.getName());
        Utilities.killApplication(flash.getName());

        Converter converter = new MEncoder(mencoder, mplayer);

        try {
            core = new Core(converter);
        } catch (Exception e) {
            LOGGER.error("", e);
            core = null;
        }

        if (core == null) {
            showMessage(resources.getString("soundError"));
            System.exit(0);
        }

        FlashCore flashCore = new FlashCore(core, FlashConstants.flashToCorePort, FlashConstants.coreToFlashPort);
        GuiFlashResource guiResource = new GuiFlashResource(core, languageFile);
        flashCore.setMainFrame(guiResource);

        StringBuilder out = new StringBuilder(1024);
        StringBuilder err = new StringBuilder(1024);
        final Process process = Utilities.startProcess("flash", out, err, flash.getAbsolutePath());
        Thread flashThread = new Thread(() -> {
            try {
                process.waitFor();
            } catch (InterruptedException e) {
                LOGGER.error("", e);
            }
            core.closeApplication();
        });
        flashThread.start();

        guiResource.processBegin(false, "cacheTitle", "cacheMessage");
        converter.init();
        if (languageFile.exists()) {
            flashCore.sendLanguageToFlash(language);
        }
        if (currentFile != null) {
            guiResource.flashLoad(currentFile);
        }

        flashCore.sendVersionToFlash(versions);
    }

    /**
     * Récupère le numéro de version d'un fichier sous Windows.
     *
     * @param filever le chemin de l'exécutable "filever.exe"
     * @param file le fichier dont on veut le numéro de version.
     *
     * @return le numéro de version ou {@code null}.*
     */
    private static String getWindowsFileVersion(File filever, File file) {
        StringBuilder out = new StringBuilder(1024);
        StringBuilder err = new StringBuilder(1024);
        String command = "\"" + filever.getAbsolutePath() + "\" /EAD \"" + file.getAbsolutePath() + "\"";
        Utilities.executeCommand("getFileVersion", out, err, command);
        String[] split = out.toString().split("[ ]+");
        if (split.length > 3) {
            return split[3];
        }
        return null;
    }

    private static JFrame window = null;

    /**
     * Affiche un message à l'écran.
     *
     * @param message le message à afficher.
     */
    private static void showMessage(String message) {
        if (window == null) {
            window = new JFrame();
            window.setUndecorated(true);
            window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        }
        window.setAlwaysOnTop(true);
        window.setLocation(400, 330);
        window.setVisible(true);

        GuiUtilities.showMessageDialog(window, message);

        window.setVisible(false);
    }

    /**
     * Affichage des options disponibles.
     */
    private static void printUsage() {
        String usage = "Options :\n"
                + "--path :\n"
                + "\t répertoire de l'application\n"
                + "\t par défaut \".\"\n";
        showMessage(usage);
    }

}
