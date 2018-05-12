package thot;

import java.awt.*;
import java.io.File;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thot.exception.ThotException;
import thot.gui.GuiUtilities;
import thot.gui.Resources;
import thot.supervision.CommandXMLUtilities;
import thot.supervision.StudentCore;
import thot.supervision.gui.ToolsDialog;
import thot.supervision.voip.Voip;
import thot.utils.Constants;
import thot.utils.ThotPort;
import thot.utils.Utilities;

/**
 * @author Fabrice Alleau
 * @version 1.8.4
 */
public class StudentLauncher {
    /*
     * Resources textes : singleInstanceError, soundError, networkError
     */

    /**
     * Instance de log.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(StudentLauncher.class);

    /**
     * Lancement de l'application.
     *
     * @param args les paramètres.
     */
    public static void main(String args[]) {
        GuiUtilities.setDefaultFont("Arial");

        boolean microphone = !Utilities.LINUX_PLATFORM;
        String multicastIP = Constants.DEFAULT_MULTICAST_IP;

        LOGGER.info("version: 1.8.4");

        File path = new File(Utilities.getApplicationPath(StudentLauncher.class), "../");
        File laboratoryPath = new File("./bin/laboratory.jar");
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
        Resources resources = new Resources("student");

        try {
            for (int i = 0; i < args.length; i++) {
                switch (args[i]) {
                    case "--help":
                    case "-h":
                        printUsage();
                        System.exit(0);
                        break;
                    case "--multicast":
                        multicastIP = args[i + 1];
                        LOGGER.info("paramètre --multicast: " + multicastIP);
                        break;
                    case "--path":
                        if (!new File(args[i + 1]).exists()) {
                            throw new IllegalArgumentException(
                                    "pathError" + args[i + 1] + " : ");
                        }
                        path = new File(args[i + 1]);
                        LOGGER.info("paramètre --path: " + path);
                        break;
                    case "--microphone":
                        microphone = Utilities.parseStringAsBoolean(args[i + 1]);
                        LOGGER.info("paramètre --microphone: " + microphone);
                        break;
                }
            }
        } catch (Exception e) {
            printUsage();
            System.exit(0);
        }

        if (!laboratoryPath.exists()) {
            laboratoryPath = new File(path, laboratoryPath.getPath());
        }
        LOGGER.info("laboratoryPath: " + laboratoryPath);

        if (Utilities.isBusyPort(ThotPort.masterToStudentPort)) {
            showMessage(resources.getString("singleInstanceError"));
            System.exit(0);
        }

        if (Utilities.getAddress() == null) {
            GuiUtilities.showModelessMessage(resources.getString("networkError"));
        }

        Voip voip = new Voip(ThotPort.audioPort, ThotPort.audioPairingPort);
        try {
            if (microphone) {
                voip.initDirectMode();
            } else {
                voip.initIndirectMode(ThotPort.soundServerPort, ThotPort.microphonePort);
            }
        } catch (ThotException e) {
            GuiUtilities.showModelessMessage(resources.getString("soundError"));
        }

        StudentCore core = new StudentCore(resources, multicastIP, voip);
        if (laboratoryPath.exists()) {
            core.setLaboratoryPath(laboratoryPath);
        }

        ToolsDialog toolsDialog = new ToolsDialog(core, resources);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        toolsDialog.setLocation((dim.width - toolsDialog.getWidth()) / 2, 10);
        toolsDialog.setState(ToolsDialog.ICONIFIED);
        core.addListener(toolsDialog);

        core.start();
        toolsDialog.setVisible(true);
    }

    /**
     * Affiche un message bloquant.
     *
     * @param message le message.
     */
    private static void showMessage(String message) {
        GuiUtilities.showMessage(message);
    }

    /**
     * Affiche les options d'usage.
     */
    private static void printUsage() {
        String usage = "Options :\n"
                + "--multicast :\n"
                + "\t adresse multicast pour la découverte\n"
                + "\t par défaut \"228.5.6.7\"\n"
                + "--path :\n"
                + "\t chemin d'installation\n"
                + "\t par défaut \".\"\n"
                + "\t ie: \"C:\\Program Files\\Siclic\\classManager\"\n"
                + "--microphone :\n"
                + "\t utilisation directe (true) ou indirecte du microphone (false)\n"
                + "\t par défaut true sous Windows et false sous Linux\n";
        showMessage(usage);
    }
}
