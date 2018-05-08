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
package thot.video.vlc;

import com.sun.jna.Library;
import com.sun.jna.Pointer;
import thot.video.vlc.internal.libvlc_callback_t;
import thot.video.vlc.internal.libvlc_event_manager_t;
import thot.video.vlc.internal.libvlc_instance_t;
import thot.video.vlc.internal.libvlc_media_player_t;
import thot.video.vlc.internal.libvlc_media_t;
import thot.video.vlc.internal.libvlc_state_t;

/**
 * Wrapper de la librairie VLC.
 *
 * @author Fabrice Alleau
 * @version 1.8.4 (VLC 1.1.0 à 3.0.x et compatible JET)
 */
public interface LibVLC extends Library {

    /**
     * Create and initialize a libvlc instance.
     *
     * @param argc the number of arguments
     * @param argv command-line-type arguments
     *
     * @return the libvlc instance or NULL in case of error
     */
    libvlc_instance_t libvlc_new(int argc, String[] argv);

    /**
     * Decrement the reference count of a libvlc instance, and destroy it if it reaches zero.
     *
     * @param p_instance the instance to destroy
     */
    void libvlc_release(libvlc_instance_t p_instance);

    /**
     * Retrieve libvlc version.
     * <p>
     * Example: "1.1.0-git The Luggage" VLC_PUBLIC_API const char * libvlc_get_version(void);
     *
     * @return a string containing the libvlc version
     */
    String libvlc_get_version();

    /**
     * Register for an event notification.
     *
     * @param p_event_manager the event manager to which you want to attach to. Generally it is obtained by
     *         vlc_my_object_event_manager() where my_object is the object you want to listen to.
     * @param i_event_type the desired event to which we want to listen
     * @param f_callback the function to call when i_event_type occurs
     * @param user_data user provided data to carry with the event
     *
     * @return 0 on success, ENOMEM on error
     */
    int libvlc_event_attach(libvlc_event_manager_t p_event_manager, int i_event_type, libvlc_callback_t f_callback,
            Pointer user_data);

    /**
     * Unregister an event notification.
     *
     * @param p_event_manager the event manager
     * @param i_event_type the desired event to which we want to unregister
     * @param f_callback the function to call when i_event_type occurs
     * @param p_user_data user provided data to carry with the event
     */
    void libvlc_event_detach(libvlc_event_manager_t p_event_manager, int i_event_type, libvlc_callback_t f_callback,
            Pointer p_user_data);

    /**
     * Create a media with a certain file path.
     *
     * @param p_instance the instance
     * @param path local filesystem path
     *
     * @return the newly created media or NULL on error
     */
    libvlc_media_t libvlc_media_new_path(libvlc_instance_t p_instance, String path);

    /**
     * Decrement the reference count of a media descriptor object. If the reference count is 0, then
     * libvlc_media_release() will release the media descriptor object. It will send out an libvlc_MediaFreed event to
     * all listeners. If the media descriptor object has been released it should not be used again.
     *
     * @param p_md the media descriptor
     */
    void libvlc_media_release(libvlc_media_t p_md);

    /**
     * Get duration (in ms) of media descriptor object item.
     *
     * @param p_md media descriptor object
     *
     * @return duration of media item or -1 on error
     */
    long libvlc_media_get_duration(libvlc_media_t p_md);

    /**
     * Parse a media.
     * <p>
     * This fetches (local) meta data and tracks information. The method is synchronous.
     *
     * @param media media descriptor object
     */
    void libvlc_media_parse(libvlc_media_t media);

    /**
     * Create an empty Media DirectAudioPlayer object
     *
     * @param p_libvlc_instance the libvlc instance in which the Media DirectAudioPlayer should be created.
     *
     * @return a new media player object, or NULL on error.
     */
    libvlc_media_player_t libvlc_media_player_new(libvlc_instance_t p_libvlc_instance);

    /**
     * Set the media that will be used by the media_player. If any, previous md will be released.
     *
     * @param p_mi the Media DirectAudioPlayer
     * @param p_md the Media. Afterwards the p_md can be safely destroyed.
     */
    void libvlc_media_player_set_media(libvlc_media_player_t p_mi, libvlc_media_t p_md);

    /**
     * Get the media used by the media_player.
     *
     * @param p_mi the Media DirectAudioPlayer
     *
     * @return the media associated with p_mi, or NULL if no media is associated
     */
    libvlc_media_t libvlc_media_player_get_media(libvlc_media_player_t p_mi);

    /**
     * Get the Event Manager from which the media player send event.
     *
     * @param p_mi the Media DirectAudioPlayer
     *
     * @return the event manager associated with p_mi
     */
    libvlc_event_manager_t libvlc_media_player_event_manager(libvlc_media_player_t p_mi);

    /**
     * Play
     *
     * @param p_mi the Media DirectAudioPlayer
     *
     * @return 0 if playback started (and was already started), or -1 on error.
     */
    int libvlc_media_player_play(libvlc_media_player_t p_mi);

    /**
     * Toggle pause (no effect if there is no media)
     *
     * @param p_mi the Media DirectAudioPlayer
     */
    void libvlc_media_player_pause(libvlc_media_player_t p_mi);

    /**
     * Stop (no effect if there is no media)
     *
     * @param p_mi the Media DirectAudioPlayer
     */
    void libvlc_media_player_stop(libvlc_media_player_t p_mi);

    /**
     * Set the NSView handler where the media player should render its video output.
     * <p>
     * Use the vout called "macosx".
     * <p>
     * The drawable is an NSObject that follow the VLCOpenGLVideoViewEmbedding protocol:
     *
     * @param p_mi the Media DirectAudioPlayer
     * @param drawable the drawable that is either an NSView or an object following
     */
    void libvlc_media_player_set_nsobject(libvlc_media_player_t p_mi, Pointer drawable);

    /**
     * Set an X Window System drawable where the media player should render its video output. If LibVLC was built
     * without X11 output support, then this has no effects.
     * <p>
     * The specified identifier must correspond to an existing Input/Output class X11 window. Pixmaps are <b>not</b>
     * supported. The caller shall ensure that the X11 server is the same as the one the VLC instance has been
     * configured with. If XVideo is <b>not</b> used, it is assumed that the drawable has the following properties in
     * common with the default X11 screen: depth, scan line pad, black pixel. This is a bug.
     *
     * @param p_mi the Media DirectAudioPlayer
     * @param drawable the ID of the X window
     */
    void libvlc_media_player_set_xwindow(libvlc_media_player_t p_mi, int drawable);

    /**
     * Set a Win32/Win64 API window handle (HWND) where the media player should render its video output. If LibVLC was
     * built without Win32/Win64 API output support, then this has no effects.
     *
     * @param p_mi the Media DirectAudioPlayer
     * @param drawable windows handle of the drawable
     */
    void libvlc_media_player_set_hwnd(libvlc_media_player_t p_mi, Pointer drawable);

    /* \bug This might go away ... to be replaced by a broader system */

    /**
     * Get the current movie length (in ms).
     *
     * @param p_mi the Media DirectAudioPlayer
     *
     * @return the movie length (in ms), or -1 if there is no media.
     */
    long libvlc_media_player_get_length(libvlc_media_player_t p_mi);

    /**
     * Get the current movie time (in ms).
     *
     * @param p_mi the Media DirectAudioPlayer
     *
     * @return the movie time (in ms), or -1 if there is no media.
     */
    long libvlc_media_player_get_time(libvlc_media_player_t p_mi);

    /**
     * Set the movie time (in ms). This has no effect if no media is being played. Not all formats and protocols support
     * this.
     *
     * @param p_mi the Media DirectAudioPlayer
     * @param i_time the movie time (in ms).
     */
    void libvlc_media_player_set_time(libvlc_media_player_t p_mi, long i_time);

    /**
     * Get movie position.
     *
     * @param p_mi the Media DirectAudioPlayer
     *
     * @return movie position, or -1. in case of error
     */
    float libvlc_media_player_get_position(libvlc_media_player_t p_mi);

    /**
     * Set movie position. This has no effect if playback is not enabled. This might not work depending on the
     * underlying input format and protocol.
     *
     * @param p_mi the Media DirectAudioPlayer
     * @param f_pos the position
     */
    void libvlc_media_player_set_position(libvlc_media_player_t p_mi, float f_pos);

    /**
     * Get current movie state
     *
     * @param p_mi the Media DirectAudioPlayer
     *
     * @return the current state of the media player (playing, paused, ...)
     *
     * @see libvlc_state_t
     */
    int libvlc_media_player_get_state(libvlc_media_player_t p_mi);

    /* end bug */

    /**
     * Enable or disable fullscreen.
     *
     * @param p_mi the media player
     * @param b_fullscreen boolean for fullscreen status
     *         <p>
     *         warning With most window managers, only a top-level windows can be in full-screen mode. Hence, this
     *         function will not operate properly if libvlc_media_player_set_xid() was used to embed the video in a
     *         non-top-level window. In that case, the embedding window must be reparented to the root window
     *         <b>before</b> fullscreen mode is enabled. You will want to reparent it back to its normal parent when
     *         disabling fullscreen.
     */
    void libvlc_set_fullscreen(libvlc_media_player_t p_mi, int b_fullscreen);

    /**
     * Get current fullscreen status.
     *
     * @param p_mi the media player
     *
     * @return the fullscreen status (boolean)
     */
    int libvlc_get_fullscreen(libvlc_media_player_t p_mi);

    /**
     * Set new video subtitle file.
     *
     * @param p_mi the media player
     * @param psz_subtitle new video subtitle file
     *
     * @return the success status (boolean)
     */
    int libvlc_video_set_subtitle_file(libvlc_media_player_t p_mi, String psz_subtitle);

    /**
     * Get current audio level.
     *
     * @param p_mi media player
     *
     * @return the audio level (int)
     */
    int libvlc_audio_get_volume(libvlc_media_player_t p_mi);

    /**
     * Set current audio level.
     *
     * @param p_mi media player
     * @param i_volume the volume (int)
     *
     * @return 0 if the volume was set, -1 if it was out of range
     */
    int libvlc_audio_set_volume(libvlc_media_player_t p_mi, int i_volume);
}
