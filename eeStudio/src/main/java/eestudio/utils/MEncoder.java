package eestudio.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.event.EventListenerList;

import eestudio.Constants;

/*
 * v0.97: ajout de private boolean processingEnd = true;
 * v0.97: ajout de public void setProcessingEnd(boolean processingEnd)
 * v0.97: supp de public String extractAviVideoFile(File destFile, File srcFile,
 *        long begin, long end)
 * v0.97: modif de private void fireProcessEnded(int exit) [processingEnd]
 * v0.97: modif de extractToWAVandFLV(...) [add paramètre keyint=6]
 * v0.97: modif de convertToFLV(...) [ajout paramètre keyint=6]
 * v0.97: modif de createVideoFile(..) en createVideoFile(.., File imageFile)
 *        [ajout imageFile, teste extension, crée 10 images, ajout paramètre keyint=6]
 * v0.97: modif de joinVideoFile(File destFile, File... srcFiles) [teste la
 *        validité des fichiers sources]
 * v0.97: modif de executeCommand(...) [add charset]
 * v0.97: modif de createReadThread(..) en createReadThread(.., String charset)
 * 
 * v0.98: modif de extractToWAVandFLV(...) [ajout de pcm:waveheader]
 * v0.98: modif de convertToWAV(...) [ajout de pcm:waveheader]
 * 
 * v0.99: modif de MEncoderUtilities en MEncoder
 * v0.99: ajout de implements Converter
 * v0.99: ajout de private static final String codec_mp3lame = "mp3lame";
 * v0.99: ajout de private File tempPath;
 * v0.99: ajout de private String convert(File destFile, File srcFile,
 *        Mp3Tags tags, int audioRate, int channels)
 * v0.99: ajout de @Override public void insertBlankVideo(File file,
 *        File imageFile, long begin, long duration)
 * v0.99: ajout de @Override public void insertDuplicatedVideo(File file,
 *        long begin, long end)
 * v0.99: ajout de @Override public void insertVideo(File file, File insertFile,
 *        long begin)
 * v0.99: ajout de @Override public void createBlankVideo(File destFile,
 *        File imageFile, long duration)
 * v0.99: ajout de @Override public void removeVideo(File file, long begin, long end)
 * v0.99: ajout de @Override public void moveVideoAndResize(File file,
 *        File imageFile, long begin, long end, long newBegin, long duration)
 * v0.99: ajout de private boolean hasCodec_mp3lame()
 * v0.99: ajout de private int executeCommand(String[] command,
 *        File workingDirectory, StringBuilder output, StringBuilder error)
 * v0.99: modif de MEncoder(File encoder, File player) [tempPath]
 * v0.99: modif de getDuration(File file) [utilisation de getTracksInfo(file)]
 * v0.99: modif de getTracksInfo(File file) [protection du chemin de l'exécutable,
 *        options -nolirc -noautosub, supp diff Linux]
 * v0.99: modif de convert(File, File) en convert(File, File, Mp3Tags tags) [use 
 *        convert(File, File, Mp3Tags, int, int)
 * v0.99: modif de convert(File, File, File, File) en convert(File, File, File,
 *        File, Mp3Tags tags)
 * v0.99: modif de extractToWAVandFLV(...) [options -nolirc -noautosub]
 * v0.99: modif de convertToMP3(..) en convertToMP3(.., Mp3Tags tags) [add option
 *        -noautosub, tags, test mp3lame codec]
 * v0.99: modif de convertToWAV(..) [options -nolirc -noautosub]
 * v0.99: modif de convertToMP4(...) en convertToMP4(..., Mp3Tags tags) [options
 *        -noautosub, tags, mp3lame codec]
 * v0.99: modif de convertToFLV(..) [options -noautosub]
 * v0.99: modif de convert(...) [protect binary path]
 * v0.99: modif de extract(..) [protect binary path]
 * v0.99: modif de executeCommand(String command, ..) [supp charset, diff Linux,
 *        use executeCommand(String[] command, ..)]
 * v0.99: modif de createReadThread(.., String charset) en createReadThread(..)
 * v0.99: modif de fireNewData(String data) [ajout test sur le split de la durée]
 * 
 * v1.01: ajout de @Override public void init() [création du cache des fonts]
 * v1.01: ajout de @Override public void setVideoRate(File file,
 *        long begin, long end, float oldRate, float newRate, File normalFile)
 * v1.01: ajout de private String convertToFLV(File destFile, File srcFile, float rate)
 * 
 * v1.02: modif de extract(String audioArgs, String videoArgs) [ajout condition
 *        d'arrêt après un échec d'extraction audio et avant d'extraire la vidéo]
 * 
 * v1.03: modif de convertToWAV(..) [add -novideo]
 * v1.03: modif de extractToWAVandFLV(..) [add -novideo sur audio -vf fixpts sur video]
 * v1.03: modif de convertToFLV(.,.) [add -vf fixpts]
 * v1.03: modif de convertToFLV(.,.,.) [add -vf fixpts]
 */

/**
 * Gestion de Mplayer/Mencoder pour la conversion de fichiers.
 * 
 * @author Fabrice Alleau
 * @since version 0.96
 * @version 1.03
 */
public class MEncoder implements Converter {
    /** Caractères représentant l'entrée de la durée du media dans le processus */
    private static final String durationProperty = "ID_LENGTH=";
    /** Caractères représentant le codec audio */
    private static final String audioCodec = "Selected audio codec:";
    /** Caractères représentant le codec audio */
    private static final String videoCodec = "Selected video codec:";
    /** Caractères représentant l'entrée d'un flux audio */
    private static final String audioProperty = "AUDIO:";
    /** Caractères représentant l'entrée de la durée du media dans le processus */
    private static final String videoProperty = "VIDEO:";
    /** Codec pour la conversion en mp3 */
    private static final String codec_mp3lame = "mp3lame";

    /** Référence sur l'exécutable de conversion */
    private File encoder;
    /** Référence sur l'exécutable de lecture */
    private File player;
    /** Liste d'écouteur pour répercuter les évènements du convertisseur */
    private final EventListenerList listeners;

    /** Nombres de canaux audio */
    private String audioChannels = "2";
    /** Fréquence d'échantillonage de l'audio en Hz */
    private String audioRate = "44100";
    /** Bitrate audio en bit/s */
    private String audioBitrate = "128";
    /** Taille de la video */
    private String videoSize = "640:480";

    /** Durée du media en seconde */
    private double duration = -1;

    /** Processus de ffmpeg */
    private Process process;

    /** Indique si le processus termine le traitement */
    private boolean processingEnd = true;

    /** Chemin du dossier temporarire */
    private File tempPath;

    /**
     * Initialisation.
     *
     * @param encoder l'exécutable "mencoder" pour la conversion.
     * @param player  l'exécutable "mplayer" pour la conversion.
     * @since version 0.96 - version 0.99
     */
    public MEncoder(File encoder, File player) {
        this.encoder = encoder;
        this.player = player;

        tempPath = new File(System.getProperty("java.io.tmpdir"), "edu4");
        tempPath.mkdirs();

        listeners = new EventListenerList();
    }
    
    @Override
    public void init() {
        if(player == null || !player.exists())
            return;

        StringBuilder command = new StringBuilder(1024);
        command.append(getProtectedName(player.getAbsolutePath()));
        command.append(" null");

        Edu4Logger.info("converter command: " + command.toString());
        duration = -1;

        StringBuilder output = new StringBuilder(1024);
        StringBuilder error = new StringBuilder(1024);

        fireProcessBegin(false);
        int exit = executeCommand(command.toString(), null, output, error);
        fireProcessEnded(exit);

        if(output.length() > 0)
            Edu4Logger.info("converter standard Message:\n" + output.toString());
        if(error.length() > 0)
            Edu4Logger.info("converter error Message:\n" + error.toString());
    }

    /**
     * Arrête le processus.
     * 
     * @since version 0.96
     */
    @Override
    public void cancel() {
        process.destroy();
        Utilities.killApplication(encoder.getName());
        Utilities.killApplication(player.getName());
    }

    /**
     * Indique si le processus termine le traitement.
     * 
     * @param processingEnd si le processus termine le traitement.
     * @since version 0.97
     */
    public void setProcessingEnd(boolean processingEnd) {
        this.processingEnd = processingEnd;
        fireProcessEnded(0);
    }

    /**
     * Ajoute d'une écoute de type ProgessListener.
     *
     * @param listener l'écoute à ajouter.
     * @since version 0.96
     */
    @Override
    public void addListener(ProgessListener listener) {
        listeners.add(ProgessListener.class, listener);
    }

    /**
     * Enlève une écoute de type ProgessListener.
     *
     * @param listener l'écoute à enlever.
     * @since version 0.96
     */
    @Override
    public void removeListener(ProgessListener listener) {
        listeners.remove(ProgessListener.class, listener);
    }

    /**
     * Notification du début du traitement.
     *
     * @param determinated indique si le processus peut afficher un poucentage
     *        de progression.
     * @since version 0.96
     */
    private void fireProcessBegin(boolean determinated) {
        for(ProgessListener listener : listeners.getListeners(ProgessListener.class)) {
            listener.processBegin(this, determinated);
        }
    }

    /**
     * Notification de fin du traitement.
     *
     * @param exit la valeur de sortie (par convention 0 équvaut à une sortie normale).
     * @since version 0.96 - version 0.97
     */
    private void fireProcessEnded(int exit) {
        if(processingEnd) {
            for(ProgessListener listener : listeners.getListeners(ProgessListener.class)) {
                listener.processEnded(this, exit);
            }
        }
    }

    /**
     * Notification du début du traitement.
     *
     * @param percent le nouveau pourcentage de progression.
     * @since version 0.96
     */
    private void firePercentChanged(int percent) {
        for(ProgessListener listener : listeners.getListeners(ProgessListener.class)) {
            listener.percentChanged(this, percent);
        }
    }

    /**
     * Modifie le bitrate de l'audio.
     *
     * @param audioBitrate le birate en bit/s.
     * @since version 0.96
     */
    private void setAudioBitrate(int audioBitrate) {
        this.audioBitrate = Integer.toString(audioBitrate/1000);
    }

    /**
     * Modifie le nombre de canaux audio.
     *
     * @param audioChannels le nombre de canaux audio.
     * @since version 0.96
     */
    @Override
    public void setAudioChannels(int audioChannels) {
        this.audioChannels = Integer.toString(audioChannels);
    }

    /**
     * Modifie le taux d'échantillonage.
     *
     * @param audioRate la fréquence en Hz.
     * @since version 0.96
     */
    @Override
    public void setAudioRate(int audioRate) {
        this.audioRate = Integer.toString(audioRate);
    }

    /**
     * Modifie la taille de la vidéo.
     * 
     * @param width la largeur.
     * @param height la hauteur.
     * @since version 0.96
     */
    @Override
    public void setVideoSize(int width, int height) {
        this.videoSize = Integer.toString(width) + ":" + Integer.toString(height); 
    }

    /**
     * Retourne la durée du fichier en ms.
     * 
     * @param file le fichier.
     * @return la durée du fichier en ms.
     * @since version 0.96 - version 0.99
     */
    @Override
    public long getDuration(File file) {
        duration = -1;
        List<String> list = getTracksInfo(file);
        for(String line : list) {
            if(line.contains(durationProperty)) {
                String split[] = line.split(durationProperty);
                split = split[1].split("\n");
                duration = Utilities.parseStringAsDouble(split[0].trim());
                break;
            }
        }
        list.clear();

        return (long)(duration * 1000);
    }

    /**
     * Retourne les informations des pistes d'un fichier.
     * 
     * @param file le fichier.
     * @return la liste des pistes.
     * @since version 0.96 - version 0.99
     */
    private List<String> getTracksInfo(File file) {
        if(player == null || !player.exists())
            return new ArrayList<String>(0);

        if(file == null || !file.exists())
            return new ArrayList<String>(0);

        StringBuilder command = new StringBuilder(1024);
        command.append(getProtectedName(player.getAbsolutePath()));
        command.append(" -noconsolecontrols -nolirc -noautosub");
        command.append(" -msglevel identify=4 -ao null -vo null -frames 0 ");
        command.append(getProtectedName(file.getAbsolutePath()));

        Edu4Logger.info("mplayer command: " + command.toString());
        duration = -1;

        StringBuilder input = new StringBuilder(1024);
        StringBuilder error = new StringBuilder(1024);
        int exit = executeCommand(command.toString(), null, input, error);
//        fireProcessEnded(exit);

//        if(input.length() > 0)
//            Edu4Logger.info("mplayer standard Message:\n" + input.toString());
//        if(error.length() > 0)
//            Edu4Logger.info("mplayer error Message:\n" + error.toString());

        List<String> list = new ArrayList<String>(4);
        String[] lines = input.toString().split("\n");
        for(String line : lines) {
            if(line.contains(durationProperty)
                    || line.contains(audioCodec) || line.contains(audioProperty)
                    || line.contains(videoCodec) || line.contains(videoProperty)) {
                list.add(line.trim());
            }
        }

        return list;
    }

    /**
     * Détermine si le fichier possède un flux audio.
     * 
     * @param file le fichier.
     * @return si le fichier possède un flux audio.
     * @since version 0.96
     */
    @Override
    public boolean hasAudioSrteam(File file) {
        boolean audio = false;
        List<String> list = getTracksInfo(file);
        for(String line : list) {
            if(line.contains(audioProperty)) {
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
     * @return si le fichier possède un flux vidéo.
     * @since version 0.96
     */
    @Override
    public boolean hasVideoSrteam(File file) {
        boolean video = false;
        List<String> list = getTracksInfo(file);
        for(String line : list) {
            if(line.contains(videoProperty)) {
                video = true;
                break;
            }
        }
        return video;
    }

    /**
     * Conversion de fichiers.
     * La conversion est définie par le type du fichier destination :
     * Types supportés: .wav, .mp3, .mp4, .flv
     *      Paramètres par défaut:
     *          - audio mono à 44,1kHz
     *          - 128kbit/s pour le mp3
     *          - VBR (quality 10) pour le ogg
     *          - taille de video "640x480"
     *          - 25 images par seconde
     *          - pas d'audio pour le flv
     *          - audio en mp3 pour le mp4
     * 
     * @param srcFile le fichier à convertir.
     * @param destFile le fichier de destination.
     * @param tags les tags au format mp3.
     * @return les messages de conversion.
     * @since version 0.96 - version 1.03
     */
    @Override
    public int convert(File destFile, File srcFile, TagList tags) {
        return convert(destFile, srcFile, tags, 44100, 1);
    }

    /**
     * Conversion de fichiers.
     * La conversion est définie par le type du fichier destination :
     * Types supportés: .wav, .mp3, .mp4, .flv
     *      Paramètres par défaut:
     *          - audio mono à 44,1kHz
     *          - 128kbit/s pour le mp3
     *          - VBR (quality 10) pour le ogg
     *          - taille de video "640x480"
     *          - 25 images par seconde
     *          - pas d'audio pour le flv
     *          - audio en mp3 pour le mp4
     * 
     * @param srcFile le fichier à convertir.
     * @param destFile le fichier de destination.
     * @param audioRate la fréquence en Hz.
     * @param channels le nombre de canaux audio.
     * @return les messages de conversion.
     * @since version 0.99 - version 1.03
     */
    private int convert(File destFile, File srcFile, TagList tags,
            int audioRate, int channels) {
        setAudioBitrate(128000);
        setAudioChannels(channels);
        setAudioRate(audioRate);

        String type = Utilities.getExtensionFile(destFile);
        if(type.endsWith(".wav")) {
            return convertToWAV(destFile, srcFile);
        }
        else if(type.endsWith(".mp3")) {
            return convertToMP3(destFile, srcFile, tags);
        }
        else if(type.endsWith(".mp4")) {
            return convertToMP4(destFile, srcFile, null, null, tags);
        }
        else if(type.endsWith(".flv")) {
            setVideoSize(640, 480);
            return convertToFLV(destFile, srcFile);
        }
        else
            return convertToMP4(destFile, srcFile, null, null, null);
    }

    /**
     * Conversion de fichiers.
     * La conversion est définie par le type du fichier destination :
     * Types supportés: .wav, .mp3, .mp4, .flv
     *      Paramètres par défaut:
     *          - audio mono à 44,1kHz
     *          - 128kbit/s pour le mp3
     *          - VBR (quality 10) pour le ogg
     *          - taille de video "640x480"
     *          - 25 images par seconde
     *          - pas d'audio pour le flv
     *          - audio en mp3 pour le mp4
     * 
     * @param destFile le fichier de destination.
     * @param videoFile le fichier pour la piste vidéo.
     * @param audioFile le fichier pour la piste audio.
     * @param subtitleFile le fichier pour les soustitres.
     * @return les messages de conversion.
     * @since version 0.96 - version 1.03
     */
    @Override
    public int convert(File destFile, File audioFile, File videoFile, File subtitleFile,
            TagList tags) {
        setAudioBitrate(128000);
        setAudioChannels(1);
        setAudioRate(44100);

        String type = Utilities.getExtensionFile(destFile);
        if(type.endsWith(".wav")) {
            return convertToWAV(destFile, audioFile);
        }
        else if(type.endsWith(".mp3")) {
            return convertToMP3(destFile, audioFile, tags);
        }
        else if(type.endsWith(".mp4")) {
            return convertToMP4(destFile, audioFile, videoFile, subtitleFile, tags);
        }
        else if(type.endsWith(".flv")) {
            setVideoSize(640, 480);
            return convertToFLV(destFile, videoFile);
        }
        else
            return convertToMP4(destFile, audioFile, videoFile, subtitleFile, tags);
    }

    /**
     * Extrait les pistes audio et vidéo du fichier source au format WAV (mono
     * à 44kHz) et FLV ("640x480", 25 fps)
     * 
     * @param srcFile le fichier source contenant les deux pistes.
     * @param audioFile le fichier de destination pour la piste audio.
     * @param videoFile le fichier de destination pour la piste vidéo.
     * @return les messages de conversion.
     * @since version 0.96 - version 1.03
     */
    @Override
    public int extractToWAVandFLV(File srcFile, File audioFile, File videoFile) {
        setAudioBitrate(128000);
        setAudioChannels(1);
        setAudioRate(44100);
        setVideoSize(640, 480);

        audioFile.delete();
        videoFile.delete();

        StringBuilder audioArgs = new StringBuilder(1024);
        audioArgs.append(getProtectedName(srcFile.getAbsolutePath()));
        audioArgs.append(" -nolirc -noautosub");
        audioArgs.append(" -nocorrect-pts -benchmark -vo null -vc null -novideo");
        audioArgs.append(" -ao pcm:waveheader:fast:file=%");//codec audio
        audioArgs.append(audioFile.getAbsolutePath().length());
        audioArgs.append("%");//codec audio
        audioArgs.append(audioFile.getAbsolutePath());
        audioArgs.append(" -af format=s16le,lavcresample=");//audio rate
        audioArgs.append(audioRate);
        audioArgs.append(",channels=");//canaux audio
        audioArgs.append(audioChannels);

        StringBuilder videoArgs = new StringBuilder(1024);
        videoArgs.append(getProtectedName(srcFile.getAbsolutePath()));
        videoArgs.append(" -noautosub");
        videoArgs.append(" -nosound");//codec audio
        videoArgs.append(" -of lavf -lavfopts format=flv");
        videoArgs.append(" -ovc lavc -lavcopts vcodec=flv:vbitrate=1000:keyint=6");//codec vidéo
        videoArgs.append(" -ofps 24");
        videoArgs.append(" -vf scale=");
        videoArgs.append(videoSize);
        videoArgs.append(",harddup,fixpts");
        videoArgs.append(" -o ");
        videoArgs.append(getProtectedName(videoFile.getAbsolutePath()));

        return extract(audioArgs.toString(), videoArgs.toString());
    }

    /**
     * Insére une vidéo "blanche" (image fixe) sur une vidéo.
     * 
     * @param file la vidéo dans la quelle on insère la vidéo "blanche".
     * @param imageFile l'image fixe à insérer.
     * @param begin le temps de départ de l'insertion dans la vidéo initiale.
     * @param duration la durée de la vidéo blanche à insérer.
     * @since version 0.99 - version 1.03
     */
    @Override
    public int insertBlankVideo(File file, File imageFile, long begin, long duration) {
        File videoFile = new File(tempPath, "videoIMG.flv");
        setProcessingEnd(false);
        int result = createBlankVideo(videoFile, imageFile, duration);
        if(result == 0)
            result = insertVideo(file, videoFile, begin);
        return result;
    }

    /**
     * Duplique la plage donnée de la vidéo et l'insére à la fin de la
     * plage.
     * 
     * @param file la vidéo à modifier.
     * @param begin le temps de départ de la partie à dupliquer.
     * @param end le temps de fin de la partie à dupliquer.
     * @since version 0.99 - version 1.03
     */
    @Override
    public int insertDuplicatedVideo(File file, long begin, long end) {
        File videoFile = new File(tempPath, "videoInsert.flv");
        setProcessingEnd(false);
        int result = extractVideoFile(videoFile, file, begin, end);
        if(result == 0)
            result = insertVideo(file, videoFile, end);
        return result;
    }

    /**
     * Insére une vidéo dans une vidéo.
     * 
     * @param file la vidéo dans la quelle on insère la vidéo.
     * @param insertFile le fichier vidéo à insérer.
     * @param begin le temps de départ de l'insertion dans la vidéo initiale.
     * @since version 0.99 - version 1.03
     */
    @Override
    public int insertVideo(File file, File insertFile, long begin) {
        long videoDuration = getDuration(file);
        File videoBefore = new File(tempPath, "videoBefore.flv");
        File videoAfter = new File(tempPath, "videoAfter.flv");
        int result = extractVideoFile(videoBefore, file, 0, begin);
        if(result == 0)
            result = extractVideoFile(videoAfter, file, begin, videoDuration);
        if(result == 0)
            result = joinVideoFile(file, videoBefore, insertFile, videoAfter);
        setProcessingEnd(true);
        return result;
    }

    /**
     * Crée une vidéo "blanche" (image fixe) d'une durée spécifique.
     * 
     * @param destFile le fichier de destination de la vidéo.
     * @param imageFile l'image fixe à insérer.
     * @param duration la durée de la vidéo blanche.
     * @since version 0.99 - version 1.03
     */
    @Override
    public int createBlankVideo(File destFile, File imageFile, long duration) {
        File imgDirectory = new File(tempPath, "_img_");
        imgDirectory.mkdirs();
        int result = createVideoFile(destFile, duration, imgDirectory, imageFile);
        return result;
    }

    /**
     * Supprime une partie de la vidéo.
     * 
     * @param file la vidéo dans la quelle on supprime une plage de temps.
     * @param begin le temps de départ de la partie à supprimer.
     * @param end le temps de fin de la partie à supprimer.
     * @since version 0.99 - version 1.03
     */
    @Override
    public int removeVideo(File file, long begin, long end) {
        setProcessingEnd(false);
        long videoDuration = getDuration(file);
        File videoBefore = new File(tempPath, "videoBefore.flv");
        File videoAfter = new File(tempPath, "videoAfter.flv");
        int result = extractVideoFile(videoBefore, file, 0, begin);
        if(result == 0)
            result = extractVideoFile(videoAfter, file, end, videoDuration);
        if(result == 0)
            result = joinVideoFile(file, videoBefore, videoAfter);
        setProcessingEnd(true);
        return result;
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
     * @since version 0.99 - version 1.03
     */
    @Override
    public int moveVideoAndResize(File file, File imageFile,
            long begin, long end, long newBegin, long duration) {
        setProcessingEnd(false);
        long videoDuration = getDuration(file);
        File videoBefore = new File(tempPath, "videoBefore.flv");
        File videoAfter = new File(tempPath, "videoAfter.flv");
        File removeVideo = new File(tempPath, "videoRemove.flv");

        int result = extractVideoFile(videoBefore, file, 0, begin);
        if(result == 0)
            result = extractVideoFile(videoAfter, file, end, videoDuration);

        if(duration > (end-begin)) {
            File removeTemp = new File(tempPath, "videoRemoveTemp.flv");
            File blankFile = new File(tempPath, "videoIMG.flv");
            if(result == 0)
                result = extractVideoFile(removeTemp, file, begin, end);
            if(result == 0)
                result = createBlankVideo(blankFile, imageFile, duration-end+begin);
            if(result == 0)
                result = joinVideoFile(removeVideo, removeTemp, blankFile);
        }
        else {
            if(result == 0)
                result = extractVideoFile(removeVideo, file, begin, begin+duration);
        }
        if(result == 0)
            result = joinVideoFile(file, videoBefore, videoAfter);

        if(result == 0)
            result = insertVideo(file, removeVideo, newBegin);
        return result;
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
     * @since version 1.01 - version 1.03
     */
    @Override
    public int setVideoRate(File file, long begin, long end,
            float oldRate, float newRate, File normalFile) {
        setProcessingEnd(false);
        long videoDuration = getDuration(file);
        File videoBefore = new File(tempPath, "videoBefore.flv");
        File videoAfter = new File(tempPath, "videoAfter.flv");
        File insertVideo = new File(tempPath, "videoInsert.flv");

        int result = extractVideoFile(videoBefore, file, 0, begin);
        if(result == 0)
            result = extractVideoFile(videoAfter, file, end, videoDuration);
        
        if(oldRate == 1) {
            if(result == 0)
                result = extractVideoFile(normalFile, file, begin, end);
        }

        if(newRate == 1) {
            insertVideo = normalFile;
        }
        else {
            if(result == 0)
                result = convertToFLV(insertVideo, normalFile, newRate);
        }

        if(result == 0)
            result = joinVideoFile(file, videoBefore, insertVideo, videoAfter);
        setProcessingEnd(true);
        return result;
    }

    /**
     * Conversion d'un fichier en mp3.
     *
     * @param srcFile le fichier à convertir.
     * @param destFile le fichier de destination.
     * @return les messages de conversion.
     * @since version 0.96 - version 1.03
     */
    private int convertToMP3(File destFile, File srcFile, TagList tags) {
        StringBuilder args = new StringBuilder(1024);
        args.append(getProtectedName(srcFile.getAbsolutePath()));
//        int dim = (int) Math.floor(Math.sqrt(srcFile.length()/getDuration(srcFile)*80/3));
//        = 48 quand la source est du wav

        args.append(" -noautosub");
        args.append(" -demuxer rawvideo -rawvideo w=48:h=48 -ovc copy ");
        if(hasCodec_mp3lame())
            args.append(" -of rawaudio -oac mp3lame -lameopts cbr:br=");
        else
            args.append(" -of rawaudio -oac lavc -lavcopts acodec=mp2:abitrate=");
        args.append(audioBitrate);
        args.append(" -af lavcresample=");//audio rate
        args.append(audioRate);
        args.append(",channels=");//canaux audio
        args.append(audioChannels);
        args.append(" -audiofile ");
        args.append(getProtectedName(srcFile.getAbsolutePath()));

        args.append(" -o ");
        args.append(getProtectedName(destFile.getAbsolutePath()));
        int result = convert(encoder, args.toString(), null, null);
        
        if(tags != null) {
            try {
                tags.writeTagsToMp3(destFile);
            } catch(IOException e) {
                Edu4Logger.error(e.getMessage());
            }
        }
        return result;
    }

    /**
     * Conversion d'un fichier en wav.
     *
     * @param srcFile le fichier à convertir.
     * @param destFile le fichier de destination.
     * @return les messages de conversion.
     * @since version 0.96 - version 1.03
     */
    private int convertToWAV(File destFile, File srcFile) {
        destFile.delete();
        StringBuilder args = new StringBuilder(1024);
        args.append(getProtectedName(srcFile.getAbsolutePath()));
        args.append(" -nolirc -noautosub");
        args.append(" -nocorrect-pts -benchmark -vo null -vc null -novideo");
        args.append(" -ao pcm:waveheader:fast:file=%");//codec audio
        args.append(destFile.getAbsolutePath().length());
        args.append("%");//codec audio
        args.append(destFile.getAbsolutePath());
        args.append(" -af format=s16le,lavcresample=");//audio rate
        args.append(audioRate);
        args.append(",channels=");//canaux audio
        args.append(audioChannels);

        return convert(player, args.toString(), null, null);
    }

    /**
     * Conversion d'un fichier en video mp4 et mp3.
     *
     * @param destFile le fichier de destination.
     * @param videoFile  le fichier video à convertir.
     * @param audioFile le fichier audio à convertir.
     * @param subtitleFile le fichier pour les soustitres.
     * @return les messages de conversion.
     * @since version 0.94 - version 1.03
     */
    private int convertToMP4(File destFile, File audioFile, File videoFile,
            File subtitleFile, TagList tags) {
        StringBuilder args = new StringBuilder(1024);

        args.append(getProtectedName(videoFile.getAbsolutePath()));
        args.append(" -noautosub");
        args.append(" -of avi");//format
        args.append(" -ovc lavc -lavcopts vcodec=mpeg4:vbitrate=1000");//codec vidéo
        args.append(" -ofps 24");
        args.append(" -vf scale=");
        args.append(videoSize);
        args.append(",harddup");

        if(audioFile != null && audioFile.exists()) {
            if(hasCodec_mp3lame())
                args.append(" -oac mp3lame -lameopts cbr:br=");
            else
                args.append(" -oac lavc -lavcopts acodec=mp2:abitrate=");
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

        if(subtitleFile != null && subtitleFile.exists()) {
            args.append(" -ffactor 10 -subfont-autoscale 3 -utf8 -subpos 100");
            args.append(" -sub ");
            args.append(getProtectedName(subtitleFile.getAbsolutePath()));
        }
        
        if(tags != null && !tags.isEmpty()) {
            args.append(" -info ");
            String value = tags.getTag(TagList.TITLE);
            if(value != null) {
                args.append("name=");
                args.append(getProtectedName(value));
                args.append(":");
            }
            value = tags.getTag(TagList.ARTIST);
            if(value != null) {
                args.append("artist=");
                args.append(getProtectedName(value));
                args.append(":");
            }
            value = tags.getTag(TagList.GENRE);
            if(value != null) {
                args.append("genre=");
                args.append(getProtectedName(value));
                args.append(":");
            }
            value = tags.getTag(TagList.COMMENT);
            if(value != null) {
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

        return convert(encoder, args.toString(), null, null);
    }

    /**
     * Conversion d'un fichier en video flv sans audio.
     *
     * @param srcFile le fichier à convertir.
     * @param destFile le fichier de destination.
     * @return les messages de conversion.
     * @since version 0.96 - version 1.03
     */
    private int convertToFLV(File destFile, File srcFile) {
        StringBuilder args = new StringBuilder(1024);
        args.append(getProtectedName(srcFile.getAbsolutePath()));
        args.append(" -noautosub");
        args.append(" -nosound");//codec audio
        args.append(" -of lavf -lavfopts format=flv");
        args.append(" -ovc lavc -lavcopts vcodec=flv:vbitrate=1000:keyint=6");//codec vidéo
        args.append(" -ofps 24");
        args.append(" -vf scale=");
        args.append(videoSize);
        args.append(",harddup,fixpts");
        args.append(" -o ");
        args.append(getProtectedName(destFile.getAbsolutePath()));

        return convert(encoder, args.toString(), null, null);
    }

    /**
     * Conversion d'un fichier en video flv sans audio.
     *
     * @param srcFile le fichier à convertir.
     * @param destFile le fichier de destination.
     * @return les messages de conversion.
     * @since version 0.96 - version 1.03
     */
    private int convertToFLV(File destFile, File srcFile, float rate) {
        StringBuilder args = new StringBuilder(1024);
        args.append(getProtectedName(srcFile.getAbsolutePath()));
        args.append(" -noautosub");
        args.append(" -nosound");//codec audio
        args.append(" -of lavf -lavfopts format=flv");
        args.append(" -ovc lavc -lavcopts vcodec=flv:vbitrate=1000:keyint=6");//codec vidéo
        args.append(" -ofps 24");
        args.append(" -speed ");
        args.append(rate);
        args.append(" -vf scale=");
        args.append(videoSize);
        args.append(",harddup,fixpts");
        args.append(" -o ");
        args.append(getProtectedName(destFile.getAbsolutePath()));

        return convert(encoder, args.toString(), null, null);
    }

//    /**
//     * Conversion d'un fichier en video mp4 et mp3.
//     *
//     * @param destFile le fichier de destination.
//     * @param videoFile  le fichier video à convertir.
//     * @return les messages de conversion.
//     * @since version 1.82
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
     * @since version 0.99 - version 1.03
     */
    private boolean hasCodec_mp3lame() {
        String args = " -oac help";
        StringBuilder audioCodecs = new StringBuilder(1024);
        int result = convert(encoder, args, null, audioCodecs);
        return audioCodecs.toString().contains(codec_mp3lame);
    }

    /**
     * Conversion et muliplexage de fichiers.
     *
     * @param binary l'exécutable de conversion.
     * @param args les arguments de la conversion.
     * @param workingDirectory le répertoire de travail (utile pour les images).
     * @return les messages de conversion.
     * @since version 0.96 - version 1.03
     */
    private int convert(File binary, String args, File workingDirectory, StringBuilder out) {
        if(binary == null || !binary.exists()) {
            Edu4Logger.error("Converter binary not found: " + binary);
            return FILE_NOT_FIND;
        }

        StringBuilder command = new StringBuilder(1024);
        command.append(getProtectedName(binary.getAbsolutePath()));
        command.append(" ");
        command.append(args);

        Edu4Logger.info("converter command: " + command.toString());
        duration = -1;

        StringBuilder output = new StringBuilder(1024);
        StringBuilder error = new StringBuilder(1024);

        fireProcessBegin(binary == encoder);
        int exit = executeCommand(command.toString(), workingDirectory, output, error);
        fireProcessEnded(exit);

        if(output.length() > 0) {
            if(out != null)
                out.append(output);
            Edu4Logger.info("converter standard Message:\n" + output.toString());
        }
        if(error.length() > 0)
            Edu4Logger.info("converter error Message:\n" + error.toString());

        if(output.length() > 0)
            return SUCCESS;
        else
            return CONVERSION_ERROR;
    }

    /**
     * Extraction et conversion des pistes audio et vidéo du fichier.
     *
     * @param audioArgs les arguments de la convertion de la piste audio.
     * @param videoArgs les arguments de la convertion de la piste vidéo.
     * @return les messages de conversion.
     * @since version 0.96 - version 1.03
     */
    private int extract(String audioArgs, String videoArgs) {
        if(player == null || !player.exists()) {
             Edu4Logger.error("Converter binary not found: " + player);
            return FILE_NOT_FIND;
        }
        if(encoder == null || !encoder.exists()) {
             Edu4Logger.error("Converter binary not found: " + encoder);
            return FILE_NOT_FIND;
        }

        StringBuilder command = new StringBuilder(1024);
        command.append(getProtectedName(player.getAbsolutePath()));
        command.append(" ");
        command.append(audioArgs);

        Edu4Logger.info("mplayer command: " + command.toString());
        duration = -1;

        StringBuilder output = new StringBuilder(1024);
        StringBuilder error = new StringBuilder(1024);

        fireProcessBegin(false);
        int exit = executeCommand(command.toString(), null, output, error);

        if(output.length() > 0)
            Edu4Logger.info("extract audio standard Message:\n" + output.toString());
        if(error.length() > 0)
            Edu4Logger.info("extract audio error Message:\n" + error.toString());
        if(exit != 0) {
            return CONVERSION_ERROR;
        }

        command = new StringBuilder(1024);
        output = new StringBuilder(1024);
        error = new StringBuilder(1024);
        command.append(getProtectedName(encoder.getAbsolutePath()));
        command.append(" ");
        command.append(videoArgs);

        Edu4Logger.info("mencoder command: " + command.toString());
        duration = -1;

        fireProcessBegin(true);
        exit = executeCommand(command.toString(), null, output, error);
        fireProcessEnded(exit);

        if(output.length() > 0)
            Edu4Logger.info("extract video standard Message:\n" + output.toString());
        if(error.length() > 0)
            Edu4Logger.info("extract video Message:\n" + error.toString());

        if(output.length() > 0)
            return SUCCESS;
        else
            return CONVERSION_ERROR;
    }

    /**
     * Crée un fichier vidéo d'une durée détermenée au format mp4 à partir des
     * images au format png contenues dans le répertoire de travail.
     * 
     * @param destFile le fichier de destination.
     * @param duration la durée de la vidéo.
     * @param workingDirectory le répertoire contenant les images.
     * @param imageFile 
     * @return les messages de conversion.
     * @since version 0.96 - version 1.03
     */
    private int createVideoFile(File destFile, long duration,
            File workingDirectory, File imageFile) {
        
        String extension = Utilities.getExtensionFile(imageFile);
        for(int i=0; i<10; i++) {
            Utilities.fileCopy(imageFile, new File(workingDirectory,
                    "img" + i + extension));
        }

        double nb = 0;
        String names[] = workingDirectory.list();
	if(names == null) {
            Edu4Logger.error("error: no file");
	    return FILE_NOT_FIND;
	}
        for(String name : names) {
            if(name.toLowerCase().endsWith(".png")
                    || name.toLowerCase().endsWith(".jpg"))
                nb++;
        }
        if(nb == 0) {
            Edu4Logger.error("error: no image file");
	    return FILE_NOT_FIND;
	}
 
        StringBuilder args = new StringBuilder(1024);
        args.append("mf://*");
        args.append(extension);
        args.append(" -mf fps=");
        args.append(String.format("%1$.12f", (1000*nb/duration)));
        args.append(" -of lavf -lavfopts format=flv");
        args.append(" -oac copy -ovc lavc -lavcopts vcodec=flv:vbitrate=1000:keyint=6");//codec vidéo
        args.append(" -ofps 24");
        args.append(" -vf scale=");
        args.append(videoSize);
        args.append(",harddup");
        args.append(" -o ");
        args.append(getProtectedName(destFile.getAbsolutePath()));

        return convert(encoder, args.toString(), workingDirectory, null);
    }

    /**
     * Joint plusieurs fichier vidéo en un seul.
     * 
     * @param destFile le fichier de destination.
     * @param srcFiles les différents fichiers sources.
     * @return les messages de conversion.
     * @since version 0.96 - version 1.03
     */
    private int joinVideoFile(File destFile, File... srcFiles) {
        StringBuilder args = new StringBuilder(1024);
        for(File file : srcFiles) {
            if(file != null && file.exists()) {
                args.append(" ");
                args.append(getProtectedName(file.getAbsolutePath()));
            }
        }

        args.append(" -of lavf -lavfopts format=flv");
        args.append(" -oac copy -ovc copy");//codec vidéo
        args.append(" -o ");
        args.append(getProtectedName(destFile.getAbsolutePath()));

        return convert(encoder, args.toString(), null, null);
    }

    /**
     * Extrait une partie d'une vidéo au format flv.
     * 
     * @param destFile le fichier vidéo de destination.
     * @param srcFile le fichier vidéo source.
     * @param begin le temps de départ de la partie à extraire (en ms).
     * @param end le temps de fin de la partie à extraire (en ms).
     * @return les messages de conversion.
     * @since version 0.96 - version 1.03
     */
    private int extractVideoFile(File destFile, File srcFile, long begin, long end) {
        StringBuilder args = new StringBuilder(1024);
        args.append(getProtectedName(srcFile.getAbsolutePath()));
        args.append(" -of lavf -lavfopts format=flv");
        args.append(" -nosound -ovc copy");
        args.append(String.format(" -ss %1$d.%2$d", (begin/1000), (begin%1000)));
        args.append(String.format(" -endpos %1$d.%2$d", ((end-begin)/1000), ((end-begin)%1000)));
        args.append(" -o ");
        args.append(getProtectedName(destFile.getAbsolutePath()));

        return convert(encoder, args.toString(), null, null);
    }

    /**
     * Execute une commande native.
     *
     * @param command la commande.
     * @param workingDirectory le répertoire de travail.
     * @param output un StringBuilder initialisé pour afficher la sortie standard.
     * @param error un StringBuilder initialisé pour afficher la sortie des erreur.
     * @return la valeur de sortie du processus résultat de la commande.
     * @since version 0.96 - version 0.99
     */
    private int executeCommand(String command, File workingDirectory,
            StringBuilder output, StringBuilder error) {
        if(Constants.LINUX_PLATFORM) {
            return executeCommand(new String[]{"/bin/sh", "-c", command},
                    workingDirectory, output, error);
        }

        StringTokenizer tokenizer = new StringTokenizer(command);
	String[] cmdarray = new String[tokenizer.countTokens()];
 	for(int i = 0; tokenizer.hasMoreTokens(); i++) {
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
     * @return la valeur de sortie du processus résultat de la commande.
     * @since version 0.99
     */
    private int executeCommand(String[] command, File workingDirectory,
            StringBuilder output, StringBuilder error) {
        int end = -1;
        Runtime runtime = Runtime.getRuntime();
        process = null;

        try {
            process = runtime.exec(command, null, workingDirectory);
            Thread outputThread = createReadThread(process.getInputStream(),
                    output);
            Thread errorThread = createReadThread(process.getErrorStream(),
                    error);
            outputThread.start();
            errorThread.start();

            try {
                end = process.waitFor();
            } catch(InterruptedException e) {
                Edu4Logger.error(e);
            }
        } catch(IOException e) {
            Edu4Logger.error(e);
        }

        if(process != null) {
            process.destroy();
        }

        return end;
    }

    /**
     * Créer une thread de traitement d'un flux.
     *
     * @param inputStream le flux à gérer.
     * @param output un StringBuilder initialisé pour afficher la sortie.
     * @return la thread de gestion du flux.
     * @since version 0.96 - version 0.99
     */
    private Thread createReadThread(final InputStream inputStream,
            final StringBuilder output) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                byte[] data = new byte[1024];
                try {
                    int cnt = inputStream.read(data);
                    while(cnt > 0) {
                        output.append(new String(data, 0, cnt));
                        fireNewData(new String(data, 0, cnt));
                        cnt = inputStream.read(data);
                    }
                } catch(IOException e) {
                    Edu4Logger.error(e);
                }
            }//end run
        };
        return thread;
    }

    /**
     * Traitement des nouvelles données pour déterminer le pourcentage de
     * conversion.
     *
     * @param data les nouvelles données.
     * @since version 0.96 - version 0.99
     */
    private void fireNewData(String data) {
        if(data.contains(durationProperty)) {
            String split[] = data.split(durationProperty);
            if(split.length > 1) {
                split = split[1].split("\n");
                duration =  Utilities.parseStringAsDouble(split[0].trim());
            }
        }
        if(data.contains("%")) {
            String split[] = data.split("\\(|%");
            if(split.length > 1)
                firePercentChanged(Utilities.parseStringAsInt(split[1].trim()));
        }
    }

    /**
     * Encode les caractères spéciaux (espace, accent) pour qu'ils soient
     * lisibles dans une URL pour le passage d'un programme à un autre.
     *
     * @param name le nom du fichier.
     * @return le nom protégé.
     * @since version 0.96
     */
    private String getProtectedName(String name) {
        if(!name.contains(" "))
            return name;
        if(Constants.WINDOWS_PLATFORM)
            return "\"" + name + "\"";
        else
            return name.replace(" ", "\\ ");
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
//        final eestudio.gui.ProcessingBar processingBar = new eestudio.gui.ProcessingBar();
//
//        processingBar.addWindowListener(new java.awt.event.WindowAdapter() {
//            @Override
//            public void windowClosing(java.awt.event.WindowEvent e) {
//                converter.cancel();
//            }
//        });
//
//        ProgessListener listener = new ProgessListener() {
//            @Override
//            public void processBegin(Object source, boolean determinated) {
//                processingBar.processBegin(determinated,
//                        "Conversion", "Conversion de " + srcFile);
//            }
//            @Override
//            public void processEnded(Object source, int exit) {
//                processingBar.close();
////                println("end statut: " + exit);
//            }
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
////        converter.setVideoRate(srcFile, begin, (long) (begin+(end-begin)/rate),
////                rate, 1f, normalRate);
//
//        long elapseTime = System.nanoTime();
//        println("temps: " + (elapseTime-initTime) + " ns");
//        println("temps: " + (elapseTime-initTime)/1000000 + " ms");
//    }
//
//    private static void println(Object message) {
//        System.out.println(message);
//    }

}//end
