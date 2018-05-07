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

import java.awt.Component;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import thot.utils.Utilities;

import thot.vlc.internal.libvlc_instance_t;
import thot.vlc.internal.libvlc_exception_t;
import thot.vlc.internal.libvlc_media_t;
import thot.vlc.internal.libvlc_media_player_t;
import thot.vlc.internal.libvlc_event_e;
import thot.vlc.internal.libvlc_event_manager_t;
import thot.vlc.internal.libvlc_state_t;

import thot.vlc.event.MediaPlayerCallback;
import thot.vlc.event.MediaPlayerListener;

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
    private static libvlc_event_e EVENT_FIRST            = libvlc_event_e.libvlc_MediaPlayerEndReached;
    /**
     * Dernier évènement à attacher.
     */
    private static libvlc_event_e EVENT_END            = libvlc_event_e.libvlc_MediaPlayerEncounteredError;

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

    /**
     * Chemin des plugins VLC
     *
     * @deprecated VLC 1.1.0
     */
    @Deprecated
    private static String pluginsPath = "plugins";
    /**
     * Notification des versions antérieures (VLC <= 1.0.x).
     */
    private static final boolean OLD_VERSION;

    /**
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
            pluginsPath = vlcPath + "\\plugins";
        } else if (Platform.isMac()) {
            vlcPath = getVLCpathOnMac();
            pluginsPath = vlcPath + "/plugins";
            vlcPath += "/lib";
        }

        if (vlcPath != null) {
            NativeLibrary.addSearchPath(libraryName, vlcPath);
        }

        LibVLC INSTANCE = Native.loadLibrary(libraryName, LibVLC.class);
        libvlc = (LibVLC) Native.synchronizedLibrary(INSTANCE);

        version = libvlc.libvlc_get_version();
        int first = version.charAt(0) - '0';
        int second = version.charAt(2) - '0';
//        int third = version.charAt(4) - '0';

        if (first < 1 || (first == 1 && second == 0)) {
            OLD_VERSION = true;
        } else {
            OLD_VERSION = false;
        }
    }

    /**
     * Création d'un lecteur multimédia.
     * Les arguments d'initialisation de la librairie vlc sont :
     *      --ignore-config
     *      --no-snapshot-preview
     *      --no-sub-autodetect-file
     *      --no-plugins-cache
     *
     * @version 1.0.0
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
        if (OLD_VERSION) {
            args.add("--plugin-path=" + pluginsPath);//pour VLC 1.0.x
//            args.add("-vvv");
        }

        String[] vlcArgs = args.toArray(new String[0]);
        createInstance(vlcArgs);
    }

    /**
     * Création et initialisation d'une librairie vlc.
     *
     * @param args les arguments d'initialisation de la librairie vlc.
     * @version 1.1.0
     */
    private synchronized void createInstance(String[] args) {
        if (OLD_VERSION) {
            createInstanceV09x(args);
            return;
        }

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
     * Création et initialisation d'une librairie vlc.
     *
     * @param args les arguments d'initialisation de la librairie vlc.
     * @since version 0.9.0
     * @deprecated VLC 1.1.0
     */
    @Deprecated
    private synchronized void createInstanceV09x(String[] args) {
        libvlc_exception_t exception = new libvlc_exception_t();
        if (args == null) {
            libvlc_instance = libvlc.libvlc_new(0, null, exception);
        } else {
            libvlc_instance = libvlc.libvlc_new(args.length, args, exception);
        }

        //création du lecteur
        media_player = libvlc.libvlc_media_player_new(libvlc_instance, exception);

        //création du manager d'évènements
        eventManager = libvlc.libvlc_media_player_event_manager(media_player,
                exception);
    }

    /**
     * Change le média à lire.
     *
     * @param mrl le nouveau média à lire.
     * @version 1.1.0
     */
    public void setMedia(String mrl) {
        if (OLD_VERSION) {
            setMediaV09x(mrl);
            return;
        }
        //représentation d'un fichier/flux multimédia
        libvlc_media_t media_instance = libvlc.libvlc_media_new_path(libvlc_instance, mrl);
        libvlc.libvlc_media_player_set_media(media_player, media_instance);
        libvlc.libvlc_media_release(media_instance);
    }

    /**
     * Change le média à lire.
     *
     * @param mrl le nouveau média à lire.
     * @since version 0.9.0
     * @deprecated VLC 1.1.0
     */
    @Deprecated
    private void setMediaV09x(String mrl) {
        libvlc_exception_t exception = new libvlc_exception_t();
        //représentation d'un fichier/flux multimédia
        libvlc_media_t media_instance = libvlc.libvlc_media_new(libvlc_instance, mrl, exception);
        libvlc.libvlc_media_player_set_media(media_player, media_instance, exception);
        libvlc.libvlc_media_release(media_instance);
    }

    /**
     * Retourne si le player a un fichier.
     *
     * @return <code>true</code> si le player a un fichier.
     * @version 1.1.0
     */
    public boolean hasMedia() {
        if (OLD_VERSION) {
            return hasMediaV09x();
        }

        libvlc_media_t media_instance = libvlc.libvlc_media_player_get_media(media_player);
        return (media_instance != null);
    }

    /**
     * Retourne si le player a un fichier.
     *
     * @return <code>true</code> si le player a un fichier.
     * @since version 0.9.0
     * @deprecated VLC 1.1.0
     */
    @Deprecated
    private boolean hasMediaV09x() {
        libvlc_exception_t exception = new libvlc_exception_t();
        //représentation d'un fichier/flux multimédia
        libvlc_media_t media_instance = libvlc.libvlc_media_player_get_media(media_player, exception);
        return (media_instance != null);
    }

    /**
     * Lit le média.
     *
     * @version 1.1.0
     */
    public void play() {
        if (OLD_VERSION) {
            playV09x();
            return;
        }
        libvlc.libvlc_media_player_play(media_player);
    }

    /**
     * Lit le média.
     *
     * @since version 0.9.0
     * @deprecated VLC 1.1.0
     */
    @Deprecated
    private void playV09x() {
        libvlc_exception_t exception = new libvlc_exception_t();
        libvlc.libvlc_media_player_play(media_player, exception);
    }

    /**
     * Mise en toggle pause la lecture du média.
     * Nécessite un <code>play()</code> pour être utilisé.
     *
     * @version 1.1.0
     */
    public void pause() {
        if (OLD_VERSION) {
            pauseV09x();
            return;
        }
        libvlc.libvlc_media_player_pause(media_player);
    }

    /**
     * Mise en toggle pause la lecture du média.
     * Nécessite un <code>play()</code> pour être utilisé.
     *
     * @since version 0.9.0
     * @deprecated VLC 1.1.0
     */
    @Deprecated
    private void pauseV09x() {
        libvlc_exception_t exception = new libvlc_exception_t();
        libvlc.libvlc_media_player_pause(media_player, exception);
    }

    /**
     * Arrête la lecture du média.
     * Nécessite un <code>play()</code> pour redémarrer.
     *
     * @version 1.1.0
     */
    public void stop() {
        if (OLD_VERSION) {
            stopV09x();
            return;
        }
        libvlc.libvlc_media_player_stop(media_player);
    }

    /**
     * Arrête la lecture du média.
     * Nécessite un <code>play()</code> pour redémarrer.
     *
     * @since version 0.9.0
     * @deprecated VLC 1.1.0
     */
    @Deprecated
    private void stopV09x() {
        libvlc_exception_t exception = new libvlc_exception_t();
        libvlc.libvlc_media_player_stop(media_player, exception);
    }

    /**
     * Donne la durée du média en ms.
     * Nécessite que le média soit en lecture.
     *
     * @return la durée du média en ms.
     * @version 1.1.0
     */
    public long getMediaLength() {
        if (OLD_VERSION) {
            return getMediaLengthV09x();
        }
        libvlc_media_t media = libvlc.libvlc_media_player_get_media(media_player);
        libvlc.libvlc_media_parse(media);
        return libvlc.libvlc_media_get_duration(media);
    }

    /**
     * Donne la durée du média en ms.
     * Nécessite que le média soit en lecture.
     *
     * @return la durée du média en ms.
     * @since version 0.9.0
     * @deprecated VLC 1.1.0
     */
    @Deprecated
    private long getMediaLengthV09x() {
        libvlc_exception_t exception = new libvlc_exception_t();
        libvlc_media_t media = libvlc.libvlc_media_player_get_media(media_player, exception);
//        libvlc.libvlc_media_parse(media);
        return libvlc.libvlc_media_get_duration(media, exception);
    }

    /**
     * Donne la durée du média en ms.
     * Nécessite que le média soit en lecture.
     *
     * @return la durée du média en ms.
     * @version 1.1.0
     */
    public long getLength() {
        if (OLD_VERSION) {
            return getLengthV09x();
        }
        return libvlc.libvlc_media_player_get_length(media_player);
    }

    /**
     * Donne la durée du média en ms.
     * Nécessite que le média soit en lecture.
     *
     * @return la durée du média en ms.
     * @since version 0.9.0
     * @deprecated VLC 1.1.0
     */
    @Deprecated
    private long getLengthV09x() {
        libvlc_exception_t exception = new libvlc_exception_t();
        return libvlc.libvlc_media_player_get_length(media_player, exception);
    }

    /**
     * Donne le temps où l'on est (en ms).
     * Bug après <code>pause()</code>, utiliser <code>getPosition()</code>.
     *
     * @return le temps où l'on est (en ms).
     * @version 1.1.0
     */
    public long getTime() {
        if (OLD_VERSION) {
            return getTimeV09x();
        }
        return libvlc.libvlc_media_player_get_time(media_player);
    }

    /**
     * Donne le temps où l'on est (en ms).
     * Bug après <code>pause()</code>, utiliser <code>getPosition()</code>.
     *
     * @return le temps où l'on est (en ms).
     * @since version 0.9.0
     * @deprecated VLC 1.1.0
     */
    @Deprecated
    private long getTimeV09x() {
        libvlc_exception_t exception = new libvlc_exception_t();
        return libvlc.libvlc_media_player_get_time(media_player, exception);
    }

    /**
     * Déplace la lecture à l'endroit voulu (en ms).
     *
     * @param time l'endroit voulu (en ms).
     * @version 1.1.0
     */
    public synchronized void setTime(long time) {
        if (OLD_VERSION) {
            setTimeV09x(time);
            return;
        }
        libvlc.libvlc_media_player_set_time(media_player, time);
    }

    /**
     * Déplace la lecture à l'endroit voulu (en ms).
     *
     * @param time l'endroit voulu (en ms).
     * @since version 0.9.0
     * @deprecated VLC 1.1.0
     */
    @Deprecated
    private synchronized void setTimeV09x(long time) {
        if (OLD_VERSION) {
            setTimeV09x(time);
            return;
        }
        libvlc_exception_t exception = new libvlc_exception_t();
        libvlc.libvlc_media_player_set_time(media_player, time, exception);
    }

    /**
     * Donne la position dans le média en pourcentage (de 0 à 1).
     *
     * @return la position dans le média.
     * @version 1.1.0
     */
    public float getPosition() {
        if (OLD_VERSION) {
            return getPositionV09x();
        }
        return libvlc.libvlc_media_player_get_position(media_player);
    }

    /**
     * Donne la position dans le média en pourcentage (de 0 à 1).
     *
     * @return la position dans le média.
     * @since version 0.9.0
     * @deprecated VLC 1.1.0
     */
    @Deprecated
    private float getPositionV09x() {
        libvlc_exception_t exception = new libvlc_exception_t();
        return libvlc.libvlc_media_player_get_position(media_player,
                exception);
    }

    /**
     * Déplace la lecture à l'endroit voulu en pourcentage (de 0 à 1).
     *
     * @param position l'endroit voulu.
     * @version 1.1.0
     */
    public synchronized void setPosition(float position) {
        if (OLD_VERSION) {
            setPositionV09x(position);
            return;
        }
        libvlc.libvlc_media_player_set_position(media_player, position);
    }

    /**
     * Déplace la lecture à l'endroit voulu en pourcentage (de 0 à 1).
     *
     * @param position l'endroit voulu.
     * @since version 0.9.0
     * @deprecated VLC 1.1.0
     */
    @Deprecated
    private synchronized void setPositionV09x(float position) {
        libvlc_exception_t exception = new libvlc_exception_t();
        libvlc.libvlc_media_player_set_position(media_player, position, exception);
    }

    /**
     * Donne la valeur du niveau sonore en pourcentage (de 0 à 200).
     *
     * @return la valeur du niveau sonore.
     * @version 1.1.0
     */
    public int getVolume() {
        if (OLD_VERSION) {
            return getVolumeV09x();
        }
        return libvlc.libvlc_audio_get_volume(media_player);
    }

    /**
     * Donne la valeur du niveau sonore en pourcentage (de 0 à 200).
     *
     * @return la valeur du niveau sonore.
     * @since version 0.9.0
     * @deprecated VLC 1.1.0
     */
    @Deprecated
    private int getVolumeV09x() {
        libvlc_exception_t exception = new libvlc_exception_t();
        return libvlc.libvlc_audio_get_volume(libvlc_instance, exception);
    }

    /**
     * Change la valeur du niveau sonore en pourcentage (de 0 à 200).
     *
     * @param volume la valeur du niveau sonore entre 0 à 200.
     * @version 1.1.0
     */
    public synchronized void setVolume(int volume) {
        if (OLD_VERSION) {
            setVolumeV09x(volume);
            return;
        }
        libvlc.libvlc_audio_set_volume(media_player, volume);
    }

    /**
     * Change la valeur du niveau sonore en pourcentage (de 0 à 200).
     *
     * @param volume la valeur du niveau sonore entre 0 à 200.
     * @since version 0.9.0
     * @deprecated VLC 1.1.0
     */
    @Deprecated
    private synchronized void setVolumeV09x(int volume) {
        libvlc_exception_t exception = new libvlc_exception_t();
        libvlc.libvlc_audio_set_volume(libvlc_instance, volume, exception);
    }

    /**
     * Indique si le média à une représentation graphique.
     *
     * @return <code>true</code> si le média à une représentation graphique.
     * @version 1.1.0
     */
    public boolean hasVideoOutput() {
        if (OLD_VERSION) {
            return hasVideoOutputV09x();
        }
        return (libvlc.libvlc_media_player_has_vout(media_player) == 1);
    }

    /**
     * Indique si le média à une représentation graphique.
     *
     * @return <code>true</code> si le média à une représentation graphique.
     * @since version 0.9.0
     * @deprecated VLC 1.1.0
     */
    @Deprecated
    private boolean hasVideoOutputV09x() {
        libvlc_exception_t exception = new libvlc_exception_t();
        return (libvlc.libvlc_media_player_has_vout(media_player, exception) == 1);
    }

    /**
     * Modifie le mode plein écran.
     *
     * @param fullscreen l'état du plein écran.
     * @version 1.1.0
     */
    public void setFullScreen(boolean fullscreen) {
        if (isFullScreen() == fullscreen) {
            return;
        }

        if (OLD_VERSION) {
            setFullScreenV09x(fullscreen);
            return;
        }

        int i_fullscreen = fullscreen ? 1 : 0;
        libvlc.libvlc_set_fullscreen(media_player, i_fullscreen);
    }

    /**
     * Modifie le mode plein écran.
     *
     * @param fullscreen l'état du plein écran.
     * @since version 0.9.0
     * @deprecated VLC 1.1.0
     */
    @Deprecated
    private void setFullScreenV09x(boolean fullscreen) {
        int i_fullscreen = fullscreen ? 1 : 0;
        libvlc_exception_t exception = new libvlc_exception_t();
        libvlc.libvlc_set_fullscreen(media_player, i_fullscreen, exception);
    }

    /**
     * Indique si on est en plein écran ou non.
     *
     * @return le mode plein écran.
     * @version 1.1.0
     */
    public boolean isFullScreen() {
        if (OLD_VERSION) {
            return isFullScreenV09x();
        }
        return (libvlc.libvlc_get_fullscreen(media_player) == 1);
    }

    /**
     * Indique si on est en plein écran ou non.
     *
     * @return le mode plein écran.
     * @since version 0.9.0
     * @deprecated VLC 1.1.0
     */
    @Deprecated
    private boolean isFullScreenV09x() {
        libvlc_exception_t exception = new libvlc_exception_t();
        return (libvlc.libvlc_get_fullscreen(media_player, exception) == 1);
    }

    /**
     * Configure la sortie vidéo.
     * Le composant graphique doit être un heavyheight component et soit visible.
     *
     * @param component le composant graphique où sera la vidéo.
     * @version 1.1.0
     */
    public synchronized void setVideoOutput(Component component) {
        if (OLD_VERSION) {
            int first = version.charAt(0) - '0';
            if (first < 1) {
                setVideoOutputV09x(component);
            } else {
                setVideoOutputV10x(component);
            }

            return;
        }

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
     * Configure la sortie vidéo.
     * Le composant graphique doit être un heavyheight component et soit visible.
     *
     * @param component le composant graphique où sera la vidéo.
     * @version 1.0.0
     * @deprecated VLC 1.1.0
     */
    @Deprecated
    private synchronized void setVideoOutputV10x(Component component) {
        boolean linux = Platform.isLinux();
        boolean windows = Platform.isWindows();
        boolean mac = Platform.isMac();

        libvlc_exception_t exception = new libvlc_exception_t();

        if (linux) {
            long drawable = Native.getComponentID(component);
            libvlc.libvlc_media_player_set_xwindow(media_player, (int) drawable, exception);
        } else if (windows) {
            Pointer drawable = Native.getComponentPointer(component);
            libvlc.libvlc_media_player_set_hwnd(media_player, drawable, exception);
        } else if (mac) {
            Pointer drawable = Native.getComponentPointer(component);
            libvlc.libvlc_media_player_set_nsobject(media_player, drawable, exception);
        }
    }

    /**
     * Configure la sortie vidéo.
     *
     * @param component le composant graphique où sera la vidéo.
     * @since version 0.9.0
     * @deprecated VLC 1.0.0
     */
    @Deprecated
    private synchronized void setVideoOutputV09x(Component component) {
        long drawable = com.sun.jna.Native.getComponentID(component);
        libvlc_exception_t exception = new libvlc_exception_t();
        libvlc.libvlc_media_player_set_drawable(media_player, (int) drawable, exception);
    }

    /**
     * Indique le fichier de soustitrage à utiliser.
     *
     * @param fileName le nom du fichier de soustitrage.
     * @version 1.1.0
     */
    public void setVideoSubtitleFile(String fileName) {
        if (OLD_VERSION) {
            setVideoSubtitleFileV09x(fileName);
            return;
        }
        libvlc.libvlc_video_set_subtitle_file(media_player, fileName);
    }

    /**
     * Indique le fichier de soustitrage à utiliser.
     *
     * @param fileName le nom du fichier de soustitrage.
     * @since version 0.9.0
     * @deprecated VLC 1.1.0
     */
    @Deprecated
    private void setVideoSubtitleFileV09x(String fileName) {
        libvlc_exception_t exception = new libvlc_exception_t();
        libvlc.libvlc_video_set_subtitle_file(media_player, fileName, exception);
    }

    /**
     * Retourne l'état du lecteur.
     *
     * @return la valeur de l'état IDLE/CLOSE = 0, OPENING = 1, BUFFERING = 2,
     * PLAYING = 3, PAUSED = 4, STOPPING = 5, ENDED = 6, ERROR = 7
     * @version 1.1.0
     */
    public int getState() {
        if (OLD_VERSION) {
            int first = version.charAt(0) - '0';
            if (first < 1) {
                return getStateV09x();
            } else {
                getStateV10x();
            }
        }
        return libvlc.libvlc_media_player_get_state(media_player);
    }

    /**
     * Retourne l'état du lecteur.
     *
     * @return la valeur de l'état IDLE/CLOSE = 0, OPENING = 1, BUFFERING = 2,
     * PLAYING = 3, PAUSED = 4, STOPPING = 5, ENDED = 6, ERROR = 7
     * @version 1.0.0
     * @deprecated VLC 1.1.0
     */
    @Deprecated
    private int getStateV10x() {
        libvlc_exception_t exception = new libvlc_exception_t();
        return libvlc.libvlc_media_player_get_state(media_player, exception);
    }

    /**
     * Retourne l'état du lecteur.
     *
     * @return la valeur de l'état IDLE/CLOSE = 0, OPENING = 1, BUFFERING = 2,
     * PLAYING = 3, PAUSED = 4, STOPPING = 5, ENDED = 8, ERROR = 9
     * @since version 0.9.0
     * @deprecated VLC 1.0.0
     */
    @Deprecated
    private int getStateV09x() {
        libvlc_exception_t exception = new libvlc_exception_t();
        int state = libvlc.libvlc_media_player_get_state(media_player, exception);

        switch (state) {
            case 6: //vlc 0.9.x libvlc_Forward -> vlc 1.0.x unused
            case 7: //vlc 0.9.x libvlc_Backward -> vlc 1.0.x unused
                state += 2;
                break;
            case 8: //vlc 0.9.x libvlc_Ended -> vlc 1.0.x libvlc_Ended
            case 9: //vlc 0.9.x libvlc_Error -> vlc 1.0.x libvlc_Error
                state -= 2;
                break;
        }
        return state;
    }

    /**
     * Indique si le lecteur a fini.
     *
     * @return <code>true</code> si le lecteur a fini.
     * @since version 0.9.0
     */
    public boolean isEndReached() {
        return getState() == libvlc_state_t.libvlc_Ended.intValue();
    }

    /**
     * Retourne si le lecteur est en mode lecture.
     *
     * @return <code>true</code> si le lecteur est en lecture.
     * @since version 0.9.0
     */
    public boolean isPlaying() {
        return getState() == libvlc_state_t.libvlc_Playing.intValue();
    }

    /**
     * Retourne si une erreur à eu lieu.
     *
     * @return <code>true</code> si une erreur est survenue.
     * @since version 0.9.0
     */
    public boolean isErrorOccured() {
        return getState() == libvlc_state_t.libvlc_Error.intValue();
    }

    /**
     * Ajoute un listener au lecteur.
     *
     * @param listener à ajouter.
     * @danger ne pas faire mediaPlayer.pause() sur l'évènement.
     * @version 1.1.0
     */
    public synchronized void addMediaPlayerListener(MediaPlayerListener listener) {
        if (OLD_VERSION) {
            addMediaPlayerListenerV09x(listener);
            return;
        }
        MediaPlayerCallback callback = new MediaPlayerCallback(this, listener);

        for (libvlc_event_e event : EnumSet.range(EVENT_FIRST, EVENT_END)) {
            libvlc.libvlc_event_attach(eventManager, event.intValue(), callback, null);
        }
        callbacks.add(callback);
    }

    /**
     * Ajoute un listener au lecteur.
     *
     * @param listener à ajouter.
     * @danger ne pas faire mediaPlayer.pause() sur l'évènement
     * @since version 0.9.0
     * @deprecated VLC 1.1.0
     */
    @Deprecated
    private synchronized void addMediaPlayerListenerV09x(MediaPlayerListener listener) {
        MediaPlayerCallback callback = new MediaPlayerCallback(this, listener);
        int eventOffset = callback.getEventOffset();
        libvlc_exception_t exception = new libvlc_exception_t();

        for (libvlc_event_e event : EnumSet.range(EVENT_FIRST, EVENT_END)) {
            libvlc.libvlc_event_attach(eventManager, event.intValue() - eventOffset,
                    callback, null, exception);
        }
        callbacks.add(callback);
    }

    /**
     * Enlève un listener au lecteur.
     *
     * @param listener à ajouter.
     * @danger ne pas faire mediaPlayer.pause() sur l'évènement.
     * @since version 0.9.0
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
     * @version 1.1.0
     */
    private synchronized void removeCallback(MediaPlayerCallback callback) {
        if (OLD_VERSION) {
            removeCallbackV09x(callback);
            return;
        }
        for (libvlc_event_e event : EnumSet.range(EVENT_FIRST, EVENT_END)) {
            libvlc.libvlc_event_detach(eventManager, event.intValue(), callback, null);
        }
    }

    /**
     * Détache le callback attaché au player VLC.
     *
     * @param callback le callback à enlever.
     * @since version 0.9.0
     * @deprecated VLC 1.1.0
     */
    @Deprecated
    private synchronized void removeCallbackV09x(MediaPlayerCallback callback) {
        int eventOffset = callback.getEventOffset();
        libvlc_exception_t exception = new libvlc_exception_t();
        for (libvlc_event_e event : EnumSet.range(EVENT_FIRST, EVENT_END)) {
            libvlc.libvlc_event_detach(eventManager, event.intValue() - eventOffset,
                    callback, null, exception);
        }
    }

    /**
     * Libère la mémoire utilisée par le lecteur et la librairie vlc.
     *
     * @since version 0.9.0
     */
    public synchronized void release() {
        for (MediaPlayerCallback callback : callbacks) {
            removeCallback(callback);
        }
        callbacks.clear();

        //libvlc.libvlc_media_player_release(media_player);
        libvlc.libvlc_release(libvlc_instance);
    }

    /**
     * Retourne le chemin du répertoire de VLC sous Windows.
     *
     * @return le chemin du répertoire de VLC.
     * @since version 0.9.0
     */
    public static String getVLCpathOnWindows() {
        String command = "reg query HKLM\\SOFTWARE\\VideoLAN\\VLC /v InstallDir";
        StringBuilder result = new StringBuilder(1024);
        StringBuilder error = new StringBuilder(1024);

        Utilities.executeCommand("reg query", command, result, error);

        String[] splitResult = result.toString().split("REG_SZ");

        if (splitResult.length == 1) {
            command = "reg query HKLM\\SOFTWARE\\Wow6432Node\\VideoLAN\\VLC /v InstallDir";
            result = new StringBuilder(1024);
            error = new StringBuilder(1024);

            Utilities.executeCommand("reg query", command, result, error);

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
     * @since version 0.9.0
     */
    private static String getVLCpathOnMac() {
        return "/Applications/VLC.app/Contents/MacOS";
    }
}
