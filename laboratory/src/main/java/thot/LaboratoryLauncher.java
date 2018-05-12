package thot;

import java.io.File;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thot.exception.ThotException;
import thot.gui.GuiUtilities;
import thot.gui.LaboratoryFrame;
import thot.gui.Resources;
import thot.supervision.CommandXMLUtilities;
import thot.utils.Constants;
import thot.utils.ThotPort;
import thot.utils.Utilities;
import thot.video.Converter;
import thot.video.vlc.VLCconverter;

/*
 * resources:
 *  parameterError (%s), pathError (%s), singleInstanceError
 */

/**
 * Student est la classe représentant un labo de langue du coté élève.
 *
 * @author Fabrice Alleau
 * @version 1.8.4
 */
public class LaboratoryLauncher {

    /**
     * Instance de log.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(LaboratoryLauncher.class);

    /**
     * Initialisation et lancement de l'application.
     *
     * @param args les paramètres de lancement.
     */
    public static void main(String[] args) {
        GuiUtilities.setDefaultFont("Arial");

        boolean microphone = !Utilities.LINUX_PLATFORM;

        LOGGER.info("version: 1.90.00");

        File userHome = new File(System.getProperty("user.home"), Constants.softNamePath);
        userHome.mkdirs();

        Locale language;
        File languageFile = new File(userHome, "language.xml");
        if (languageFile.exists()) {
            String defaultLanguage = CommandXMLUtilities.getLanguage(languageFile);
            if (defaultLanguage != null) {
                language = new Locale(defaultLanguage);
                GuiUtilities.setDefaultLocale(language);
            }
        }
        Resources resources = new Resources("laboratory");

        try {
            for (int i = 0; i < args.length; i++) {
                switch (args[i]) {
                    case "--help":
                    case "-h":
                        printUsage();
                        System.exit(0);
                        break;
                    case "--microphone":
                        microphone = Utilities.parseStringAsBoolean(args[i + 1]);
                        LOGGER.info("paramètre --microphone: " + microphone);
                        break;
                }
            }
        } catch (Exception e) {
            showMessage(String.format(resources.getString("parameterError"), e.getMessage()));
            printUsage();
            System.exit(0);
        }

        File vlc = null;
        try {
            vlc = VLCconverter.getVLC();
            if (vlc == null || !vlc.exists()) {
                showMessage(String.format(resources.getString("pathError"), vlc));
                System.exit(0);
            }
        } catch (ThotException e) {
            showMessage("Impossible de charger la librairie de VLC", e);
        }


        Converter converter = new VLCconverter(vlc, ThotPort.launcherPort);

        if (Utilities.isBusyPort(ThotPort.masterToStudentLaboPort)) {
            showMessage(resources.getString("singleInstanceError"));
            System.exit(0);
        }

        LaboratoryCore studentCore = new LaboratoryCore(converter, userHome);
        try {
            studentCore.initValues(resources, microphone);
            LaboratoryFrame frame = new LaboratoryFrame(studentCore, resources, userHome);
            frame.showApplication();
        } catch (ThotException e) {
            showMessage("Impossible d'initinialiser l'application", e);
            System.exit(0);
        }

    }

    /**
     * Affiche un message à l'écran.
     *
     * @param message le message à afficher.
     * @param params les objets contenus dans le message.
     */
    private static void showMessage(String message, Object... params) {
        LOGGER.error(message, params);
        GuiUtilities.showMessage(String.format(message.replace("{}", "%s"), params));
    }

    private static void printUsage() {
        String usage = "Options :\n"
                + "--microphone :\n"
                + "\t utilisation directe (true) ou indirecte du microphone (false)\n"
                + "\t par défaut true sous Windows et false sous Linux\n";
        showMessage(usage);
    }
}
