package eevision;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URLDecoder;
import java.util.Enumeration;

import javax.swing.*;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.sun.jna.Platform;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

/**
 * @author fabrice
 * @version 0.83
 */
public class Main {

    /**
     * Nom du charset pour le format UTF-8.
     */
    private static final String UTF8_CHARSET = "UTF-8";
    /**
     * Nom du charset pour le format des fenêtres DOS.
     */
    private static final String DOS_CHARSET = "IBM850";
    /**
     * Nom du charset pour le format par défaut de Windows.
     */
    private static final String WINDOWS_CHARSET = "windows-1252";


    private JLabel jvmStatut;
    private JLabel jvmInfo;

    private JLabel javaStatut;
    private JLabel javaInfo;

    private JLabel vlcStatut;
    private JLabel vlcInfo;

    private JLabel vlcAxStatut;
    private JLabel vlcAxInfo;

    private JLabel aossStatut;
    private JLabel aossInfo;

    private JLabel multicastStatut;
    private JLabel multicastInfo;

    private JLabel microStatut;
    private JLabel microInfo;


    private Process[] processes = new Process[8];
    private StringBuilder[] out = new StringBuilder[8];
    private StringBuilder[] err = new StringBuilder[8];


    public static void main(String[] args) {
        new Main();
        //launch("eevision.Test", "param param2");
    }

    private Main() {
        final JFrame frame = new JFrame("Vérifacation de prérequis");

        JPanel labelsPanel = new JPanel();
        JPanel statutPanel = new JPanel();
        JPanel infoPanel = new JPanel();
        infoPanel.setMinimumSize(new Dimension(200, 10));
        infoPanel.setPreferredSize(new Dimension(200, 200));

        labelsPanel.setLayout(new BoxLayout(labelsPanel, BoxLayout.Y_AXIS));
        statutPanel.setLayout(new BoxLayout(statutPanel, BoxLayout.Y_AXIS));
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));

        //nom machine virtuelle java
        labelsPanel.add(new JLabel("Nom de la machine virtuelle"));
        jvmStatut = new JLabel("--");
        statutPanel.add(jvmStatut);
        jvmInfo = new JLabel("");
        infoPanel.add(jvmInfo);
        jvmStatut.setMinimumSize(new Dimension(50, 20));
        jvmInfo.setMinimumSize(new Dimension(50, 20));
        jvmStatut.setPreferredSize(new Dimension(50, 20));
        jvmInfo.setPreferredSize(new Dimension(50, 20));

        //version de java
        labelsPanel.add(new JLabel("Version de Java"));
        javaStatut = new JLabel("--");
        statutPanel.add(javaStatut);
        javaInfo = new JLabel("");
        infoPanel.add(javaInfo);

        //version de VLC
        labelsPanel.add(new JLabel("Version de VLC"));
        vlcStatut = new JLabel("--");
        statutPanel.add(vlcStatut);
        vlcInfo = new JLabel();
        infoPanel.add(vlcInfo);

        //version de l'activeX de VLC pour windows
        vlcAxStatut = new JLabel("--");
        vlcAxInfo = new JLabel("");
        if (Platform.isWindows()) {
            labelsPanel.add(new JLabel("Version de axvlc.dll"));
            statutPanel.add(vlcAxStatut);
            infoPanel.add(vlcAxInfo);
        }

        //aoss pour linux
        aossStatut = new JLabel("--");
        aossInfo = new JLabel("");
        if (Platform.isLinux()) {
            labelsPanel.add(new JLabel("Présence de aoss"));
            statutPanel.add(aossStatut);
            infoPanel.add(aossInfo);
        }

        //multicast
        labelsPanel.add(new JLabel("Etat du multicast"));
        multicastStatut = new JLabel("--");
        statutPanel.add(multicastStatut);
        multicastInfo = new JLabel("");
        infoPanel.add(multicastInfo);

        //entrées microphones
        labelsPanel.add(new JLabel("Entrées microphones"));
        microStatut = new JLabel("--");
        statutPanel.add(microStatut);
        microInfo = new JLabel("");
        infoPanel.add(microInfo);

        labelsPanel.add(Box.createVerticalStrut(20));
        statutPanel.add(Box.createVerticalStrut(20));
        infoPanel.add(Box.createVerticalStrut(20));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
        mainPanel.add(Box.createHorizontalStrut(20));
        mainPanel.add(labelsPanel);
        mainPanel.add(Box.createHorizontalStrut(20));
        mainPanel.add(statutPanel);
        mainPanel.add(Box.createHorizontalStrut(20));
        mainPanel.add(infoPanel);
        mainPanel.add(Box.createHorizontalStrut(20));

        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.X_AXIS));
        JButton verife = new JButton("Vérification");
        menuPanel.add(verife);

        verife.addActionListener(e -> {
            verification();
            frame.pack();
        });

        frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.getContentPane().add(Box.createVerticalStrut(20));
        frame.getContentPane().add(mainPanel);
        frame.getContentPane().add(menuPanel);
        frame.getContentPane().add(Box.createVerticalStrut(20));

        frame.pack();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);

    }

    private void verification() {
        boolean javaVersionPass = false;
        boolean javaHotSpotPass = false;
        boolean vlcVersionPass = false;
        boolean axvlcVersionPass = false;
        boolean aossPass = false;
        boolean multicastPass = false;
        boolean microphonePass = false;
        boolean sameMicrophonePass = false;

        //nom de l'OS
        System.out.println("os name: " + System.getProperty("os.name"));

        //nom machine virtuelle java
        String jvmName = getJvmName();
        System.out.println("java virtual machine : " + jvmName);
        jvmInfo.setText(jvmName);
        if (jvmName.contains("Java HotSpot")) {
            javaHotSpotPass = true;
            jvmStatut.setText("OK");
        } else {
            jvmStatut.setText("NO");
        }

        //version de java
        String javaVersion = getJavaVersion();
        System.out.println("java version : " + javaVersion);
        javaInfo.setText(javaVersion);
        if (javaVersion.startsWith("1.8.")) {
            javaVersionPass = true;
            javaStatut.setText("OK");
        } else {
            javaStatut.setText("NO");
        }

        //version de VLC
        String vlcVersion = getVLCVersion();
        System.out.println("vlc version : " + vlcVersion);
        if (vlcVersion == null) {
            vlcInfo.setText("vlc not installed");
        } else {
            vlcInfo.setText(vlcVersion);
        }
        if (vlcVersion != null && vlcVersion.startsWith("1.")) {
            vlcVersionPass = true;
            vlcStatut.setText("OK");
        } else {
            vlcStatut.setText("NO");
        }

        //version de l'activeX de VLC pour windows
        if (Platform.isWindows()) {
            String vlcPath = getVLCpathOnWindows();
            if (vlcPath != null) {
                String axvlcVersion = getAxvlcVersion(vlcPath + "\\axvlc.dll");
                System.out.println("vlc activex version : " + axvlcVersion);
                if (axvlcVersion == null) {
                    vlcAxInfo.setText("axvlc.dll not installed");
                } else {
                    vlcAxInfo.setText(axvlcVersion);
                }
                if (axvlcVersion != null && axvlcVersion.contains("0.9.8a")) {
                    axvlcVersionPass = true;
                    vlcAxStatut.setText("OK");
                } else {
                    vlcAxStatut.setText("NO");
                }
            }
        }

        //aoss pour linux
        if (Platform.isLinux()) {
            String aossPath = getAossPath();
            System.out.println("aoss path : " + aossPath);
            aossInfo.setText(aossPath);
            if (aossPath.contains("aoss")) {
                aossPass = true;
                aossStatut.setText("OK");
            } else {
                aossStatut.setText("NO");
            }
        }

        String addressIP = getAddress();

        //multicast
        if (addressIP == null) {
            multicastInfo.setText("Pas de connection réseau");
            multicastStatut.setText("NO");
            System.out.println("Pas de connection réseau");
        } else {
            String multicastIP = "228.5.6.7";
            multicastPass = portMutlicastOpen(multicastIP, 3858);
            System.out.println("multicast operationel : " + multicastPass);
            if (multicastPass) {
                multicastStatut.setText("OK");
                multicastInfo.setText("Multicast opérationnel");
            } else {
                multicastStatut.setText("Multicast bloqué");
            }
        }

        long duration = 10000;

        launch(0, "eevision.TCPListener", "4001 " + String.valueOf(duration));
        waitInMillisecond(100);
        launch(1, "eevision.TCPListener", "4002 " + String.valueOf(duration));
        waitInMillisecond(1000);
        launch(2, "eevision.TCPSpeeker", "127.0.0.1 4001 " + String.valueOf(duration));
        waitInMillisecond(100);
        launch(3, "eevision.TCPSpeeker", "127.0.0.1 4002 " + String.valueOf(duration));

        waitInMillisecond(duration);

        for (int i = 0; i < 4; i++) {
            processes[i].destroy();
        }

        if (err[2].length() == 0 && err[3].length() == 0) {
            microphonePass = true;
        }

        if (getDifferences(out[2].toString(), out[3].toString()) == 0) {
            sameMicrophonePass = true;
        }

        if (microphonePass) {
            if (sameMicrophonePass) {
                microStatut.setText("OK");
                microInfo.setText(out[2].toString());
            } else {
                microStatut.setText("??");
                microInfo.setText("Nécessite un serveur pour le microphone");
            }
        } else {
            microStatut.setText("NO");
            microInfo.setText("pas d'entrée microphone");
        }

        System.out.println("Pass: ");
        System.out.println("JVM Pass: " + javaHotSpotPass);
        System.out.println("Java version Pass: " + javaVersionPass);
        System.out.println("VLC version Pass: " + vlcVersionPass);
        if (Platform.isWindows()) {
            System.out.println("VLC ActiveX version Pass: " + axvlcVersionPass);
        }
        System.out.println("two microphone line Pass: " + microphonePass);
        System.out.println("same microphone Pass: " + sameMicrophonePass);
        if (Platform.isLinux()) {
            System.out.println("aoss Pass: " + aossPass);
        }
    }

    private static String getJvmName() {
        return System.getProperty("java.vm.name");
    }

    private static String getJavaVersion() {
        return System.getProperty("java.runtime.version");
    }

    private static String getVLCVersion() {
        System.setProperty("jna.encoding", "UTF8");

        String vlcPath = null;
        String libraryName = "vlc";

        if (Platform.isWindows()) {
            libraryName = "libvlc";
            vlcPath = getVLCpathOnWindows();
        } else if (Platform.isMac()) {
            vlcPath = getVLCpathOnMac() + "/lib";
        }

        if (vlcPath != null) {
            NativeLibrary.addSearchPath(libraryName, vlcPath);
        }

        try {
            LibVLC INSTANCE = Native.loadLibrary(libraryName, LibVLC.class);
            LibVLC libVLC = (LibVLC) Native.synchronizedLibrary(INSTANCE);
            return libVLC.libvlc_get_version();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String getAxvlcVersion(String fileName) {
        try {
            LibFileVersion INSTANCE = Native.loadLibrary("version", LibFileVersion.class);
            LibFileVersion libFileVersion = (LibFileVersion) Native.synchronizedLibrary(INSTANCE);
            int size = libFileVersion.GetFileVersionInfoSizeA(fileName, null);
            byte[] fileVersion = new byte[size];
            libFileVersion.GetFileVersionInfoA(fileName, 0, size, fileVersion);

            PointerByReference data = new PointerByReference();
            IntByReference len = new IntByReference();
            libFileVersion.VerQueryValueA(fileVersion, "\\VarFileInfo\\Translation", data, len);

            byte[] code = data.getValue().getByteArray(0, len.getValue());
            String lang_page = String.format("%1$02x%2$02x%3$02x%4$02x", code[1], code[0], code[3], code[2]);
            System.out.println("lang_page: " + lang_page);

            int bool = libFileVersion
                    .VerQueryValueA(fileVersion, "\\StringFileInfo\\" + lang_page + "\\FileVersion", data, len);
            if (bool == 0) {
                bool = libFileVersion.VerQueryValueA(fileVersion, "\\StringFileInfo\\040904E4\\FileVersion", data, len);
            }

            if (bool == 0) {
                return null;
            } else {
                return data.getValue().getString(0);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private String getAossPath() {
        return executeCommand("which aoss").trim();
    }

    private String getAddress() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();

                while (inetAddresses.hasMoreElements()) {
                    InetAddress inetAddress = inetAddresses.nextElement();

                    if (inetAddress instanceof Inet4Address && !inetAddress.isLoopbackAddress()) {
                        System.out.println("Nom Machine = " + inetAddress.getHostName());
                        System.out.println("address IP = " + inetAddress.getHostAddress());

                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }

//        try {
//            InetAddress localHost = InetAddress.getLocalHost();
//            System.out.println("Nom Machine = " + localHost.getHostName());
//            System.out.println("IP = " + localHost.getHostAddress());
//            return localHost.getHostAddress();
//        } catch(UnknownHostException e) {
//            e.printStackTrace();
//        }

        return null;
    }


    private String getJavaCommand(String className, String parameters) {
        if (parameters == null) {
            parameters = "";
        }

        String path = ClassLoader.getSystemClassLoader().getResource("eevision/Main.class").toString();

        try {
            path = URLDecoder.decode(path, "US-ASCII");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        int offset = 0;
        if (Platform.isWindows()) {
            offset = 1;
        }

        int begin = path.indexOf('/') + offset;
        int end = path.indexOf('!');
        if (end < 0) {
            end = path.length();
        }

        String jar = path.substring(begin, end);

        return "java -classpath " + jar + " " + className + " " + parameters;
    }

    /**
     * Retourne le chemin du répertoire de VLC sous Windows.
     *
     * @return le chemin du répertoire de VLC.
     *
     * @since version 0.9.0
     */
    private static String getVLCpathOnWindows() {
        String command = "reg query HKLM\\SOFTWARE\\VideoLAN\\VLC /v InstallDir";

        StringBuilder result = new StringBuilder(1024);
        StringBuilder error = new StringBuilder(1024);

        executeCommand("reg query", result, error, command);

        String[] splitResult = result.toString().split("REG_SZ");

        if (splitResult.length == 1) {
            command = "reg query HKLM\\SOFTWARE\\Wow6432Node\\VideoLAN\\VLC /v InstallDir";
            result = new StringBuilder(1024);
            error = new StringBuilder(1024);

            executeCommand("reg query", result, error, command);

            splitResult = result.toString().split("REG_SZ");
        }

        if (splitResult.length > 1) {
            return splitResult[splitResult.length - 1].trim();
        } else {
            return error.toString();
        }
    }

    /**
     * Retourne le chemin du répertoire de VLC sous Mac.
     *
     * @return le chemin du répertoire de VLC.
     *
     * @since version 0.9.0
     */
    private static String getVLCpathOnMac() {
        return "/Applications/VLC.app/Contents/MacOS";
    }

    private void launch(final int index, String className, String parameters) {
        out[index] = new StringBuilder();
        err[index] = new StringBuilder();

        String command;
        if (Platform.isLinux()) {
            command = "aoss " + getJavaCommand(className, parameters);
        } else {
            command = getJavaCommand(className, parameters);
        }//end if

        Runtime runtime = Runtime.getRuntime();

        try {
            processes[index] = runtime.exec(command);

            Thread outputThread = createReadThread(index + " (out)", processes[index].getInputStream(), out[index],
                    UTF8_CHARSET);
            Thread errorThread = createReadThread(index + " (err)", processes[index].getErrorStream(), err[index],
                    UTF8_CHARSET);
            outputThread.start();
            errorThread.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean portMutlicastOpen(final String multicastIP, final int port) {
        final String envoi = "test multicast";
        final String[] output = new String[1];

        //end run()
        Thread thread = new Thread(() -> {
            try {
                InetAddress group = InetAddress.getByName(multicastIP);
                MulticastSocket multicastSocket = new MulticastSocket(port);
                multicastSocket.joinGroup(group);

                byte[] data = new byte[1024];
                DatagramPacket packet = new DatagramPacket(data, data.length);
                //attente de la connection
                multicastSocket.receive(packet);

                output[0] = new String(data, 0, packet.getLength(), UTF8_CHARSET);
            } catch (IOException e) {
                e.printStackTrace();
                output[0] = e.getMessage();
            }
        });
        thread.start();

        try {
            InetAddress group = InetAddress.getByName(multicastIP);
            MulticastSocket multicastSocket = new MulticastSocket(port);
            multicastSocket.joinGroup(group);
            byte[] data = envoi.getBytes("UTF-8");
            DatagramPacket packet = new DatagramPacket(data, data.length, group, port);
            multicastSocket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }

//        while(thread.isAlive())
        waitInMillisecond(1000);

        if (output[0].contentEquals(envoi)) {
            return true;
        }

        return false;
    }

    private int getDifferences(String one, String two) {
        int diff = Math.abs(one.length() - two.length());
        int max = Math.min(one.length(), two.length());

        for (int i = 0; i < max; i++) {
            if (one.charAt(i) != two.charAt(i)) {
                diff++;
            }
        }

        return diff;
    }

    private static String executeCommand(String... command) {
        StringBuilder result = new StringBuilder(1024);
        StringBuilder error = new StringBuilder(1024);

        executeCommand(command[0], result, error, command);

        if (error.length() != 0) {
            return error.insert(0, "error:").toString();
        } else {
            return result.toString();
        }
    }

    /**
     * Execute une commande native.
     *
     * @param name nom pour le process.
     * @param command la commande.
     * @param output un builder initialisé pour afficher la sortie standard.
     * @param error un builder initialisé pour afficher la sortie des erreur.
     *
     * @return la valeur de sortie du processus résultat de la commande.
     */
    private static int executeCommand(String name, StringBuilder output, StringBuilder error, String... command) {
        int end = -1;
        Runtime runtime = Runtime.getRuntime();
        Process process = null;

        String charset = UTF8_CHARSET;
        if (Platform.isWindows()) {
            if (command[0].contains("cmd")) {
                charset = DOS_CHARSET;
            } else {
                charset = WINDOWS_CHARSET;
            }
        }

        try {
            if (command.length == 1) {
                process = runtime.exec(command[0]);
            } else {
                process = runtime.exec(command);
            }
            Thread outputThread = createReadThread(name + " (out)", process.getInputStream(), output, charset);
            Thread errorThread = createReadThread(name + " (err)", process.getErrorStream(), error, charset);
            outputThread.start();
            errorThread.start();

            try {
                end = process.waitFor();
                while (outputThread.isAlive()) {
                    waitInMillisecond(10);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (process != null) {
            process.destroy();
        }
        return end;
    }

    /**
     * Créer une thread de traitement d'un flux.
     *
     * @param name le nom de la thread.
     * @param inputStream le flux à gérer.
     * @param output un StringBuilder initialisé pour afficher la sortie.
     *
     * @return la thread de gestion du flux.
     */
    private static Thread createReadThread(final String name, final InputStream inputStream, final StringBuilder output,
            final String charset) {
        return new Thread(name) {
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
                    e.printStackTrace();
                }
            }
        };
    }

    /**
     * Permet de suspendre une Thread pendant une certaine durée.
     *
     * @param millisecond la durée à attendre en millisecondes.
     */
    private static void waitInMillisecond(long millisecond) {
        try {
            Thread.sleep(millisecond);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
