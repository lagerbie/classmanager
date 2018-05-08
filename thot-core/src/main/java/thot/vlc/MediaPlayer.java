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
package thot.vlc;

import java.awt.*;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import thot.utils.Utilities;
import thot.vlc.event.MediaPlayerCallback;
import thot.vlc.event.MediaPlayerListener;
import thot.vlc.internal.libvlc_event_e;
import thot.vlc.internal.libvlc_event_manager_t;
import thot.vlc.internal.libvlc_instance_t;
import thot.vlc.internal.libvlc_media_player_t;
import thot.vlc.internal.libvlc_media_t;
import thot.vlc.internal.libvlc_state_t;

/**
 * Lecteur multimédia basé sur VLC.
 *
 * @author Fabrice Alleau
 * @version 2.1.4 (VLC 0.9.x à 2.1.x et compatible JET)
 */
public class MediaPlayer {

    /**
     * Référence à la librairie.
     */
    private static final LibVLC libvlc;
    /**
     * Version de VLC.
     */
    public static final String version;

    /**
     * Premier évènement à attacher.
     */
    private static libvlc_event_e EVENT_FIRST = libvlc_event_e.libvlc_MediaPlayerEndReached;
    /**
     * Dernier évènement à attacher.
     */
    private static libvlc_event_e EVENT_END = libvlc_event_e.libvlc_MediaPlayerEncounteredError;

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

    /*
     * @version 1.1.0
     */
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

        LibVLC INSTANCE = Native.loadLibrary(libraryName, LibVLC.class);
        libvlc = (LibVLC) Native.synchronizedLibrary(INSTANCE);

        version = libvlc.libvlc_get_version();
    }

    /**
     * Création d'un lecteur multimédia. Les arguments d'initialisation de la librairie vlc sont : --ignore-config
     * --no-snapshot-preview --no-sub-autodetect-file --no-plugins-cache
     */
    public MediaPlayer() {
        callbacks = new ArrayList<>(1);

        //arguments d'initialisation du module VLC
        List<String> args = new ArrayList<>(8);
        args.add("--ignore-config");//=valeur par défaut
        args.add("--no-snapshot-preview");
        args.add("--no-sub-autodetect-file");
        args.add("--no-overlay");
        args.add("--no-plugins-cache");
//        args.add("-vvv");

        String[] vlcArgs = args.toArray(new String[0]);
        createInstance(vlcArgs);
    }

    /**
     * Création et initialisation d'une librairie vlc.
     *
     * @param args les arguments d'initialisation de la librairie vlc.
     */
    private synchronized void createInstance(String[] args) {
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
    public void setMedia(String mrl) {
        //représentation d'un fichier/flux multimédia
        libvlc_media_t media_instance = libvlc.libvlc_media_new_path(libvlc_instance, mrl);
        libvlc.libvlc_media_player_set_media(media_player, media_instance);
        libvlc.libvlc_media_release(media_instance);
    }

    /**
     * Retourne si le player a un fichier.
     *
     * @return <code>true</code> si le player a un fichier.
     */
    public boolean hasMedia() {
        libvlc_media_t media_instance = libvlc.libvlc_media_player_get_media(media_player);
        return (media_instance != null);
    }

    /**
     * Lit le média.
     */
    public void play() {
        libvlc.libvlc_media_player_play(media_player);
    }

    /**
     * Mise en toggle pause la lecture du média. Nécessite un <code>play()</code> pour être utilisé.
     */
    public void pause() {
        libvlc.libvlc_media_player_pause(media_player);
    }

    /**
     * Arrête la lecture du média. Nécessite un <code>play()</code> pour redémarrer.
     */
    public void stop() {
        libvlc.libvlc_media_player_stop(media_player);
    }

    /**
     * Donne la durée du média en ms. Nécessite que le média soit en lecture.
     *
     * @return la durée du média en ms.
     */
    public long getMediaLength() {
        libvlc_media_t media = libvlc.libvlc_media_player_get_media(media_player);
        libvlc.libvlc_media_parse(media);
        return libvlc.libvlc_media_get_duration(media);
    }

    /**
     * Donne la durée du média en ms. Nécessite que le média soit en lecture.
     *
     * @return la durée du média en ms.
     */
    public long getLength() {
        return libvlc.libvlc_media_player_get_length(media_player);
    }

    /**
     * Donne le temps où l'on est (en ms). Bug après <code>pause()</code>, utiliser <code>getPosition()</code>.
     *
     * @return le temps où l'on est (en ms).
     */
    public long getTime() {
        return libvlc.libvlc_media_player_get_time(media_player);
    }

    /**
     * Déplace la lecture à l'endroit voulu (en ms).
     *
     * @param time l'endroit voulu (en ms).
     */
    public synchronized void setTime(long time) {
        libvlc.libvlc_media_player_set_time(media_player, time);
    }

    /**
     * Donne la position dans le média en pourcentage (de 0 à 1).
     *
     * @return la position dans le média.
     */
    public float getPosition() {
        return libvlc.libvlc_media_player_get_position(media_player);
    }

    /**
     * Déplace la lecture à l'endroit voulu en pourcentage (de 0 à 1).
     *
     * @param position l'endroit voulu.
     */
    public synchronized void setPosition(float position) {
        libvlc.libvlc_media_player_set_position(media_player, position);
    }

    /**
     * Donne la valeur du niveau sonore en pourcentage (de 0 à 200).
     *
     * @return la valeur du niveau sonore.
     */
    public int getVolume() {
        return libvlc.libvlc_audio_get_volume(media_player);
    }

    /**
     * Change la valeur du niveau sonore en pourcentage (de 0 à 200).
     *
     * @param volume la valeur du niveau sonore entre 0 à 200.
     */
    public synchronized void setVolume(int volume) {
        libvlc.libvlc_audio_set_volume(media_player, volume);
    }

    /**
     * Indique si le média à une représentation graphique.
     *
     * @return <code>true</code> si le média à une représentation graphique.
     */
    public boolean hasVideoOutput() {
        return (libvlc.libvlc_media_player_has_vout(media_player) == 1);
    }

    /**
     * Modifie le mode plein écran.
     *
     * @param fullscreen l'état du plein écran.
     */
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
    public boolean isFullScreen() {
        return (libvlc.libvlc_get_fullscreen(media_player) == 1);
    }

    /**
     * Configure la sortie vidéo. Le composant graphique doit être un heavyheight component et soit visible.
     *
     * @param component le composant graphique où sera la vidéo.
     */
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
    public void setVideoSubtitleFile(String fileName) {
        libvlc.libvlc_video_set_subtitle_file(media_player, fileName);
    }

    /**
     * Retourne l'état du lecteur.
     *
     * @return la valeur de l'état IDLE/CLOSE = 0, OPENING = 1, BUFFERING = 2, PLAYING = 3, PAUSED = 4, STOPPING = 5,
     *         ENDED = 6, ERROR = 7
     */
    public int getState() {
        return libvlc.libvlc_media_player_get_state(media_player);
    }

    /**
     * Indique si le lecteur a fini.
     *
     * @return <code>true</code> si le lecteur a fini.
     */
    public boolean isEndReached() {
        return getState() == libvlc_state_t.libvlc_Ended.intValue();
    }

    /**
     * Retourne si le lecteur est en mode lecture.
     *
     * @return <code>true</code> si le lecteur est en lecture.
     */
    public boolean isPlaying() {
        return getState() == libvlc_state_t.libvlc_Playing.intValue();
    }

    /**
     * Retourne si une erreur à eu lieu.
     *
     * @return <code>true</code> si une erreur est survenue.
     */
    public boolean isErrorOccured() {
        return getState() == libvlc_state_t.libvlc_Error.intValue();
    }

    /**
     * Ajoute un listener au lecteur.
     *
     * @param listener à ajouter.
     *
     * @danger ne pas faire mediaPlayer.pause() sur l'évènement.
     */
    public synchronized void addMediaPlayerListener(MediaPlayerListener listener) {
        MediaPlayerCallback callback = new MediaPlayerCallback(this, listener);

        for (libvlc_event_e event : EnumSet.range(EVENT_FIRST, EVENT_END)) {
            libvlc.libvlc_event_attach(eventManager, event.intValue(), callback, null);
        }
        callbacks.add(callback);
    }

    /**
     * Enlève un listener au lecteur.
     *
     * @param listener à ajouter.
     *
     * @danger ne pas faire mediaPlayer.pause() sur l'évènement.
     */
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
            libvlc.libvlc_event_detach(eventManager, event.intValue(), callback, null);
        }
    }

    /**
     * Libère la mémoire utilisée par le lecteur et la librairie vlc.
     */
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
            return splitResult[splitResult.length - 1].trim();
        } else {
            return null;
        }
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
