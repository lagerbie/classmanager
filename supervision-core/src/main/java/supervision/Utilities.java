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
package supervision;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import java.net.InetAddress;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.UnknownHostException;

import java.util.Enumeration;

/**
 * Utilitaires divers.
 *
 * @author Fabrice Alleau
 * @version 1.90
 */
public class Utilities {

    /**
     * Vérification si la plateforme est Linux.
     */
    public static final boolean LINUX_PLATFORM
            = System.getProperty("os.name").toLowerCase().contains("linux");
    /**
     * Vérification si la plateforme est Windows.
     */
    public static final boolean WINDOWS_PLATFORM
            = System.getProperty("os.name").toLowerCase().contains("windows");
    /**
     * Vérification si la plateforme est Machintoch.
     */
    public static final boolean MAC_PLATFORM
            = System.getProperty("os.name").toLowerCase().contains("mac");

    /**
     * Nom du charset pour le format UTF-8.
     */
    public static final String UTF8_CHARSET = "UTF-8";
    /**
     * Nom du charset pour le format des fenêtres DOS.
     */
    public static final String DOS_CHARSET = "IBM850";
    /**
     * Nom du charset pour le format par défaut de Windows.
     */
    public static final String WINDOWS_CHARSET = "windows-1252";

    /**
     * Parse la chaîne de caractères comme un entier (int).
     *
     * @param value la chaîne de caractères.
     * @return l'entier correspondant ou -1.
     */
    public static int parseStringAsInt(String value) {
        int parseValue = -1;
        if (value != null) {
            try {
                parseValue = Integer.parseInt(value);
            } catch (NumberFormatException e) {
                CommonLogger.warning("parseStringAsInt: " + e.getMessage());
            }
        }
        return parseValue;
    }

    /**
     * Parse la chaîne de caractères comme un entier long (long).
     *
     * @param value la chaîne de caractères.
     * @return l'entier correspondant ou -1.
     */
    public static long parseStringAsLong(String value) {
        long parseValue = -1;
        if (value != null) {
            try {
                parseValue = Long.parseLong(value);
            } catch (NumberFormatException e) {
                CommonLogger.warning("parseStringAsLong: " + e.getMessage());
            }
        }
        return parseValue;
    }

    /**
     * Parse la chaîne de caractères comme un nombre (double).
     *
     * @param value la chaîne de caractères.
     * @return le nombre correspondant ou -1.
     */
    public static double parseStringAsDouble(String value) {
        double parseValue = -1;
        if (value != null) {
            try {
                parseValue = Double.parseDouble(value);
            } catch (NumberFormatException e) {
                CommonLogger.warning("parseStringAsDouble: " + e.getMessage());
            }
        }
        return parseValue;
    }

    /**
     * Parse la chaîne de caractères comme un booléen (boolean).
     *
     * @param value la chaîne de caractères.
     * @return le booléen correspondant (par défaut false).
     */
    public static boolean parseStringAsBoolean(String value) {
        boolean parseValue = false;
        if (value != null) {
            try {
                parseValue = Boolean.parseBoolean(value);
            } catch (NumberFormatException e) {
                CommonLogger.warning("parseStringAsBoolean: " + e.getMessage());
            }
        }
        return parseValue;
    }

    /**
     * Sauvegarde d'un texte brut dans un fichier.
     *
     * @param text le texte à sauvegarder.
     * @param file le fichier.
     * @return la réussite.
     */
    public static boolean saveText(String text, File file) {
        try (OutputStreamWriter writer = new OutputStreamWriter(
                new FileOutputStream(file), UTF8_CHARSET)) {
            // le texte doit être non null
            if (text == null) {
                writer.write("");
            } else {
                writer.write(text);
            }
        } catch (IOException e) {
            CommonLogger.error(e);
            return false;
        }

        return true;
    }

    /**
     * Indique si le fichier est une archive jar.
     *
     * @param file le fichier à tester.
     * @return <code>true</code> si le fichier est une archive jar.
     */
    public static boolean isJarFile(File file) {
        return file.getName().toLowerCase().endsWith(".jar");
    }

    /**
     * Efface tous les fichiers et répetoires contenus dans le répertoire
     * indiqué.
     *
     * @param directory le répartoire dont il faut effacer les fichiers.
     */
    public static void deteleFiles(File directory) {
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                deteleFiles(file);
            }

            if (!file.delete()) {
                file.deleteOnExit();
            }
        }
    }

    /**
     * Vérifie si un port est occupé.
     *
     * @param port le numéro du port.
     * @return true si le port est occupé.
     */
    public static boolean isBusyPort(int port) {
        boolean occuped = true;
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            occuped = false;
        } catch (IOException e) {
            CommonLogger.error(e);
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    CommonLogger.error(e);
                }
            }
        }

        return occuped;
    }

    /**
     * Envoi une commande xml à l'adresse et sur le port indiqués.
     *
     * @param xml la commande xml.
     * @param addressIP l'adresse d'envoi.
     * @param port le port d'envoi.
     * @return 1 si la commande a été transmise, sinon l'erreur (<0).
     */
    public static boolean sendXml(String xml, String addressIP, int port) {
        if (xml == null) {
            return false;
        }

        InetSocketAddress sockaddr = new InetSocketAddress(addressIP, port);
        Socket socket = new Socket();
        DataOutputStream outputStream;
        boolean send = false;
        try {
            CommonLogger.info("sendXML " + xml + " à " + addressIP + ":" + port);
            socket.connect(sockaddr, Constants.TIME_MAX_FOR_ORDER);

            outputStream = new DataOutputStream(socket.getOutputStream());
            outputStream.write(xml.getBytes(UTF8_CHARSET));
            //out.write((byte)0);
            outputStream.flush();
            send = true;
        } catch (IOException e) {
            CommonLogger.warning("erreur sendXML " + xml + " à " + addressIP
                    + ":" + port + "; message: " + e);
        } finally {
            //Fermeture
            try {
                socket.close();
            } catch (IOException e) {
                CommonLogger.error(e);
            }
        }
        return send;
    }

    /**
     * Execute une commande native.
     *
     * @param command la commande.
     * @param output un StringBuilder initialisé pour afficher la sortie standard.
     * @param error un StringBuilder initialisé pour afficher la sortie des erreur.
     * @return la valeur de sortie du processus résultat de la commande.
     */
    public static int executeCommand(String command,
            StringBuilder output, StringBuilder error) {
        int end = -1;
        Runtime runtime = Runtime.getRuntime();
        Process process = null;

        String charset = UTF8_CHARSET;
        if (WINDOWS_PLATFORM) {
            if (command.contains("cmd")) {
                charset = DOS_CHARSET;
            } else {
                charset = WINDOWS_CHARSET;
            }
        }

        try {
            process = runtime.exec(command);
            Thread outputThread = createReadThread(process.getInputStream(),
                    output, charset, command);
            Thread errorThread = createReadThread(process.getErrorStream(),
                    error, charset, command);
            outputThread.start();
            errorThread.start();

            try {
                end = process.waitFor();
                while (outputThread.isAlive()) {
                    waitInMillisecond(10);
                }
            } catch (InterruptedException e) {
                CommonLogger.error(e);
            }
        } catch (IOException e) {
            CommonLogger.error(e);
        }

        if (process != null) {
            process.destroy();
        }

        return end;
    }

    /**
     * Execute une commande native.
     *
     * @param command la commande.
     * @param output un StringBuilder initialisé pour afficher la sortie standard.
     * @param error un StringBuilder initialisé pour afficher la sortie des erreur.
     * @return la valeur de sortie du processus résultat de la commande.
     */
    public static int executeCommand(String[] command,
            StringBuilder output, StringBuilder error) {
        int end = -1;
        Runtime runtime = Runtime.getRuntime();
        Process process = null;

        String name = command[0];
        String charset = UTF8_CHARSET;
        if (WINDOWS_PLATFORM) {
            if (command[0].contains("cmd")) {
                charset = DOS_CHARSET;
                name = command[1];
            } else {
                charset = WINDOWS_CHARSET;
            }
        }

        try {
            process = runtime.exec(command);
            Thread outputThread = createReadThread(process.getInputStream(),
                    output, charset, name + " out");
            Thread errorThread = createReadThread(process.getErrorStream(),
                    error, charset, name + " err");
            outputThread.start();
            errorThread.start();

            try {
                end = process.waitFor();
                while (outputThread.isAlive()) {
                    waitInMillisecond(10);
                }
            } catch (InterruptedException e) {
                CommonLogger.error(e);
            }
        } catch (IOException e) {
            CommonLogger.error(e);
        }

        if (process != null) {
            process.destroy();
        }

        return end;
    }

    /**
     * Démarre une commande native.
     *
     * @param command la commande.
     * @param output un StringBuilder initialisé pour afficher la sortie standard.
     * @param error un StringBuilder initialisé pour afficher la sortie des erreur.
     * @return le processus démarré ou <code>null</code>.
     */
    public static Process startProcess(String command,
            StringBuilder output, StringBuilder error) {
        return startProcess(command, output, error, null);
    }

    /**
     * Démarre une commande native.
     *
     * @param command la commande.
     * @param output un StringBuilder initialisé pour afficher la sortie standard.
     * @param error un StringBuilder initialisé pour afficher la sortie des erreur.
     * @param workingDirectory le répertoire de travail.
     * @return le processus démarré ou <code>null</code>.
     */
    public static Process startProcess(String command,
            StringBuilder output, StringBuilder error, File workingDirectory) {
        Runtime runtime = Runtime.getRuntime();
        Process process;

        String charset = UTF8_CHARSET;
        if (WINDOWS_PLATFORM) {
            if (command.contains("cmd")) {
                charset = DOS_CHARSET;
            } else {
                charset = WINDOWS_CHARSET;
            }
        }

        try {
            process = runtime.exec(command, null, workingDirectory);
            Thread outputThread = createReadThread(process.getInputStream(),
                    output, charset, command);
            Thread errorThread = createReadThread(process.getErrorStream(),
                    error, charset, command);
            outputThread.start();
            errorThread.start();
        } catch (IOException e) {
            CommonLogger.error(e);
            return null;
        }

        return process;
    }

    /**
     * Démarre une commande native.
     *
     * @param command la commande.
     * @param output un StringBuilder initialisé pour afficher la sortie standard.
     * @param error un StringBuilder initialisé pour afficher la sortie des erreur.
     * @return le processus démarré ou <code>null</code>.
     */
    public static Process startProcess(String[] command,
            StringBuilder output, StringBuilder error) {
        Runtime runtime = Runtime.getRuntime();
        Process process;

        String name = command[0];
        String charset = UTF8_CHARSET;
        if (WINDOWS_PLATFORM) {
            if (command[0].contains("cmd")) {
                charset = DOS_CHARSET;
                name = command[1];
            } else {
                charset = WINDOWS_CHARSET;
            }
        }

        try {
            process = runtime.exec(command);
            Thread outputThread = createReadThread(process.getInputStream(),
                    output, charset, name + " out");
            Thread errorThread = createReadThread(process.getErrorStream(),
                    error, charset, name + " err");
            outputThread.start();
            errorThread.start();
        } catch (IOException e) {
            CommonLogger.error(e);
            return null;
        }

        return process;
    }

    /**
     * Créer une thread de traitement d'un flux.
     *
     * @param inputStream le flux à gérer.
     * @param output un StringBuilder initialisé pour afficher la sortie.
     * @return la thread de gestion du flux.
     */
    private static Thread createReadThread(final InputStream inputStream,
            final StringBuilder output, final String charset, String name) {
        Thread thread = new Thread(name) {
            @Override
            public void run() {
                byte[] data = new byte[1024];
                try {
                    int cnt = inputStream.read(data);
                    while (cnt > 0) {
                        output.append(new String(data, 0, cnt, charset));
                        cnt = inputStream.read(data);
                    }
                } catch (IOException e) {
                    CommonLogger.error(e);
                }
            }
        };
        return thread;
    }

    /**
     * Retourne le répertoire où est située le fichier associé à la classe.
     *
     * @param c la classe.
     * @return le répertoire où est situé le fichier associé à la classe.
     */
    public static String getApplicationPath(Class<?> c) {
        String classPath = c.getName().replace(".", "/") + ".class";
        URL url = ClassLoader.getSystemResource(classPath);// "test/Launcher.class"

        if (url == null) {
            return null;
        }

        String path = url.toString();
        //file:/C:/..../Launcher.class (si le fichier .jar n'existe pas)
        //jar:file:/C:/.../test.jar!/test/Launcher.class (Windows)
        //jar:file:/opt/.../test.jar!/test/Launcher.class (Linux)

        try {
            path = URLDecoder.decode(path, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            CommonLogger.error(e);
            return null;
        }

        int offset = 0;
        if (WINDOWS_PLATFORM) {
            offset = 1;
        }

        int begin = path.indexOf('/') + offset;
        int end = path.indexOf('!');

        try {
            if (end < 0) {//no jar
                end = path.lastIndexOf('/') + 1;
                path = path.substring(begin, end);
            } else {
                path = path.substring(begin, end);// = current jar
                end = path.lastIndexOf('/') + 1;
                path = path.substring(0, end);
            }
        } catch (IndexOutOfBoundsException e) {
            CommonLogger.error(e);
            return null;
        }

        return path;
    }

    /**
     * Retourne le chemin d'une application suivant son nom sous Linux.
     *
     * @param name le nom de l'application.
     * @return le chemin de l'application.
     */
    public static File getApplicationPathOnLinux(String name) {
        File file = null;
        String[] command = new String[]{"/bin/sh", "-c", "which " + name};
        StringBuilder input = new StringBuilder(1024);
        StringBuilder error = new StringBuilder(1024);

        executeCommand(command, input, error);
        if (!input.toString().isEmpty()) {
            file = new File(input.toString().trim());
        }

        return file;
    }

    /**
     * Fermeture des applications interdites.
     *
     * @param application le nom de l'application.
     */
    public static void killApplication(String application) {
        if (WINDOWS_PLATFORM) {
            killApplicationOnWindows(application);
        } else if (LINUX_PLATFORM) {
            killApplicationOnLinux(application);
        }
    }

    /**
     * Fermeture des applications interdites sous Linux.
     *
     * @param application le nom de l'application.
     */
    private static void killApplicationOnLinux(String application) {
        String[] command = new String[]{
            "/bin/sh", "-c", "pkill -f " + application};
        StringBuilder out = new StringBuilder(1024);
        StringBuilder err = new StringBuilder(1024);
        startProcess(command, out, err);
    }

    /**
     * Fermeture des applications interdites sous Windows.
     *
     * @param application le nom de l'application.
     */
    private static void killApplicationOnWindows(String application) {
        Runtime runtime = Runtime.getRuntime();
        try {
            CommonLogger.info("taskkill /F /IM " + application);
            runtime.exec("taskkill /F /IM " + application);
        } catch (IOException e) {
            CommonLogger.error(e);
        }
    }

    /**
     * Retourne la commande pour lancer JClic sous Windows.
     *
     * @return la commande pour lancer JClic sous Windows.
     */
    public static String getJClicCommand() {
        String command = "reg query HKLM\\SOFTWARE\\Classes\\JClic.install\\shell\\open\\command /ve";

        StringBuilder result = new StringBuilder(1024);
        StringBuilder error = new StringBuilder(1024);
        executeCommand(command, result, error);

        String[] splitResult = result.toString().split("REG_SZ");
        if (splitResult.length > 1) {
            return splitResult[splitResult.length - 1].trim();
        } else {
            return null;
        }
    }

    /**
     * Retourne la commande pour lancer JClicReports sous Windows.
     *
     * @return la commande pour lancer JClicReports sous Windows.
     */
    public static String getJClicReportsCommand() {
        String jclic = getJClicCommand();

        if (jclic == null) {
            return null;
        } else {
            return jclic.replace("jclic.jar", "jclicreports.jar");
        }
    }

    /**
     * Retourne le chemin absolu de l'application relative contenue dans les
     * "Program Files" en testant les répertoires 32 et 64bits.
     *
     * @param path le chemin relatif de l'application dans "Program Files".
     * @return le chemin absolu de l'application.
     */
    public static File pathOnWindowsProgramFiles(String path) {
        File file;
        StringBuilder out = new StringBuilder(1024);
        StringBuilder err = new StringBuilder(1024);

        //Récupère le chemin de "Program Files" pour un Windows 64bits
        executeCommand("cmd /c echo %ProgramW6432%", out, err);
        String progamPath = out.toString().trim();

        //Si Windows 32bits
        if (progamPath.startsWith("%")) {
            out = new StringBuilder(1024);
            err = new StringBuilder(1024);
            executeCommand("cmd /c echo %ProgramFiles%", out, err);
            progamPath = out.toString().trim();

            file = new File(progamPath, path);
        } else {
            //Windows 64bits
            file = new File(progamPath, path);

            //Si ce n'est pas dans le répertoire des programmes 64bits, on
            //recherche dans les programmes 32bits
            if (!file.exists()) {
                out = new StringBuilder(1024);
                err = new StringBuilder(1024);
                executeCommand("cmd /c echo %ProgramFiles(x86)%", out, err);
                progamPath = out.toString().trim();

                file = new File(progamPath, path);
            }
        }

        return file;
    }

    /**
     * Retourne le chemin relatif dans "Program Files" d'après le chemin absolu.
     *
     * @param file le chemin absolu.
     * @return le chemin relatif dans "Program Files" ou <code>null</code>
     * fichier.
     */
    public static String pathWithoutWindowsProgramFiles(File file) {
        String absolutePath = file.getAbsolutePath();
        String relativePath = null;
        StringBuilder out = new StringBuilder(1024);
        StringBuilder err = new StringBuilder(1024);

        //Récupère le chemin de "Program Files" pour un Windows 64bits
        executeCommand("cmd /c echo %ProgramW6432%", out, err);
        String progamPath = out.toString().trim();

        if (progamPath.startsWith("%")) {
            //cas Windows 32bits
            out = new StringBuilder(1024);
            err = new StringBuilder(1024);
            executeCommand("cmd /c echo %ProgramFiles%", out, err);
            progamPath = out.toString().trim();
            //si dans répertoire le Program Files
            if (absolutePath.startsWith(progamPath)) {
                relativePath = absolutePath.substring(progamPath.length());
            }
        } else {
            //test si c'est dans le répertoire Program Files 32bits
            out = new StringBuilder(1024);
            err = new StringBuilder(1024);
            executeCommand("cmd /c echo %ProgramFiles(x86)%", out, err);
            String progamPath32bits = out.toString().trim();

            //cas Windows 64bits
            if (absolutePath.startsWith(progamPath)
                    && !absolutePath.startsWith(progamPath32bits)) {
                //si dans le répertoire Program Files 64bits
                relativePath = absolutePath.substring(progamPath.length());
            } else if (absolutePath.startsWith(progamPath32bits)) {
                //si dans le répertoire Program Files 32bits
                relativePath = absolutePath.substring(progamPath32bits.length());
            }
        }

        return relativePath;
    }

    /**
     * Lancement d'un clavier virtuel.
     */
    public static void virtualKeyboard() {
        if (WINDOWS_PLATFORM) {
            windowsVirtualKeyboard();
        }
    }

    /**
     * Lancement d'un clavier virtuel sous Windows.
     */
    private static void windowsVirtualKeyboard() {
        String command = "cmd /C osk";

        StringBuilder result = new StringBuilder(1024);
        StringBuilder error = new StringBuilder(1024);
        startProcess(command, result, error);
    }

    /**
     * Retourne l'adresse IP sur le réseau.
     *
     * @return la première adresse IP valide sur le réseu connecté.
     */
    public static String getAddress() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();

                while (inetAddresses.hasMoreElements()) {
                    InetAddress inetAddress = inetAddresses.nextElement();

                    if (inetAddress instanceof Inet4Address
                            && !inetAddress.isLoopbackAddress()) {
                        CommonLogger.info("Nom Machine = " + inetAddress.getHostName());
                        CommonLogger.info("IP = " + inetAddress.getHostAddress());

                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            CommonLogger.error(e);
        }

        try {
            InetAddress localHost = InetAddress.getLocalHost();
            CommonLogger.info("Nom Machine = " + localHost.getHostName());
            CommonLogger.info("IP = " + localHost.getHostAddress());
            return localHost.getHostAddress();
        } catch (UnknownHostException e) {
            CommonLogger.error(e);
        }

        return null;
    }

    /**
     * Permet de suspendre une Thread pendant une certaine durée.
     *
     * @param millisecond la durée à attendre en millisecondes.
     */
    public static void waitInMillisecond(long millisecond) {
        try {
            Thread.sleep(millisecond);
        } catch (InterruptedException e) {
            CommonLogger.error(e);
        }
    }

    /**
     * Permet de suspendre une Thread pendant une certaine durée.
     *
     * @param nanoseconds la durée à attendre en nanosecondes.
     */
    public static void waitInNanosecond(long nanoseconds) {
        try {
            Thread.sleep(nanoseconds / 1000000, (int) (nanoseconds % 1000000));
        } catch (InterruptedException e) {
            CommonLogger.error(e);
        }
    }
}
