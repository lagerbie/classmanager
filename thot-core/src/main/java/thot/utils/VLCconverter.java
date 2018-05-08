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
package thot.utils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.swing.event.EventListenerList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Outils autour de VLC.
 *
 * @author Fabrice Alleau
 * @version 1.8.4 (VLC 1.1.x et 1.0.x)
 */
public class VLCconverter implements Converter {

    /**
     * Instance de log.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(VLCconverter.class);

    private static String resamplingError = "Failed to find conversion filter for resampling";
    /**
     * Caractères représentant l'entrée de la durée du media dans le processus.
     */
    private static final String durationProperty = "ID_LENGTH=";
    private static final String mux_wav = "wav mux debug:";
    /**
     * Caractères représentant l'entrée d'un flux audio.
     */
    private static final String audioProperty = "adding";
    /**
     * Caractères représentant l'entrée de la durée du media dans le processus.
     */
    private static final String videoProperty = "not an audio stream";
    /**
     * Codec pour la conversion en mp3.
     */
    private static final String lib_mp3lame = "libmp3lame.so.0";
    /**
     * Référence sur l'exécutable de conversion.
     */
    private File encoder;
    /**
     * Liste d'écouteur pour répercuter les évènements du convertisseur.
     */
    private final EventListenerList listeners;
    /**
     * Nombres de canaux audio.
     */
    private String audioChannels = "2";
    /**
     * Fréquence d'échantillonage de l'audio en Hz.
     */
    private String audioRate = "44100";
    /**
     * Bitrate audio en bit/s.
     */
    private String audioBitrate = "128";
    /**
     * Taille de la video.
     */
    private String videoSize = "640:480";
    /**
     * Durée du media en seconde.
     */
    private double duration = -1;
    /**
     * Processus de ffmpeg.
     */
    private Process process;

    private int deportedPort;

    /**
     * Initialisation.
     *
     * @param converter l'exécutable pour la conversion.
     * @param deportedPort le numéro du port pour des conversions déportée.
     */
    public VLCconverter(File converter, int deportedPort) {
        this.encoder = converter;
        this.deportedPort = deportedPort;
        listeners = new EventListenerList();
    }

    @Override
    public void cancel() {
        process.destroy();
        Utilities.killApplication(encoder.getName());
    }

    @Override
    public void addListener(ProgressPercentListener listener) {
        listeners.add(ProgressPercentListener.class, listener);
    }

    @Override
    public void removeListener(ProgressPercentListener listener) {
        listeners.remove(ProgressPercentListener.class, listener);
    }

    /**
     * Notification du changement du titre.
     *
     * @param title le nouveau titre.
     */
    private void fireTitleChanged(String title) {
        for (ProgressPercentListener listener :
                listeners.getListeners(ProgressPercentListener.class)) {
            listener.processTitleChanged(this, title);
        }
    }

    /**
     * Notification du changement du message.
     *
     * @param message le nouveau message.
     */
    private void fireMessageChanged(String message) {
        for (ProgressPercentListener listener :
                listeners.getListeners(ProgressPercentListener.class)) {
            listener.processMessageChanged(this, message);
        }
    }

    /**
     * Notification du début du traitement.
     *
     * @param determinated indique si le processus peut afficher un poucentage de progression.
     */
    private void fireProcessBegin(boolean determinated) {
        for (ProgressPercentListener listener :
                listeners.getListeners(ProgressPercentListener.class)) {
            listener.processBegin(this, determinated);
        }
    }

    /**
     * Notification de fin du traitement.
     *
     * @param exit la valeur de sortie (par convention 0 équvaut à une sortie normale).
     */
    private void fireProcessEnded(int exit) {
        for (ProgressPercentListener listener :
                listeners.getListeners(ProgressPercentListener.class)) {
            listener.processEnded(this, exit);
        }
    }

    /**
     * Notification du début du traitement.
     *
     * @param percent le nouveau pourcentage de progression.
     */
    private void firePercentChanged(int percent) {
        for (ProgressPercentListener listener : listeners.getListeners(ProgressPercentListener.class)) {
            listener.percentChanged(this, percent);
        }
    }

    @Override
    public void setAudioRate(int audioRate) {
        this.audioRate = Integer.toString(audioRate);
    }

    @Override
    public void setAudioChannels(int audioChannels) {
        this.audioChannels = Integer.toString(audioChannels);
    }

    @Override
    public void setVideoSize(int width, int height) {
        this.videoSize = Integer.toString(width) + "x" + Integer.toString(height);
    }

    @Override
    public long getDuration(File file) {
        return -1;
    }

    /**
     * Retourne les informations des pistes d'un fichier.
     *
     * @param file le fichier.
     *
     * @return la liste des pistes.
     */
    private List<String> getTracksInfo(File file) {
        List<String> list = new ArrayList<>(2);
        File tempFile = new File(System.getProperty("java.io.tmpdir"), file.getName() + ".wav");
        String vlcArgs = "--sout=#duplicate{dst=std{access=file,mux=wav,dst="
                + getProtectedName(tempFile.getAbsolutePath()) + "}}";

        String result = convert(file, vlcArgs);
        String[] lines = result.split("\n");
        for (String line : lines) {
            if (line.contains(mux_wav) && (line.contains(videoProperty) || line.contains(audioProperty))) {
                list.add(line.trim());
                LOGGER.info("conveter tracks info:\n{}", line);
            }
        }

        return list;
    }

    @Override
    public boolean hasAudioSrteam(File file) {
        boolean audio = false;
        List<String> list = getTracksInfo(file);
        for (String stream : list) {
            if (stream.contains(audioProperty)) {
                audio = true;
                break;
            }
        }
        list.clear();
        return audio;
    }

    @Override
    public boolean hasVideoSrteam(File file) {
        boolean video = false;
        List<String> list = getTracksInfo(file);
        for (String stream : list) {
            if (stream.contains(videoProperty)) {
                video = true;
                break;
            }
        }
        list.clear();
        return video;
    }

    /**
     * Teste la présence du codec mp3lame sur l'encodeur.
     *
     * @return la présence du codec mp3lame sur l'encodeur.
     */
    public boolean hasCodec_mp3lame() {
        return Utilities.hasFileOnLinux(lib_mp3lame);
    }

    /**
     * Conversion de fichiers. La conversion est définie par le type du fichier destination : Types supportés: .wav,
     * .mp3, .mp4 avec les paramètres: - audio mono à 44,1kHz - 128kbit/s pour le mp3 - taille de video "640x480" - 24
     * images par seconde - audio en mp3 pour le mp4
     *
     * @param srcFile le fichier à convertir.
     * @param destFile le fichier de destination.
     *
     * @return les messages de conversion.
     */
    @Override
    public String convert(File destFile, File srcFile) {
        return convert(destFile, srcFile, 44100, 2);
    }

    /**
     * Conversion de fichiers. La conversion est définie par le type du fichier destination : Types supportés: .wav,
     * .mp3, .mp4 avec les paramètres: - audio mono à 44,1kHz - 128kbit/s pour le mp3 - taille de video "640x480" - 24
     * images par seconde - audio en mp3 pour le mp4
     *
     * @param srcFile le fichier à convertir.
     * @param destFile le fichier de destination.
     * @param audioRate la fréquence en Hz.
     * @param channels le nombre de canaux audio.
     *
     * @return les messages de conversion.
     */
    @Override
    public String convert(File destFile, File srcFile, int audioRate, int channels) {
        setAudioChannels(channels);
        setAudioRate(audioRate);

        String type = Utilities.getExtensionFile(destFile);
        if (type.endsWith(".wav")) {
            return convertToWAV(destFile, srcFile);
        } else if (type.endsWith(".mp3")) {
            return convertToMP3(destFile, srcFile);
        } else {
            return convertToMP4(destFile, srcFile);
        }
    }

    /**
     * Conversion d'un fichier en mp3 mono-canal à 16 kHz.
     *
     * @param srcFile le fichier à convertir.
     * @param destFile le fichier de destination.
     *
     * @return les messages de vlc.
     */
    private String convertToMP3(File destFile, File srcFile) {
        String audioCodec = "mp3";
        if (Utilities.LINUX_PLATFORM && !hasCodec_mp3lame()) {
            audioCodec = "mpga";
        }

        String vlcArgs = "--sout=#transcode{vcodec=none,acodec=" + audioCodec + ",ab=" + audioBitrate + ",channels="
                + audioChannels + ",samplerate=" + audioRate + "}" + ":duplicate{dst=std{access=file,mux=dummy,dst="
                + getProtectedName(destFile.getAbsolutePath()) + "}}";

        return convert(srcFile, vlcArgs);
    }

    /**
     * Conversion d'un fichier en wav mono-canal à 8 kHz.
     *
     * @param srcFile le fichier à convertir.
     * @param destFile le fichier de destination.
     *
     * @return les messages de vlc.
     */
    private String convertToWAV(File destFile, File srcFile) {
        String vlcArgs = "--sout=#transcode{vcodec=none,acodec=s16l,ab=" + audioBitrate + ",channels=" + audioChannels
                + ",samplerate=" + audioRate + "}" + ":duplicate{dst=std{access=file,mux=wav,dst=" + getProtectedName(
                destFile.getAbsolutePath()) + "}}";

        String error = convert(srcFile, vlcArgs);

        /* [0132cdd4] main stream out error: Failed to create audio filter
         [0132cdd4] stream_out_transcode stream out error: Failed to find conversion filter for resampling
         [0132cdd4] stream_out_transcode stream out error: cannot create audio chain
         */

        if (error.contains(resamplingError)) {
            File audioFileTemp = new File(System.getProperty("java.io.tmpdir"), "temp.wav");
            String resample = convertToWAVinSameSamplerate(audioFileTemp, srcFile);
            if (resample.contains("error")) {
                return error;
            } else {
                return convertToWAV(destFile, audioFileTemp);
            }
        }

        return error;
    }

    /**
     * Conversion d'un fichier en wav mono-canal sans rééchantillonage.
     *
     * @param srcFile le fichier à convertir.
     * @param destFile le fichier de destination.
     *
     * @return les messages de vlc.
     */
    private String convertToWAVinSameSamplerate(File destFile, File srcFile) {
        String vlcArgs = "--sout=#transcode{vcodec=none,acodec=s16l,ab="
                + audioBitrate + ",channels=" + audioChannels + "}"
                + ":duplicate{dst=std{access=file,mux=wav,dst="
                + getProtectedName(destFile.getAbsolutePath()) + "}}";
        return convert(srcFile, vlcArgs);
    }

    /**
     * Conversion d'un fichier en video mp4 et mp3 mono-canal à 16 kHz.
     *
     * @param srcFile le fichier à convertir.
     * @param destFile le fichier de destination.
     *
     * @return les messages de vlc.
     */
    private String convertToMP4(File destFile, File srcFile) {
        String audioCodec = "mp3";
        if (Utilities.LINUX_PLATFORM && !hasCodec_mp3lame()) {
            audioCodec = "mpga";
        }

        String vlcArgs = "--sout=#transcode{vcodec=mp4v,vb=800,scale=1,acodec=" + audioCodec
                + ",ab=" + audioBitrate + ",channels=" + audioChannels
                + ",samplerate=" + audioRate + "}"
                + ":duplicate{dst=std{access=file,mux=ps,dst="
                + getProtectedName(destFile.getAbsolutePath()) + "}}";

        return convert(srcFile, vlcArgs);
    }

    /**
     * Conversion de fichier avec les paramètres voulus.
     *
     * @param srcFile le fichier à convertir.
     * @param vlcArgs les arguments pour vlc.
     *
     * @return les messages de vlc.
     */
    private String convert(File srcFile, String vlcArgs) {
        if (encoder == null || !encoder.exists()) {
            return "error: vlc not found";
        }

        StringBuilder command = new StringBuilder(64);
        command.append(getProtectedName(encoder.getAbsolutePath()));
        command.append(" -vv");
        command.append(" --intf=dummy");
        if (Utilities.WINDOWS_PLATFORM) {
            command.append(" --dummy-quiet");
        }
        command.append(" --ignore-config --no-stats");
        command.append(" ");
        command.append(vlcArgs);
        command.append(" ");
        command.append(getProtectedName(srcFile.getAbsolutePath()));
        command.append(" vlc://quit");

        LOGGER.info("VLC command: {}", command);
        duration = -1;

        StringBuilder input = new StringBuilder(1024);
        StringBuilder error = new StringBuilder(1024);

        fireProcessBegin(false);
        int exit = executeCommand(command.toString(), input, error);
        fireProcessEnded(exit);

        if (input.length() > 0) {
            LOGGER.info("VLC Standard Message:\n{}", input);
        }
        if (error.length() > 0) {
            LOGGER.warn("VLC Error Message:\n{}", error);
        }

        if (input.length() > 0) {
            return input.toString();
        } else {
            return error.toString();
        }
    }

    /**
     * Donne le chemin complet de l'exécutable de VLC.
     *
     * @return le chemin de VLC.
     */
    public static File getVLC() {
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
    private static File getVLConWindows() {
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
            return new File(splitResult[splitResult.length - 1].trim(), "vlc.exe");
        } else {
            return null;
        }
    }

    /**
     * Retourne le chemin de l'exécutable de VLC sous Linux.
     *
     * @return le chemin de VLC.
     */
    private static File getVLConLinux() {
        return Utilities.getApplicationPathOnLinux("vlc");
    }

    /**
     * Retourne le chemin de l'exécutable de VLC sous Mac.
     *
     * @return le chemin de VLC.
     */
    private static File getVLConMac() {
        return new File("/Applications/VLC.app/Contents/MacOS/VLC");
    }

    /**
     * Encode les caractères spéciaux (espace, accent) pour qu'ils soient lisibles dans une URL pour le passage d'un
     * programme à un autre.
     *
     * @param name le nom du fichier.
     *
     * @return le nom protégé.
     */
    private String getProtectedName(String name) {
        if (!name.contains(" ")) {
            return name;
        } else if (Utilities.WINDOWS_PLATFORM) {
            return "\"" + name + "\"";
        } else {
            return name.replace(" ", "\\ ");
        }
    }

    /**
     * Execute une commande native.
     *
     * @param command la commande.
     * @param output un StringBuilder initialisé pour afficher la sortie standard.
     * @param error un StringBuilder initialisé pour afficher la sortie des erreur.
     *
     * @return la valeur de sortie du processus résultat de la commande.
     */
    private int executeCommand(String command, StringBuilder output, StringBuilder error) {
        if (Utilities.LINUX_PLATFORM) {
            return executeCommandDeported(command, output, error);
        }

        int end = -1;
        Runtime runtime = Runtime.getRuntime();

        try {
            process = runtime.exec(command);
            Thread outputThread = createReadThread(process.getInputStream(), output, "vlc out");
            Thread errorThread = createReadThread(process.getErrorStream(), error, "vlc err");
            outputThread.start();
            errorThread.start();

            try {
                end = process.waitFor();
                while (outputThread.isAlive()) {
                    Utilities.waitInMillisecond(10);
                }
            } catch (InterruptedException e) {
                LOGGER.error("", e);
            }
            process.destroy();
        } catch (IOException e) {
            LOGGER.error("", e);
        }

        return end;
    }

    /**
     * Execute une commande native avec un lancement par un autre logiciel.
     *
     * @param command la commande.
     * @param output un builder initialisé pour afficher la sortie standard.
     * @param error un builder initialisé pour afficher la sortie des erreur.
     *
     * @return la valeur de sortie du processus résultat de la commande.
     */
    private int executeCommandDeported(String command, StringBuilder output, StringBuilder error) {
        try (Socket socket = new Socket("127.0.0.1", deportedPort)) {
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            outputStream.writeUTF(command);
            outputStream.flush();

            String tmp;
            while (inputStream.available() >= 0) {
                tmp = inputStream.readUTF();
                output.append(tmp);
                fireNewData(tmp);
            }
        } catch (EOFException e) {
            //do nothing
        } catch (IOException e) {
            LOGGER.error("", e);
        }
        return 0;
    }

    /**
     * Créer une thread de traitement d'un flux.
     *
     * @param inputStream le flux à gérer.
     * @param output un StringBuilder initialisé pour afficher la sortie.
     *
     * @return la thread de gestion du flux.
     */
    private Thread createReadThread(final InputStream inputStream, final StringBuilder output, String name) {
        Thread thread = new Thread(name) {
            @Override
            public void run() {
                byte[] data = new byte[1024];
                try {
                    int cnt = inputStream.read(data);
                    while (cnt > 0) {
                        output.append(new String(data, 0, cnt));
//                        fireNewData(new String(data, 0, cnt));
                        cnt = inputStream.read(data);
                    }
                } catch (IOException e) {
                    LOGGER.error("", e);
                }
            }
        };
        return thread;
    }

    /**
     * Traitement des nouvelles données pour déterminer le pourcentage de conversion.
     *
     * @param data les nouvelles données.
     */
    private void fireNewData(String data) {
//        System.out.println("data: " + data);
    }

    //    public static void main(String[] args) {
//        File vlc = getVLC();
//        Converter converter = new VLCconverter(vlc, -1);
//
////        "Ludmila bringing a love.ogg"   "the roof is on fire.mp3";
////        "BMW X6 Promotional Video.avi"  "Philadelphia.flv"
////        "videoTemp.flv"                 "A Scrat Adventure - Il était une noix.mkv"
//        File srcFile = new File("/home/fabrice/",
//                "the roof is on fire.mp3");
//
//        File destFile = new File("/home/fabrice/",
//                "temp.wav");
//
//        long init = System.nanoTime();
//        converter.convert(destFile, srcFile);
//        double elapse = System.nanoTime() - init;
//        System.out.println("elapse: " + elapse/1000000);
//    }
}
