/*
 * ClassManager - Supervision de classes et Laboratoire de langue
 * Copyright (C) 2013 Fabrice Alleau <fabrice.alleau@siclic.fr>
 *
 * This file is part of ClassManager.
 *
 * ClassManager is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * ClassManager is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with ClassManager.  If not, see <http://www.gnu.org/licenses/>.
 */
package thot;

import java.awt.*;
import java.io.File;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thot.gui.GuiUtilities;
import thot.gui.Resources;
import thot.gui.ToolsDialog;
import thot.model.Constants;
import thot.model.ThotPort;
import thot.utils.Utilities;
import thot.utils.XMLUtilities;
import thot.voip.Voip;

/**
 * @author Fabrice Alleau
 * @version 1.90
 */
public class Launcher {
    /*
     * Resources textes : singleInstanceError, soundError, networkError
     */

    /**
     * Instance de log.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Launcher.class);

    /**
     * Lancement de l'application.
     *
     * @param args les paramètres.
     */
    public static void main(String args[]) {
        GuiUtilities.setDefaultFont("Arial");

        boolean microphone = !Utilities.LINUX_PLATFORM;
        String multicastIP = Constants.DEFAULT_MULTICAST_IP;

        LOGGER.info("version: 1.90.00");

        File path = new File(Utilities.getApplicationPath(Launcher.class), "../");
        File laboratoryPath = new File("./bin/laboratory.jar");
        File userHome = new File(System.getProperty("user.home"), Constants.softNamePath);
        userHome.mkdirs();

        Locale language;
        File languageFile = new File(userHome, "language.xml");
        if (languageFile.exists()) {
            String defaultLanguage = XMLUtilities.getLanguage(languageFile);
            if (defaultLanguage != null) {
                language = new Locale(defaultLanguage);
                GuiUtilities.setDefaultLocale(language);
            }
        }
        Resources resources = new Resources("thot.gui.resources.student");

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

        Voip voip;
        if (microphone) {
            voip = new Voip(ThotPort.audioPort, ThotPort.audioPairingPort);
        } else {
            voip = new Voip(ThotPort.audioPort, ThotPort.audioPairingPort,
                    ThotPort.soundServerPort, ThotPort.microphonePort);
        }

        if (!voip.isLinesOpen()) {
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
        StringBuilder stringBuilder = new StringBuilder(1024);
        stringBuilder.append("Options :\n");

        stringBuilder.append("--multicast :\n");
        stringBuilder.append("\t adresse multicast pour la découverte\n");
        stringBuilder.append("\t par défaut \"228.5.6.7\"\n");
        stringBuilder.append("--path :\n");
        stringBuilder.append("\t chemin d'installation\n");
        stringBuilder.append("\t par défaut \".\"\n");
        stringBuilder.append("\t ie: \"C:\\Program Files\\Siclic\\classManager\"\n");
        stringBuilder.append("--microphone :\n");
        stringBuilder.append("\t utilisation directe (true) ou indirecte du microphone (false)\n");
        stringBuilder.append("\t par défaut true sous Windows et false sous Linux\n");

        showMessage(stringBuilder.toString());
    }
}
