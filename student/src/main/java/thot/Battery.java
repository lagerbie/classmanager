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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thot.dll.WindowsUtilities;
import thot.utils.Utilities;

/**
 * Classe fournissant le niveau de batterie pour Windows, Linux et Mac.
 *
 * @author Fabrice Alleau
 * @version 1.90
 */
public class Battery {

    /**
     * Instance de log.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Battery.class);

    public static final int UNDEFINED_LEVEL = -1;

    /**
     * Retourne le niveau de la batterie.
     *
     * @return le niveau de la batterie de 0 à 100, ou -1 si undefined.
     */
    public static int getBatteryLevel() {
        if (Utilities.WINDOWS_PLATFORM) {
            return getBatteryLevelWindows();
        } else if (Utilities.LINUX_PLATFORM) {
            return getBatteryLevelLinux();
        } else if (Utilities.MAC_PLATFORM) {
            return getBatteryLevelMacOs();
        } else {
            return UNDEFINED_LEVEL;
        }
    }

    /**
     * Retourne le niveau de la batterie sous windows.
     *
     * @return le niveau de la batterie de 0 à 100, ou -1 si undefined.
     */
    private static int getBatteryLevelWindows() {
        return WindowsUtilities.getBatteryLevel();
    }

    /**
     * Retourne le niveau de la batterie sous Linux.
     *
     * @return le niveau de la batterie de 0 à 100, ou -1 si undefined.
     */
    private static int getBatteryLevelLinux() {
        int capacity;
        int current;

        File file;
        File dir = new File("/proc/acpi/battery/BAT0");
        if (dir.exists()) {
            file = new File("/proc/acpi/battery/BAT0/info");
        } else {
            file = new File("/proc/acpi/battery/BAT1/info");
        }

        if (!file.exists()) {
            return UNDEFINED_LEVEL;
        }

        byte[] data = new byte[8192];
        StringBuilder result = new StringBuilder(1024);

        try (FileInputStream fin = new FileInputStream(file)) {
            int cnt = fin.read(data);
            while (cnt > 0) {
                result.append(new String(data, 0, cnt));
                cnt = fin.read(data);
            }
        } catch (IOException e) {
            LOGGER.error("", e);
            return UNDEFINED_LEVEL;
        }

        String[] split = result.toString().split("last full capacity:|mAh");

        if (split.length > 2) {
            capacity = Utilities.parseStringAsInt(split[2].trim());
        } else {
            return UNDEFINED_LEVEL;
        }

        if (dir.exists()) {
            file = new File("/proc/acpi/battery/BAT0/state");
        } else {
            file = new File("/proc/acpi/battery/BAT1/state");
        }

        if (!file.exists()) {
            return UNDEFINED_LEVEL;
        }

        data = new byte[8192];
        result = new StringBuilder(1024);

        try (FileInputStream fin = new FileInputStream(file)) {
            int cnt = fin.read(data);
            while (cnt > 0) {
                result.append(new String(data, 0, cnt));
                cnt = fin.read(data);
            }
        } catch (IOException e) {
            LOGGER.error("", e);
            return UNDEFINED_LEVEL;
        }

        //Baterie chargée
        if (result.toString().contains("charged")) {
            return 100;
        }

        split = result.toString().split("remaining capacity:|mAh");

        if (split.length > 1) {
            current = Utilities.parseStringAsInt(split[1].trim());
        } else {
            return UNDEFINED_LEVEL;
        }

        double percent = (double) (current) / capacity * 100;
        if (percent > 100) {
            percent = 100;
        }

        return (int) percent;
    }

    /**
     * Retourne le niveau de la batterie sous Mac.
     *
     * @return le niveau de la batterie de 0 à 100, ou -1 si undefined.
     */
    private static int getBatteryLevelMacOs() {
        int level = UNDEFINED_LEVEL;
        StringBuilder result = new StringBuilder(1024);
        StringBuilder error = new StringBuilder(1024);
        Utilities.executeCommand("pmset", "pmset -g ps", result, error);

        if (result.toString().contains("charged")) {
            return 100;
        }

        String[] split = result.toString().split("-InternalBattery-0|%");
        if (split.length > 1) {
            String percent = split[1].trim();
            level = (int) Utilities.parseStringAsDouble(percent);
        }
        return level;
    }
}
