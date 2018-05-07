package eestudio;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.event.EventListenerList;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import eestudio.audio.Player;
import eestudio.audio.ProcessingListener;
import eestudio.audio.Recorder;
import eestudio.utils.Converter;
import eestudio.utils.Edu4Logger;
import eestudio.utils.TagList;
import eestudio.utils.Utilities;
import eestudio.utils.Wave;
import eestudio.utils.XMLUtilities;

/*
 * v0.95: ajout de private final File videoFileTemp;
 * v0.95: ajout de private FFMPEGutilities ffmpeg; pour la conversion de fichier
 * v0.95: supp de private Index currentMediaIndex; [gestion dans interface]
 * v0.95: supp de private MediaPlayer mediaPlayer; [gestion vidéos dans flash]
 * v0.95: modif de private Collection<Listener> listeners en private final
 *        EventListenerList listeners; -> modification de tous les fireXXX
 * v0.95: modif de private static final long TIME_MAX : de 30 min à 60 min
 * v0.95: modif de private static final long TIME_TEMP_MAX : de 6 min à 10 min
 * v0.95: ajout de private void fireInsertVoiceTimeChanged(long time)
 * v0.95: ajout de private void fireAudioDataChanged()
 * v0.95: ajout de private void fireAudioWaveFileChanged(File leftChannelFile,
 *        File rigthChannelFile)
 * v0.95: ajout de private void fireVideoFileChanged(File file)
 * v0.95: ajout de public void setProtectedTime(long time) en remplacement de
 *        setPosition(double position) pour la sécurisation de la modif du temps
 * v0.95: ajout de public void recordOnRange(long begin, long end)
 * v0.95: ajout de public Iterator<Index> indexesIterator()
 * v0.95: supp de private long getTimeAt(double position)
 * v0.95: supp de public int getAudioVolume()
 * v0.95: supp de private void fireCurrentIndexChanged(Index index)
 * v0.95: supp de public void setMediaRate(double position)
 * v0.95: supp de public void setMediaRateToNormal()
 * v0.95: supp de private void setRate(float rate)
 * v0.95: supp de public String getMediaIndexesXMLDescription()
 * v0.95: supp de public long getMediaIndexDuration(double position)
 * v0.95: supp de public void setTimeBeginIndex(double position)
 * v0.95: supp de public void setTimeEndIndex(double position)
 * v0.95: supp de public long getMaximalLengthFor(double position)
 * v0.95: supp de public void addMediaSubtitleIndex(double position)
 * v0.95: supp de public boolean addIndexAfterIndex(double position, long length)
 * v0.95: supp de public boolean addRepeatMediaIndex(double position)
 * v0.95: supp de public void setPosition(double position) (voir ci-dessous)
 * v0.95: supp de public void playOnIndex(double position)
 * v0.95: supp de public void recordOnIndex(double position)
 * v0.95: supp de private boolean updateSubtitleFile()
 * v0.95: supp de private void mediaPlayerInit()
 * v0.95: supp de private void mediaPlayerClose()
 * v0.95: supp de public void mediaPlayerSetVideoOutput(Canvas canvas)
 * v0.95: supp de private void mediaPlayerSetSubtitleFile(String fileName)
 * v0.95: supp de private boolean mediaPlayerSetFile(File file)
 * v0.95: supp de private void mediaPlayerPlay()
 * v0.95: supp de private void mediaPlayerPause()
 * v0.95: supp de private void mediaPlayerSetTime(long time)
 * v0.95: supp de public Index getMediaIndex(int indice)
 * v0.95: supp de public int getMediaIndexIndice(long time)
 * v0.95: modif de public Core() en public Core(File ffmepConverter):
 * v0.95: modif de private void initValues() [supp listeners;
 *        ajout ProcessingListener sur recorder et player]
 * v0.95: modif de public void closeApplication() [supp mediaPlayer]
 * v0.95: modif de public void eraseProject() pour tout réinitialiser
 * v0.95: modif de void addListener(Listener listener) [prise en compte ffmpeg]
 * v0.95: modif de void removeListener(Listener listener) [prise en compte ffmpeg]
 * v0.95: modif de fireIndexesChanged() en fireIndexesChanged(Indexes indexes)
 * v0.95: modif de getMediaIndex(double position) en getIndex(long time)
 * v0.95: modif de getMediaIndexIndice(double position) en getMediaIndexIndice(long time)
 * v0.95: modif de onMediaIndex(double position) en onIndex(long time)
 * v0.95: modif de removeMediaIndex(double position) en removeIndex(long time)
 * v0.95: modif de addMediaIndexAt(double position, ..) en addIndexAt(long time, ..)
 * v0.95: modif de addFileIndex(double position, ..) en addFileIndex(long time, ..)
 * v0.95: modif de addVoiceRecord(double position) en addVoiceRecord(long time)
 * v0.95: modif de updateData(Index newIndex, Index oldIndex) [supp mediaPlayer]
 * v0.95: modif de setTime(long time) (supp currentIndex et mediaPlayer)
 * v0.95: modif de timeToZero() [setPosition(0) en setProtectedTime(0))
 * v0.95: modif de loadFile(File file, double position) en loadFile(File file, long time)
 * v0.95: modif de loadSubtitleFile(File file) [supp mediaPlayer]
 * v0.95: modif de saveSubtitleFile(File file) [format LRC]
 * v0.95: modif de audioPlay() [supp mediaPlayer]
 * v0.95: modif de audioPause() [supp mediaPlayer]
 * v0.95: modif de audioRecord() [supp mediaPlayer]
 * v0.95: modif de executeConverter(File srcFile, File destFile) [modif
 *        convertisseur de VLC par FFMPEG]
 * v0.95: modif de eraseIndexRecord(double position) en eraseIndexRecord(long time)
 * v0.95: modif de setByteBufferLimit(ByteBuffer byteBuffer, long time)
 * v0.95: modif de fireProcessTimeChanged(long time) [prise en compte du
 *        changement du temps lors de l'insertion de voix]
 * v0.95: modif de loadText(File file) [conversion rtf -> html]
 * v0.95: modif de saveText(File file) [conversion html -> rtf]
 * 
 * v0.96: ajout de private MEncoderUtilities converter;
 * v0.96: supp de private FFMPEGutilities ffmpeg;
 * v0.96: supp de private final String defaultFileName;
 * v0.96: modif de private int audioVolume (50 -> 75)
 * v0.96: ajout de private void eraseInsertionDataIndexes()
 * v0.96: ajout de public void removeIndex(List<Long> ids)
 * v0.96: ajout de public void cancelConversion()
 * v0.96: supp de public String getDefaultFileName()
 * v0.96: supp de public void eraseProject()
 * v0.96: supp de public boolean onIndex(long time)
 * v0.96: supp de private boolean loadSubtitleFile(File file)
 * v0.96: supp de public boolean saveFile(File file)
 * v0.96: modif de Core(File ffmpegConverter) en Core(File converter, File player)
 *        throws Exception
 * v0.96: modif de initValues() [ajout de throws Exception]
 * v0.96: modif de eraseIndexes() [supp données insérées, mise à jour temps max]
 * v0.96: modif de fireTextLoaded(String text) en
 *        fireTextLoaded(String text, boolean styled)
 * v0.96: modif de void removeIndex(long id) [public -> private, supp de
 *        fireIndexesChanged(mediaIndexes);]
 * v0.96: modif de updateData(Index newIndex, Index oldIndex) en
 *        updateData(Index newIndex, Index oldIndex, boolean update)
 * v0.96: modif de setRecordTimeMax(long time) [public -> private]
 * v0.96: modif de loadProject(ProjectFiles project) [effacement des données
 *        lors de chargement de nouvelles, videoOriginalFile, supp index en dernier]
 * v0.96: modif de loadFile(...) en insertFile(...)
 * v0.96: modif de loadText(File file) [fireTextLoaded avec boolean styled]
 * v0.96: modif de loadAudio(File file, ByteBuffer byteBuffer) [gestion de
 *        BufferOverflowException, flip() et write = limit() dans finally]
 * v0.96: modif de saveProject(File file, ProjectFiles project, String soft) en
 *        saveProject(File file, ProjectFiles project) [soft dans project, sous-
 *        titres en SRT si vidéo (même poue easyLab), converter avec fichier de
 *        sous-titres, save videoOriginal pour easyLab]
 * v0.96: modif de saveText(File file) [public -> private]
 * v0.96: modif de saveVideo(File file) [ne fait plus rien]
 * v0.96: modif de converter(File destFile, File audioFile, File videoFile,
 *        String soft) en converter(File destFile, File audioFile, File videoFile,
 *        File subtitleFile, String soft)
 * v0.96: modif de fireProcessTimeChanged(long time) [public -> private]
 * v0.96: modif de fireProcessEnded(final boolean running) [public -> private]
 * 
 * v0.97: ajout de private static final String videoImagePath
 *        = "eestudio/resources/images/videoImage.jpg";
 * v0.97: supp de private boolean onIndex = false;
 * v0.97: ajout de private void insertBlankVideo(long begin, long duration)
 * v0.97: ajout de private void insertDuplicatedVideo(long begin, long end)
 * v0.97: ajout de private void insertVideo(long begin, File file)
 * v0.97: ajout de private void createBlankVideo(File destFile, long duration)
 * v0.97: ajout de private void removeVideo(long begin, long end)
 * v0.97: ajout de private void moveVideoAndResize(long begin, long end,
 *        long newBegin, long duration)
 * v0.97: supp de private void audioRecord()
 * v0.97: supp de private boolean loadDiaporama(File file)
 * v0.97: supp de private boolean saveDiaporama(File file)
 * v0.97: supp de private boolean saveVideo(File file)
 * v0.97: supp de private synchronized void waitInMillisecond(long millisecond)
 * v0.97: modif de void setMediaIndex(...) en public Index setMediaIndex(...)
 * v0.97: modif de addFileIndex(long time, File file) [gestion vidéo]
 * v0.97: modif de addVoiceIndex(long time) [gestion vidéo, état RECORDING_INSERT]
 * v0.97: modif de updateData(...) [gestion vidéo]
 * v0.97: modif de setProtectedTime(long time) [gestion état RECORDING_INSERT]
 * v0.97: modif de playOnRange(long begin, long end) [supp onIndex]
 * v0.97: modif de public void recordOnRange(long begin, long end) [supp
 *        onIndex et supp audioRecord()]
 * v0.97: modif de saveProject(File file, ProjectFiles project) [supp saveVideo();
 *        sauvegarde images originales]
 * v0.97: modif de audioPlay() [gestion état RECORDING_INSERT]
 * v0.97: modif de audioPause() [gestion état RECORDING_INSERT]
 * v0.97: modif de fireProcessTimeChanged(long time) [état RECORDING_INSERT]
 * v0.97: modif de private void fireProcessEnded(final boolean running) [gestion
 *        état RECORDING_INSERT]
 * v0.97: modif de loadAudio(File file, ByteBuffer byteBuffer) [test du format
 *        de fichier avant, supp flip() dans finally]
 * 
 * v0.98: modif de saveProject(File file, ProjectFiles project) [export en
 *        simples fichiers et effacement des fichiers dans le répertoire de
 *        destination]
 * 
 * v0.99: supp de implements Constants
 * v0.99: ajout de private TagList tags;
 * v0.99: modif de MEncoderUtilities converter en private Converter converter;
 * v0.99: ajout de public Mp3Tags getTags()
 * v0.99: ajout de public void setTags(Mp3Tags tags)
 * v0.99: modif de Core(File converter, File player) en Core(Converter converter)
 * v0.99: modif de initValues() [tags]
 * v0.99: modif de addFileIndex(long time, File file) [boolean for tag]
 * v0.99: modif de loadAudio(File file, ByteBuffer byteBuffer) [boolean for tag]
 * v0.99: modif de loadVideo(File file) [boolean for tag]
 * v0.99: modif de saveProject(File file, ProjectFiles project) [boolean for tag]
 * v0.99: modif de saveAudio(File file) [boolean for tag]
 * v0.99: modif de converter(File, File) en converter((File, File, boolean withTags)
 * v0.99: modif de converter(File, File, File, File, String) [tags]
 * v0.99: modif de insertBlankVideo(long begin, long duration) [Converter]
 * v0.99: modif de insertDuplicatedVideo(long begin, long end) [Converter]
 * v0.99: modif de insertVideo(long begin, File file) [Converter]
 * v0.99: modif de createBlankVideo(File destFile, long duration) [Converter]
 * v0.99: modif de removeVideo(long begin, long end) [Converter]
 * v0.99: modif de moveVideoAndResize(begin, end, newBegin, duration) [Converter]
 * 
 * v1.00: modif de saveProject(File file, ProjectFiles project) [add tagFile]
 * v1.00: modif de loadProject(ProjectFiles project) [add tagFile]
 * v1.00: modif de insertFile(File file, long time) [supp Utilities.getFileType]
 * 
 * v1.01: supp de public static final float RATE_MIN = 0.5f;
 * v1.01: supp de public static final float RATE_MAX = 2.0f;
 * v1.01: ajout de public boolean hasStudentRecordIndex()
 * v1.01: ajout de private void setIndexAudioRate(Index index, float oldRate,
 *        float newRate)
 * v1.01: ajout de private void saveAudioData(byte[] data, float rate, File file)
 * v1.01: ajout de private void setIndexVideoRate(Index index, float oldRate,
 *        float newRate)
 * v1.01: modif de setMediaIndex(...) en setMediaIndex(..., float speed) [speed]
 * v1.01: modif de updateData(...) [speed]
 * v1.01: modif de loadProject(ProjectFiles project) [gestion durées différentes]
 * v1.01: modif de saveProject(File file, ProjectFiles project) [test création
 *        du répertoire]
 * v1.01: modif de insertBlankVideo(long begin, long duration) [vidéo du flash]
 * v1.01: modif de insertDuplicatedVideo(long begin, long end) [vidéo du flash]
 * v1.01: modif de moveVideoAndResize(...) [vidéo du flash]
 * v1.01: modif de fireProcessEnded(...) [test running pour mettre en pause]
 */

/**
 * Noyau de l'application.
 * 
 * @author Fabrice Alleau
 * @since version 0.94
 * @version 1.01
 */
public class Core {
    /** Chemin du fichier beep */
    private static final String beepPath = "eestudio/resources/beep.wav";
    /** Chemin du fichier image pour créer une vidéo */
    private static final String videoImagePath
            = "eestudio/resources/images/videoImage.jpg";

    /** Chemin du dossier temporarire */
    private final File tempPath;
    /** Fichier de sauvegarde temporaire de l'audio */
    private final File audioFileTemp;
    /** Fichier de sauvegarde temporaire de la vidéo */
    private final File videoFileTemp;

    /** Fichiers pour le projet */
    private ProjectFiles projectFiles;
    /** Sauvegarde des écoutes sur différents éléments */
    private final EventListenerList listeners;
    /** Indexes multimédia */
    private Indexes mediaIndexes;
    /** Tags au format mp3 */
    private TagList tags;

    /** Format Audio pour la capture du microphone */
    private AudioFormat audioFormat;
    /** Stockage des données audio à enregistrer */
    private ByteBuffer audioBuffer;
    /** Stockage des données audio temporaires */
    private ByteBuffer tempBuffer;

    /** Etat du lecteur/enregistreur */
    private int runningState = Constants.PAUSE;
    /** Volume pour la lecture des données audio (de 0 à 100) */
    private int audioVolume = 75;
    /** Sauvegarde du temps courant en millisecondes */
    private long currentTime = 0;
    /** Sauvegarde du temps maximum d'enregistrement en millisecondes */
    private long recordTimeMax = 0;
    /** Temps d'allocation maximum en millisecondes (=60 min) */
    public static final long TIME_MAX = 1000*60 * 60;
    /** Temps d'allocation maximum en millisecondes pour les insertions (=10 min) */
    private static final long TIME_TEMP_MAX = 1000*60 * 10;
    /** Temps où l'on met en pause */
    private long stopTime = recordTimeMax;

    /** Thread pour la lecture de la piste élève */
    private Player player;
    /** Thread pour enregistrer les données du microphone */
    private Recorder recorder;
//    /** Indique si on sur une lecture ou enregistrement d'une plage donnée */
//    private boolean onIndex = false;

    /** Document pour l'utilisation de style dans la zone de texte */
    private StyledDocument styledDocument;
    /** Editeur de texte avec gestion de style de type RTF */
    private StyledEditorKit styledEditorKit;

    /** Utilitaire pour les conversions des media */
    private Converter converter;

    /** Taille des buffer utilisés */
    private static final int BUFFER_SIZE = 1024*64;

    /**
     * Initialisation avec le répertoire de travail.
     *
     * @param converter l'utilitaire pour les conversions.
     * @throws Exception
     * @since version 0.94 - version 0.99
     */
    public Core(Converter converter) throws Exception {
        listeners = new EventListenerList();

        tempPath = new File(System.getProperty("java.io.tmpdir"), "edu4");
        tempPath.mkdirs();

        audioFileTemp = new File(tempPath, "audioTemp.wav");
        videoFileTemp = new File(tempPath, "videoTemp.flv");

        this.converter = converter;

        initValues();
    }

    /**
     * Initialise les valeurs par défaut.
     *
     * @throws Exception
     * @since version 0.94 - version 0.99
     */
    private void initValues() throws Exception {
        //gestion du projet
        projectFiles = new ProjectFiles();
        //gestion des index
        mediaIndexes = new Indexes();
        mediaIndexes.setMediaLength(recordTimeMax);
        
        tags = new TagList();

        //Editeur de texte
        styledEditorKit = new HTMLEditorKit();
        styledDocument = new HTMLDocument();

        //Initialisation du format audio
        setAudioFormat(null);
        initByteBuffers();

        ProcessingListener listener = new ProcessingListener() {
            @Override
            public void timeChanged(long newTime) {
                fireProcessTimeChanged(newTime);
            }

            @Override
            public void endProcess(boolean running) {
                fireProcessEnded(running);
            }
        };

        recorder = new Recorder(audioBuffer, audioFormat);
        player = new Player(audioBuffer, audioFormat);
        setAudioVolume(audioVolume);

        recorder.addListener(listener);
        player.addListener(listener);
    }//end initValues()

    /**
     * Ferme l'application.
     *
     * @since version 0.94 - version 0.95
     */
    public void closeApplication() {
        recorder.close();
        player.close();
        System.exit(0);
    }

    /**
     * Efface tous les index
     * 
     * @since version 0.95 - version 0.96
     */
    public void eraseIndexes() {
        if(mediaIndexes.getIndexesCount() > 0) {
            if(projectFiles.getAudioFile() != null
                    || projectFiles.getVideoFile() != null)
                eraseInsertionDataIndexes();

            mediaIndexes.removeAll();
            fireIndexesChanged(mediaIndexes);
            setRecordTimeMaxByIndexes();
            projectFiles.setIndexesFile(null);
        }
    }

    /**
     * Efface tous les données des index insérés.
     * 
     * @since version 0.96
     */
    private void eraseInsertionDataIndexes() {
        Iterator<Index> it = mediaIndexes.iterator();
        while(it.hasNext()) {
            Index index = it.next();
            if(index.isTimeLineModifier()) {
                updateData(null, index, false);
            }
        }
        fireAudioDataChanged();
    }

    /**
     * Retourne le temps d'enregistrement maximum possible.
     *
     * @return le temps d'enregistrement maximum possible.
     * @since version 0.94
     */
    public long getRecordTimeMax() {
        return recordTimeMax;
    }

    /**
     * Retourne le temps courrant.
     *
     * @return le temps actuel en ms.
     * @since version 0.94
     */
    public long getCurrentTime() {
        return currentTime;
    }

    /**
     * Met à jour l'état du module audio.
     * Il peut prendre les valeurs suivantes: <code>StudentCore.PAUSE</code>,
     * <code>StudentCore.PLAYING</code> ou <code>StudentCore.RECORDING</code>.
     *
     * @param state la nouvelle valeur de l'état du module audio.
     * @since version 0.94
     */
    private void setRunningState(int state) {
        runningState = state;
        fireRunningStateChanged(state);
    }

    /**
     * Met à jour le volume du lecteur du module audio.
     *
     * @param value la nouvelle valeur comprise entre 0 et 100.
     * @since version 0.94
     */
    public void setAudioVolume(int value) {
        audioVolume = value;
        player.setVolume(value);
    }

    /**
     * Change le mode mute du module audio.
     *
     * @since version 0.94
     */
    public void toggleAudioMute() {
        setAudioVolume((audioVolume==0) ? 50 : 0);
    }

    /**
     * Retourne le document sauvegardant le texte de la zone de texte.
     *
     * @return le document sauvegardant le texte.
     * @since version 0.94
     */
    public StyledDocument getStyledDocument() {
        return styledDocument;
    }

    /**
     * Retourne l'éditeur gérant le texte de la zone de texte.
     *
     * @return l'éditeur gérant le texte.
     * @since version 0.94
     */
    public StyledEditorKit getStyledEditorKit() {
        return styledEditorKit;
    }

    /**
     * Retourne la liste des tags MP3.
     * 
     * @return la liste des tags MP3.
     * @since version 0.99
     */
    public TagList getTags() {
        return tags;
    }

    /**
     * Modifie la liste des tags MP3.
     * 
     * @param tags la liste des tags MP3.
     * @since version 0.99
     */
    public void setTags(TagList tags) {
        this.tags.removeAll();
        this.tags = tags;
    }

    /**
     * Ajoute d'une écoute de type StudentListener.
     *
     * @param listener l'écoute à ajouter.
     * @since version 0.94 - version 0.96
     */
    public void addListener(Listener listener) {
        listeners.add(Listener.class, listener);
        converter.addListener(listener);
    }

    /**
     * Enlève une écoute de type StudentListener.
     *
     * @param listener l'écoute à enlever.
     * @since version 0.94 - version 0.96
     */
    public void removeListener(Listener listener) {
        listeners.remove(Listener.class, listener);
        converter.removeListener(listener);
    }

    /**
     * Notification du changement d'état.
     *
     * @param state le nouvel état.
     * @since version 0.94 - version 0.95
     */
    private void fireRunningStateChanged(int state) {
        for(Listener listener : listeners.getListeners(Listener.class)) {
            listener.runningStateChanged(state);
        }
    }

    /**
     * Notification du changement du temp.
     *
     * @param time le nouveau temps.
     * @since version 0.94 - version 0.95
     */
    private void fireTimeChanged(long time) {
        for(Listener listener : listeners.getListeners(Listener.class)) {
            listener.timeChanged(time);
        }
    }

    /**
     * Notification du changement du temp lors de l'insertion de la voix.
     *
     * @param time le nouveau temps.
     * @since version 0.95
     */
    private void fireInsertVoiceTimeChanged(long time) {
        for(Listener listener : listeners.getListeners(Listener.class)) {
            listener.insertVoiceTimeChanged(time);
        }
    }

    /**
     * Notification du changement de la durée maximum d'enregistrement.
     *
     * @param recordTimeMax le temps maximum d'enregistrement.
     * @since version 0.94 - version 0.95
     */
    private void fireRecordTimeMaxChanged(long recordTimeMax) {
        for(Listener listener : listeners.getListeners(Listener.class)) {
            listener.recordTimeMaxChanged(recordTimeMax);
        }
    }

    /**
     * Notification de changement dans les index d'enregistrement.
     *
     * @param indexes la liste d'index.
     * @since version 0.94 - version 0.95
     */
    private void fireIndexesChanged(Indexes indexes) {
        for(Listener listener : listeners.getListeners(Listener.class)) {
            listener.indexesChanged(XMLUtilities.getXMLDescription(indexes));
        }
    }

    /**
     * Notification de modification des données audio.
     *
     * @since version 0.95
     */
    private void fireAudioDataChanged() {
        File leftChannelFile = new File(tempPath,
                "waveLeft." + Wave.imageExtension);
        File rigthChannelFile = new File(tempPath,
                "waveRigth." + Wave.imageExtension);
        Wave.createWaveImages(leftChannelFile, rigthChannelFile,
                audioFormat, audioBuffer, 0, recordTimeMax);
        fireAudioWaveFileChanged(leftChannelFile, rigthChannelFile);
    }

    /**
     * Notification du changement des représentations graphiques de l'audio.
     *
     * @param leftChannelFile le nouveau fichier pour le canal droit.
     * @param rigthChannelFile le nouveau fichier pour le canal gauche.
     * @since version 0.95
     */
    private void fireAudioWaveFileChanged(
            File leftChannelFile, File rigthChannelFile) {
        for(Listener listener : listeners.getListeners(Listener.class)) {
            listener.audioWaveFileChanged(leftChannelFile, rigthChannelFile);
        }
    }

    /**
     * Notification du changement du fichier vidéo.
     *
     * @param file le nouveau fichier vidéo.
     * @since version 0.95
     */
    private void fireVideoFileChanged(File file) {
        for(Listener listener : listeners.getListeners(Listener.class)) {
            listener.videoFileChanged(file);
        }
    }

    /**
     * Notification qu'un texte a été chargé.
     *
     * @param text le texte chargé.
     * @param styled indique si le texte comporte des styles.
     * @since version 0.94 - version 0.96
     */
    private void fireTextLoaded(String text, boolean styled) {
        for(Listener listener : listeners.getListeners(Listener.class)) {
            listener.textLoaded(text, styled);
        }
    }

    /**
     * Notification que l'image du module multimédia a changé.
     *
     * @param image la nouvelle image.
     * @since version 0.94 - version 0.95
     */
    private void fireImageChanged(BufferedImage image) {
        for(Listener listener : listeners.getListeners(Listener.class)) {
            listener.imageChanged(image);
        }
    }

    /**
     * Retourne le nombre d'index de la piste multimédia.
     *
     * @return le nombre d'index.
     * @since version 0.94
     */
    public int getIndexesCount() {
        return mediaIndexes.getIndexesCount();
    }

    /**
     * Retourne un iterateur sur les Index.
     *
     * @return un iterateur sur les Index.
     * @since version 0.95
     */
    public Iterator<Index> indexesIterator() {
        return mediaIndexes.iterator();
    }

    /**
     * Retourne l'index correspondant à l'identifiant.
     *
     * @param id l'identifiant.
     * @return l'index correspondant à l'identifiant.
     * @since version 0.95
     */
    public Index getIndexWithId(long id) {
        return mediaIndexes.getIndexWithId(id);
    }

    /**
     * Indique si la liste contient un index où l'élève doit s'enregistrer.
     * 
     * @return si la liste contient un index où l'élève doit s'enregistrer.
     * @since version 1.01
     */
    public boolean hasStudentRecordIndex() {
        return mediaIndexes.hasStudentRecordIndex();
    }

    /**
     * Retourne l'index qui comprent le temps.
     *
     * @param time le temps.
     * @return l'index contenant le temps désiré.
     * @since version 0.95
     */
    public Index getIndexAtTime(long time) {
        return mediaIndexes.getIndexAtTime(time);
    }

    /**
     * Retourne le temps minimum pour le temps de départ de l'index.
     *
     * @param index l'index.
     * @return le temps minimum pour le début de l'index.
     * @since version 0.95
     */
    public long getMinimalTimeBefore(Index index) {
        return mediaIndexes.getMinimalTime(index);
    }

    /**
     * Retourne le temps maximum pour le temps de fin de l'index.
     *
     * @param index l'index.
     * @return le temps maximum pour le temps de fin de l'index.
     * @since version 0.95
     */
    public long getMaximalTimeAfter(Index index) {
        return mediaIndexes.getMaximalTime(index);
    }

    /**
     * Retourne l'index précédent.
     *
     * @param index l'index courant.
     * @return l'index précédent.
     * @since version 0.95
     */
    public Index previousIndex(Index index) {
        return mediaIndexes.previousIndex(index);
    }

    /**
     * Retourne l'index suivant.
     *
     * @param index l'index courant.
     * @return l'index suivant.
     * @since version 0.95
     */
    public Index nextIndex(Index index) {
        return mediaIndexes.nextIndex(index);
    }

    /**
     * Trie par ordre chronologique les index.
     *
     * @since version 0.95
     */
    public void sortIndexes() {
        mediaIndexes.sortIndexes();
    }

    /**
     * Ajoute un index sans valeur de type lecture.
     *
     * @return l'id
     * @since version 0.95
     */
    public long addNullIndex() {
        return mediaIndexes.addNullIndex();
    }

    /**
     * Enlève l'index au temps indiqué.
     *
     * @param time le temps.
     * @since version 0.95
     */
    public void removeIndexAtTime(long time) {
        Index index = mediaIndexes.removeIndexAtTime(time);
        updateData(null, index, true);
        fireIndexesChanged(mediaIndexes);
    }

    /**
     * Enlève l'index avec l'identifiant indiqué.
     *
     * @param id l'identifiant de l'index.
     * @since version 0.95 - version 0.96
     */
    private void removeIndex(long id) {
        Index index = mediaIndexes.getIndexWithId(id);
        mediaIndexes.removeIndex(index);
        updateData(null, index, false);
    }

    /**
     * Enlève les index avec la liste d'identifiants indiquée.
     *
     * @param ids la liste d'identifiants de l'index.
     * @since version 0.96
     */
    public void removeIndex(List<Long> ids) {
        for(long id : ids) {
            removeIndex(id);
        }
        fireAudioDataChanged();
        fireIndexesChanged(mediaIndexes);
    }

    /**
     * Efface les index dont les bords sont inférieurs sont égaux.
     *
     * @since version 0.95
     */
    public void removeNullIndex() {
        mediaIndexes.removeNullIndex();
        fireIndexesChanged(mediaIndexes);
    }

    /**
     * Modifie un index.
     *
     * @param id l'id de l'index.
     * @param begin le temps de départ.
     * @param end le temps de fin.
     * @param type le type d'index.
     * @param subtitle le soustitre associé.
     * @param speed la vitesse de l'index.
     * @return le nouvel index.
     * @since version 0.95 - version 1.01
     */
    public Index setMediaIndex(long id, long begin, long end, String type,
            String subtitle, float speed) {
        Index oldIndex = mediaIndexes.getIndexWithId(id);
        if(oldIndex == null)
            return null;

        //changement dans les donnée: type modifiant la durée et à bouger
        boolean dataMove = (oldIndex.isTimeLineModifier()
                && (begin != oldIndex.getInitialTime() || end != oldIndex.getFinalTime()));
        //changement dans les donnée: changement de type modifiant des données
        boolean dataChanged
                = (oldIndex.isBlankType() && !oldIndex.getType().contentEquals(type))
                || (speed > 0 && speed != oldIndex.getRate());

        Index newIndex = null;
        try {
            newIndex = (Index) oldIndex.clone();
        } catch(CloneNotSupportedException e) {
            Edu4Logger.error(e);
        }

        if(newIndex == null) {
            newIndex = oldIndex;
            dataMove = false;
            dataChanged = false;
        }

        newIndex.setInitialTime(begin);
        newIndex.setFinalTime(end);
        newIndex.setType(type);
        newIndex.setSubtitle(subtitle);
        if(speed > 0)
            newIndex.setRate(speed);

        mediaIndexes.removeIndex(oldIndex);
        mediaIndexes.addIndex(newIndex);

        if(dataMove || dataChanged)
            updateData(newIndex, oldIndex, true);

        fireIndexesChanged(mediaIndexes);
        return newIndex;
    }

    /**
     * Ajoute un index.
     *
     * @param begin le temps de départ.
     * @param end le temps de fin.
     * @param type le type d'index.
     * @param subtitle le soustitre associé.
     * @param fileName le fichier pour un index d'insetion de fichier.
     * @return la réussite.
     * @since version 0.95
     */
    private boolean addMediaIndex(long begin, long end, String type,
            String subtitle, String fileName) {
        Index index = new Index(type);
        if(fileName != null)
            index = new IndexFile(fileName);
        index.setInitialTime(begin);
        index.setFinalTime(end);
        index.setType(type);
        index.setSubtitle(subtitle);
        boolean added = mediaIndexes.addIndex(index);
        updateData(index, null, true);
        fireIndexesChanged(mediaIndexes);
        return added;
    }

    /**
     * Ajoute un index au temps indiqué.
     *
     * @param time le temps.
     * @param length la durée de l'index.
     * @param type le type de l'index.
     * @param subtitle le soustitre
     * @return la réussite.
     * @since version 0.95
     */
    public boolean addMediaIndexAt(long time, long length, String type,
            String subtitle) {
        if(recordTimeMax == 0) {
            setRecordTimeMax(length);
        }

        long begin = time;
        if(begin < 0)
            begin = currentTime;
        long end = begin + length;
        return addMediaIndex(begin, end, type, subtitle, null);
    }

    /**
     * Ajoute un index d'insetion de fichier au temps donné.
     *
     * @param time le temps.
     * @param file le fichier.
     * @return la réussite.
     * @since version 0.95 - version 0.99
     */
    private int addFileIndex(long time, File file) {
        long begin = time;
        long intialDuration = recordTimeMax;
        long duration = 0;
        int write;

        //extraction de la piste audio
        if(hasAudioSrteam(file)) {
            write = loadAudio(file, tempBuffer);
            if(write > 0){
                write = insertData(begin, tempBuffer);
                if(write > 0){
                    duration = (long) (tempBuffer.limit() / audioFormat.getFrameSize()
                            / audioFormat.getSampleRate() * 1000);

                    addMediaIndex(begin, begin+duration, Index.FILE, null, file.getAbsolutePath());
                }
            }
        }
        else {
            duration = getFileDuration(file);

            //remplisage de blanc de l'index courant
            int nbSample = (int) (duration/1000.0f * audioFormat.getSampleRate());
            int nbBytes = nbSample * audioFormat.getFrameSize();
            byte data[] = new byte[nbBytes];
            write = insertData(begin, data);
            if(write > 0){
                duration = (long) (tempBuffer.limit() / audioFormat.getFrameSize()
                        / audioFormat.getSampleRate() * 1000);

                addMediaIndex(begin, begin+duration, Index.FILE, null, file.getAbsolutePath());
            }
        }

        if(hasVideoSrteam(file)) {
            //si pas encore de préssence de vidéo création d'une vidéo
            if(projectFiles.getVideoFile() == null) {
                createBlankVideo(videoFileTemp, intialDuration);
                projectFiles.setVideoFile(file.getAbsolutePath());
            }
            //todo test si déjà vidéo
            File videoFile = new File(tempPath, "videoInsert.flv");
            converter(videoFile, file, false);
            insertVideo(begin, videoFile);
        }
        else if(projectFiles.getVideoFile() != null && duration > 0) {
            insertBlankVideo(begin, duration);
        }

        return write;
    }

    /**
     * Ajoute un index de voix du professeur au temps précisé en
     * déclanchant l'enregistrement du microphone dans une thread séparée.
     *
     * @param time le temps de départ de l'enregistrement.
     * @since version 0.95
     */
    public void addVoiceRecord(long time) {
        final long begin;
        if(time < 0)
            begin = currentTime;
        else
            begin = time;

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                int success = addVoiceIndex(begin);
            }
        });
        thread.start();
    }

    /**
     * Ajoute un index de voix du professeur au temps précisé.
     *
     * @param time le temps de départ de l'enregistrement.
     * @return la réussite.
     * @since version 0.95 - version 0.97
     */
    private int addVoiceIndex(long time) {
        tempBuffer.clear();
        recorder.setBuffer(tempBuffer);
        //Modification de l'état des boutons du module audio
        setRunningState(Constants.RECORDING_INSERT);
        recorder.start(0, Math.min(TIME_TEMP_MAX, getRemainingTime()));

        while(recorder.isAlive()) {
            Utilities.waitInMillisecond(100);
        }

        recorder.setBuffer(audioBuffer);

        tempBuffer.flip();
        int write = insertData(time, tempBuffer);
        if(write > 0) {
            long lenght = (long) (tempBuffer.limit() / audioFormat.getFrameSize()
                    / audioFormat.getSampleRate() * 1000);
            addMediaIndex(time, time+lenght, Index.VOICE, null, null);

            if(projectFiles.getVideoFile() != null) {
                insertBlankVideo(time, lenght);
            }
        }

        return write;
    }

    /**
     * Actualise les index avec la modifiction.
     *
     * @param newIndex le nouveau index.
     * @param oldIndex l'ancien index.
     * @param update option pour avertir la modification des données.
     * @since version 0.95 - version 1.01
     */
    private void updateData(Index newIndex, Index oldIndex, boolean update) {
        if(newIndex == null && oldIndex == null)
            return;
        
        int write = 0;
//        long speedOffset = 0;
        //Mise à jour de la bande audio
        if(oldIndex == null) {//Ajout d'un Index
            if(newIndex.isBlankType()) {
                //remplisage de blanc de l'index courant
                int nbSample = (int) ((newIndex.getFinalTime()-newIndex.getInitialTime())
                        /1000.0f * audioFormat.getSampleRate());
                int nbBytes = nbSample * audioFormat.getFrameSize();
                byte data[] = new byte[nbBytes];
                write = insertData(newIndex.getInitialTime(), data);
                if(projectFiles.getVideoFile() != null) {
                    insertBlankVideo(newIndex.getInitialTime(),
                            newIndex.getFinalTime()-newIndex.getInitialTime());
                }
            }
            else if(newIndex.getType().contentEquals(Index.REPEAT)) {
                long end = newIndex.getInitialTime();
                long begin = end - newIndex.getLength();
                //remplisage de blanc de l'index courant
                int sample = (int) (begin/1000.0f * audioFormat.getSampleRate());
                int offset = sample * audioFormat.getFrameSize();
                int nbSample = (int) ((end-begin)/1000.0f * audioFormat.getSampleRate());
                int nbBytes = nbSample * audioFormat.getFrameSize();
                byte[] data = new byte[nbBytes];
                audioBuffer.position(offset);
                audioBuffer.get(data);
                write = insertData(end, data);
                if(projectFiles.getVideoFile() != null) {
                    insertDuplicatedVideo(begin, end);
                }
            }
            else if(newIndex.getRate() != Index.NORMAL_RATE) {
                setIndexAudioRate(newIndex, Index.NORMAL_RATE, newIndex.getRate());
                
                if(projectFiles.getVideoFile() != null) {
                    setIndexVideoRate(newIndex, Index.NORMAL_RATE, newIndex.getRate());
                }
//                speedOffset = -newIndex.getLength();
                long newEnd = (long) (newIndex.getInitialTime()
                        + newIndex.getLength() / newIndex.getRate());
                newIndex.setFinalTime(newEnd);
//                speedOffset += newEnd;
            }
        }
        else if(newIndex == null) {//supp d'un index
            if(oldIndex.getRate() != Index.NORMAL_RATE) {
                setIndexAudioRate(oldIndex, oldIndex.getRate(), Index.NORMAL_RATE);

                if(projectFiles.getVideoFile() != null) {
                    setIndexVideoRate(oldIndex, oldIndex.getRate(), Index.NORMAL_RATE);
                }
//                speedOffset = -oldIndex.getLength();
                long newEnd = (long) (oldIndex.getInitialTime()
                        + oldIndex.getLength() / oldIndex.getRate());
                oldIndex.setFinalTime(newEnd);
//                speedOffset += newEnd;
            }
            else if(oldIndex.isTimeLineModifier()) {
                removeData(oldIndex.getInitialTime(), oldIndex.getFinalTime());
                if(projectFiles.getVideoFile() != null) {
                    removeVideo(oldIndex.getInitialTime(), oldIndex.getFinalTime());
                }
            }
        }
        else if(oldIndex.getRate() != Index.NORMAL_RATE
                || newIndex.getRate() != Index.NORMAL_RATE) {
            //modification de la vitesse et possiblement des bornes
            long begin = oldIndex.getInitialTime();
            long end = oldIndex.getFinalTime();
            long newBegin = newIndex.getInitialTime();
            long newEnd = newIndex.getFinalTime();
            
            if(begin == newBegin && end == newEnd) {
                //changement uniquemenent de vitesse
                setIndexAudioRate(newIndex, oldIndex.getRate(), newIndex.getRate());
                if(projectFiles.getVideoFile() != null)
                    setIndexVideoRate(newIndex, oldIndex.getRate(), newIndex.getRate());

                newEnd = (long) (newIndex.getInitialTime() +
                        newIndex.getLength() * oldIndex.getRate() / newIndex.getRate());
                newIndex.setFinalTime(newEnd);
            }
            else {
                //changement de positon et changement de vitesse: on repasse
                //l'ancien index en vitesse normale si nécessaire, puis on passe
                //le nouvel index à la nouvelle vitesse si nécessaire
                float oldRate = oldIndex.getRate();

                if(oldRate != Index.NORMAL_RATE) {
                    setIndexAudioRate(oldIndex, oldIndex.getRate(), Index.NORMAL_RATE);
                    if(projectFiles.getVideoFile() != null)
                        setIndexVideoRate(oldIndex, oldIndex.getRate(), Index.NORMAL_RATE);

                    //calcul borne inférieure du nouvel index
                    if(newBegin > end) {
                        //nb' = nb + L' - L = nb + L * r - L = nb + L * (r - 1)
                        newBegin = (long) (newBegin + (end-begin) * (oldRate-1));
                    } else if(newBegin > begin) {
                        //nb' = nb+Li'-Li = nb+Li*(r-1) = nb+(nb-b)*(r-1) = b+(nb-b)*r
                        newBegin = (long) (begin + (newBegin-begin) * oldRate);
                    }
                    newIndex.setInitialTime(newBegin);

                    //calcul borne suppérieure du nouvel index
                    if(newEnd > end)
                        newEnd = (long) (newEnd + (end-begin) * (oldRate-1));
                    else if(newEnd > begin)
                        newEnd = (long) (begin + (newEnd-begin) * oldRate);
                    newIndex.setFinalTime(newEnd);
                }

                if(oldRate != Index.NORMAL_RATE) {
                    setIndexAudioRate(newIndex, Index.NORMAL_RATE, newIndex.getRate());
                    if(projectFiles.getVideoFile() != null)
                        setIndexVideoRate(newIndex, Index.NORMAL_RATE, newIndex.getRate());

                    newEnd = (long) (newIndex.getInitialTime()
                            + newIndex.getLength() / newIndex.getRate());
                    newIndex.setFinalTime(newEnd);
                }
            }
        
        }
        else if(newIndex.isTimeLineModifier() || oldIndex.isTimeLineModifier()) {
            //Modification d'un index d'insertion autre que la vitesse

            //récupération des données de l'ancien index
            byte[] data = removeData(oldIndex.getInitialTime(), oldIndex.getFinalTime());
            tempBuffer.clear();

            //calcul du nombre de byte du nouvel index
            int nbSample = (int) (newIndex.getLength()/1000.0f * audioFormat.getSampleRate());
            int nbBytes = nbSample * audioFormat.getFrameSize();

            if(nbBytes < data.length) {//diminution de l'index
                tempBuffer.put(data, 0, nbBytes);
            } else {//alongement de l'index
                tempBuffer.put(data);
                int supp = nbBytes - data.length;
                if(supp > 0){
                    //ajout des données supplémentaires vierges
                    tempBuffer.put(new byte[supp]);
                }
            }
            tempBuffer.flip();
            write = insertData(newIndex.getInitialTime(), tempBuffer);

            if(projectFiles.getVideoFile() != null) {
                moveVideoAndResize(oldIndex.getInitialTime(), oldIndex.getFinalTime(),
                        newIndex.getInitialTime(), newIndex.getLength());
            }
        }

        if(newIndex!=null && newIndex.getType().contentEquals(Index.BLANK_BEEP)) {
            insertBeep(newIndex.getInitialTime());
        }

        setRecordTimeMaxByIndexes();

        //notification du changement des données
        if(update && ((oldIndex!=null && oldIndex.isTimeLineModifier())
                || (newIndex!=null && newIndex.isTimeLineModifier()))) {
            fireAudioDataChanged();
        }
    }

    /**
     * Modifie le temps courant en millisecondes.
     * Doit être appelé en mode pause sauf pour les threads de déplacement du
     * curseur temps.
     *
     * @param time le temps (en ms) où l'on doit se rendre.
     * @since version 0.95
     */
    private void setTime(long time) {
        currentTime = time;
        fireTimeChanged(time);
    }

    /**
     * Modifie le temps courant en millisecondes.
     *
     * @param time le temps (en ms) où l'on doit se rendre.
     * @since version 0.95 - version 0.97
     */
    public void setProtectedTime(long time) {
        boolean play = (runningState == Constants.PLAYING);

        //si piste élève active on stop l'activité élève en sauvegardant l'activité
        if(runningState != Constants.PAUSE) {
            audioPause();
        }

        setTime(time);

        //redémarge de l'activité précédente si c'était la lecture
        if(play)
            audioPlay();
    }

    /**
     * Met le temps à zéro.
     *
     * @since version 0.95
     */
    public void timeToZero() {
        setProtectedTime(0);
    }

    /**
     * Lecture d'une plage de temps.
     *
     * @param begin le début de la plage.
     * @param end la fin de la plage.
     * @since version 0.95 - version 0.97
     */
    public void playOnRange(long begin, long end) {
        stopTime = end;
        setTime(begin);
//        onIndex = true;//pour dire que l'on lit une plage de temps
        audioPlay();
    }

    /**
     * Enregistrement sur une plage de temps.
     *
     * @param begin le début de la plage.
     * @param end la fin de la plage.
     * @since version 0.95 - version 0.97
     */
    public void recordOnRange(long begin, long end) {
        if(runningState != Constants.PAUSE) {
            audioPause();
        }//end if

        setTime(begin);
        stopTime = end;
//        onIndex = true;//pour dire que l'on lit une plage de temps

        //Modification de l'état des boutons du module audio
        setRunningState(Constants.RECORDING);
        recorder.start(currentTime, stopTime);
    }

    /**
     * Change la valeur maximale de la barre de défilement.
     *
     * @param time la nouvelle valeur en en ms.
     * @since version 0.95
     */
    private void setRecordTimeMax(long time) {
        mediaIndexes.setMediaLength(time);
        long timeWithIndex = mediaIndexes.getLength();

        if(timeWithIndex > TIME_MAX)
            timeWithIndex = TIME_MAX;

        recordTimeMax = timeWithIndex;
        stopTime = recordTimeMax;

        setByteBufferLimit(audioBuffer, recordTimeMax);
        fireRecordTimeMaxChanged(recordTimeMax);
    }

    /**
     * Change la valeur maximale de la barre de défilement.
     *
     * @since version 0.95
     */
    private void setRecordTimeMaxByIndexes() {
        long time = mediaIndexes.getLength();

        if(time > TIME_MAX)
            time = TIME_MAX;

        recordTimeMax = time;
        stopTime = recordTimeMax;

        setByteBufferLimit(audioBuffer, recordTimeMax);
        fireRecordTimeMaxChanged(recordTimeMax);
    }

    /**
     * Retourne la durée maximale de la bande.
     * 
     * @return la durée maximale de la bande.
     * @since version 0.95
     */
    public long getDurationMax() {
        return TIME_MAX;
    }

    /**
     * Retourne la durée maximale restante de la bande.
     * 
     * @return la durée maximale restante.
     * @since version 0.95
     */
    public long getRemainingTime() {
        return TIME_MAX - recordTimeMax;
    }

    /**
     * Retourne la durée maximale pour une insertion.
     * 
     * @return la durée maximale pour une insertion.
     * @since version 0.95
     */
    public long getInsertionDurationMax() {
        return TIME_TEMP_MAX;
    }

    /**
     * Charge les fichiers contenus dans le projet.
     * Si un fichier est null, les données correspondantes sont effacées.
     * 
     * @param project l'ensembles des fichiers à charger.
     * @return la réussite du chargement.
     * @since version 0.95 - version 1.01
     */
    public boolean loadProject(ProjectFiles project) {
        boolean success = true;
        long length = -1;

        //si plus de texte ou nouveau texte, on efface l'ancien
        if(project.getTextFile() == null
                || !project.getTextFile().startsWith(Constants.textDefaultExtension)) {
            eraseText();
        } else {
            //mise à null pour garder l'ancien
            project.setTextFile(null);
        }

        //si plus de piste vidéo ou nouvelle piste vidéo, on efface l'ancienne
        if(project.getVideoFile() == null
                || !project.getVideoFile().startsWith(Constants.videoDefaultExtension)) {
            eraseVideo();
            if(project.getVideoOriginalFile() != null)
                project.setVideoFile(project.getVideoOriginalFile());
        } else {
            //mise à null pour garder l'ancien
            project.setVideoFile(null);
            length = getFileDuration(new File(projectFiles.getVideoFile()));
        }

        //si plus de piste audio ou nouvelle piste audio, on efface l'ancienne
        if(project.getAudioFile() == null
                || !project.getAudioFile().startsWith(Constants.audioDefaultExtension)) {
            eraseAudio();
        } else {
            //mise à null pour garder l'ancien
            project.setAudioFile(null);
            length = recordTimeMax;
        }

        if(project.getIndexesFile() == null
                || !project.getIndexesFile().startsWith(Constants.indexesExtension)) {
            eraseIndexes();
        } else {
            //mise à null pour garder l'ancien
            project.setIndexesFile(null);
        }
        
        if(project.getTagFile() == null
                || !project.getTagFile().startsWith(Constants.tagExtension)) {
            tags.removeAll();
        } else {
            //mise à null pour garder l'ancien
            project.setTagFile(null);
        }

        if(project.getVideoFile() != null && project.getAudioFile() != null
                && project.getVideoFile().contentEquals(project.getAudioFile())) {
            File srcFile = new File(project.getVideoFile());
            fireVideoFileChanged(null);
            extract(srcFile, audioFileTemp, videoFileTemp);
            if(hasVideoSrteam(videoFileTemp)) {
                boolean loaded = loadVideo(videoFileTemp);
                success &= loaded;
                if(loaded) {
                    projectFiles.setVideoFile(srcFile.getAbsolutePath());
                }
            }
            if(hasAudioSrteam(audioFileTemp)) {
                int loaded = loadAudio(audioFileTemp, audioBuffer);
                success &= (loaded>0);
                if(loaded > 0) {
                    projectFiles.setAudioFile(srcFile.getAbsolutePath());
                    long lenght = (long) (audioBuffer.limit() / audioFormat.getFrameSize()
                            / audioFormat.getSampleRate() * 1000);
                    setRecordTimeMax(lenght);
                    fireAudioDataChanged();
                }
            }
            project.setAudioFile(null);
            project.setVideoFile(null);
        }

        if(project.getVideoFile() != null) {
            File videoFile = new File(project.getVideoFile());
            long videoLength = getFileDuration(videoFile);
            //si l'ancienne durée n'est valide ou supérieure à la durée de la
            //vidéo, on la remplace par la durée de la vidéo
            if(length < 0 || length > videoLength)
                length = videoLength;

            boolean loaded = loadVideo(videoFile);
            success &= loaded;
            if(loaded) {
                projectFiles.setVideoFile(videoFile.getAbsolutePath());
                
                if(project.getAudioFile() == null) {
                    setRecordTimeMax(length);
                    fireAudioDataChanged();
                }
            }
        }

        if(project.getAudioFile() != null) {
            File audioFile = new File(project.getAudioFile());
            int loaded = loadAudio(audioFile, audioBuffer);
            success &= (loaded>0);
            if(loaded > 0) {
                projectFiles.setAudioFile(audioFile.getAbsolutePath());
                long audioLength = (long) (audioBuffer.limit() / audioFormat.getFrameSize()
                        / audioFormat.getSampleRate() * 1000);
                //si l'ancienne durée n'est valide ou supérieure à la durée de
                //l'audio, on la remplace par la durée de l'audio
                if(length < 0 || length > audioLength) {
                    length = audioLength;
                }
                setRecordTimeMax(length);
                fireAudioDataChanged();
            }
        }

        if(project.getTextFile() != null) {
            File textFile = new File(project.getTextFile());
            boolean loaded = loadText(textFile);
            success &= loaded;
        }

        if(project.getIndexesFile() != null) {
            File indexesFile = new File(project.getIndexesFile());
            boolean loaded = loadIndexes(indexesFile);
            success &= loaded;
        }

        if(project.getTagFile() != null) {
            File tagFile = new File(project.getTagFile());
            tags = XMLUtilities.loadTags(tagFile);
        }

//        if(project.getSubtitleFile() != null) {
//            File subtitleFile = new File(project.getSubtitleFile());
//            boolean loaded = loadSubtitleFile(subtitleFile);
//            success &= loaded;
//        }

        return success;
    }

    /**
     * Charge d'un fichier au temps donné.
     *
     * @param file le fichier.
     * @param time le temps.
     * @return <code>true<\code> si le chargement s'est bien passé.
     * @since version 0.95 - version 1.00
     */
    public boolean insertFile(File file, long time) {
        long begin = time;
        if(begin < 0)
            begin = currentTime;

        if(!file.exists())
            return false;

        boolean success;
        if(Utilities.isTextFile(file)) {
            success = loadText(file);
        }
        else if(Utilities.isImageFile(file)) {
            success = loadImage(file);
        }
        else  {
            int cnt = mediaIndexes.getIndexesCount();
            if(cnt > 0 || projectFiles.getAudioFile() != null) {
                int write = addFileIndex(begin, file);
                success = (write > 0);
            }
            else {
                success = loadFile(file);
            }
        }
        return success;
    }

    /**
     * Charge d'un fichier.
     *
     * @param file le fichier.
     * @return <code>true<\code> si le chargement s'est bien passé.
     * @since version 0.95
     */
    private boolean loadFile(File file) {
        boolean success = false;

        if(hasAudioSrteam(file) && hasVideoSrteam(file)) {
            fireVideoFileChanged(null);
            extract(file, audioFileTemp, videoFileTemp);
            if(hasVideoSrteam(videoFileTemp)) {
                boolean loaded = loadVideo(videoFileTemp);
                success &= loaded;
                if(loaded) {
                    projectFiles.setVideoFile(file.getAbsolutePath());
                }
            }
            if(hasAudioSrteam(audioFileTemp)) {
                int loaded = loadAudio(audioFileTemp, audioBuffer);
                success &= (loaded>0);
                if(loaded > 0) {
                    projectFiles.setAudioFile(file.getAbsolutePath());
                    long lenght = (long) (audioBuffer.limit() / audioFormat.getFrameSize()
                            / audioFormat.getSampleRate() * 1000);
                    setRecordTimeMax(lenght);
                    fireAudioDataChanged();
                }
            }
        }
        else if(hasVideoSrteam(file)) {
            boolean loaded = loadVideo(file);
            success &= loaded;
            if(loaded) {
                projectFiles.setVideoFile(file.getAbsolutePath());
            }
        }
        else if(hasAudioSrteam(file)) {
            int loaded = loadAudio(file, audioBuffer);
            success &= (loaded>0);
            if(loaded > 0) {
                projectFiles.setAudioFile(file.getAbsolutePath());
                long lenght = (long) (audioBuffer.limit() / audioFormat.getFrameSize()
                        / audioFormat.getSampleRate() * 1000);
                setRecordTimeMax(lenght);
                fireAudioDataChanged();
            }
        }        
        return success;
    }

    /**
     * Charge un fichier d'index.
     *
     * @param file le fichier.
     * @return <code>true<\code> si le chargement s'est bien passé.
     * @since version 0.95
     */
    private boolean loadIndexes(File file) {
        mediaIndexes = Utilities.getIndexes(file);
        projectFiles.setIndexesFile(file.getAbsolutePath());
        setRecordTimeMaxByIndexes();
        fireIndexesChanged(mediaIndexes);
        return true;
    }

    /**
     * Chargement d'une image.
     *
     * @param file le fichier image.
     * @return la réussite du chargement.
     * @since version 0.95
     */
    private boolean loadImage(File file) {
        boolean success;
        try {
            BufferedImage image = ImageIO.read(file);
            success = true;
            fireImageChanged(image);
        } catch(IOException e) {
            Edu4Logger.error(e);
            fireImageChanged(null);
            return false;
        }//end try

        return success;
    }

    /**
     * Charge un fichier texte dans la zone de texte.
     *
     * @param file le fichier à charger.
     * @return <code>true<\code> si le chargement s'est bien passé.
     * @since version 0.95 - version 0.96
     */
    public boolean loadText(File file) {
        if(!file.exists())
            return false;

        eraseText();
        boolean load = false;
        projectFiles.setTextFile(file.getAbsolutePath());

        boolean styled = false;
        String text = null;

        if(Utilities.isTextStyledFile(file)) {
            if(Utilities.getExtensionFile(file).contentEquals(Constants.RTF_extension)) {
                File htmlFile = new File(tempPath, file.getName()+Constants.HTML_extension);
                boolean converted = Utilities.rtf2html(file, htmlFile);
                if(converted) {
                    load = loadTextStyled(htmlFile);
                }
            } else {
                load = loadTextStyled(file);
            }
            styled = load;
        }
        else {
            //pour le charset en UTF-8
            text = Utilities.getTextInFile(file, "UTF-8");

            //pour le charset par défaut de windows
            if(text == null || text.isEmpty()) {
                text = Utilities.getTextInFile(file, "windows-1252");
            }//end if

            if(text != null) {
                load = true;
            }
        }
        fireTextLoaded(text, styled);
        return load;
    }

    /**
     * Charge un fichier de type RTF dans la zone de texte.
     *
     * @param file le fichier à charger.
     * @return <code>true<\code> si le chargement s'est bien passé.
     * @since version 0.95
     */
    private boolean loadTextStyled(File file) {
        try {
            //effacement
            styledDocument.remove(0, styledDocument.getLength());
            FileInputStream fileInputStream = new FileInputStream(file);
            //insertion
            styledEditorKit.read(fileInputStream, styledDocument, 0);
   	    fileInputStream.close();
	} catch(Exception e) {
            Edu4Logger.error(e);
            return false;
        }//end try

        return true;
    }

    /**
     * Charge un fichier audio.
     *
     * @param file le fichier a charger.
     * @return <code>true<\code> si le chargement s'est bien passé.
     * @since version 0.95 - version 0.99
     */
    private int loadAudio(File file, ByteBuffer byteBuffer) {
        //Java ne décode que le .wav
        if(!Constants.WAV_extension.equalsIgnoreCase(Utilities.getExtensionFile(file))) {
            converter(audioFileTemp, file, false);
            return loadAudio(audioFileTemp, byteBuffer);
        }//end if

        AudioInputStream audioInputStream;
        //Récupération du flux audio 
        try {
            audioInputStream = AudioSystem.getAudioInputStream(file);

            if(audioInputStream == null)
                return -1;

            //convesion du fichier si son format ne correspond pas à celui utilisé
            AudioFormat fileAudioFormat = audioInputStream.getFormat();
            if(!fileAudioFormat.matches(audioFormat)) {
                try {
                   audioInputStream.close();
                } catch(IOException e) {
                    Edu4Logger.error(e);
                }
                converter(audioFileTemp, file, false);
                return loadAudio(audioFileTemp, byteBuffer);
            }//end if

            //on quitte si le fichier est plus grand que la mémoire réservée
            if(audioInputStream.available() > byteBuffer.capacity()) {
                try {
                   audioInputStream.close();
                } catch(IOException e) {
                    Edu4Logger.error(e);
                }
                return -byteBuffer.capacity();
            }
        } catch(IOException e) {
            Edu4Logger.error(e);
            return -1;
        } catch(UnsupportedAudioFileException e) {
            Edu4Logger.error(e);
            return -1;
        }

        byteBuffer.clear();

        try {
            int cnt = 0;
            byte[] buffer = new byte[BUFFER_SIZE];
            //enregistrement des données audio dans le ByteBuffer.
            while(cnt != -1) {
                cnt = audioInputStream.read(buffer);
                if(cnt > 0)
                    byteBuffer.put(buffer, 0, cnt);
            }//end while
        } catch(IOException e) {
            Edu4Logger.error(e);
        } catch(BufferOverflowException e) {
            Edu4Logger.error(e);
        } finally {
            try {
                if(audioInputStream != null)
                    audioInputStream.close();
            } catch(IOException e) {
                Edu4Logger.error(e);
            }
        }

        byteBuffer.flip();
        return byteBuffer.limit();
    }//end loadAudio(File file, ByteBuffer byteBuffer)

    /**
     * Charge un fichier vidéo.
     * Conversion de la vidér en format lisible par le flash sans audio.
     *
     * @param file le fichier a charger.
     * @return <code>true<\code> si le chargement s'est bien passé.
     * @since version 0.95 - version 0.99
     */
    private boolean loadVideo(File file) {
        if(!file.exists())
            return false;
        //Flash ne décode que du flv ??
        //si on a un fichier video de charger on le décharge pour que
        //ffmpeg puisse convertir dans le fichier utilisé par flash
        if(projectFiles.getVideoFile() != null)
            fireVideoFileChanged(null);

        if(file != videoFileTemp)
            converter(videoFileTemp, file, false);

        fireVideoFileChanged(videoFileTemp);

        return true;
    }

    /**
     * Sauvegarde le projet dans un fihier dans le répertoire donné (les
     * références sont relatives).
     *
     * @param file le fichier projet.
     * @param project les éléments à sauvegarder.
     * @return <code>true<\code> si la sauvegarde s'est bien passée.
     * @since version 0.95 - version 1.01
     */
    public boolean saveProject(File file, ProjectFiles project) {
        String soft = project.getSoft();
        if(soft == null)
            soft = Constants.COMMON_SOFT;
        //le nom sans extension du fichier principal pour le nom du répertoire
        String name = Utilities.getNameWithoutExtension(file);

        //répertoire où le(s) fichier(s) seront enregistrés.
        File path = new File(tempPath, name);
        if(path.exists()) {
            Utilities.deteleFiles(path);
            path.delete();
        }

        if(!path.mkdirs() && !path.exists()) 
            return false;

        boolean saved = false;
        //réfrences sur les fichiers sauvegardés dans un projet
        ProjectFiles savedFiles = new ProjectFiles();

        //sauvegarde des index
        if(project.getIndexesFile() != null) {
            File indexesFile = new File(path, name+project.getIndexesFile());
            saved = saveMediaIndexes(indexesFile);
            if(saved)
                savedFiles.setIndexesFile(indexesFile.getName());
        }

        //sauvegarde des soustitres
        if(project.getSubtitleFile() != null) {
            if(project.getVideoFile() != null)
                project.setSubtitleFile(Constants.SRT_extension);

            File subtitleFile = new File(path, name+project.getSubtitleFile());
            saved = saveSubtitleFile(subtitleFile);
            if(saved)
                savedFiles.setSubtitleFile(subtitleFile.getName());
        }

        //sauvegarde de l'audio
        if(project.getAudioFile() != null) {
            //sauvegarde de l'audio dans un fichier temporaire au format wav
            saved = saveAudio(audioFileTemp);
            //si il n'y a pas de vidéo on convertie le fichier
            if(saved && project.getVideoFile() == null) {
                File audioFile = new File(path, name+project.getAudioFile());
                converter(audioFile, audioFileTemp, true);
                savedFiles.setVideoFile(audioFile.getName());
            }
        }

        //sauvegarde de la vidéo et conversion video+audio
        if(project.getVideoFile() != null) {
            File subtitleFile = null;
            if(savedFiles.getSubtitleFile() != null)
                subtitleFile = new File(path, savedFiles.getSubtitleFile());
            //sauvegarde de la vidéo dans un fichier temporaire au format flv
            File videoFile = new File(path, name+project.getVideoFile());
            //on convertie la vidéo et l'audio si il existe dans le fichier video
            if(project.getAudioFile() == null) {
                converter(videoFile, null, videoFileTemp, subtitleFile, soft);
                savedFiles.setVideoFile(videoFile.getName());
            }
            else if(saved) {
                converter(videoFile, audioFileTemp, videoFileTemp, subtitleFile, soft);
                savedFiles.setVideoFile(videoFile.getName());
            }
            if(soft.contentEquals(Constants.EASYLAB)) {
                File videoOriginalFile = new File(path, name+"_original"+project.getVideoFile());
                Utilities.fileCopy(videoFileTemp, videoOriginalFile);
                savedFiles.setVideoOriginalFile(videoOriginalFile.getName());
            }
        }

        //sauvegarde du texte associé
        if(project.getTextFile() != null) {
            File textFile = new File(path, name+project.getTextFile());
            saved = saveText(textFile);
            if(saved)
                savedFiles.setTextFile(textFile.getName());
        }

        //sauvegarde des tags
        if(project.getTagFile() != null) {
            File tagFile = new File(path, name+project.getTagFile());
            saved = Utilities.saveText(XMLUtilities.getXML(tags), tagFile);
            if(saved)
                savedFiles.setTagFile(tagFile.getName());
        }

        //sauvegarde des images du diaporama
        for(Iterator<Index> it = mediaIndexes.iterator(); it.hasNext();) {
            Index index = it.next();
            if(index.hasImage()) {
                File imageOriginalFile = new File(index.getImage());
                File imageFile = new File(path, imageOriginalFile.getName());
                Utilities.fileCopy(imageOriginalFile, imageFile);
            }
        }

        //sauvegarde du fichier projet et compression
        if(!savedFiles.isEmptyProject()) {
            if(soft.contentEquals(Constants.SIMPLE_EXPORT)) {
                Utilities.fileDirectoryCopy(path, file);
            } else {
                File projectFile = new File(path, name+Constants.projectExtension);
                saved = Utilities.saveObject(savedFiles, projectFile);
                if(saved) {
                    saved = Utilities.compressFile(path, file);
                }
            }
        }

        savedFiles.clear();
        return saved;
    }

    /**
     * Sauvegarde les d'index dans un fichier.
     *
     * @param file le fichier.
     * @return <code>true<\code> si la sauvegarde s'est bien passée.
     * @since version 0.95
     */
    private boolean saveMediaIndexes(File file) {
        Utilities.saveObject(mediaIndexes, file);
        projectFiles.setIndexesFile(file.getAbsolutePath());
        return true;
    }

    /**
     * Sauvegarde les soustitres.
     *
     * @param file le fichier.
     * @return <code>true<\code> si la sauvegarde s'est bien passée.
     * @since version 0.95
     */
    private boolean saveSubtitleFile(File file) {
        boolean success = false;

        String extension = Utilities.getExtensionFile(file);
        if(extension.equalsIgnoreCase(Constants.SRT_extension))
            success = Utilities.saveSRTSubtitleFile(mediaIndexes, file);
        else if(extension.equalsIgnoreCase(Constants.SUB_extension))
            success = Utilities.saveSUBSubtitleFile(mediaIndexes, file);
        else if(extension.equalsIgnoreCase(Constants.LRC_extension))
            success = Utilities.saveLRCSubtitleFile(mediaIndexes, file);

        if(success)
            projectFiles.setSubtitleFile(file.getAbsolutePath());
        return success;
    }

    /**
     * Sauvegarde les données saisies dans la zone de texte dans un fichier.
     *
     * @param file le fichier.
     * @return <code>true<\code> si la sauvegarde s'est bien passée.
     * @since version 0.95 - version 0.96
     */
    private boolean saveText(File file) {
        boolean saved = false;
        File destFile = file;
        if(!Utilities.isTextFile(destFile))
            destFile = new File(file.getAbsoluteFile()+Constants.textDefaultExtension);

        projectFiles.setTextFile(destFile.getAbsolutePath());

        if(Utilities.isTextStyledFile(destFile)) {
            if(Utilities.getExtensionFile(file).contentEquals(Constants.RTF_extension)) {
                File htmlFile = new File(tempPath, destFile.getName()+Constants.HTML_extension);
                saved = saveTextStyled(htmlFile);
                if(saved) {
                    saved = Utilities.html2rtf(htmlFile, destFile);
                }
            } else {
                saved = saveTextStyled(destFile);
            }
        } else {
            try {
                String text = styledDocument.getText(0, styledDocument.getLength());
                saved = Utilities.saveText(text, destFile);
            } catch(BadLocationException e) {
                Edu4Logger.error(e);
            }
        }
        return saved;
    }

    /**
     * Sauvegarde les données saisies dans la zone de texte dans un fichier RTF.
     *
     * @param file le fichier.
     * @return <code>true<\code> si la sauvegarde s'est bien passée.
     * @since version 0.95
     */
    private boolean saveTextStyled(File file) {
        try {
            FileOutputStream output = new FileOutputStream(file);
            styledEditorKit.write(output, styledDocument, 0, styledDocument.getLength());
            output.flush();
            output.close();
	} catch(Exception e) {
            Edu4Logger.error(e);
            return false;
        }//end try

        return true;
    }

    /**
     * Sauvegarde les données enregistrées par le microphone dans un fichier.
     *
     * @param file le fichier où l'on veut enregistrer.
     * @return <code>true<\code> si la sauvegarde s'est bien passée.
     * @since version 0.95 - version 0.99
     */
    private boolean saveAudio(File file) {
        boolean success;

        //Si le format n'est pas du .wav, on convertie le fichier wav temporaire.
        if(!Constants.WAV_extension.equalsIgnoreCase(Utilities.getExtensionFile(file))) {
            success = saveAudio(audioFileTemp);
            converter(file, audioFileTemp, true);
        } else {
            try {
                //Initialisation du type du fichier de sortie à wav
                AudioFileFormat.Type audioFileFormatType
                        = AudioFileFormat.Type.WAVE;

                //Création d'un flux d'entrée audio
                ByteArrayInputStream byteArrayInputStream
                        = new ByteArrayInputStream(audioBuffer.array());

                AudioInputStream audioInputStream
                        = new AudioInputStream(byteArrayInputStream, audioFormat,
                        audioBuffer.limit()/audioFormat.getFrameSize());

                //on écrit dans le fichier les données.
                AudioSystem.write(audioInputStream, audioFileFormatType, file);
                audioInputStream.close();
                success = true;
            } catch(IOException e) {
                Edu4Logger.error(e);
                success = false;
            }//end catch
        }//end if
        return success;
    }//end saveAudio(File file)

    /**
     * Retourne si le projet contient une vidéo.
     * 
     * @return si le projet contient une vidéo.
     * @since version 0.95
     */
    public boolean hasVideo() {
        return (projectFiles.getVideoFile() != null);
    }

    /**
     * Efface le texte dans le module texte.
     *
     * @since version 0.95
     */
    public void eraseText() {
        try {
            styledDocument.remove(0, styledDocument.getLength());
        } catch(BadLocationException e) {
            Edu4Logger.error(e);
        }
        projectFiles.setTextFile(null);
        fireTextLoaded(null, false);
    }

    /**
     * Retourne si il y a du texte associé.
     * 
     * @return si il y a du texte associé.
     * @since version 0.95
     */
    public boolean hasText() {
        return (styledDocument.getLength() > 0);
    }

    /**
     * Lance la lecture des données enregistrées au microphone.
     *
     * @since version 0.95 - version 0.97
     */
    public void audioPlay() {
        if(runningState != Constants.PAUSE) {
            audioPause();
        }//end if

        //si on est à la fin de la bande on remet à 0
        if(currentTime >= recordTimeMax)
            setTime(0);

        //Modification de l'état des boutons du module audio
        setRunningState(Constants.PLAYING);

        player.start(currentTime, stopTime);
    }

    /**
     * Stop la lecture ou l'enregistrement du module audio.
     *
     * @since version 0.95 - version 0.97
     */
    public void audioPause() {
        boolean isRecordOnIndex = (runningState == Constants.RECORDING);

//        //pour dire que la lecture sur un index est finie
//        onIndex = false;

        if(runningState == Constants.RECORDING || runningState == Constants.RECORDING_INSERT) {
            recorder.stop();
            while(recorder.isAlive()) {
                Utilities.waitInMillisecond(5);
            }
        }
        else if(runningState == Constants.PLAYING) {
            player.stop();
            while(player.isAlive()) {
                Utilities.waitInMillisecond(5);
            }
        }//end if

        //Modification de l'état des boutons du module audio
        setRunningState(Constants.PAUSE);

        stopTime = recordTimeMax;

        if(isRecordOnIndex)
            fireAudioDataChanged();
    }//end audioPause()

//    /**
//     * Lance l'enregistrement du microphone.
//     *
//     * @since version 0.95
//     */
//    private void audioRecord() {
//        if(runningState == PLAYING) {
//            audioPause();
//        }//end if
//
//        //Modification de l'état des boutons du module audio
//        setRunningState(RECORDING);
//
//        recorder.start(currentTime, stopTime);
//    }

    /**
     * Efface la bande audio.
     *
     * @since version 0.95
     */
    public void eraseAudio() {
        int NbByte = audioBuffer.limit();
        audioBuffer.rewind();

        if(NbByte > 0) {
            byte data[] = new byte[NbByte];
            audioBuffer.put(data);
        }//end if

        if(!hasVideo()) {
            eraseIndexes();
            setRecordTimeMax(0);
        }//end if

        projectFiles.setAudioFile(null);
        fireAudioDataChanged();
    }

    /**
     * Efface la bande video.
     *
     * @since version 0.95
     */
    public void eraseVideo() {
        projectFiles.setVideoFile(null);
        fireVideoFileChanged(null);
    }

    /**
     * Appele le programme de conversion avec le fichier source et le
     * fichier de destination.
     *
     * @param srcFile le fichier source.
     * @param destFile le fichier destination.
     * @since version 0.95 - version 1.03
     */
    private int converter(File destFile, File srcFile, boolean withTags) {
        int result;
        if(withTags)
            result = converter.convert(destFile, srcFile, tags);
        else
            result = converter.convert(destFile, srcFile, null);
        return result;
    }

    /**
     * Appele le programme de conversion avec les fichiers sources et le
     * fichier de destination.
     * 
     * @param destFile le fichier destination.
     * @param audioFile le fichier source de la psite audio.
     * @param videoFile le fichier source de la psite vidéo.
     * @param subtitleFile le fichier source de la psite de sous-titres.
     * @param soft le logiciel cible.
     * @since version 0.95 - version 1.03
     */
    private int converter(File destFile, File audioFile, File videoFile,
            File subtitleFile, String soft) {
        int result;
        if(soft.contentEquals(Constants.EASYLAB)) {
            converter.setVideoSize(320, 240);
            result = converter.convert(destFile, audioFile, videoFile, subtitleFile, tags);
        }
        else {
            converter.setVideoSize(640, 480);
            result = converter.convert(destFile, audioFile, videoFile, null, tags);
        }
        return result;
    }

    /**
     * Appele le programme de conversion et d'extraction avec le fichier source
     * et les fichiers de destination.
     * 
     * @param srcFile le fichier source.
     * @param audioFile le fichier destination pour la psite vidéo.
     * @param videoFile le fichier destination pour la psite vidéo.
     * @since version 0.95 - version 1.03
     */
    private int extract(File srcFile, File audioFile, File videoFile) {
        int result = converter.extractToWAVandFLV(srcFile, audioFile, videoFile);
        return result;
    }

    /**
     * Retourne la durée du fichier en ms.
     * 
     * @param file le fichier source.
     * @return la durée du fichier.
     * @since version 0.95
     */
    public long getFileDuration(File file) {
        return converter.getDuration(file);
    }

    /**
     * Retourne la présence d'une piste audio dans le fichier.
     * 
     * @param file le fichier source.
     * @return si le fichier contient une piste audio.
     * @since version 0.95
     */
    public boolean hasAudioSrteam(File file) {
        return converter.hasAudioSrteam(file);
    }

    /**
     * Retourne la présence d'une piste vidéo dans le fichier.
     * 
     * @param file le fichier source.
     * @return si le fichier contient une piste vidéo.
     * @since version 0.95
     */
    public boolean hasVideoSrteam(File file) {
        return converter.hasVideoSrteam(file);
    }

    /**
     * Annule une conversion en cours.
     * 
     * @since version 0.96 - version 1.03
     */
    public void cancelConversion() {
        converter.cancel();
    }

    /**
     * Efface l'enregistrement effectué sur l'index comprenant le temps indiqué.
     *
     * @param time le temps de l'index.
     * @since version 0.95
     */
    public void eraseIndexRecord(long time) {
        Index index = getIndexAtTime(time);
        if(index != null) {
            //sauvegarde de ce qu'il y a avant l'index
            int sample = (int) (index.getInitialTime()/1000.0f * audioFormat.getSampleRate());
            int bufferSize = sample * audioFormat.getFrameSize();
            audioBuffer.position(bufferSize);

            //remplisage de blanc de l'index courant
            sample = (int) ((index.getFinalTime()-index.getInitialTime())
                    /1000.0f * audioFormat.getSampleRate());
            bufferSize = sample * audioFormat.getFrameSize();
            byte data[] = new byte[bufferSize];
            audioBuffer.put(data);

            fireAudioDataChanged();
        }//end if
    }

    /**
     * Iitialisation des buffer audio.
     *
     * @since version 0.95
     */
    private void initByteBuffers() {
        //ByteBuffer.allocateDirect(cnt) peut provoquer un outOfMemory
        int bytePerSample = audioFormat.getSampleSizeInBits() / 8;

        int cnt = (int) Math.floor(TIME_MAX/1000.0f * audioFormat.getSampleRate())
                * bytePerSample;
        audioBuffer = ByteBuffer.allocate(cnt);
        setByteBufferLimit(audioBuffer, recordTimeMax);

        cnt = (int) Math.floor(TIME_TEMP_MAX/1000.0f * audioFormat.getSampleRate())
                * bytePerSample;
        tempBuffer = ByteBuffer.allocate(cnt);
        setByteBufferLimit(tempBuffer, TIME_TEMP_MAX);
    }

    /**
     * Modifie la limite du buffer des données audio suivant le temps maximum.
     *
     * @param byteBuffer le buffer des données audio.
     * @param time le temps maximum d'enregistrement (en ms).
     * @since version 0.95
     */
    private void setByteBufferLimit(ByteBuffer byteBuffer, long time) {
        //nombre de bytes pour un échantillon
        int bytePerSample = audioFormat.getSampleSizeInBits() / 8;

        int cnt = (int) Math.floor(time/1000.0f * audioFormat.getSampleRate())
                * bytePerSample;
        byteBuffer.limit(cnt);
    }

    /**
     * Insère un beep au temps indiqué.
     * 
     * @param begin le temps où doit se faire l'insertion.
     * @return la durée du beep inséré.
     * @since version 0.95
     */
    private long insertBeep(long begin) {
        File file = Utilities.getResource(beepPath, tempPath);
        long lenght = 0;
        int load = loadAudio(file, tempBuffer);
        if(load > 0) {
            tempBuffer.position(0);
            lenght = (long) (tempBuffer.limit() / audioFormat.getFrameSize()
                / audioFormat.getSampleRate() * 1000);

            int samplePosition = (int) (begin/1000.0f * audioFormat.getSampleRate());
            int offset = samplePosition * audioFormat.getFrameSize();
            audioBuffer.position(offset);
            audioBuffer.put(tempBuffer);
        }
        return lenght;
    }

    /**
     * Insere le ByteBuffer au temps indiqué.
     *
     * @param begin le temps de départ de l'insertion.
     * @param buffer le buffer de données.
     * @since version 0.93
     */
     private int insertData(long begin, ByteBuffer buffer) {
         if(buffer.limit()+audioBuffer.limit() > audioBuffer.capacity())
             return -audioBuffer.capacity();
        //Sauvegarde des données après la postion d'insertion.
        int samplePosition = (int) (begin/1000.0f * audioFormat.getSampleRate());
        int offset = samplePosition * audioFormat.getFrameSize();
        audioBuffer.position(offset);
        //nombres de bytes à lire
        int nbBytes = audioBuffer.remaining();
        byte[] afterData = new byte[nbBytes];
        audioBuffer.get(afterData);

        audioBuffer.clear();
        audioBuffer.position(offset);
        audioBuffer.put(buffer);
        audioBuffer.put(afterData);
        audioBuffer.flip();
        return audioBuffer.limit();
    }

    /**
     * Insere le tableau de byte au temps indiqué.
     *
     * @param begin le temps de départ de l'insertion.
     * @param buffer le buffer de données.
     * @since version 0.93
     */
    private int insertData(long begin, byte[] buffer) {
        if(buffer.length+audioBuffer.limit() > audioBuffer.capacity())
             return -audioBuffer.capacity();
        //Sauvegarde des données après la postion d'insertion.
        int samplePosition = (int) (begin/1000.0f * audioFormat.getSampleRate());
        int offset = samplePosition * audioFormat.getFrameSize();
        audioBuffer.position(offset);
        //nombres de bytes à lire
        int nbBytes = audioBuffer.remaining();
        byte[] afterData = new byte[nbBytes];
        audioBuffer.get(afterData);

        audioBuffer.clear();
        audioBuffer.position(offset);
        audioBuffer.put(buffer);
        audioBuffer.put(afterData);
        audioBuffer.flip();
        return audioBuffer.limit();
    }

    /**
     * Enlève les données audio entre les deux temps indiqués.
     *
     * @param begin le temps de départ.
     * @param end le temps de fin.
     * @return les données enlevées.
     * @since version 0.93
     */
    private byte[] removeData(long begin, long end) {
        if(end <= begin)
            return new byte[0];
        //Sauvegarde des données supprimées.
        int samplePosition = (int) (begin/1000.0f * audioFormat.getSampleRate());
        int offset = samplePosition * audioFormat.getFrameSize();
        int nbSample = (int) ((end-begin)/1000.0f * audioFormat.getSampleRate());
        int nbBytes = nbSample * audioFormat.getFrameSize();
        audioBuffer.position(offset);
        byte[] removeData = new byte[nbBytes];
        audioBuffer.get(removeData);

        //Sauvegarde des données après la postion d'insertion.
        samplePosition = (int) (end/1000.0f * audioFormat.getSampleRate());
        offset = samplePosition * audioFormat.getFrameSize();
        audioBuffer.position(offset);
        nbBytes = audioBuffer.remaining();
        byte[] afterData = new byte[nbBytes];
        if(nbBytes > 0)
            audioBuffer.get(afterData);

        samplePosition = (int) (begin/1000.0f * audioFormat.getSampleRate());
        offset = samplePosition * audioFormat.getFrameSize();
        audioBuffer.position(offset);
        audioBuffer.put(afterData);
        audioBuffer.flip();

        return removeData;
    }

    /**
     * Modifie la vitesse des données audio d'un index.
     * 
     * @param index l'index.
     * @param oldRate l'ancienne vitesse de l'index.
     * @param newRate la nouvelle vitesse de l'index.
     * @since version 1.01
     */
    private void setIndexAudioRate(Index index, float oldRate, float newRate) {
        if(oldRate == newRate)
            return;

        File file = new File(tempPath, index.getId()+Constants.WAV_extension);
        File fileTemp = new File(tempPath, index.getId()+"_speed"+Constants.WAV_extension);
        byte[] data = removeData(index.getInitialTime(), index.getFinalTime());

        if(oldRate == Index.NORMAL_RATE) {
            //sauvegarde des données à la vitesse normale
            saveAudioData(data, Index.NORMAL_RATE, file);
            //création des données à la nouvelle vitesse
            saveAudioData(data, newRate, fileTemp);
            //changement du fichier à charger
            file = fileTemp;
        }
        else if(newRate != Index.NORMAL_RATE) {
            //chargement des données à la vitesse normale
            loadAudio(file, tempBuffer);
            int nbBytes = tempBuffer.limit();
            data = new byte[nbBytes];
            System.arraycopy(tempBuffer.array(), 0, data, 0, nbBytes);
            //création des données à la nouvelle vitesse
            saveAudioData(data, newRate, fileTemp);
            //changement du fichier à charger
            file = fileTemp;
        }
        loadAudio(file, tempBuffer);
        insertData(index.getInitialTime(), tempBuffer);
    }

    /**
     * Sauvegarde les données audio dans un fichier avec une certaine vitesse.
     * 
     * @param data les données audio.
     * @param rate la vitesse de sauvegarde.
     * @param file le fichier de sauvegarde.
     * @since version 1.01
     */
    private void saveAudioData(byte[] data, float rate, File file) {
        float sampleRate = audioFormat.getSampleRate() * rate;

        AudioFormat saveFormat = new AudioFormat(sampleRate,
                audioFormat.getSampleSizeInBits(), audioFormat.getChannels(),
                audioFormat.getEncoding() == AudioFormat.Encoding.PCM_SIGNED,
                audioFormat.isBigEndian());

        try {
            //Initialisation du type du fichier de sortie à wav
            AudioFileFormat.Type audioFileFormatType = AudioFileFormat.Type.WAVE;

            //Création d'un flux d'entrée audio
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);

            AudioInputStream audioInputStream
                    = new AudioInputStream(byteArrayInputStream, saveFormat,
                    data.length/saveFormat.getFrameSize());

            //on écrit dans le fichier les données.
            AudioSystem.write(audioInputStream, audioFileFormatType, file);
            audioInputStream.close();
        } catch(IOException e) {
            Edu4Logger.error(e);
        }
    }

    /**
     * Insére une vidéo "blanche" (image fixe) sur la vidéo courante.
     * 
     * @param begin le temps de départ de l'insertion dans la vidéo initiale.
     * @param duration la durée de la vidéo blanche à insérer.
     * @since version 0.97 - version 1.01
     */
    private void insertBlankVideo(long begin, long duration) {
        File imgDirectory = new File(tempPath, "_img_");
        imgDirectory.mkdirs();
        File imageFile = Utilities.getResource(videoImagePath, tempPath);
        fireVideoFileChanged(null);
        converter.insertBlankVideo(videoFileTemp, imageFile, begin, duration);
        fireVideoFileChanged(videoFileTemp);
    }

    /**
     * Duplique la plage donnée de la vidéo courante et l'insére à la fin de la
     * plage.
     * 
     * @param begin le temps de départ de la partie à dupliquer.
     * @param end le temps de fin de la partie à dupliquer.
     * @since version 0.97 - version 1.01
     */
    private void insertDuplicatedVideo(long begin, long end) {
        fireVideoFileChanged(null);
        converter.insertDuplicatedVideo(videoFileTemp, begin, end);
        fireVideoFileChanged(videoFileTemp);
    }

    /**
     * Insére une vidéo sur la vidéo courante.
     * 
     * @param begin le temps de départ de l'insertion dans la vidéo initiale.
     * @param file le fichier vidéo à insérer.
     * @since version 0.97 - version 0.99
     */
    private void insertVideo(long begin, File file) {
        fireVideoFileChanged(null);
        converter.insertVideo(videoFileTemp, file, begin);
        fireVideoFileChanged(videoFileTemp);
    }

    /**
     * Crée une vidéo "blanche" (image fixe) d'une durée spécifique.
     * 
     * @param destFile le fichier de destination de la vidéo.
     * @param duration la durée de la vidéo blanche.
     * @since version 0.97 - version 0.99
     */
    private void createBlankVideo(File destFile, long duration) {
        File imgDirectory = new File(tempPath, "_img_");
        imgDirectory.mkdirs();
        File imageFile = Utilities.getResource(videoImagePath, tempPath);
        converter.createBlankVideo(destFile, imageFile, duration);
    }

    /**
     * Supprime une partie de la vidéo courante.
     * 
     * @param begin le temps de départ de la partie à supprimer.
     * @param end le temps de fin de la partie à supprimer.
     * @since version 0.97 - version 0.99
     */
    private void removeVideo(long begin, long end) {
        fireVideoFileChanged(null);
        converter.removeVideo(videoFileTemp, begin, end);
        fireVideoFileChanged(videoFileTemp);
    }

    /**
     * Déplace et redimensionne une partie de la vidéo courante.
     * 
     * @param begin le temps de départ de la partie à déplacer.
     * @param end le temps de fin de la partie à déplacer.
     * @param newBegin le nouveau temps de départ de la partie à déplacer.
     * @param duration la nouvelle durée de la partie sélectionnée.
     * @since version 0.97 - version 1.01
     */
    private void moveVideoAndResize(long begin, long end, long newBegin, long duration) {
        File imgDirectory = new File(tempPath, "_img_");
        imgDirectory.mkdirs();
        File imageFile = Utilities.getResource(videoImagePath, tempPath);
        fireVideoFileChanged(null);
        converter.moveVideoAndResize(videoFileTemp, imageFile, begin, end, newBegin, duration);
        fireVideoFileChanged(videoFileTemp);
    }

    /**
     * Modifie la vitesse de la vidéo d'un index.
     * 
     * @param index l'index.
     * @param oldRate l'ancienne vitesse de l'index.
     * @param newRate la nouvelle vitesse de l'index.
     * @since version 1.01
     */
    private void setIndexVideoRate(Index index, float oldRate, float newRate) {
        File normalFile = new File(tempPath, index.getId()+Constants.FLV_extension);
        fireVideoFileChanged(null);
        converter.setVideoRate(videoFileTemp, index.getInitialTime(),
                index.getFinalTime(), oldRate, newRate, normalFile);
        fireVideoFileChanged(videoFileTemp);
    }

    /**
     * Met à jour du format audio pour la capture de sons et de la variable
     * bytePerSample.
     *
     * @param format le nouveau format. Mettre <code>null</code> pour avoir le
     *        format par défaut (8000 Hz, 16 bits, mono, signed, little-endian).
     * @since version 0.94
     */
    private void setAudioFormat(AudioFormat format) {
        if(format == null) {
            //Format Audio pour la capture du microphone
            float sampleRate = 44100.0F; //8000,11025,16000,22050,44100
            int sampleSizeInBits = 16; //8,16 (8 trop petit: bruits parasites)
            int channels = 1; //1,2
            boolean signed = true; //false ne marche pas en 16 bits
            boolean bigEndian = false; //true, false (.wav always in lttle-endian)

            audioFormat = new AudioFormat(sampleRate, sampleSizeInBits, channels,
                    signed, bigEndian);
        }
        else
            audioFormat = format;
    }

    /**
     * Notification interne de changement du temps pour les thread de lecture et
     * d'enregistrement.
     *
     * @param time le nouveau temps.
     * @since version 0.94 - version 0.97
     */
    private void fireProcessTimeChanged(long time) {
        //quand on est en insertion de voix on ajuste le temps
        if(runningState == Constants.RECORDING_INSERT)
            fireInsertVoiceTimeChanged(time);
        else 
            setTime(time);
    }

    /**
     * Notification interne de la fin des thread de lecture et d'enregistrement.
     *
     * @param running <code>true</code> si la thread ne sait pas stoppée d'elle même.
     * @since version 0.94 - version 1.01
     */
    private void fireProcessEnded(final boolean running) {
        //Traitement dans un thread séparée pour éviter un blocage
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(running)
                    audioPause();
                //si on est arrivé à la fin de la piste (à une erreur d'arrondi
                //près) et que le traitement s'est arrêté de lui même
                //alors on retourne en 0
                if(running && currentTime >= recordTimeMax-1) {
                    setTime(0);
                }
            }
        }).start();
    }

}//end