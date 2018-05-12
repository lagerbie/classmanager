package thot.supervision;

import java.io.File;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thot.exception.ThotException;
import thot.gui.GuiUtilities;
import thot.gui.Resources;
import thot.supervision.com.StudentSearch;
import thot.supervision.com.StudentServer;
import thot.supervision.gui.MainFrame;
import thot.supervision.voip.Voip;
import thot.utils.Constants;
import thot.utils.ThotPort;
import thot.utils.Utilities;
import thot.video.Converter;
import thot.video.vlc.VLCconverter;

/**
 * Lancement de la supervision professeur.
 *
 * @author Fabrice Alleau
 * @version 1.8.4
 */
public class MasterLauncher {
    /*
     * Resources textes : pathError, parameterError, converterError,
     *                    singleInstanceError, networkError, soundError
     */

    /**
     * Instance de log.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MasterLauncher.class);

    /**
     * Noyau de l'application.
     */
    private static MasterCore core;
    private static int nbLicences = 30;

    public static void main(String[] args) {
        GuiUtilities.setDefaultFont("Arial");

        boolean microphone = !Utilities.LINUX_PLATFORM;

        LOGGER.info("version 1.91.00");

        int verbose;
        boolean debug = false;
        int quality = 80;
        int fps = 20;
        int nbLines = 32;
        int timeout = 100;
        int mosaiqueTimeout = 100;
        long mosaiqueDelay = 30;
        double mosaiqueFps = 20;
        int studentTimeout = 100;
        String multicastIP = Constants.DEFAULT_MULTICAST_IP;

        File path = new File(Utilities.getApplicationPath(MasterLauncher.class), "../");
        File thumbPath = new File("./bin/thumb.jar");

        final File userHome = new File(System.getProperty("user.home"), Constants.softNamePath);
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
        Resources resources = new Resources("master");

        try {
            for (int i = 0; i < args.length; i++) {
                String parameter = args[i];
                switch (parameter) {
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
                                    String.format(resources.getString("pathError"), args[i + 1]));
                        }
                        path = new File(args[i + 1]);
                        LOGGER.info("paramètre --path: " + path);
                        break;
                    case "--quality":
                        quality = Utilities.parseStringAsInt(args[i + 1]);
                        if (quality < 1 || quality > 100) {
                            throw new IllegalArgumentException("quality range 1 to 100");
                        }
                        LOGGER.info("paramètre --quality: " + quality);
                        break;
                    case "--fps":
                        fps = Utilities.parseStringAsInt(args[i + 1]);
                        if (fps < 1 || fps > 40) {
                            throw new IllegalArgumentException("frames per second range 1 to 40");
                        }
                        LOGGER.info("paramètre --fps: " + fps);
                        break;
                    case "--lines":
                        nbLines = Utilities.parseStringAsInt(args[i + 1]);
                        if (nbLines < 1 || nbLines > 64) {
                            throw new IllegalArgumentException("frames per second range 1 to 64");
                        }
                        LOGGER.info("paramètre --lines: " + nbLines);
                        break;
                    case "--timeout":
                        timeout = Utilities.parseStringAsInt(args[i + 1]);
                        if (timeout < 0 || timeout > 2000) {
                            throw new IllegalArgumentException("timeout range 0 to 2000 ms");
                        }
                        LOGGER.info("paramètre --timeout: " + timeout);
                        break;
                    case "--mosaiqueTimeout":
                        mosaiqueTimeout = Utilities.parseStringAsInt(args[i + 1]);
                        if (mosaiqueTimeout < 0 || mosaiqueTimeout > 2000) {
                            throw new IllegalArgumentException("mosaiqueTimeout range 0 to 2000 ms");
                        }
                        LOGGER.info("paramètre --mosaiqueTimeout: " + mosaiqueTimeout);
                        break;
                    case "--mosaiqueDelay":
                        mosaiqueDelay = Utilities.parseStringAsInt(args[i + 1]);
                        if (mosaiqueDelay < 0 || mosaiqueDelay > 2000) {
                            throw new IllegalArgumentException("mosaiqueDelay range 0 to 2000 ms");
                        }
                        LOGGER.info("paramètre --mosaiqueDelay: " + mosaiqueDelay);
                        break;
                    case "--mosaiqueFps":
                        mosaiqueFps = Utilities.parseStringAsDouble(args[i + 1]);
                        if (mosaiqueFps < 0.1 || mosaiqueFps > 40) {
                            throw new IllegalArgumentException("frames per second range 0.1 to 40");
                        }
                        LOGGER.info("paramètre --mosaiqueFps: " + mosaiqueFps);
                        break;
                    case "--studentTimeout":
                        studentTimeout = Utilities.parseStringAsInt(args[i + 1]);
                        if (studentTimeout < 0 || studentTimeout > 2000) {
                            throw new IllegalArgumentException("studentTimeout range 0 to 2000 ms");
                        }
                        LOGGER.info("paramètre --studentTimeout: " + studentTimeout);
                        break;
                    case "--language":
                        String languageID = args[i + 1];
                        if (languageID.length() != 2) {
                            throw new IllegalArgumentException("language code ISO 639-2");
                        }
                        LOGGER.info("paramètre --language: " + languageID);
                        language = new Locale(languageID);
                        GuiUtilities.setDefaultLocale(language);
                        resources.updateLocale(language);
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
                showMessage(String.format(resources.getString("converterError"), "vlc", vlc));
                System.exit(0);
            }
        } catch (ThotException e) {
            showMessage("Impossible de charger la librairie de VLC", e);
        }

        if (!thumbPath.exists()) {
            thumbPath = new File(path, thumbPath.getPath());
            if (!thumbPath.exists()) {
                showMessage(String.format(resources.getString("pathError"), thumbPath));
                printUsage();
                System.exit(0);
            }
        }

        if (Utilities.isBusyPort(ThotPort.studentToMasterPort)) {
            showMessage(resources.getString("singleInstanceError"));
            System.exit(0);
        }

        if (Utilities.getAddress() == null) {
            String message = resources.getString("networkError");
            GuiUtilities.showMessageDialog(null, message);
        }

        Voip voip;
        if (microphone) {
            voip = new Voip(ThotPort.audioPortMaster, ThotPort.audioPairingPortMaster);
        } else {
            voip = new Voip(ThotPort.audioPort, ThotPort.audioPairingPort, ThotPort.soundServerPort,
                    ThotPort.microphoneMasterPort);
        }
        if (!voip.isLinesOpen()) {
            String message = resources.getString("soundError");
            GuiUtilities.showMessageDialog(null, message);
        }

        core = new MasterCore(nbLicences, resources, thumbPath, voip);
        core.setParameters(quality, fps, nbLines);
        core.setTimeout(timeout);
        core.setMosaiqueParameters(mosaiqueTimeout, mosaiqueDelay, mosaiqueFps);
        core.setSendStudentTimeout(studentTimeout);

        boolean laboratory = false;
        if (laboratory) {
            Converter converter = new VLCconverter(vlc, ThotPort.launcherPort);
//            LaboModule classeLabo = new LaboModule(resources, converter, core);
//            core.setLaboModule(classeLabo);
        }

        File wavFile = new File(path, "appel.wav");
        StudentServer studentServer = new StudentServer(core, ThotPort.studentToMasterPort, wavFile);
        StudentSearch studentSearch = new StudentSearch(multicastIP, ThotPort.multicastPort);

        if (debug) {
            ReglageDialog dialog = new ReglageDialog(core);
            dialog.showDialog();
        }

        MainFrame frame = new MainFrame(core, resources, userHome);
        core.addListener(frame);
        core.start();
        studentSearch.start();
        studentServer.start();
        frame.setVisible(true);

//        Student student = new Student("ip1", 1);
//        frame.studentAdded(student);

//        Student student;
//        for(int i=1; i<18; i++) {
//            student = new Student("eleve "+i, 0);
//            frame.studentAdded(student);
//        }
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
        StringBuilder stringBuilder = new StringBuilder(1024);
        stringBuilder.append("Options :\n");

        stringBuilder.append("--multicast :\n");
        stringBuilder.append("\t adresse multicast pour la découverte\n");
        stringBuilder.append("\t par défaut \"228.5.6.7\"\n");

        stringBuilder.append("--path :\n");
        stringBuilder.append("\t répertoire de l'application\n");
        stringBuilder.append("\t nécessaire pour le fichier appel.wav\n");
        stringBuilder.append("\t par défaut \".\"\n");
        stringBuilder.append("\t ie: \"C:\\Program Files\\Siclic\"\n");

        stringBuilder.append("--quality :\n");
        stringBuilder.append("\t qualité pour les envois d'écran (compris entre 1 et 100)\n");
        stringBuilder.append("\t par défaut 80 (Haute qualité)\n");
        stringBuilder.append("--fps :\n");
        stringBuilder.append("\t nombre de frames par seconde (compris entre 1 et 40)\n");
        stringBuilder.append("\t par défaut 20\n");
        stringBuilder.append("--lines :\n");
        stringBuilder.append("\t nombre de lignes pour l'envoi d'écran (compris entre 1 et 64)\n");
        stringBuilder.append("\t par défaut 32\n");

        stringBuilder.append("--timeout :\n");
        stringBuilder.append("\t temps d'attente général pour les envois d'écran (compris entre 0 et 2000ms)\n");
        stringBuilder.append("\t par défaut 100ms\n");
        stringBuilder.append("--mosaiqueTimeout :\n");
        stringBuilder.append("\t temps d'attente pour les envois d'écran en mosaique (compris entre 0 et 2000ms)\n");
        stringBuilder.append("\t par défaut 100ms\n");
        stringBuilder.append("--mosaiqueDelay :\n");
        stringBuilder
                .append("\t délai entre les envois des ordres pour l'affichage de la mosaique (compris entre 0 et 2000ms)\n");
        stringBuilder.append("\t par défaut 30ms\n");
        stringBuilder.append("--studentTimeout :\n");
        stringBuilder.append("\t temps d'attente pour l'envoi d'écran élève (compris entre 0 et 2000ms)\n");
        stringBuilder.append("\t par défaut 100ms\n");

        stringBuilder.append("--language :\n");
        stringBuilder.append("\t code ISO 639-2 de la langue\n");
//        stringBuilder.append("\t ca pour le Catalan\n");
        stringBuilder.append("\t de pour l'Allemand\n");
        stringBuilder.append("\t en pour l'Anglais\n");
        stringBuilder.append("\t es pour l'Espagnol\n");
//        stringBuilder.append("\t eu pour le Basque\n");
        stringBuilder.append("\t fr pour le Français\n");
        stringBuilder.append("\t it pour l'Italien\n");
        stringBuilder.append("\t par défaut la langue de l'OS\n");

        stringBuilder.append("--microphone :\n");
        stringBuilder.append("\t utilisation directe (true) ou indirecte du microphone (false)\n");
        stringBuilder.append("\t par défaut true sous Windows et false sous Linux\n");
        stringBuilder.append("--verbose :\n");
        stringBuilder.append("\t level pour les info (compris entre 0 et 3)\n");
        stringBuilder.append("\t par défaut 0. 0=error, 1=warning, 2=info, 3=debug\n");

        showMessage(stringBuilder.toString());
    }
}
