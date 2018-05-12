package eestudio.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.event.EventListenerList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thot.exception.ThotCodeException;
import thot.exception.ThotException;
import thot.labo.TagList;
import thot.utils.ProgressListener;
import thot.utils.Utilities;
import thot.video.Converter;

/**
 * Gestion de Mplayer/Mencoder pour la conversion de fichiers.
 *
 * @author Fabrice Alleau
 */
public class MEncoder implements Converter {
    /**
     * Instance de log.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MEncoder.class);

    /**
     * Caractères représentant l'entrée de la durée du media dans le processus
     */
    private static final String DURATION_PROPERTY = "ID_LENGTH=";
    /**
     * Caractères représentant le codec audio
     */
    private static final String SELECTED_AUDIO_CODEC = "Selected audio codec:";
    /**
     * Caractères représentant le codec audio
     */
    private static final String SELECTED_VIDEO_CODEC = "Selected video codec:";
    /**
     * Caractères représentant l'entrée d'un flux audio
     */
    private static final String AUDIO_PROPERTY = "AUDIO:";
    /**
     * Caractères représentant l'entrée de la durée du media dans le processus
     */
    private static final String VIDEO_PROPERTY = "VIDEO:";
    /**
     * Codec pour la conversion en mp3
     */
    private static final String CODEC_MP3_LAME = "mp3lame";

    /**
     * Référence sur l'exécutable de conversion
     */
    private File encoder;
    /**
     * Référence sur l'exécutable de lecture
     */
    private File player;
    /**
     * Liste d'écouteur pour répercuter les évènements du convertisseur
     */
    private final EventListenerList listeners;

    /**
     * Nombres de canaux audio
     */
    private String audioChannels = "2";
    /**
     * Fréquence d'échantillonage de l'audio en Hz
     */
    private String audioRate = "44100";
    /**
     * Bitrate audio en bit/s
     */
    private String audioBitrate = "128";
    /**
     * Taille de la video
     */
    private String videoSize = "640:480";

    /**
     * Durée du media en seconde
     */
    private double duration = -1;

    /**
     * Processus de ffmpeg
     */
    private Process process;

    /**
     * Indique si le processus termine le traitement
     */
    private boolean processingEnd = true;

    /**
     * Chemin du dossier temporarire
     */
    private File tempPath;

    /**
     * Initialisation.
     *
     * @param encoder l'exécutable "mencoder" pour la conversion.
     * @param player l'exécutable "mplayer" pour la conversion.
     */
    public MEncoder(File encoder, File player) {
        this.encoder = encoder;
        this.player = player;

        tempPath = new File(System.getProperty("java.io.tmpdir"), "edu4");
        tempPath.mkdirs();

        listeners = new EventListenerList();
    }

    /**
     * Arrête le processus.
     */
    @Override
    public void cancel() throws ThotException {
        process.destroy();
        Utilities.killApplication(encoder.getName());
        Utilities.killApplication(player.getName());
    }

    /**
     * Indique si le processus termine le traitement.
     *
     * @param processingEnd si le processus termine le traitement.
     */
    private void setProcessingEnd(boolean processingEnd) {
        this.processingEnd = processingEnd;
        fireProcessEnded(0);
    }

    /**
     * Ajoute d'une écoute de type ProgessListener.
     *
     * @param listener l'écoute à ajouter.
     */
    @Override
    public void addListener(ProgressListener listener) {
        listeners.add(ProgressListener.class, listener);
    }

    /**
     * Enlève une écoute de type ProgessListener.
     *
     * @param listener l'écoute à enlever.
     */
    @Override
    public void removeListener(ProgressListener listener) {
        listeners.remove(ProgressListener.class, listener);
    }

    /**
     * Notification du début du traitement.
     *
     * @param determinated indique si le processus peut afficher un poucentage de progression.
     */
    private void fireProcessBegin(boolean determinated) {
        for (ProgressListener listener : listeners.getListeners(ProgressListener.class)) {
            listener.processBegin(this, determinated);
        }
    }

    /**
     * Notification de fin du traitement.
     *
     * @param exit la valeur de sortie (par convention 0 équvaut à une sortie normale).
     */
    private void fireProcessEnded(int exit) {
        if (processingEnd) {
            for (ProgressListener listener : listeners.getListeners(ProgressListener.class)) {
                listener.processEnded(this, exit);
            }
        }
    }

    /**
     * Notification du début du traitement.
     *
     * @param percent le nouveau pourcentage de progression.
     */
    private void firePercentChanged(int percent) {
        for (ProgressListener listener : listeners.getListeners(ProgressListener.class)) {
            listener.percentChanged(this, percent);
        }
    }

    /**
     * Modifie le bitrate de l'audio.
     *
     * @param audioBitrate le birate en bit/s.
     */
    private void setAudioBitrate(int audioBitrate) {
        this.audioBitrate = Integer.toString(audioBitrate / 1000);
    }

    /**
     * Modifie le nombre de canaux audio.
     *
     * @param audioChannels le nombre de canaux audio.
     */
    private void setAudioChannels(int audioChannels) {
        this.audioChannels = Integer.toString(audioChannels);
    }

    /**
     * Modifie le taux d'échantillonage.
     *
     * @param audioRate la fréquence en Hz.
     */
    private void setAudioRate(int audioRate) {
        this.audioRate = Integer.toString(audioRate);
    }

    /**
     * Modifie la taille de la vidéo.
     *
     * @param width la largeur.
     * @param height la hauteur.
     */
    @Override
    public void setVideoSize(int width, int height) {
        this.videoSize = Integer.toString(width) + ":" + Integer.toString(height);
    }

    /**
     * Retourne la durée du fichier en ms.
     *
     * @param file le fichier.
     *
     * @return la durée du fichier en ms.
     */
    @Override
    public long getDuration(File file) throws ThotException {
        duration = -1;
        List<String> list = getTracksInfo(file);
        for (String line : list) {
            if (line.contains(DURATION_PROPERTY)) {
                String split[] = line.split(DURATION_PROPERTY);
                split = split[1].split("\n");
                duration = Utilities.parseStringAsDouble(split[0].trim());
                break;
            }
        }
        list.clear();

        return (long) (duration * 1000);
    }

    /**
     * Retourne les informations des pistes d'un fichier.
     *
     * @param file le fichier.
     *
     * @return la liste des pistes.
     */
    private List<String> getTracksInfo(File file) throws ThotException {
        if (player == null || !player.exists()) {
            throw new ThotException(ThotCodeException.MPLAYER_NOT_FOUND, "L'exécutable {} est introuvable", player);
        }

        if (file == null || !file.exists()) {
            throw new ThotException(ThotCodeException.FILE_NOT_FOUND, "Le fichier {} est introuvable", file);
        }

        StringBuilder command = new StringBuilder(1024);
        command.append(getProtectedName(player.getAbsolutePath()));
        command.append(" -noconsolecontrols -nolirc -noautosub");
        command.append(" -msglevel identify=4 -ao null -vo null -frames 0 ");
        command.append(getProtectedName(file.getAbsolutePath()));

        LOGGER.info("mplayer command: " + command.toString());
        duration = -1;

        StringBuilder input = new StringBuilder(1024);
        StringBuilder error = new StringBuilder(1024);
        int exit = executeCommand(command.toString(), null, input, error);
//        fireProcessEnded(exit);

        List<String> list = new ArrayList<>(4);
        String[] lines = input.toString().split("\n");
        for (String line : lines) {
            if (line.contains(DURATION_PROPERTY) || line.contains(SELECTED_AUDIO_CODEC) || line.contains(AUDIO_PROPERTY)
                    || line.contains(SELECTED_VIDEO_CODEC) || line.contains(VIDEO_PROPERTY)) {
                list.add(line.trim());
            }
        }

        return list;
    }

    /**
     * Détermine si le fichier possède un flux audio.
     *
     * @param file le fichier.
     *
     * @return si le fichier possède un flux audio.
     */
    @Override
    public boolean hasAudioSrteam(File file) throws ThotException {
        boolean audio = false;
        List<String> list = getTracksInfo(file);
        for (String line : list) {
            if (line.contains(AUDIO_PROPERTY)) {
                audio = true;
                break;
            }
        }
        return audio;
    }

    /**
     * Détermine si le fichier possède un flux vidéo.
     *
     * @param file le fichier.
     *
     * @return si le fichier possède un flux vidéo.
     */
    @Override
    public boolean hasVideoSrteam(File file) throws ThotException {
        boolean video = false;
        List<String> list = getTracksInfo(file);
        for (String line : list) {
            if (line.contains(VIDEO_PROPERTY)) {
                video = true;
                break;
            }
        }
        return video;
    }

    /**
     * Conversion de fichiers. La conversion est définie par le type du fichier destination : Types supportés: .wav,
     * .mp3, .mp4, .flv Paramètres par défaut: - audio mono à 44,1kHz - 128kbit/s pour le mp3 - VBR (quality 10) pour le
     * ogg - taille de video "640x480" - 25 images par seconde - pas d'audio pour le flv - audio en mp3 pour le mp4
     *
     * @param srcFile le fichier à convertir.
     * @param destFile le fichier de destination.
     * @param tags les tags au format mp3.
     */
    @Override
    public void convert(File destFile, File srcFile, TagList tags) throws ThotException {
        convert(destFile, srcFile, tags, 44100, 1);
    }

    /**
     * Conversion de fichiers. La conversion est définie par le type du fichier destination : Types supportés: .wav,
     * .mp3, .mp4, .flv Paramètres par défaut: - audio mono à 44,1kHz - 128kbit/s pour le mp3 - VBR (quality 10) pour le
     * ogg - taille de video "640x480" - 25 images par seconde - pas d'audio pour le flv - audio en mp3 pour le mp4
     *
     * @param srcFile le fichier à convertir.
     * @param destFile le fichier de destination.
     * @param audioRate la fréquence en Hz.
     * @param channels le nombre de canaux audio.
     */
    @Override
    public void convert(File destFile, File srcFile, TagList tags, int audioRate, int channels) throws ThotException {
        setAudioBitrate(128000);
        setAudioChannels(channels);
        setAudioRate(audioRate);

        String type = Utilities.getExtensionFile(destFile);
        if (type.endsWith(".wav")) {
            convertToWAV(destFile, srcFile);
        } else if (type.endsWith(".mp3")) {
            convertToMP3(destFile, srcFile, tags);
        } else if (type.endsWith(".mp4")) {
            convertToMP4(destFile, srcFile, null, null, tags);
        } else if (type.endsWith(".flv")) {
            setVideoSize(640, 480);
            convertToFLV(destFile, srcFile);
        } else {
            convertToMP4(destFile, srcFile, null, null, null);
        }
    }

    /**
     * Conversion de fichiers. La conversion est définie par le type du fichier destination : Types supportés: .wav,
     * .mp3, .mp4, .flv Paramètres par défaut: - audio mono à 44,1kHz - 128kbit/s pour le mp3 - VBR (quality 10) pour le
     * ogg - taille de video "640x480" - 25 images par seconde - pas d'audio pour le flv - audio en mp3 pour le mp4
     *
     * @param destFile le fichier de destination.
     * @param videoFile le fichier pour la piste vidéo.
     * @param audioFile le fichier pour la piste audio.
     * @param subtitleFile le fichier pour les soustitres.
     */
    @Override
    public void convert(File destFile, File audioFile, File videoFile, File subtitleFile, TagList tags)
            throws ThotException {
        setAudioBitrate(128000);
        setAudioChannels(1);
        setAudioRate(44100);

        String type = Utilities.getExtensionFile(destFile);
        if (type.endsWith(".wav")) {
            convertToWAV(destFile, audioFile);
        } else if (type.endsWith(".mp3")) {
            convertToMP3(destFile, audioFile, tags);
        } else if (type.endsWith(".mp4")) {
            convertToMP4(destFile, audioFile, videoFile, subtitleFile, tags);
        } else if (type.endsWith(".flv")) {
            setVideoSize(640, 480);
            convertToFLV(destFile, videoFile);
        } else {
            convertToMP4(destFile, audioFile, videoFile, subtitleFile, tags);
        }
    }

    /**
     * Extrait les pistes audio et vidéo du fichier source au format WAV (mono à 44kHz) et FLV ("640x480", 25 fps)
     *
     * @param srcFile le fichier source contenant les deux pistes.
     * @param audioFile le fichier de destination pour la piste audio.
     * @param videoFile le fichier de destination pour la piste vidéo.
     */
    @Override
    public void extractToWAVandFLV(File srcFile, File audioFile, File videoFile) throws ThotException {
        setAudioBitrate(128000);
        setAudioChannels(1);
        setAudioRate(44100);
        setVideoSize(640, 480);

        audioFile.delete();
        videoFile.delete();

        String audioArgs = getProtectedName(srcFile.getAbsolutePath())
                + " -nolirc -noautosub"
                + " -nocorrect-pts -benchmark -vo null -vc null -novideo"
                + " -ao pcm:waveheader:fast:file=%" + audioFile.getAbsolutePath().length()
                + "%" + audioFile.getAbsolutePath()
                + " -af format=s16le,lavcresample=" + audioRate + ",channels=" + audioChannels;

        String videoArgs = getProtectedName(srcFile.getAbsolutePath())
                + " -noautosub"
                + " -nosound"//codec audio
                + " -of lavf -lavfopts format=flv"
                + " -ovc lavc -lavcopts vcodec=flv:vbitrate=1000:keyint=6"//codec vidéo
                + " -ofps 24"
                + " -vf scale=" + videoSize + ",harddup,fixpts"
                + " -o " + getProtectedName(videoFile.getAbsolutePath());

        extract(audioArgs, videoArgs);
    }

    /**
     * Insére une vidéo "blanche" (image fixe) sur une vidéo.
     *
     * @param file la vidéo dans la quelle on insère la vidéo "blanche".
     * @param imageFile l'image fixe à insérer.
     * @param begin le temps de départ de l'insertion dans la vidéo initiale.
     * @param duration la durée de la vidéo blanche à insérer.
     */
    @Override
    public void insertBlankVideo(File file, File imageFile, long begin, long duration) throws ThotException {
        File videoFile = new File(tempPath, "videoIMG.flv");
        setProcessingEnd(false);
        createBlankVideo(videoFile, imageFile, duration);
        insertVideo(file, videoFile, begin);
    }

    /**
     * Duplique la plage donnée de la vidéo et l'insére à la fin de la plage.
     *
     * @param file la vidéo à modifier.
     * @param begin le temps de départ de la partie à dupliquer.
     * @param end le temps de fin de la partie à dupliquer.
     */
    @Override
    public void insertDuplicatedVideo(File file, long begin, long end) throws ThotException {
        File videoFile = new File(tempPath, "videoInsert.flv");
        setProcessingEnd(false);
        extractVideoFile(videoFile, file, begin, end);
        insertVideo(file, videoFile, end);
    }

    /**
     * Insére une vidéo dans une vidéo.
     *
     * @param file la vidéo dans la quelle on insère la vidéo.
     * @param insertFile le fichier vidéo à insérer.
     * @param begin le temps de départ de l'insertion dans la vidéo initiale.
     */
    @Override
    public void insertVideo(File file, File insertFile, long begin) throws ThotException {
        long videoDuration = getDuration(file);
        File videoBefore = new File(tempPath, "videoBefore.flv");
        File videoAfter = new File(tempPath, "videoAfter.flv");
        extractVideoFile(videoBefore, file, 0, begin);
        extractVideoFile(videoAfter, file, begin, videoDuration);
        joinVideoFile(file, videoBefore, insertFile, videoAfter);
        setProcessingEnd(true);
    }

    /**
     * Crée une vidéo "blanche" (image fixe) d'une durée spécifique.
     *
     * @param destFile le fichier de destination de la vidéo.
     * @param imageFile l'image fixe à insérer.
     * @param duration la durée de la vidéo blanche.
     */
    @Override
    public void createBlankVideo(File destFile, File imageFile, long duration) throws ThotException {
        File imgDirectory = new File(tempPath, "_img_");
        imgDirectory.mkdirs();
        createVideoFile(destFile, duration, imgDirectory, imageFile);
    }

    /**
     * Supprime une partie de la vidéo.
     *
     * @param file la vidéo dans la quelle on supprime une plage de temps.
     * @param begin le temps de départ de la partie à supprimer.
     * @param end le temps de fin de la partie à supprimer.
     */
    @Override
    public void removeVideo(File file, long begin, long end) throws ThotException {
        setProcessingEnd(false);
        long videoDuration = getDuration(file);
        File videoBefore = new File(tempPath, "videoBefore.flv");
        File videoAfter = new File(tempPath, "videoAfter.flv");
        extractVideoFile(videoBefore, file, 0, begin);
        extractVideoFile(videoAfter, file, end, videoDuration);
        joinVideoFile(file, videoBefore, videoAfter);
        setProcessingEnd(true);
    }

    /**
     * Déplace et redimensionne une partie de la vidéo courante.
     *
     * @param file la vidéo.
     * @param imageFile l'image fixe à insérer.
     * @param begin le temps de départ de la partie à déplacer.
     * @param end le temps de fin de la partie à déplacer.
     * @param newBegin le nouveau temps de départ de la partie à déplacer.
     * @param duration la nouvelle durée de la partie sélectionnée.
     */
    @Override
    public void moveVideoAndResize(File file, File imageFile, long begin, long end, long newBegin, long duration)
            throws ThotException {
        setProcessingEnd(false);
        long videoDuration = getDuration(file);
        File videoBefore = new File(tempPath, "videoBefore.flv");
        File videoAfter = new File(tempPath, "videoAfter.flv");
        File removeVideo = new File(tempPath, "videoRemove.flv");

        extractVideoFile(videoBefore, file, 0, begin);
        extractVideoFile(videoAfter, file, end, videoDuration);

        if (duration > (end - begin)) {
            File removeTemp = new File(tempPath, "videoRemoveTemp.flv");
            File blankFile = new File(tempPath, "videoIMG.flv");
            extractVideoFile(removeTemp, file, begin, end);
            createBlankVideo(blankFile, imageFile, duration - end + begin);
            joinVideoFile(removeVideo, removeTemp, blankFile);
        } else {
            extractVideoFile(removeVideo, file, begin, begin + duration);
        }
        joinVideoFile(file, videoBefore, videoAfter);

        insertVideo(file, removeVideo, newBegin);
    }

    /**
     * Modifie la vitesse d'une partie de la vidéo.
     *
     * @param file la vidéo.
     * @param begin le temps de départ de la partie à modifier.
     * @param end le temps de fin de la partie à modifier.
     * @param oldRate l'ancienne vitesse de la partie à modifier.
     * @param newRate la nouvelle vitesse de la partie à modifier.
     * @param normalFile la vidéo correspondante au temps à un vitesse normale.
     */
    @Override
    public void setVideoRate(File file, long begin, long end, float oldRate, float newRate, File normalFile)
            throws ThotException {
        setProcessingEnd(false);
        long videoDuration = getDuration(file);
        File videoBefore = new File(tempPath, "videoBefore.flv");
        File videoAfter = new File(tempPath, "videoAfter.flv");
        File insertVideo = new File(tempPath, "videoInsert.flv");

        extractVideoFile(videoBefore, file, 0, begin);
        extractVideoFile(videoAfter, file, end, videoDuration);

        if (oldRate == 1) {
            extractVideoFile(normalFile, file, begin, end);
        }
        if (newRate == 1) {
            insertVideo = normalFile;
        } else {
            convertToFLV(insertVideo, normalFile, newRate);
        }

        joinVideoFile(file, videoBefore, insertVideo, videoAfter);
        setProcessingEnd(true);
    }

    /**
     * Conversion d'un fichier en mp3.
     *
     * @param srcFile le fichier à convertir.
     * @param destFile le fichier de destination.
     */
    private void convertToMP3(File destFile, File srcFile, TagList tags) throws ThotException {
        StringBuilder args = new StringBuilder(1024);
        args.append(getProtectedName(srcFile.getAbsolutePath()));
//        int dim = (int) Math.floor(Math.sqrt(srcFile.length()/getDuration(srcFile)*80/3));
//        = 48 quand la source est du wav

        args.append(" -noautosub");
        args.append(" -demuxer rawvideo -rawvideo w=48:h=48 -ovc copy ");
        if (hasCodec_mp3lame()) {
            args.append(" -of rawaudio -oac mp3lame -lameopts cbr:br=");
        } else {
            args.append(" -of rawaudio -oac lavc -lavcopts acodec=mp2:abitrate=");
        }
        args.append(audioBitrate);
        args.append(" -af lavcresample=");//audio rate
        args.append(audioRate);
        args.append(",channels=");//canaux audio
        args.append(audioChannels);
        args.append(" -audiofile ");
        args.append(getProtectedName(srcFile.getAbsolutePath()));

        args.append(" -o ");
        args.append(getProtectedName(destFile.getAbsolutePath()));
        convert(encoder, args.toString(), null, null);

        if (tags != null) {
            try {
                tags.writeTagsToMp3(destFile);
            } catch (IOException e) {
                LOGGER.error("", e);
            }
        }
    }

    /**
     * Conversion d'un fichier en wav.
     *
     * @param srcFile le fichier à convertir.
     * @param destFile le fichier de destination.
     */
    private void convertToWAV(File destFile, File srcFile) throws ThotException {
        destFile.delete();

        String args = getProtectedName(srcFile.getAbsolutePath())
                + " -nolirc -noautosub"
                + " -nocorrect-pts -benchmark -vo null -vc null -novideo"
                + " -ao pcm:waveheader:fast:file=%" + destFile.getAbsolutePath().length()
                + "%" + destFile.getAbsolutePath()
                + " -af format=s16le,lavcresample=" + audioRate + ",channels=" + audioChannels;
        convert(player, args, null, null);
    }

    /**
     * Conversion d'un fichier en video mp4 et mp3.
     *
     * @param destFile le fichier de destination.
     * @param videoFile le fichier video à convertir.
     * @param audioFile le fichier audio à convertir.
     * @param subtitleFile le fichier pour les soustitres.
     */
    private void convertToMP4(File destFile, File audioFile, File videoFile, File subtitleFile, TagList tags)
            throws ThotException {
        StringBuilder args = new StringBuilder(1024);

        args.append(getProtectedName(videoFile.getAbsolutePath()));
        args.append(" -noautosub");
        args.append(" -of avi");//format
        args.append(" -ovc lavc -lavcopts vcodec=mpeg4:vbitrate=1000");//codec vidéo
        args.append(" -ofps 24");
        args.append(" -vf scale=");
        args.append(videoSize);
        args.append(",harddup");

        if (audioFile != null && audioFile.exists()) {
            if (hasCodec_mp3lame()) {
                args.append(" -oac mp3lame -lameopts cbr:br=");
            } else {
                args.append(" -oac lavc -lavcopts acodec=mp2:abitrate=");
            }
            args.append(audioBitrate);
            args.append(" -af lavcresample=");//audio rate
            args.append(audioRate);
            args.append(",channels=");//canaux audio
            args.append(audioChannels);
            args.append(" -audiofile ");
            args.append(getProtectedName(audioFile.getAbsolutePath()));
        } else {
            args.append(" -nosound");
        }

        if (subtitleFile != null && subtitleFile.exists()) {
            args.append(" -ffactor 10 -subfont-autoscale 3 -utf8 -subpos 100");
            args.append(" -sub ");
            args.append(getProtectedName(subtitleFile.getAbsolutePath()));
        }

        if (tags != null && !tags.isEmpty()) {
            args.append(" -info ");
            String value = tags.getTag(TagList.TITLE);
            if (value != null) {
                args.append("name=");
                args.append(getProtectedName(value));
                args.append(":");
            }
            value = tags.getTag(TagList.ARTIST);
            if (value != null) {
                args.append("artist=");
                args.append(getProtectedName(value));
                args.append(":");
            }
            value = tags.getTag(TagList.GENRE);
            if (value != null) {
                args.append("genre=");
                args.append(getProtectedName(value));
                args.append(":");
            }
            value = tags.getTag(TagList.COMMENT);
            if (value != null) {
                args.append("comment=");
                args.append(getProtectedName(value));
//                args.append(":");
            }

            /*
             * subject=<value> contents of the work
             * copyright=<value> copyright information
             * srcform=<value> original format of the digitized material
             */
        }

        args.append(" -o ");
        args.append(getProtectedName(destFile.getAbsolutePath()));

        convert(encoder, args.toString(), null, null);
    }

    /**
     * Conversion d'un fichier en video flv sans audio.
     *
     * @param srcFile le fichier à convertir.
     * @param destFile le fichier de destination.
     */
    private void convertToFLV(File destFile, File srcFile) throws ThotException {
        String args = getProtectedName(srcFile.getAbsolutePath())
                + " -noautosub"
                + " -nosound"//codec audio
                + " -of lavf -lavfopts format=flv"
                + " -ovc lavc -lavcopts vcodec=flv:vbitrate=1000:keyint=6"//codec vidéo
                + " -ofps 24"
                + " -vf scale=" + videoSize + ",harddup,fixpts"
                + " -o " + getProtectedName(destFile.getAbsolutePath());
        convert(encoder, args, null, null);
    }

    /**
     * Conversion d'un fichier en video flv sans audio.
     *
     * @param srcFile le fichier à convertir.
     * @param destFile le fichier de destination.
     */
    private void convertToFLV(File destFile, File srcFile, float rate) throws ThotException {
        String args = getProtectedName(srcFile.getAbsolutePath())
                + " -noautosub"
                + " -nosound"//codec audio
                + " -of lavf -lavfopts format=flv"
                + " -ovc lavc -lavcopts vcodec=flv:vbitrate=1000:keyint=6"//codec vidéo
                + " -ofps 24"
                + " -speed " + rate
                + " -vf scale=" + videoSize + ",harddup,fixpts"
                + " -o " + getProtectedName(destFile.getAbsolutePath());
        convert(encoder, args, null, null);
    }

//    /**
//     * Conversion d'un fichier en video mp4 et mp3.
//     *
//     * @param destFile le fichier de destination.
//     * @param videoFile  le fichier video à convertir.
//     * @return les messages de conversion.
//     */
//    private int convertToXvid(File destFile, File videoFile) {
//        StringBuilder args = new StringBuilder(1024);
//        
//        args.append(getProtectedName(videoFile.getAbsolutePath()));
//        args.append(" -noautosub");
//        args.append(" -of avi");//format
//        args.append(" -ovc xvid -xvidencopts bitrate=400:me_quality=0");//codec vidéo
//        args.append(" -ofps 24");
//        args.append(" -vf scale=");
//        args.append(videoSize);
//        args.append(",harddup");
//        args.append(" -oac lavc -lavcopts acodec=mp2:abitrate=");
//        args.append(audioBitrate);
//        args.append(" -af lavcresample=");//audio rate
//        args.append(audioRate);
//        args.append(",channels=");//canaux audio
//        args.append(audioChannels);
//
//        //-xvidencopts bitrate=400:max_bframes=0:quant_type=h263:me_quality=0
//        //-vf expand=160:128:-1:-1:1
//
//        args.append(" -o ");
//        args.append(getProtectedName(destFile.getAbsolutePath()));
//
//        return convert(encoder, args.toString());
//    }

    /**
     * Teste la présence du codec mp3lame sur l'encodeur.
     *
     * @return la présence du codec mp3lame sur l'encodeur.
     */
    private boolean hasCodec_mp3lame() throws ThotException {
        String args = " -oac help";
        StringBuilder audioCodecs = new StringBuilder(1024);
        convert(encoder, args, null, audioCodecs);
        return audioCodecs.toString().contains(CODEC_MP3_LAME);
    }

    /**
     * Conversion et muliplexage de fichiers.
     *
     * @param binary l'exécutable de conversion.
     * @param args les arguments de la conversion.
     * @param workingDirectory le répertoire de travail (utile pour les images).
     */
    private void convert(File binary, String args, File workingDirectory, StringBuilder out) throws ThotException {
        if (binary == null || !binary.exists()) {
            throw new ThotException(ThotCodeException.BINARY_NOT_FOUND, "L'exécutable {} est introuvable", binary);
        }

        StringBuilder command = new StringBuilder(1024);
        command.append(getProtectedName(binary.getAbsolutePath()));
        command.append(" ");
        command.append(args);

        LOGGER.info("converter command: " + command.toString());
        duration = -1;

        StringBuilder output = new StringBuilder(1024);
        StringBuilder error = new StringBuilder(1024);

        fireProcessBegin(binary == encoder);
        int exit = executeCommand(command.toString(), workingDirectory, output, error);
        fireProcessEnded(exit);

        if (output.length() > 0) {
            if (out != null) {
                out.append(output);
            }
            LOGGER.info("converter standard Message:\n" + output.toString());
        }
        if (error.length() > 0) {
            LOGGER.info("converter error Message:\n" + error.toString());
        }

        if (error.length() > 0) {
            throw new ThotException(ThotCodeException.CONVESRION_ERROR,
                    "Erreur lors de l'exécution de la commande {} : {}", command, error);
        }
        if (exit != 0) {
            throw new ThotException(ThotCodeException.CONVESRION_ERROR,
                    "Erreur lors de l'exécution de la commande {} : code de sortie {}", command, exit);
        }
    }

    /**
     * Extraction et conversion des pistes audio et vidéo du fichier.
     *
     * @param audioArgs les arguments de la convertion de la piste audio.
     * @param videoArgs les arguments de la convertion de la piste vidéo.
     */
    private void extract(String audioArgs, String videoArgs) throws ThotException {
        if (player == null || !player.exists()) {
            throw new ThotException(ThotCodeException.MPLAYER_NOT_FOUND, "L'exécutable {} est introuvable", player);
        }
        if (encoder == null || !encoder.exists()) {
            throw new ThotException(ThotCodeException.MPENCODER_NOT_FOUND, "L'exécutable {} est introuvable", encoder);
        }

        StringBuilder command = new StringBuilder(1024);
        command.append(getProtectedName(player.getAbsolutePath()));
        command.append(" ");
        command.append(audioArgs);

        LOGGER.info("mplayer command: " + command.toString());
        duration = -1;

        StringBuilder output = new StringBuilder(1024);
        StringBuilder error = new StringBuilder(1024);

        fireProcessBegin(false);
        int exit = executeCommand(command.toString(), null, output, error);

        if (output.length() > 0) {
            LOGGER.info("extract audio standard Message:\n" + output.toString());
        }
        if (error.length() > 0) {
            LOGGER.info("extract audio error Message:\n" + error.toString());
        }

        if (error.length() > 0) {
            throw new ThotException(ThotCodeException.CONVESRION_ERROR,
                    "Erreur lors de l'exécution de la commande {} : {}", command, error);
        }
        if (exit != 0) {
            throw new ThotException(ThotCodeException.CONVESRION_ERROR,
                    "Erreur lors de l'exécution de la commande {} : code de sortie {}", command, exit);
        }

        command = new StringBuilder(1024);
        output = new StringBuilder(1024);
        error = new StringBuilder(1024);
        command.append(getProtectedName(encoder.getAbsolutePath()));
        command.append(" ");
        command.append(videoArgs);

        LOGGER.info("mencoder command: " + command.toString());
        duration = -1;

        fireProcessBegin(true);
        exit = executeCommand(command.toString(), null, output, error);
        fireProcessEnded(exit);

        if (output.length() > 0) {
            LOGGER.info("extract video standard Message:\n" + output.toString());
        }
        if (error.length() > 0) {
            LOGGER.info("extract video Message:\n" + error.toString());
        }

        if (error.length() > 0) {
            throw new ThotException(ThotCodeException.CONVESRION_ERROR,
                    "Erreur lors de l'exécution de la commande {} : {}", command, error);
        }
        if (exit != 0) {
            throw new ThotException(ThotCodeException.CONVESRION_ERROR,
                    "Erreur lors de l'exécution de la commande {} : code de sortie {}", command, exit);
        }
    }

    /**
     * Crée un fichier vidéo d'une durée détermenée au format mp4 à partir des images au format png contenues dans le
     * répertoire de travail.
     *
     * @param destFile le fichier de destination.
     * @param duration la durée de la vidéo.
     * @param workingDirectory le répertoire contenant les images.
     * @param imageFile
     */
    private void createVideoFile(File destFile, long duration, File workingDirectory, File imageFile)
            throws ThotException {
        String extension = Utilities.getExtensionFile(imageFile);
        for (int i = 0; i < 10; i++) {
            Utilities.fileCopy(imageFile, new File(workingDirectory, "img" + i + extension));
        }

        double nb = 0;
        String names[] = workingDirectory.list();
        if (names == null) {
            throw new ThotException(ThotCodeException.EMPTY_DIRECTORY, "Le répertoire {} est vide",
                    workingDirectory.getAbsolutePath());
        }
        for (String name : names) {
            if (name.toLowerCase().endsWith(".png") || name.toLowerCase().endsWith(".jpg")) {
                nb++;
            }
        }
        if (nb == 0) {
            throw new ThotException(ThotCodeException.EMPTY_DIRECTORY, "Le répertoire {} ne contient pas d'image",
                    workingDirectory.getAbsolutePath());
        }

        String args = "mf://*" + extension
                + " -mf fps=" + String.format("%1$.12f", (1000 * nb / duration))
                + " -of lavf -lavfopts format=flv"
                + " -oac copy -ovc lavc -lavcopts vcodec=flv:vbitrate=1000:keyint=6"//codec vidéo
                + " -ofps 24"
                + " -vf scale=" + videoSize + ",harddup"
                + " -o " + getProtectedName(destFile.getAbsolutePath());
        convert(encoder, args, workingDirectory, null);
    }

    /**
     * Joint plusieurs fichier vidéo en un seul.
     *
     * @param destFile le fichier de destination.
     * @param srcFiles les différents fichiers sources.
     */
    private void joinVideoFile(File destFile, File... srcFiles) throws ThotException {
        StringBuilder args = new StringBuilder(1024);
        for (File file : srcFiles) {
            if (file != null && file.exists()) {
                args.append(" ");
                args.append(getProtectedName(file.getAbsolutePath()));
            }
        }

        args.append(" -of lavf -lavfopts format=flv");
        args.append(" -oac copy -ovc copy");//codec vidéo
        args.append(" -o ");
        args.append(getProtectedName(destFile.getAbsolutePath()));

        convert(encoder, args.toString(), null, null);
    }

    /**
     * Extrait une partie d'une vidéo au format flv.
     *
     * @param destFile le fichier vidéo de destination.
     * @param srcFile le fichier vidéo source.
     * @param begin le temps de départ de la partie à extraire (en ms).
     * @param end le temps de fin de la partie à extraire (en ms).
     */
    private void extractVideoFile(File destFile, File srcFile, long begin, long end) throws ThotException {
        String args = getProtectedName(srcFile.getAbsolutePath())
                + " -of lavf -lavfopts format=flv"
                + " -nosound -ovc copy"
                + String.format(" -ss %1$d.%2$d", (begin / 1000), (begin % 1000))
                + String.format(" -endpos %1$d.%2$d", ((end - begin) / 1000), ((end - begin) % 1000))
                + " -o " + getProtectedName(destFile.getAbsolutePath());
        convert(encoder, args, null, null);
    }

    /**
     * Execute une commande native.
     *
     * @param command la commande.
     * @param workingDirectory le répertoire de travail.
     * @param output un StringBuilder initialisé pour afficher la sortie standard.
     * @param error un StringBuilder initialisé pour afficher la sortie des erreur.
     *
     * @return la valeur de sortie du processus résultat de la commande.
     */
    private int executeCommand(String command, File workingDirectory, StringBuilder output, StringBuilder error) {
        if (Utilities.LINUX_PLATFORM) {
            return executeCommand(new String[]{"/bin/sh", "-c", command}, workingDirectory, output, error);
        }

        StringTokenizer tokenizer = new StringTokenizer(command);
        String[] cmdarray = new String[tokenizer.countTokens()];
        for (int i = 0; tokenizer.hasMoreTokens(); i++) {
            cmdarray[i] = tokenizer.nextToken();
        }

        return executeCommand(cmdarray, workingDirectory, output, error);
    }

    /**
     * Execute une commande native.
     *
     * @param command la commande.
     * @param workingDirectory le répertoire de travail.
     * @param output un StringBuilder initialisé pour afficher la sortie standard.
     * @param error un StringBuilder initialisé pour afficher la sortie des erreur.
     *
     * @return la valeur de sortie du processus résultat de la commande.
     */
    private int executeCommand(String[] command, File workingDirectory, StringBuilder output, StringBuilder error) {
        int end = -1;
        Runtime runtime = Runtime.getRuntime();
        process = null;

        try {
            process = runtime.exec(command, null, workingDirectory);
            Thread outputThread = createReadThread(process.getInputStream(), output);
            Thread errorThread = createReadThread(process.getErrorStream(), error);
            outputThread.start();
            errorThread.start();

            try {
                end = process.waitFor();
            } catch (InterruptedException e) {
                LOGGER.error("", e);
            }
        } catch (IOException e) {
            LOGGER.error("", e);
        }

        if (process != null) {
            process.destroy();
        }

        return end;
    }

    /**
     * Créer une thread de traitement d'un flux.
     *
     * @param inputStream le flux à gérer.
     * @param output un StringBuilder initialisé pour afficher la sortie.
     *
     * @return la thread de gestion du flux.
     */
    private Thread createReadThread(final InputStream inputStream, final StringBuilder output) {
        return new Thread(() -> {
            byte[] data = new byte[1024];
            try {
                int cnt = inputStream.read(data);
                while (cnt > 0) {
                    output.append(new String(data, 0, cnt));
                    fireNewData(new String(data, 0, cnt));
                    cnt = inputStream.read(data);
                }
            } catch (IOException e) {
                LOGGER.error("", e);
            }
        });
    }

    /**
     * Traitement des nouvelles données pour déterminer le pourcentage de conversion.
     *
     * @param data les nouvelles données.
     */
    private void fireNewData(String data) {
        if (data.contains(DURATION_PROPERTY)) {
            String split[] = data.split(DURATION_PROPERTY);
            if (split.length > 1) {
                split = split[1].split("\n");
                duration = Utilities.parseStringAsDouble(split[0].trim());
            }
        }
        if (data.contains("%")) {
            String split[] = data.split("\\(|%");
            if (split.length > 1) {
                firePercentChanged(Utilities.parseStringAsInt(split[1].trim()));
            }
        }
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
        }
        if (Utilities.WINDOWS_PLATFORM) {
            return "\"" + name + "\"";
        } else {
            return name.replace(" ", "\\ ");
        }
    }

//    public static void main(String[] args) {
//        File mencoder = new File("C:/Program Files/Edu4/MPlayer/mencoder.exe");
//        File mplayer = new File("C:/Program Files/Edu4/MPlayer/mplayer.exe");
//        final MEncoder converter = new MEncoder(mencoder, mplayer);
//
//        File directory = new File("C:/Users/fabrice.au/eeStudio");
//
////        "Ludmila bringing a love.ogg"         "the roof is on fire.mp3";
////        "BMW X6 Promotional Video.avi"        "Philadelphia.flv"
////        "videoTemp.flv"                       "A Scrat Adventure - Il était une noix.mkv"
////        Hubert_Reeves_-_R_chauffement_climatique.flv
//        final File srcFile = new File(directory, "Hubert_Reeves_-_R_chauffement_climatique.flv");
//
//        File destFile = new File(directory, "test.flv");
////        File audioFile = new File(directory, "test.wav");
////        File videoFile = new File(directory, "test.flv");
////        File srtFile = new File(directory, "aabbb.txt");
////        File workingDirectory = new File(directory, "img");
//
//        final thot.gui.ProcessingBar processingBar = new thot.gui.ProcessingBar(null, null);
//
//        processingBar.addWindowListener(new java.awt.event.WindowAdapter() {
//            @Override
//            public void windowClosing(java.awt.event.WindowEvent e) {
//                converter.cancel();
//            }
//        });
//
//        ProgressListener listener = new ProgressListener() {
//            @Override
//            public void processBegin(Object source, boolean determinated) {
//                processingBar.processBegin(determinated, "Conversion", "Conversion de " + srcFile);
//            }
//
//            @Override
//            public void processEnded(Object source, int exit) {
//                processingBar.close();
////                println("end statut: " + exit);
//            }
//
//            @Override
//            public void percentChanged(Object source, int percent) {
//                processingBar.setValue(percent);
//            }
//        };
//        converter.addListener(listener);
//
//        TagList tags = new TagList();
////        tags.putTag(TagList.TITLE, "titleFabrice");
////        tags.putTag(TagList.AUTHOR, "authorFabrice"); //pas dans le mp3
////        tags.putTag(TagList.ARTIST, "artist Fabrice");
////        tags.putTag(TagList.ALBUM, "albumFabrice");
////        tags.putTag(TagList.COMMENT, "commentFabrice");
////        tags.putTag(TagList.TRACK, "9");
////        tags.putTag(TagList.GENRE, "123");
////        tags.putTag(TagList.YEAR, "2012");
////        tags.putTag(TagList.INFORMATION, "infoFabrice"); //pas dans le mp3
////        tags.putTag(TagList.IMAGE, "00son.png"); //pas dans le mp3
//
////        converter.setAudioChannels(2);
////        converter.setAudioRate(48000);
////        converter.setVideoSize(320, 240);
//
//        long initTime = System.nanoTime();
////        converter.convertToWAV(audioFile, srcFile);
//        converter.convertToFLV(destFile, srcFile);
////        converter.extractToWAVandFLV(srcFile, audioFile, videoFile);
////        converter.convertToMP4(destFile, srcFile, srcFile, null, tags);
////        converter.convertToMP3(destFile, srcFile);
////        converter.createVideoFile(destFile, 10000, workingDirectory);
////        converter.joinVideoFile(destFile, srcFile, videoFile, srcFile, videoFile);
////        converter.extractVideoFile(destFile, srcFile, 10000, 90800);
////        converter.getDuration(srcFile);
//
////        long begin = 10000;
////        long end = 120000;
////        float rate = 2f;
////        File normalRate = new File(directory, "123456.flv");
////        converter.setVideoRate(srcFile, begin, (long) (begin+(end-begin)/rate), rate, 1f, normalRate);
//
//        long elapseTime = System.nanoTime();
//        LOGGER.info("temps: {} ns", (elapseTime - initTime));
//        LOGGER.info("temps: {} ms", (elapseTime - initTime) / 1000000);
//    }


}
