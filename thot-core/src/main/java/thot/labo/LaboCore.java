package thot.labo;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.EventListener;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.event.EventListenerList;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.rtf.RTFEditorKit;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thot.audio.AudioPlayer;
import thot.audio.AudioProcessing;
import thot.audio.AudioRecorder;
import thot.audio.TimeProcessingListener;
import thot.exception.ThotCodeException;
import thot.exception.ThotException;
import thot.labo.index.Index;
import thot.labo.index.IndexProcessing;
import thot.labo.index.IndexType;
import thot.labo.index.Indexes;
import thot.utils.Constants;
import thot.utils.Utilities;
import thot.video.Converter;
import thot.video.MediaPlayer;
import thot.video.event.MediaPlayerAdapter;
import thot.video.event.MediaPlayerEvent;
import thot.video.vlc.VLCMediaPlayer;

/**
 * Noyau commun aux poste élèves et professeurs.
 *
 * @author Fabrice Alleau
 * @version 1.8.4
 */
public abstract class LaboCore implements ProjectManager, IndexProcessing {

    /**
     * Instance de log.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(LaboCore.class);

    /**
     * Nom de l'extension par défaut pour l'enregistrement de fichiers texte.
     */
    private static final String textDefaultExtension = Constants.TXT_extension;

    /**
     * Chemin du dossier temporarire.
     */
    protected final File tempPath;
    /**
     * Nom de fichier par défaut.
     */
    protected final String defaultFileName;

    /**
     * Sauvegarde des écoutes sur différents éléments.
     */
    private final EventListenerList listeners;

    /**
     * Sauvegarde des endroits où l'élève s'est enregistré.
     */
    private Indexes recordIndexes;
    /**
     * Index du fichier multimédia.
     */
    private Indexes mediaIndexes;
    /**
     * Fichiers pour le projet.
     */
    @Getter
    private ProjectFiles projectFiles;

    /**
     * Etat du mode actif.
     */
    @Getter
    private int runningState;
    /**
     * Type du fichier du module multimédia.
     */
    @Getter
    private int mediaType;
    /**
     * Pour indiquer si on est en lecture automatique ou non.
     */
    @Getter
    private boolean indexesMode;

    /**
     * Volume pour la lecture des données issues du microphone (de 0 à 100).
     */
    @Getter
    private int audioVolume;
    /**
     * Volume pour le lecteur multimédia (de 0 à 100).
     */
    @Getter
    private int mediaVolume;
    /**
     * Sauvegarde du temps courant en millisecondes.
     */
    @Getter
    private long currentTime;
    /**
     * Sauvegarde le temps maximum d'enregistrement en millisecondes.
     */
    @Getter
    private long recordTimeMax;
    /**
     * Temps d'allocation maximum en millisecondes (=30 min).
     */
    protected static final long TIME_MAX = 1800000;
    /**
     * Temps où l'on met en pause.
     */
    private long stopTime;
    /**
     * Référence à l'index multimédia où l'on est rendu pour afficher le soustitre.
     */
    private Index currentMediaIndex = null;
    /**
     * Sauvegarde du temps du début d'un enregistrement.
     */
    private long beginRecordTime;

    /**
     * Lecteur pour les fichiers multimédia (vidéo et audio).
     */
    private MediaPlayer mediaPlayer;
    /**
     * Thread pour la lecture de la piste élève.
     */
    private AudioPlayer audioPlayer;
    /**
     * Thread pour enregistrer les données du microphone.
     */
    private AudioRecorder audioRecorder;
    /**
     * Thread pour la lecture programmée des index.
     */
    private IndexesPlayer indexesPlayer;
    /**
     * Indique si le stop à été utiliser par l'utilisateur ou par le programme.
     */
    private boolean userStop;
    /**
     * Indique si on sur une lecture d'index, ou une plage donnée.
     */
    private boolean onIndex;

    /**
     * Document pour l'utilisation de style dans la zone de texte.
     */
    @Getter
    private StyledDocument styledDocument;
    /**
     * Editeur de texte avec gestion de style de type RTF.
     */
    @Getter
    private StyledEditorKit styledEditorKit;

    /**
     * Utilitaire pour les conversions des media.
     */
    private Converter converter;

    /**
     * Initialisation avec le répertoire de travail.
     *
     * @param encoder l'exécutable "mencoder" pour les conversions.
     */
    public LaboCore(Converter encoder) {
        tempPath = new File(System.getProperty("java.io.tmpdir"), "Siclic");
        tempPath.mkdirs();

        //nom par défaut s'il n'y a pas de fichier de créé.
        Calendar calendar = Calendar.getInstance();
        defaultFileName = String.format("work%1$tF-%1$tH.%1$tM.%1$tS", calendar);

        listeners = new EventListenerList();

        this.converter = encoder;

        initValues();

        //initialisation des deux players du module VLC
        mediaPlayerInit();
    }

    /**
     * Initialise les threads pour la lecture et l'enregistrement.
     *
     * @param audioRecorder la thread pour l'enregistrement.
     * @param audioPlayer la thread pour la lecture.
     */
    protected void initRecorderAndPlayer(AudioRecorder audioRecorder, AudioPlayer audioPlayer) {
        this.audioRecorder = audioRecorder;
        this.audioPlayer = audioPlayer;
        this.audioPlayer.setVolume(audioVolume);

        TimeProcessingListener listener = new TimeProcessingListener() {
            @Override
            public void timeChanged(Object source, long oldValue, long newValue) {
                fireProcessTimeChanged(newValue);
            }

            @Override
            public void endProcess(Object source, boolean selfStop) {
                fireProcessEnded(selfStop);
            }
        };
        this.audioRecorder.addListener(listener);
        this.audioPlayer.addListener(listener);
    }

    /**
     * Initialise les valeurs par défaut.
     */
    private void initValues() {
        //gestion du projet élève
        projectFiles = new ProjectFiles();
        //gestion des enregistrements
        recordIndexes = new Indexes();
        //gestion des index du fichier multimédia
        mediaIndexes = new Indexes();

        styledDocument = new DefaultStyledDocument();
        styledEditorKit = new RTFEditorKit();
        //sup: styledDocument = new HTMLDocument();
        //sup: styledEditorKit = new HTMLEditorKit();

        //états des modules multimédia et audio
        runningState = Constants.PAUSE;
        mediaType = Constants.UNLOAD;
        indexesMode = false;

        //volume pour la lecture des données issues du microphone (de 0 à 100)
        audioVolume = 100;
        //volume pour la lecture des données multimédia (de 0 à 100)
        mediaVolume = 50;

        //postion du curseur temps
        currentTime = 0;
        recordTimeMax = 2 * 60 * 1000;// = 2 minutes
        stopTime = recordTimeMax;
        mediaIndexes.setMediaLength(recordTimeMax);

        userStop = true;
        onIndex = false;

        indexesPlayer = new IndexesPlayer();
    }

    /**
     * Ferme l'application.
     */
    public void closeApplication() {
        audioPause();
        mediaPlayerClose();
        audioRecorder.close();
        audioPlayer.close();
        System.exit(0);
    }

    /**
     * Met à jour l'état du module audio.
     *
     * @param state la nouvelle valeur de l'état du module audio.
     */
    private void setRunningState(int state) {
        runningState = state;
        fireStateChanged(state, mediaType);
    }

    /**
     * Modifie le type de média du module multimédia. Il peut prendre les valeurs suivantes: {@code StudentCore.UNLOAD},
     * {@code StudentCore.AUDIO}, {@code StudentCore.VIDEO}, {@code StudentCore.IMAGE}.
     *
     * @param type le nouveau type de fichier.
     */
    private void setMediaType(int type) {
        mediaType = type;
        fireStateChanged(runningState, type);
    }

    /**
     * Modifie le mode de lecture automatique.
     *
     * @param mode le mode de lecture automatique.
     */
    public void setIndexesMode(boolean mode) {
        indexesMode = mode;
        fireIndexesModeChanged(mode);
    }

    /**
     * Met à jour le volume du lecteur mutimédia.
     *
     * @param value la nouvelle valeur comprise entre 0 et 100.
     */
    public void setMediaVolume(int value) {
        mediaVolume = value;
        mediaPlayer.setVolume(value);
    }

    /**
     * Change le mode mute du module multimédia.
     */
    public void toggleMediaMute() {
        setMediaVolume((mediaVolume == 0) ? 50 : 0);
    }

    /**
     * Met à jour le volume du lecteur du module audio.
     *
     * @param value la nouvelle valeur comprise entre 0 et 100.
     */
    public void setAudioVolume(int value) {
        audioVolume = value;
        audioPlayer.setVolume(value);
    }

    /**
     * Change le mode mute du module audio.
     */
    public void toggleAudioMute() {
        setAudioVolume((audioVolume == 0) ? 50 : 0);
    }

    /**
     * Ajoute d'une écoute de type StudentListener.
     *
     * @param listener l'écoute à ajouter.
     */
    public void addListener(LaboListener listener) {
        listeners.add(LaboListener.class, listener);
        converter.addListener(listener);
    }

    /**
     * Enlève une écoute de type StudentListener.
     *
     * @param listener l'écoute à enlever.
     */
    public void removeListener(LaboListener listener) {
        listeners.add(LaboListener.class, listener);
        converter.addListener(listener);
    }

    /**
     * Retourne un iterator sur les listeners.
     *
     * @param t
     * @param <T>
     *
     * @return un iterator sur les listeners.
     */
    protected <T extends EventListener> T[] getListeners(Class<T> t) {
        return listeners.getListeners(t);
    }

    /**
     * Notification du changement d'état.
     *
     * @param running le nouvel état.
     * @param mediaType le type de média chargé.
     */
    private void fireStateChanged(int running, int mediaType) {
        for (LaboListener listener : listeners.getListeners(LaboListener.class)) {
            listener.stateChanged(running, mediaType);
        }
    }

    /**
     * Notification du changement de mode de lectute (automatique ou manuel).
     *
     * @param indexesMode le nouveau mode de lecture (automatique = true).
     */
    private void fireIndexesModeChanged(boolean indexesMode) {
        for (LaboListener listener : listeners.getListeners(LaboListener.class)) {
            listener.indexesModeChanged(indexesMode);
        }
    }

    /**
     * Notification du changement de la durée maximum d'enregistrement.
     *
     * @param recordTimeMax le temps maximum d'enregistrement.
     */
    protected void fireRecordTimeMaxChanged(long recordTimeMax) {
        for (LaboListener listener : listeners.getListeners(LaboListener.class)) {
            listener.recordTimeMaxChanged(recordTimeMax);
        }
    }

    /**
     * Notification du changement du temp.
     *
     * @param time le nouveau temps.
     */
    private void fireTimeChanged(long time) {
        for (LaboListener listener : listeners.getListeners(LaboListener.class)) {
            listener.timeChanged(time);
        }
    }

    /**
     * Notification du changement du soustitre.
     *
     * @param index le soustitre.
     */
    private void fireCurrentIndexChanged(Index index) {
        for (LaboListener listener : listeners.getListeners(LaboListener.class)) {
            listener.currentIndexChanged(index);
        }
    }

    /**
     * Notification qu'un texte a été chargé.
     *
     * @param text le texte chargé.
     */
    private void fireTextLoaded(String text) {
        for (LaboListener listener : listeners.getListeners(LaboListener.class)) {
            listener.textLoaded(text);
        }
    }

    /**
     * Notification que l'image du module multimédia a changé.
     *
     * @param image la nouvelle image.
     */
    private void fireImageChanged(BufferedImage image) {
        for (LaboListener listener : listeners.getListeners(LaboListener.class)) {
            listener.imageChanged(image);
        }
    }

    /**
     * Notification du changement du mode plein écran.
     *
     * @param fullscreen le mode plein écran.
     */
    private void fireFullScreenChanged(boolean fullscreen) {
        for (LaboListener listener : listeners.getListeners(LaboListener.class)) {
            listener.fullScreenChanged(fullscreen);
        }
    }

    /**
     * Notification de changement dans les index d'enregistrement.
     */
    protected void fireMediaIndexesChanged() {
        mediaIndexes.sortIndexes();
//        setRecordTimeMaxByIndexes();
        fireIndexesChanged();
    }

    /**
     * Notification de changement dans les index d'enregistrement.
     */
    protected void fireIndexesChanged() {
        for (LaboListener listener : listeners.getListeners(LaboListener.class)) {
            listener.indexesChanged();
        }
    }

    /**
     * Retourne le nombre de pistes où l'élève s'est enregistré.
     *
     * @return le nombre de pistes où l'élève s'est enregistré.
     */
    public int getRecordIndexesCount() {
        return recordIndexes.getIndexesCount();
    }

    /**
     * Retourne un iterateur sur les Index d'enregistrement.
     *
     * @return un iterateur sur les Index d'enregistrement.
     */
    @Override
    public Iterator<Index> recordIndexIterator() {
        return recordIndexes.iterator();
    }

    /**
     * Retourne l'index d'enregistrement élève à la position relative.
     *
     * @param position la position relative dans le temps.
     *
     * @return l'index d'enregistrement si il existe.
     */
    protected Index getRecordIndex(double position) {
        return recordIndexes.getIndexAtTime((long) (position * recordTimeMax));
    }

    /**
     * Retourne le nombre d'index de la piste multimédia.
     *
     * @return le nombre d'index.
     */
    public int getMediaIndexesCount() {
        return mediaIndexes.getIndexesCount();
    }

    /**
     * Retourne un iterateur sur les Index de la piste multimédia.
     *
     * @return un iterateur sur les Index de la piste multimédia.
     */
    @Override
    public Iterator<Index> mediaIndexIterator() {
        return mediaIndexes.iterator();
    }

    /**
     * Retourne l'index le plus proche du temps fourni. Retourne en priorité en cas d'égalité l'index d'enregistrement
     * par rapport à un index multimédia et l'index avec le temps de début par rapport à l'index avec le temps de fin.
     *
     * @param time le temps au tour du quel ou veut l'index.
     *
     * @return l'index le plus proche du temps ou {@code null}.
     */
    private Index getTimeIndex(long time) {
        long min = Long.MAX_VALUE;

        Index index = null;
        Index currentIndex;

        currentIndex = recordIndexes.getIndexAtTime(time);

        //calcul l'index le plus proche
        if (currentIndex != null) {
            if (time - currentIndex.getInitialTime() < min) {
                min = time - currentIndex.getInitialTime();
                index = currentIndex;
            }

            if (currentIndex.getFinalTime() - time < min) {
                min = currentIndex.getFinalTime() - time;
                index = currentIndex;
            }
        }

        currentIndex = mediaIndexes.getIndexAtTime(time);
        //calcul l'index le plus proche
        if (currentIndex != null) {
            if (time - currentIndex.getInitialTime() < min) {
                min = time - currentIndex.getInitialTime();
                index = currentIndex;
            }

            if (currentIndex.getFinalTime() - time < min) {
//                min = currentIndex.getFinalTime() - time;
                index = currentIndex;
            }
        }

        return index;
    }

    /**
     * Indique si on est sur un index d'enregistrement sur la piste élèvé.
     *
     * @param time le temps où l'on est.
     *
     * @return {@code true} si l'on est sur un index d'enregistrement.
     */
    private boolean onRecordIndex(long time) {
        Index index = recordIndexes.getIndexAtTime(time);
        return (index != null);
    }

    /**
     * Modifie le temps courant en millisecondes. Doit être appelé en mode pause sauf pour les threads de déplacement du
     * curseur temps.
     *
     * @param time le temps (en ms) où l'on doit se rendre.
     */
    protected void setTime(long time) {
        currentTime = time;
        if (mediaType == Constants.VIDEO_FILE || mediaType == Constants.AUDIO_FILE) {
            mediaPlayerSetTime(time);
        }
        fireTimeChanged(time);

        Index mediaIndex = mediaIndexes.getIndexAtTime(time);
        if (mediaIndex != currentMediaIndex) {
            currentMediaIndex = mediaIndex;
            fireCurrentIndexChanged(mediaIndex);
        }
    }

    /**
     * Postionne le temps à la position relative voulue.
     *
     * @param position la position relative (entre 0 et 1).
     */
    public void setPosition(double position) {
        boolean record = false;
        boolean play = false;

        //si piste élève active on stop l'activité élève en sauvegardant l'activité
        if (runningState == Constants.RECORDING) {
            audioPause();
            record = true;
        } else if (runningState == Constants.PLAYING) {
            audioPause();
            play = true;
        }

        while (indexesPlayer.isAlive()) {
            Utilities.waitInMillisecond(5);
        }

        setTime((long) (position * recordTimeMax));

        //redémarge de l'activité précédente
        if (record) {
            audioRecord();
        } else if (play) {
            audioPlay();
        }
    }

    /**
     * Met le temps à zéro.
     */
    public void setTimeToZero() {
        setPosition(0);
    }

    /**
     * Retourne si la position est sur un index.
     *
     * @param position la position relative entre 0 et 1.
     *
     * @return si on est sur un index.
     */
    @Override
    public boolean onIndex(double position) {
        return (getTimeIndex((long) (position * recordTimeMax)) != null);
    }

    /**
     * Retourne si la position est sur un index de l'élève.
     *
     * @param position la position relative entre 0 et 1.
     *
     * @return si on est sur un index de l'élève.
     */
    @Override
    public boolean onStudentIndex(double position) {
        return onRecordIndex((long) (position * recordTimeMax));
    }

    /**
     * Lecture de l'index où l'on est positionner.
     *
     * @param position la position relative entre 0 et 1.
     */
    @Override
    public void playOnIndex(double position) {
        Index index = getTimeIndex((long) (position * recordTimeMax));
        if (index != null) {
            stopTime = index.getFinalTime();
            setTime(index.getInitialTime());
            onIndex = true;//pour dire que l'on est pas en lecture automatique
            audioPlay();
        }
    }

    /**
     * Enregistrement sur l'index où l'on est positionner.
     *
     * @param position la position relative entre 0 et 1.
     */
    @Override
    public void recordOnIndex(double position) {
        Index index = getTimeIndex((long) (position * recordTimeMax));
        if (index != null) {
            stopTime = index.getFinalTime();
            setTime(index.getInitialTime());
            onIndex = true;//pour dire que l'on est pas en lecture automatique
            audioRecord();
        }
    }

    /**
     * Positionne le temps au début de l'index sélectionné.
     *
     * @param position la position relative entre 0 et 1.
     */
    @Override
    public void setTimeBeginIndex(double position) {
        Index index = getTimeIndex((long) (position * recordTimeMax));
        if (index != null) {
            setTime(index.getInitialTime());
        }
    }

    /**
     * Positionne le temps à la fin de l'index sélectionné.
     *
     * @param position la position relative entre 0 et 1.
     */
    @Override
    public void setTimeEndIndex(double position) {
        Index index = getTimeIndex((long) (position * recordTimeMax));
        if (index != null) {
            setTime(index.getFinalTime());
        }
    }

    /**
     * Efface l'index d'enregistrement sélectionné.
     *
     * @param position la position relative entre 0 et 1.
     */
    @Override
    public void eraseIndex(double position) {
        Index index = recordIndexes.getIndexAtTime((long) (position * recordTimeMax));
        if (index != null) {
            recordIndexes.removeIndex(index);
        }
    }

    /**
     * Efface tous les index d'enregistrement.
     */
    protected void eraseAllRecordIndex() {
        recordIndexes.removeAll();
    }

    /**
     * Change la valeur maximale de la barre de défilement.
     *
     * @param time la nouvelle valeur en en ms.
     */
    public void setRecordTimeMax(long time) {
        mediaIndexes.setMediaLength(time);
        long timeWithIndex = mediaIndexes.getLength();

        if (timeWithIndex > TIME_MAX) {
            timeWithIndex = TIME_MAX;
        }

        recordTimeMax = timeWithIndex;
        stopTime = recordTimeMax;

        //sup: setByteArrayLimit(recordTimeMax);
        fireRecordTimeMaxChanged(recordTimeMax);
    }

    /**
     * Change la valeur maximale de la barre de défilement.
     */
    private void setRecordTimeMaxByIndexes() {
        long time = mediaIndexes.getLength();

        if (time > TIME_MAX) {
            time = TIME_MAX;
        }

        recordTimeMax = time;
        stopTime = recordTimeMax;

        fireRecordTimeMaxChanged(recordTimeMax);
    }

    /**
     * Charge les fichiers contenus dans le projet. Si un fichier est null, les données correspondantes sont effacées.
     *
     * @param project l'ensembles des fichiers à charger.
     */
    @Override
    public void loadProject(ProjectFiles project) throws ThotException {
        //Effacement des éléments précédement chargés
        unloadMedia();
        audioErase();
        textErase();

        if (project.getVideoFile() != null) {
            loadMedia(new File(project.getVideoFile()));
        }
        if (project.getAudioFile() != null) {
            audioLoad(new File(project.getAudioFile()));
        }
        if (project.getTextFile() != null) {
            loadText(new File(project.getTextFile()));
        }
        if (project.getIndexesFile() != null) {
            loadIndexes(new File(project.getIndexesFile()));
        }
    }

    /**
     * Charge un fichier d'index.
     *
     * @param file le fichier.
     */
    private void loadIndexes(File file) throws ThotException {
        mediaIndexes = Utilities.getIndexes(file);
        mediaIndexes.sortIndexes();

        int indexesNormality = mediaIndexes.getGlobalValidity();
        setIndexesMode((indexesNormality == Indexes.NORMAL));
        if (mediaIndexes.getIndexesCount() == 0) {
            setIndexesMode(false);
        }

        projectFiles.setIndexesFile(file.getAbsolutePath());
        setRecordTimeMaxByIndexes();
        fireIndexesChanged();

        File subtitleFile = new File(file.getParentFile(), file.getName() + Constants.SRT_extension);
        Utilities.saveSRTSubtitleFile(mediaIndexes, subtitleFile);
        loadSubtitleFile(subtitleFile);
    }

    /**
     * Charge un fichier de soustitre.
     *
     * @param file le fichier.
     */
    private void loadSubtitleFile(File file) {
        if (Utilities.isSubtitleFile(file)) {
            projectFiles.setSubtitleFile(file.getAbsolutePath());
            mediaPlayerSetSubtitleFile(file.getAbsolutePath());
        }
    }

    /**
     * Chargement d'une image.
     *
     * @param file le fichier
     */
    private void loadImage(File file) {
        try {
            BufferedImage image = ImageIO.read(file);
            fireImageChanged(image);
        } catch (IOException e) {
            LOGGER.error("Impossible de charger l'image {}", e, file.getAbsolutePath());
            fireImageChanged(null);
        }
    }

    /**
     * Charge un fichier texte dans la zone de texte.
     *
     * @param file le fichier à charger.
     */
    private void loadText(File file) throws ThotException {
        projectFiles.setTextFile(file.getAbsolutePath());

        if (Utilities.isTextStyledFile(file)) {
            if (file.getName().toLowerCase().endsWith(Constants.RTF_extension)) {
                loadTextStyled(file);
            } else {
                File rtfFile = new File(tempPath, file.getName() + Constants.RTF_extension);
                Utilities.html2rtf(file, rtfFile);
                loadTextStyled(rtfFile);
            }
        } else {
            //pour le charset en UTF-8
            String text = Utilities.getTextInFile(file, Utilities.UTF8_CHARSET);

            //pour le charset par défaut de windows
            if (text == null || text.isEmpty()) {
                text = Utilities.getTextInFile(file, Utilities.WINDOWS_CHARSET);
            }

            if (text != null) {
                fireTextLoaded(text);
            }
        }
    }

    /**
     * Charge un fichier de type RTF dans la zone de texte.
     *
     * @param file le fichier à charger.
     */
    private void loadTextStyled(File file) throws ThotException {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            //effacement
            styledDocument.remove(0, styledDocument.getLength());
            //insertion
            styledEditorKit.read(fileInputStream, styledDocument, 0);
        } catch (BadLocationException | IOException e) {
            throw new ThotException(ThotCodeException.READ_RIGHT, "Impossible de charger le fichier texte {}", e,
                    file.getAbsolutePath());
        }
    }

    /**
     * Charge un fichier multimédia.
     *
     * @param file le chemin absolu du fichier à charger.
     */
    private void loadMedia(File file) throws ThotException {
        if (mediaType != Constants.UNLOAD) {
            unloadMedia();
        }

        projectFiles.setVideoFile(file.getAbsolutePath());

        //test si c'est une image lisible par java.
        boolean image = Utilities.isImageFile(file);
        if (image) {
            loadImage(file);
        } else {
            mediaPlayer.setMedia(file.getAbsolutePath());
            mediaPlayer.setVideoSubtitleFile(null);
            mediaPlayer.setVolume(0);

            long time = mediaPlayer.getMediaLength();
//            time = -1;//v0.88: pour initialiser VLC en version 1.1.x avec la vidéo

            if (time <= 0) {
                //on est obligé de faire play pour charger la vidéo et ainsi obtenir la taille de la vidéo.
                mediaPlayer.play();

                time = mediaPlayer.getLength();

                long initTime = System.currentTimeMillis();
                //attendre que la vidéo soit compétement configurée
                while (time <= 0 || !mediaPlayer.isPlaying()) {
                    mediaPlayer.setVolume(0);

                    if (mediaPlayer.isEndReached() || mediaPlayer.isErrorOccured()) {
                        setMediaType(Constants.UNLOAD);
                        projectFiles.setVideoFile(null);
                        throw new ThotException(ThotCodeException.READ_RIGHT,
                                "Impossible de charger le fichier {} : le lecteur multimedia est dans l'état {}",
                                file.getAbsolutePath(), mediaPlayer.getState());
                    }

                    Utilities.waitInMillisecond(10);
                    time = mediaPlayer.getLength();

                    //temps trop long pour chargé -> conversion en mp4 et mp3
                    if (System.currentTimeMillis() - initTime > 2000) {
                        if (mediaPlayer.isPlaying()) {
                            mediaPlayer.pause();
                        }

                        String tempExtension = ".convert.mp4";
                        if (file.getName().endsWith(tempExtension)) {
                            throw new ThotException(ThotCodeException.READ_RIGHT,
                                    "Impossible de charger le fichier {} : le format n'est pas supporté",
                                    file.getAbsolutePath());
                        }

                        File destFile = new File(file.getParentFile(),
                                Utilities.getNameWithoutExtension(file) + tempExtension);
                        executeConverter(file, destFile);
                        loadMedia(destFile);
                    }
                }

                //waitInMillisecond(100);
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                }
            }

            mediaPlayer.setPosition(0);
            //Ajustement de l'audio
            mediaPlayer.setVolume(mediaVolume);

            setMediaType(Constants.VIDEO_FILE);

            setRecordTimeMax(time);
        }

        setTime(0);
    }

    @Override
    public void removeProject(ProjectFiles project) {
        if (project.getVideoFile() != null) {
            unloadMedia();
        }
        if (project.getAudioFile() != null) {
            audioErase();
        }
        if (project.getTextFile() != null) {
            textErase();
        }
    }

    /**
     * Déchargement de la vidéo.
     */
    private void unloadMedia() {
        //on nettoie le fichier projet
        projectFiles.setVideoFile(null);
        projectFiles.setIndexesFile(null);
        projectFiles.setSubtitleFile(null);
        mediaPlayerSetSubtitleFile(null);
        setIndexesMode(false);

        //On enlève les index multimédia précédents
        if (mediaIndexes.getIndexesCount() > 0) {
            mediaIndexes.removeAll();
            setRecordTimeMaxByIndexes();
            fireIndexesChanged();
        }

        if (mediaType == Constants.IMAGE_FILE) {
            fireImageChanged(null);//chargement de l'image par défaut
        } else if (mediaType == Constants.VIDEO_FILE || mediaType == Constants.AUDIO_FILE) {
            mediaPlayer.stop();//libére la resource de dessin utilisée par VLC
        }

        setMediaType(Constants.UNLOAD);
    }

    /**
     * Vérifie la validité des indexes et ajuste le mode de lecture des index.
     *
     * @return la normalité des index.
     */
    public int checkMultimediaIndexesValidity() {
        return mediaIndexes.getGlobalValidity();
    }

    /**
     * Sauvegarde le projet dans un fihier dans le répertoire donné (les références sont relatives).
     *
     * @param file le fichier projet.
     * @param project les éléments à sauvegarder.
     */
    @Override
    public void saveProject(File file, ProjectFiles project) throws ThotException {
        //le nom sans extension du fichier principal pour le nom du répertoire
        String name = Utilities.getNameWithoutExtension(file);

        //répertoire où le(s) fichier(s) seront enregistrés.
        File path = new File(tempPath, name);
        if (path.exists()) {
            Utilities.deteleFiles(path);
            path.delete();
        }

        path.mkdirs();

        //réfrences sur les fichiers sauvegardés dans un projet
        ProjectFiles savedFiles = new ProjectFiles();

        //sauvegarde de la vidéo et conversion video+audio
        if (project.getVideoFile() != null) {
            File currentVideoFile = new File(projectFiles.getVideoFile());
            File videoFile = new File(path, currentVideoFile.getName());
            Utilities.fileCopy(currentVideoFile, videoFile);
            savedFiles.setVideoFile(videoFile.getName());

            if (projectFiles.getIndexesFile() != null) {
                File currentIndexFile = new File(projectFiles.getIndexesFile());
                File indexesFile = new File(path, currentIndexFile.getName());
                Utilities.fileCopy(currentIndexFile, indexesFile);
                savedFiles.setIndexesFile(indexesFile.getName());
            }
        }

        //sauvegarde de l'audio
        if (project.getAudioFile() != null) {
            File audioFile = new File(path, name + project.getAudioFile());
            //sauvegarde de l'audio dans un fichier temporaire au format wav
            saveAudio(audioFile);
            savedFiles.setAudioFile(audioFile.getName());
        }

        //sauvegarde du texte associé
        if (project.getTextFile() != null) {
            File textFile = new File(path, name + project.getTextFile());
            saveText(textFile);
            savedFiles.setTextFile(textFile.getName());
        }

        //sauvegarde du fichier projet et compression
        if (!savedFiles.isEmptyProject()) {
            File projectFile = new File(path, name + Constants.projectExtension);
            Utilities.saveObject(savedFiles, projectFile);
            Utilities.compressFile(path, file);
        }

        savedFiles.clear();
    }

    /**
     * Sauvegarde les données saisies dans la zone de texte dans un fichier.
     *
     * @param file le fichier.
     */
    private void saveText(File file) throws ThotException {
        File destFile = file;
        if (!Utilities.isTextFile(destFile)) {
            destFile = new File(file.getAbsoluteFile() + textDefaultExtension);
        }

        projectFiles.setTextFile(destFile.getAbsolutePath());

        if (Utilities.isTextStyledFile(destFile)) {
            if (destFile.getName().toLowerCase().endsWith(Constants.RTF_extension)) {
                saveTextStyled(destFile);
            } else {
                File rtfFile = new File(tempPath, destFile.getName() + Constants.RTF_extension);
                saveTextStyled(rtfFile);
                Utilities.rtf2html(rtfFile, destFile);
            }
        } else {
            try {
                String text = styledDocument.getText(0, styledDocument.getLength());
                Utilities.saveText(text, destFile);
            } catch (BadLocationException e) {
                throw new ThotException(ThotCodeException.WRITE_RIGHT,
                        "Impossible de sauvegarder le texte dans le fichier {}", e, file.getAbsolutePath());
            }
        }
    }

    /**
     * Sauvegarde les données saisies dans la zone de texte dans un fichier RTF.
     *
     * @param file le fichier.
     */
    private void saveTextStyled(File file) throws ThotException {
        try (FileOutputStream output = new FileOutputStream(file)) {
            styledEditorKit.write(output, styledDocument, 0, styledDocument.getLength());
            output.flush();
        } catch (IOException | BadLocationException e) {
            throw new ThotException(ThotCodeException.WRITE_RIGHT,
                    "Impossible de sauvegarder le texte dans le fichier {}", e, file.getAbsolutePath());
        }

    }

    /**
     * Donne le texte pour le boc-notes des index.
     *
     * @return le texte de la fenêtre principale.
     */
    @Override
    public String getText() {
        String text = null;
        try {
            text = styledDocument.getText(0, styledDocument.getLength());
        } catch (BadLocationException e) {
            LOGGER.error("Impossible de récupérer le texte", e);
        }
        return text;
    }

    /**
     * Efface le texte dans le module texte.
     */
    private void textErase() {
        projectFiles.setTextFile(null);

        try {
            styledDocument.remove(0, styledDocument.getLength());
        } catch (BadLocationException e) {
            LOGGER.error("Impossible d'effacer le texte", e);
        }
        fireTextLoaded(null);
    }

    /**
     * Lance la lecture des données enregistrées au microphone.
     */
    public void audioPlay() {
        //si on en mode index et que l'on ne lit pas un index et la thread de
        //lecture automatique n'est pas lancée, on la lance.
        if (indexesMode && !onIndex && !indexesPlayer.isAlive()) {
            indexesPlayer.start();
            return;
        }

        if (runningState == Constants.RECORDING) {
            userStop = false;
            audioPause();
        }

        //Modification de l'état des boutons du module audio
        setRunningState(Constants.PLAYING);

        //si on est à la fin de la bande on remet à 0
        if (currentTime >= recordTimeMax) {
            setTime(0);
        }

        if (!mediaPlayer.isPlaying() && (mediaType == Constants.VIDEO_FILE || mediaType == Constants.AUDIO_FILE)) {
            mediaPlayerPlay();
        }

        audioPlayer.start(currentTime, stopTime);
    }

    /**
     * Stop la lecture ou l'enregistrement du module audio.
     */
    public void audioPause() {
        //si c'est l'utilisateur ou si on lit un index sans être en lecture
        //automatique, on arrête la bande multimédia si nécessaire.
        if ((userStop || (!indexesPlayer.isAlive() && onIndex))
                && (mediaType == Constants.VIDEO_FILE || mediaType == Constants.AUDIO_FILE)) {
            mediaPlayerPause();
        }

        //si c'est l'utilisateur qui arrête, on arrête la lecture automatique
        if (userStop && indexesPlayer.isAlive()) {
            indexesPlayer.stop();
        }

        //pour dire que la lecture sur un index est finie
        onIndex = false;
        userStop = true;

        if (runningState == Constants.RECORDING) {
            audioRecorder.stop();
            while (audioRecorder.isAlive()) {
                Utilities.waitInMillisecond(5);
            }

            recordIndexes.addHalfSubtitleIndex(beginRecordTime, currentTime);
            fireIndexesChanged();
        } else if (runningState == Constants.PLAYING) {
            audioPlayer.stop();
            while (audioPlayer.isAlive()) {
                Utilities.waitInMillisecond(5);
            }
        }

        //Modification de l'état des boutons du module audio
        setRunningState(Constants.PAUSE);

        stopTime = recordTimeMax;
    }

    /**
     * Lance l'enregistrement du microphone.
     */
    public void audioRecord() {
        //si on en mode index et que l'on ne lit pas un index et la thread de
        //lecture automatique n'est pas lancée, on la lance.
        if (indexesMode && !onIndex && !indexesPlayer.isAlive()) {
            indexesPlayer.start();
            return;
        }

        if (runningState == Constants.PLAYING) {
            userStop = false;
            audioPause();
        }

        //Modification de l'état des boutons du module audio
        setRunningState(Constants.RECORDING);

        //si on est pas sur un index, ou que l'index n'est pas un index
        //d'enregistrement alors on crée un index d'enregistrement.
        if (!onRecordIndex(currentTime)) {
            recordIndexes.add(new Index(IndexType.RECORD, currentTime));
            fireIndexesChanged();
        }

        if (!mediaPlayer.isPlaying() && (mediaType == Constants.VIDEO_FILE || mediaType == Constants.AUDIO_FILE)) {
            mediaPlayerPlay();
        }

        beginRecordTime = currentTime;
        audioRecorder.start(currentTime, stopTime);
    }

    /**
     * Efface la bande audio.
     */
    protected void audioErase() {
        projectFiles.setAudioFile(null);

        if (recordIndexes.getIndexesCount() > 0) {
            recordIndexes.removeAll();
            fireIndexesChanged();
        }
    }

    protected boolean audioLoad(File file) throws ThotException {
        return false;
    }

    protected boolean saveAudio(File file) throws ThotException {
        return false;
    }

    /**
     * Pour appeler le programme de conversion avec le fichier source et le fichier de destination.
     *
     * @param srcFile le fichier source.
     * @param destFile le fichier destination.
     */
    protected void executeConverter(File srcFile, File destFile) throws ThotException {
        converter.convert(destFile, srcFile, null);
    }

    /**
     * Pour appeler le programme de conversion avec le fichier source et le fichier de destination.
     *
     * @param srcFile le fichier source.
     * @param destFile le fichier destination.
     * @param audioRate la fréquence en Hz.
     * @param channels le nombre de canaux audio.
     */
    protected void executeConverter(File srcFile, File destFile, int audioRate, int channels) throws ThotException {
        converter.convert(destFile, srcFile, null, audioRate, channels);
    }

    /**
     * Retourne la présence d'une piste audio dans le fichier.
     *
     * @param file le fichier source.
     *
     * @return si le fichier contient une piste audio.
     */
    @Override
    public boolean hasAudioSrteam(File file) throws ThotException {
        return converter.hasAudioSrteam(file);
    }

    /**
     * Retourne la présence d'une piste vidéo dans le fichier.
     *
     * @param file le fichier source.
     *
     * @return si le fichier contient une piste vidéo.
     */
    @Override
    public boolean hasVideoSrteam(File file) throws ThotException {
        return converter.hasVideoSrteam(file);
    }

    /**
     * Annule une conversion en cours.
     */
    public void cancelConversion() throws ThotException {
        converter.cancel();
    }

    /**
     * Notification interne de changement du temps pour les thread de lecture et d'enregistrement.
     *
     * @param time le nouveau temps.
     */
    private void fireProcessTimeChanged(long time) {
        setTime(time);
    }

    /**
     * Notification interne de la fin des thread de lecture et d'enregistrement.
     *
     * @param running {@code true} si la thread ne sait pas stoppée d'elle même.
     */
    public void fireProcessEnded(final boolean running) {
        //Traitement dans un thread séparée pour éviter un blocage
        new Thread(() -> {
            //si on est sur un index
            if (onIndex) {
                userStop = false;
                audioPause();
            } else if (running) {
                audioPause();
                fireProcessTimeChanged(0);
            }
        }, this.getClass().getName()).start();
    }

    /**
     * Réinitialise le audioPlayer VLC. Nécessaire après la conversion qui change les paramètres de VLC (qui sont
     * globaux à toutes les instances de vlc).
     */
    private void mediaPlayerInit() {
        //Lecteurs multimédia
        mediaPlayer = new VLCMediaPlayer();
        mediaPlayer.setVolume(mediaVolume);

        //pour mettre en pause la video quand on est arrivé à la fin
        mediaPlayer.addMediaPlayerListener(new MediaPlayerAdapter() {
            @Override
            public void endReached(MediaPlayerEvent event) {
                audioPause();//mise à jour de l'état des boutons
                setTime(0);
            }
        });
    }

    /**
     * Ferme et désalloue le audioPlayer VLC.
     */
    private void mediaPlayerClose() {
        //mediaPlayer.stop();
        mediaPlayer.release();
    }

    /**
     * Initialise la sortie vidéo.
     *
     * @param canvas le canvas où sera afficher la vidéo.
     */
    public void mediaPlayerSetVideoOutput(Canvas canvas) {
        mediaPlayer.setVideoOutput(canvas);
    }

    /**
     * Change le mode plein écran.
     *
     * @param fullscreen le mode plein écran.
     */
    protected void setFullScreen(boolean fullscreen) {
        if (mediaType == Constants.VIDEO_FILE) {
            mediaPlayer.setFullScreen(fullscreen);
        }
//        else if(mediaType == Constants.IMAGE_FILE)
        fireFullScreenChanged(fullscreen);
    }

    /**
     * Modifie le fichier de soustitres.
     *
     * @param fileName le fichier de soustitres.
     */
    private void mediaPlayerSetSubtitleFile(String fileName) {
        projectFiles.setSubtitleFile(fileName);
        mediaPlayer.setVideoSubtitleFile(fileName);
    }

    /**
     * Modifie le temps du media.
     *
     * @param time le temps sur la piste élève.
     */
    private void mediaPlayerSetTime(long time) {
        //si on est pause on change le temps du mediaPlayer
        if (runningState == Constants.PAUSE) {
            double position = (double) time / recordTimeMax;
            mediaPlayer.setPosition((float) position);
//            mediaPlayer.setTime(playerTime);
        }
    }

    /**
     * Déclenche la lecture du module multimédia.
     */
    private void mediaPlayerPlay() {
        if (mediaPlayer.isEndReached()) {
            mediaPlayer.stop();
        }

        mediaPlayer.play();
        mediaPlayer.setVideoSubtitleFile(projectFiles.getSubtitleFile());
    }

    /**
     * Arrête la lecture du module multimédia.
     */
    private void mediaPlayerPause() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    /**
     * Classe pour la lecture automatique des index multimédia.
     */
    private class IndexesPlayer implements Runnable {

        /**
         * Mode actif.
         */
        private boolean run;
        /**
         * Thread du procesus.
         */
        private Thread thread;

        /**
         * Démarre le processus.
         */
        private void start() {
            run = true;
            thread = new Thread(this, this.getClass().getName());
            thread.setPriority(Thread.MAX_PRIORITY);
            thread.start();
            thread.setPriority(Thread.MAX_PRIORITY);
        }

        /**
         * Arrête le processus.
         */
        private void stop() {
            run = false;
        }

        /**
         * Etat normal de processus.
         *
         * @return si le processus n'a pas été arrêté.
         */
        private boolean isRun() {
            return run;
        }

        /**
         * Retourne si le processus est actif.
         *
         * @return si le processus est actif.
         */
        private boolean isAlive() {
            if (thread == null) {
                return false;
            } else {
                return thread.isAlive();
            }
        }

        /**
         * Traitement courant.
         */
        @Override
        public void run() {
            int timeIndex;
            IndexType type;

            AudioProcessing process;

            //Temps de fin de chaque plage
            List<Long> stopTimes = new ArrayList<>(mediaIndexes.getIndexesCount());
            //Type de chaque plage avec l'impératif que types(i) != types(i+1)
            //pour éviter un grand nombre de pauses
            List<IndexType> types = new ArrayList<>(mediaIndexes.getIndexesCount());

            //remplisage des différentes plage de lecture et enregistrement avec
            //fusion des plages consécutives.
            long oldTime = 0;

            for (Iterator<Index> it = mediaIndexes.iterator(); it.hasNext(); ) {
                Index index = it.next();

                if (index.getInitialTime() > oldTime) {
                    //si le temps initial de l'index n'est pas le même que le
                    //temps final de l'index précédent, on insère une plage de
                    //lecture.
                    timeIndex = types.size();
                    if (timeIndex == 0) {//si premier index
                        stopTimes.add(index.getInitialTime());
                        types.add(IndexType.PLAY);
                    } else if (types.get(timeIndex - 1) == IndexType.PLAY) {
                        //si la précende plage est une plage de lecture,
                        //on fusionne avec l'ancienne
                        oldTime = index.getInitialTime();
                        stopTimes.set(timeIndex - 1, oldTime);
                    } else {//== Index.RECORD
                        //si la précende plage n'est pas une plage de lecture,
                        //on rajoute une plage de lecture
                        stopTimes.add(index.getInitialTime());
                        types.add(IndexType.PLAY);
                    }
                }

                //insertion de l'index
                timeIndex = types.size();
                if (index.isStudentRecord()) {
                    type = IndexType.RECORD;
                } else {
                    type = IndexType.PLAY;
                }

                if (timeIndex == 0) {//si premier index
                    oldTime = index.getFinalTime();
                    stopTimes.add(oldTime);
                    types.add(type);
                } else if (types.get(timeIndex - 1) == type) {
                    //si l'index est du même type que la plage présédente,
                    //on fusionne avec l'ancienne
                    oldTime = index.getFinalTime();
                    stopTimes.set(timeIndex - 1, oldTime);
                } else {
                    //création de nouvelle plage
                    oldTime = index.getFinalTime();
                    stopTimes.add(oldTime);
                    types.add(type);
                }
            }

            //rajoute une piste de lecture si le temps de fin n'est pas le temps
            //de fin de la dernière plage.
            if (oldTime < recordTimeMax) {
                timeIndex = types.size();
                if (types.get(timeIndex - 1) == IndexType.RECORD) {
                    stopTimes.add(recordTimeMax);
                    types.add(IndexType.PLAY);
                } else {
                    stopTimes.set(timeIndex - 1, recordTimeMax);
                }
            }

            timeIndex = 0;
            //recherche l'index courant de la table des pistes
            for (int i = 0; i < stopTimes.size(); i++) {
                if (currentTime >= stopTimes.get(i)) {
                    timeIndex = i + 1;
                }
            }

            while (isRun()) {
                //si l'index de la table des pistes est trop grand, la lecture
                //automatique est finie.
                if (timeIndex == stopTimes.size()) {
                    break;
                }

                onIndex = true;//pour dire que l'on lit une plage de temps
                stopTime = stopTimes.get(timeIndex);

//                setTimeMediaPlayer(currentTime);

                type = types.get(timeIndex);
                //lancement de l'enregistrement ou la lecture suivant le type de
                //la piste et on attend la fin de la piste.
                switch (type) {
                    case PLAY:
                        process = audioPlayer;
                        audioPlay();
                        break;
                    case RECORD:
                        process = audioRecorder;
                        audioRecord();
                        break;
                    default:
                        process = audioPlayer;
                        audioPlay();
                }

                while (process.isAlive()) {
                    Utilities.waitInMillisecond(10);
                }

                //passage à la piste suivante
                timeIndex++;
            }
        }
    }
}
