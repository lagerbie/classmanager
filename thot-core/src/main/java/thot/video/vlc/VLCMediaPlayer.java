package thot.video.vlc;

import java.awt.*;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thot.exception.ThotException;
import thot.utils.Utilities;
import thot.video.MediaPlayer;
import thot.video.MediaPlayerState;
import thot.video.event.MediaPlayerListener;
import thot.video.vlc.internal.MediaPlayerCallback;
import thot.video.vlc.internal.libvlc_event_e;
import thot.video.vlc.internal.libvlc_event_manager_t;
import thot.video.vlc.internal.libvlc_instance_t;
import thot.video.vlc.internal.libvlc_media_player_t;
import thot.video.vlc.internal.libvlc_media_t;
import thot.video.vlc.internal.libvlc_state_t;

/**
 * Lecteur multimédia basé sur VLC.
 *
 * @author Fabrice Alleau
 * @version 1.8.4 (VLC 1.1.0 à 3.0.x et compatible JET)
 */
public class VLCMediaPlayer implements MediaPlayer {

    /**
     * Instance de log.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(VLCMediaPlayer.class);

    /**
     * Référence à la librairie.
     */
    private static final LibVLC libvlc;

    /**
     * Premier évènement à attacher.
     */
    private static final libvlc_event_e EVENT_FIRST = libvlc_event_e.libvlc_MediaPlayerEndReached;
    /**
     * Dernier évènement à attacher.
     */
    private static final libvlc_event_e EVENT_END = libvlc_event_e.libvlc_MediaPlayerEncounteredError;

    /**
     * Instance du module vlc.
     */
    private libvlc_instance_t libvlc_instance;
    /**
     * Multimédia player instance.
     */
    private libvlc_media_player_t media_player;
    /**
     * Manager d'évènements.
     */
    private libvlc_event_manager_t eventManager;
    /**
     * Liste des procédures callback associées aux listeners.
     */
    private List<MediaPlayerCallback> callbacks;

    static {
        //Pour être sur que jna utilise de l'UTF8 et non le format par défaut de la machine virtuelle de l'OS.
        //Ainsi vlc lira correctement les fichiers avec des caractères spéciaux (entre autre les accents).
        System.setProperty("jna.encoding", "UTF8");

        String vlcPath = null;
        String libraryName = "vlc";

        if (Platform.isWindows()) {
            libraryName = "libvlc";
            vlcPath = getVLCpathOnWindows();
        } else if (Platform.isMac()) {
            vlcPath = getVLCpathOnMac();
            vlcPath += "/lib";
        }

        if (vlcPath != null) {
            NativeLibrary.addSearchPath(libraryName, vlcPath);
        }
        LOGGER.info("Chargement de la librairie de VLC");
        LibVLC INSTANCE = Native.loadLibrary(libraryName, LibVLC.class);
        libvlc = (LibVLC) Native.synchronizedLibrary(INSTANCE);

        LOGGER.info("Librairie VLC chargée en version {}", libvlc.libvlc_get_version());
    }

    /**
     * Création d'un lecteur multimédia.
     * <p>
     * Les arguments d'initialisation de la librairie vlc sont :
     * <p>
     * --ignore-config
     * <p>
     * --no-snapshot-preview
     * <p>
     * --no-sub-autodetect-file
     * <p>
     * --no-plugins-cache
     */
    public VLCMediaPlayer() {
        callbacks = new ArrayList<>(1);
//        options pour le verbose "-vvv"

        createInstance("--ignore-config", "--no-snapshot-preview", "--no-sub-autodetect-file", "--no-overlay",
                "--no-plugins-cache");
    }

    /**
     * Création et initialisation d'une librairie vlc.
     *
     * @param args les arguments d'initialisation de la librairie vlc.
     */
    private synchronized void createInstance(String... args) {
        LOGGER.info("Initialisation de VLC {}", libvlc.libvlc_get_version());
        if (args == null) {
            libvlc_instance = libvlc.libvlc_new(0, null);
        } else {
            libvlc_instance = libvlc.libvlc_new(args.length, args);
        }

        //création du lecteur
        media_player = libvlc.libvlc_media_player_new(libvlc_instance);

        //création du manager d'évènements
        eventManager = libvlc.libvlc_media_player_event_manager(media_player);
    }

    /**
     * Change le média à lire.
     *
     * @param mrl le nouveau média à lire.
     */
    @Override
    public void setMedia(String mrl) {
        //représentation d'un fichier/flux multimédia
        libvlc_media_t media_instance = libvlc.libvlc_media_new_path(libvlc_instance, mrl);
        libvlc.libvlc_media_player_set_media(media_player, media_instance);
        libvlc.libvlc_media_release(media_instance);
    }

    /**
     * Lit le média.
     */
    @Override
    public void play() {
        libvlc.libvlc_media_player_play(media_player);
    }

    /**
     * Mise en toggle pause la lecture du média.
     * <p>
     * Nécessite un {@code play()} pour être utilisé.
     */
    @Override
    public void pause() {
        libvlc.libvlc_media_player_pause(media_player);
    }

    /**
     * Arrête la lecture du média.
     * <p>
     * Nécessite un {@code play()} pour redémarrer.
     */
    @Override
    public void stop() {
        libvlc.libvlc_media_player_stop(media_player);
    }

    /**
     * Donne la durée du média en ms.
     * <p>
     * Nécessite que le média soit en lecture.
     *
     * @return la durée du média en ms.
     */
    @Override
    public long getMediaLength() {
        libvlc_media_t media = libvlc.libvlc_media_player_get_media(media_player);
        libvlc.libvlc_media_parse(media);
        return libvlc.libvlc_media_get_duration(media);
    }

    /**
     * Donne la durée du média en ms.
     * <p>
     * Nécessite que le média soit en lecture.
     *
     * @return la durée du média en ms.
     */
    @Override
    public long getLength() {
        return libvlc.libvlc_media_player_get_length(media_player);
    }

    /**
     * Donne le temps où l'on est (en ms).
     * <p>
     * Bug après {@code pause()}, utiliser {@code getPosition()}.
     *
     * @return le temps où l'on est (en ms).
     */
    @Override
    public long getTime() {
        return libvlc.libvlc_media_player_get_time(media_player);
    }

    /**
     * Déplace la lecture à l'endroit voulu (en ms).
     *
     * @param time l'endroit voulu (en ms).
     */
    @Override
    public synchronized void setTime(long time) {
        libvlc.libvlc_media_player_set_time(media_player, time);
    }

    /**
     * Donne la position dans le média en pourcentage (de 0 à 1).
     *
     * @return la position dans le média.
     */
    @Override
    public float getPosition() {
        return libvlc.libvlc_media_player_get_position(media_player);
    }

    /**
     * Déplace la lecture à l'endroit voulu en pourcentage (de 0 à 1).
     *
     * @param position l'endroit voulu.
     */
    @Override
    public synchronized void setPosition(float position) {
        libvlc.libvlc_media_player_set_position(media_player, position);
    }

    /**
     * Donne la valeur du niveau sonore en pourcentage (de 0 à 200).
     *
     * @return la valeur du niveau sonore.
     */
    @Override
    public int getVolume() {
        return libvlc.libvlc_audio_get_volume(media_player);
    }

    /**
     * Change la valeur du niveau sonore en pourcentage (de 0 à 200).
     *
     * @param volume la valeur du niveau sonore entre 0 à 200.
     */
    @Override
    public synchronized void setVolume(int volume) {
        libvlc.libvlc_audio_set_volume(media_player, volume);
    }

    /**
     * Modifie le mode plein écran.
     *
     * @param fullscreen l'état du plein écran.
     */
    @Override
    public void setFullScreen(boolean fullscreen) {
        if (isFullScreen() == fullscreen) {
            return;
        }

        int i_fullscreen = fullscreen ? 1 : 0;
        libvlc.libvlc_set_fullscreen(media_player, i_fullscreen);
    }

    /**
     * Indique si on est en plein écran ou non.
     *
     * @return le mode plein écran.
     */
    @Override
    public boolean isFullScreen() {
        return (libvlc.libvlc_get_fullscreen(media_player) == 1);
    }

    /**
     * Configure la sortie vidéo.
     * <p>
     * Le composant graphique doit être un heavyheight component et soit visible.
     *
     * @param component le composant graphique où sera la vidéo.
     */
    @Override
    public synchronized void setVideoOutput(Component component) {
        boolean linux = Platform.isLinux();
        boolean windows = Platform.isWindows();
        boolean mac = Platform.isMac();

        if (linux) {
            long drawable = Native.getComponentID(component);
            libvlc.libvlc_media_player_set_xwindow(media_player, (int) drawable);
        } else if (windows) {
            Pointer drawable = Native.getComponentPointer(component);
            libvlc.libvlc_media_player_set_hwnd(media_player, drawable);
        } else if (mac) {
            Pointer drawable = Native.getComponentPointer(component);
            libvlc.libvlc_media_player_set_nsobject(media_player, drawable);
        }
    }

    /**
     * Indique le fichier de soustitrage à utiliser.
     *
     * @param fileName le nom du fichier de soustitrage.
     */
    @Override
    public void setVideoSubtitleFile(String fileName) {
        libvlc.libvlc_video_set_subtitle_file(media_player, fileName);
    }

    /**
     * Retourne l'état du lecteur.
     *
     * @return la valeur de l'état IDLE/CLOSE = 0, OPENING = 1, BUFFERING = 2, PLAYING = 3, PAUSED = 4, STOPPING = 5,
     *         ENDED = 6, ERROR = 7
     */
    @Override
    public MediaPlayerState getState() {
        return libvlc_state_t.getState(libvlc.libvlc_media_player_get_state(media_player));
    }

    /**
     * Ajoute un listener au lecteur.
     *
     * @param listener à ajouter.
     *         <p>
     *         WARNING: ne pas faire mediaPlayer.pause() sur l'évènement.
     */
    @Override
    public synchronized void addMediaPlayerListener(MediaPlayerListener listener) {
        MediaPlayerCallback callback = new MediaPlayerCallback(this, listener);

        for (libvlc_event_e event : EnumSet.range(EVENT_FIRST, EVENT_END)) {
            libvlc.libvlc_event_attach(eventManager, event.getVlcValue(), callback, null);
        }
        callbacks.add(callback);
    }

    /**
     * Enlève un listener au lecteur.
     * <p>
     * WARNING: ne pas faire mediaPlayer.pause() sur l'évènement.
     *
     * @param listener à ajouter.
     */
    @Override
    public synchronized void removeMediaPlayerListener(MediaPlayerListener listener) {
        for (MediaPlayerCallback callback : callbacks) {
            if (callback.getListener() == listener) {
                callbacks.remove(callback);
                removeCallback(callback);
            }
        }
    }

    /**
     * Détache le callback attaché au player VLC.
     *
     * @param callback le callback à enlever.
     */
    private synchronized void removeCallback(MediaPlayerCallback callback) {
        for (libvlc_event_e event : EnumSet.range(EVENT_FIRST, EVENT_END)) {
            libvlc.libvlc_event_detach(eventManager, event.getVlcValue(), callback, null);
        }
    }

    /**
     * Libère la mémoire utilisée par le lecteur et la librairie vlc.
     */
    @Override
    public synchronized void release() {
        for (MediaPlayerCallback callback : callbacks) {
            removeCallback(callback);
        }
        callbacks.clear();

        libvlc.libvlc_release(libvlc_instance);
    }

    /**
     * Retourne le chemin du répertoire de VLC sous Windows.
     *
     * @return le chemin du répertoire de VLC.
     */
    private static String getVLCpathOnWindows() {
        String path = null;
        try {
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
                path = splitResult[splitResult.length - 1].trim();
            }
        } catch (ThotException e) {
            LOGGER.error("Impossible de trouver la librairie de VLC", e);
        }
        return path;
    }

    /**
     * Retourne le chemin du répertoire de VLC sous Mac.
     *
     * @return le chemin du répertoire de VLC.
     */
    private static String getVLCpathOnMac() {
        return "/Applications/VLC.app/Contents/MacOS";
    }
}
