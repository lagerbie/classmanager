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

import com.sun.jna.Library;
import com.sun.jna.Pointer;

import thot.vlc.internal.libvlc_callback_t;
import thot.vlc.internal.libvlc_event_manager_t;
import thot.vlc.internal.libvlc_exception_t;
import thot.vlc.internal.libvlc_instance_t;
import thot.vlc.internal.libvlc_media_player_t;
import thot.vlc.internal.libvlc_media_t;
import thot.vlc.internal.libvlc_state_t;

/**
 * Wrapper de la librairie VLC.
 *
 * @author Fabrice Alleau
 * @version 2.1.4 (VLC 0.9.x à 2.1.x et compatible JET)
 */
public interface LibVLC extends Library {

    /**
     * Create and initialize a libvlc instance.
     *
     * VLC_PUBLIC_API libvlc_instance_t *libvlc_new(
     * int argc , const char *const *argv);
     *
     * @param argc the number of arguments
     * @param argv command-line-type arguments
     * @return the libvlc instance or NULL in case of error
     * @since version 1.1.0
     */
    libvlc_instance_t libvlc_new(int argc, String[] argv);
    /**
     * Create and initialize a libvlc instance.
     *
     * @param argc the number of arguments
     * @param argv command-line-type arguments. argv[0] must be the path of the
     * calling program.
     * @param p_e an initialized exception pointer
     * @return the libvlc instance
     * @since version 0.9.0
     * @deprecated VLC 1.1.0
     */
    @Deprecated
    libvlc_instance_t libvlc_new(int argc, String[] argv, libvlc_exception_t p_e);

    /**
     * Decrement the reference count of a libvlc instance, and destroy it if it
     * reaches zero.
     *
     * VLC_PUBLIC_API void libvlc_release( libvlc_instance_t *p_instance );
     *
     * @param p_instance the instance to destroy
     * @since version 0.9.0
     */
    void libvlc_release(libvlc_instance_t p_instance);

    /**
     * Retrieve libvlc version.
     *
     * Example: "1.1.0-git The Luggage"
     * VLC_PUBLIC_API const char * libvlc_get_version(void);
     *
     * @return a string containing the libvlc version
     * @since version 0.9.0
     */
    String libvlc_get_version();

    /**
     * Register for an event notification.
     *
     * VLC_PUBLIC_API int libvlc_event_attach(libvlc_event_manager_t *p_event_manager,
     * libvlc_event_type_t i_event_type, libvlc_callback_t f_callback, void *user_data);
     *
     * @param p_event_manager the event manager to which you want to attach to.
     * Generally it is obtained by vlc_my_object_event_manager() where my_object
     * is the object you want to listen to.
     * @param i_event_type the desired event to which we want to listen
     * @param f_callback the function to call when i_event_type occurs
     * @param user_data user provided data to carry with the event
     * @return 0 on success, ENOMEM on error
     * @since version 1.1.0
     */
    int libvlc_event_attach(libvlc_event_manager_t p_event_manager,
            int i_event_type, libvlc_callback_t f_callback, Pointer user_data);
    /**
     * Register for an event notification.
     *
     * @param p_event_manager the event manager to which you want to attach to.
     * Generally it is obtained by vlc_my_object_event_manager() where my_object
     * is the object you want to listen to.
     * @param i_event_type the desired event to which we want to listen
     * @param f_callback the function to call when i_event_type occurs
     * @param user_data user provided data to carry with the event
     * @param p_e an initialized exception pointer
     * @since version 0.9.0
     * @deprecated VLC 1.1.0
     */
    @Deprecated
    void libvlc_event_attach(libvlc_event_manager_t p_event_manager,
            int i_event_type, libvlc_callback_t f_callback, Pointer user_data,
            libvlc_exception_t p_e);

    /**
     * Unregister an event notification.
     *
     * VLC_PUBLIC_API void libvlc_event_detach(libvlc_event_manager_t *p_event_manager,
     * libvlc_event_type_t i_event_type, libvlc_callback_t f_callback, void *p_user_data);
     *
     * @param p_event_manager the event manager
     * @param i_event_type the desired event to which we want to unregister
     * @param f_callback the function to call when i_event_type occurs
     * @param p_user_data user provided data to carry with the event
     * @since version 1.1.0
     */
    void libvlc_event_detach(libvlc_event_manager_t p_event_manager,
            int i_event_type, libvlc_callback_t f_callback, Pointer p_user_data);
    /**
     * Unregister an event notification.
     *
     * @param p_event_manager the event manager
     * @param i_event_type the desired event to which we want to unregister
     * @param f_callback the function to call when i_event_type occurs
     * @param p_user_data user provided data to carry with the event
     * @param p_e an initialized exception pointer
     * @since version 0.9.0
     * @deprecated VLC 1.1.0
     */
    @Deprecated
    void libvlc_event_detach(libvlc_event_manager_t p_event_manager,
            int i_event_type, libvlc_callback_t f_callback, Pointer p_user_data,
            libvlc_exception_t p_e);

    /**
     * Create a media with a certain given media resource location.
     *
     * VLC_PUBLIC_API libvlc_media_t *libvlc_media_new_location(
     * libvlc_instance_t *p_instance, const char * psz_mrl );
     *
     * @see libvlc_media_release
     * @param p_instance the instance
     * @param psz_mrl the MRL to read
     * @return the newly created media or NULL on error
     * @since version 1.1.0
     */
    libvlc_media_t libvlc_media_new_location(libvlc_instance_t p_instance, String psz_mrl);

    /**
     * Create a media with a certain file path.
     *
     * VLC_PUBLIC_API libvlc_media_t *libvlc_media_new_path(
     * libvlc_instance_t *p_instance, const char *path );
     *
     * @see libvlc_media_release
     * @param p_instance the instance
     * @param path local filesystem path
     * @return the newly created media or NULL on error
     * @since version 1.1.0
     */
    libvlc_media_t libvlc_media_new_path(libvlc_instance_t p_instance, String path);
    /**
     * Create a media with the given MRL.
     *
     * @param p_instance the instance
     * @param psz_mrl the MRL to read
     * @param p_e an initialized exception pointer
     * @return the newly created media
     * @since version 0.9.0
     * @deprecated VLC 1.1.0
     */
    @Deprecated
    libvlc_media_t libvlc_media_new(libvlc_instance_t p_instance, String psz_mrl,
            libvlc_exception_t p_e);

    /**
     * Decrement the reference count of a media descriptor object. If the
     * reference count is 0, then libvlc_media_release() will release the media
     * descriptor object. It will send out an libvlc_MediaFreed event to all
     * listeners. If the media descriptor object has been released it should not
     * be used again.
     *
     * VLC_PUBLIC_API void libvlc_media_release( libvlc_media_t *p_md );
     *
     * @param p_md the media descriptor
     * @since version 0.9.0
     */
    void libvlc_media_release(libvlc_media_t p_md);

    /**
     * Get duration (in ms) of media descriptor object item.
     *
     * VLC_PUBLIC_API libvlc_time_t libvlc_media_get_duration(libvlc_media_t * p_md);
     *
     * @param p_md media descriptor object
     * @return duration of media item or -1 on error
     * @since version 1.1.0
     */
    long libvlc_media_get_duration(libvlc_media_t p_md);
    /**
     * Get duration of media descriptor object item.
     *
     * @param p_md media descriptor object
     * @param p_e an initialized exception object
     * @return duration of media item
     * @since version 0.9.0
     * @deprecated VLC 1.1.0
     */
    @Deprecated
    long libvlc_media_get_duration(libvlc_media_t p_md, libvlc_exception_t p_e);

    /**
     * Parse a media.
     *
     * This fetches (local) meta data and tracks information. The method is
     * synchronous.
     *
     * VLC_PUBLIC_API void libvlc_media_parse(libvlc_media_t *media);
     *
     * @see libvlc_media_parse_async
     * @see libvlc_media_get_meta
     * @see libvlc_media_get_tracks_info
     *
     * @param media media descriptor object
     * @since version 1.1.0
     */
    void libvlc_media_parse(libvlc_media_t media);

    /**
     * Create an empty Media AudioPlayer object
     *
     * VLC_PUBLIC_API libvlc_media_player_t * libvlc_media_player_new(
     * libvlc_instance_t *p_libvlc_instance );
     *
     * @param p_libvlc_instance the libvlc instance in which the Media AudioPlayer
     * should be created.
     * @return a new media player object, or NULL on error.
     * @since version 1.1.0
     */
    libvlc_media_player_t libvlc_media_player_new(libvlc_instance_t p_libvlc_instance);
    /**
     * Create an empty Media AudioPlayer object
     *
     * @param p_libvlc_instance the libvlc instance in which the Media AudioPlayer
     * should be created.
     * @param p_e an initialized exception pointer
     * @return
     * @since version 0.9.0
     * @deprecated VLC 1.1.0
     */
    @Deprecated
    libvlc_media_player_t libvlc_media_player_new(libvlc_instance_t p_libvlc_instance,
            libvlc_exception_t p_e);

    /**
     * Release a media_player after use Decrement the reference count of a media
     * player object. If the reference count is 0, then
     * libvlc_media_player_release() will release the media player object. If
     * the media player object has been released, then it should not be used
     * again.
     *
     * VLC_PUBLIC_API void libvlc_media_player_release(libvlc_media_player_t *p_mi);
     *
     * @param p_mi the Media AudioPlayer to free
     * @since version 0.9.0
     */
    void libvlc_media_player_release(libvlc_media_player_t p_mi);

    /**
     * Set the media that will be used by the media_player. If any, previous md
     * will be released.
     *
     * VLC_PUBLIC_API void libvlc_media_player_set_media(
     * libvlc_media_player_t *p_mi, libvlc_media_t *p_md );
     *
     * @param p_mi the Media AudioPlayer
     * @param p_md the Media. Afterwards the p_md can be safely destroyed.
     * @since version 1.1.0
     */
    void libvlc_media_player_set_media(libvlc_media_player_t p_mi, libvlc_media_t p_md);
    /**
     * Set the media that will be used by the media_player. If any, previous md
     * will be released.
     *
     * @param p_mi the Media AudioPlayer
     * @param p_md the Media. Afterwards the p_md can be safely destroyed.
     * @param p_e an initialized exception pointer
     * @since version 0.9.0
     * @deprecated VLC 1.1.0
     */
    @Deprecated
    void libvlc_media_player_set_media(libvlc_media_player_t p_mi, libvlc_media_t p_md,
            libvlc_exception_t p_e);

    /**
     * Get the media used by the media_player.
     *
     * VLC_PUBLIC_API libvlc_media_t * libvlc_media_player_get_media(
     * libvlc_media_player_t *p_mi );
     *
     * @param p_mi the Media AudioPlayer
     * @return the media associated with p_mi, or NULL if no media is associated
     * @since version 1.1.0
     */
    libvlc_media_t libvlc_media_player_get_media(libvlc_media_player_t p_mi);
    /**
     * Get the media used by the media_player.
     *
     * @param p_mi the Media AudioPlayer
     * @param p_e an initialized exception pointer
     * @return the media associated with p_mi, or NULL if no media is associated
     * @since version 0.9.0
     * @deprecated VLC 1.1.0
     */
    @Deprecated
    libvlc_media_t libvlc_media_player_get_media(libvlc_media_player_t p_mi,
            libvlc_exception_t p_e);

    /**
     * Get the Event Manager from which the media player send event.
     *
     * VLC_PUBLIC_API libvlc_event_manager_t * libvlc_media_player_event_manager(
     * libvlc_media_player_t *p_mi );
     *
     * @param p_mi the Media AudioPlayer
     * @return the event manager associated with p_mi
     * @since version 1.1.0
     */
    libvlc_event_manager_t libvlc_media_player_event_manager(libvlc_media_player_t p_mi);
    /**
     * Get the Event Manager from which the media player send event.
     *
     * @param p_mi the Media AudioPlayer
     * @param p_e an initialized exception pointer
     * @return the event manager associated with p_mi
     * @since version 0.9.0
     * @deprecated VLC 1.1.0
     */
    @Deprecated
    libvlc_event_manager_t libvlc_media_player_event_manager(libvlc_media_player_t p_mi,
            libvlc_exception_t p_e);

    /**
     * Play
     *
     * VLC_PUBLIC_API int libvlc_media_player_play(libvlc_media_player_t *p_mi);
     *
     * @param p_mi the Media AudioPlayer
     * @return 0 if playback started (and was already started), or -1 on error.
     * @since version 1.1.0
     */
    int libvlc_media_player_play(libvlc_media_player_t p_mi);
    /**
     * Play
     *
     * @param p_mi the Media AudioPlayer
     * @param p_e an initialized exception pointer
     * @since version 0.9.0
     * @deprecated VLC 1.1.0
     */
    @Deprecated
    void libvlc_media_player_play(libvlc_media_player_t p_mi, libvlc_exception_t p_e);

    /**
     * Toggle pause (no effect if there is no media)
     *
     * VLC_PUBLIC_API void libvlc_media_player_pause(libvlc_media_player_t *p_mi);
     *
     * @param p_mi the Media AudioPlayer
     * @since version 1.1.0
     */
    void libvlc_media_player_pause(libvlc_media_player_t p_mi);
    /**
     * Pause
     *
     * @param p_mi the Media AudioPlayer
     * @param p_e an initialized exception pointer
     * @since version 0.9.0
     * @deprecated VLC 1.1.0
     */
    @Deprecated
    void libvlc_media_player_pause(libvlc_media_player_t p_mi, libvlc_exception_t p_e);

    /**
     * Stop (no effect if there is no media)
     *
     * VLC_PUBLIC_API void libvlc_media_player_stop(libvlc_media_player_t *p_mi);
     *
     * @param p_mi the Media AudioPlayer
     * @since version 1.1.0
     */
    void libvlc_media_player_stop(libvlc_media_player_t p_mi);
    /**
     * Stop
     *
     * @param p_mi the Media AudioPlayer
     * @param p_e an initialized exception pointer
     * @since version 0.9.0
     * @deprecated VLC 1.1.0
     */
    @Deprecated
    void libvlc_media_player_stop(libvlc_media_player_t p_mi, libvlc_exception_t p_e);

    /**
     * Set the NSView handler where the media player should render its video
     * output.
     *
     * Use the vout called "macosx".
     *
     * The drawable is an NSObject that follow the VLCOpenGLVideoViewEmbedding
     * protocol:
     *
     * @begincode
     * \@protocol VLCOpenGLVideoViewEmbedding <NSObject>
     * - (void)addVoutSubview:(NSView *)view;
     * - (void)removeVoutSubview:(NSView *)view;
     * \@end
     * @endcode
     *
     * Or it can be an NSView object.
     *
     * If you want to use it along with Qt4 see the QMacCocoaViewContainer. Then
     * the following code should work:
     * @begincode
     * {
     *     NSView *video = [[NSView alloc] init];
     *     QMacCocoaViewContainer *container = new QMacCocoaViewContainer(video, parent);
     *     libvlc_media_player_set_nsobject(mp, video);
     *     [video release];
     * }
     * @endcode
     *
     * You can find a live example in VLCVideoView in VLCKit.framework.
     *
     * VLC_PUBLIC_API void libvlc_media_player_set_nsobject(
     * libvlc_media_player_t *p_mi, void * drawable );
     *
     * @param p_mi the Media AudioPlayer
     * @param drawable the drawable that is either an NSView or an object
     * following
     * @since version 1.1.0
     */
    void libvlc_media_player_set_nsobject(libvlc_media_player_t p_mi, Pointer drawable);
    /**
     * Set the agl handler where the media player should render its video
     * output.
     *
     * @param p_mi the Media AudioPlayer
     * @param drawable the agl handler
     * @param p_e an initialized exception pointer
     * @since version 1.0.0
     * @deprecated VLC 1.1.0
     */
    @Deprecated
    void libvlc_media_player_set_nsobject(libvlc_media_player_t p_mi, Pointer drawable,
            libvlc_exception_t p_e);

    /**
     * Get the NSView handler previously set with
     * libvlc_media_player_set_nsobject().
     *
     * VLC_PUBLIC_API void * libvlc_media_player_get_nsobject(libvlc_media_player_t *p_mi);
     *
     * @param p_mi the Media AudioPlayer
     * @return the NSView handler or 0 if none where set
     * @since version 1.0.0
     */
    Pointer libvlc_media_player_get_nsobject(libvlc_media_player_t p_mi);

    /**
     * Set the agl handler where the media player should render its video
     * output.
     *
     * VLC_PUBLIC_API void libvlc_media_player_set_agl(
     * libvlc_media_player_t *p_mi, uint32_t drawable );
     *
     * @param p_mi the Media AudioPlayer
     * @param drawable the agl handler
     * @since version 1.1.0
     */
    void libvlc_media_player_set_agl(libvlc_media_player_t p_mi, int drawable);
    /**
     * Set the agl handler where the media player should render its video
     * output.
     *
     * @param p_mi the Media AudioPlayer
     * @param drawable the agl handler
     * @param p_e an initialized exception pointer
     * @since version 1.0.0
     * @deprecated VLC 1.1.0
     */
    @Deprecated
    void libvlc_media_player_set_agl(libvlc_media_player_t p_mi, int drawable,
            libvlc_exception_t p_e);

    /**
     * Get the agl handler previously set with libvlc_media_player_set_agl().
     *
     * VLC_PUBLIC_API uint32_t libvlc_media_player_get_agl(libvlc_media_player_t *p_mi);
     *
     * @param p_mi the Media AudioPlayer
     * @return the agl handler or 0 if none where set
     * @since version 1.0.0
     */
    int libvlc_media_player_get_agl(libvlc_media_player_t p_mi);

    /**
     * Set an X Window System drawable where the media player should render its
     * video output. If LibVLC was built without X11 output support, then this
     * has no effects.
     *
     * The specified identifier must correspond to an existing Input/Output
     * class X11 window. Pixmaps are <b>not</b> supported. The caller shall
     * ensure that the X11 server is the same as the one the VLC instance has
     * been configured with. If XVideo is <b>not</b> used, it is assumed that
     * the drawable has the following properties in common with the default X11
     * screen: depth, scan line pad, black pixel. This is a bug.
     *
     * VLC_PUBLIC_API void libvlc_media_player_set_xwindow (
     * libvlc_media_player_t *p_mi, uint32_t drawable );
     *
     * @param p_mi the Media AudioPlayer
     * @param drawable the ID of the X window
     * @since version 1.1.0
     */
    void libvlc_media_player_set_xwindow(libvlc_media_player_t p_mi, int drawable);
    /**
     * Set an X Window System drawable where the media player should render its
     * video output. If LibVLC was built without X11 output support, then this
     * has no effects.
     *
     * The specified identifier must correspond to an existing Input/Output
     * class X11 window. Pixmaps are <b>not</b> supported. The caller shall
     * ensure that the X11 server is the same as the one the VLC instance has
     * been configured with. If XVideo is <b>not</b> used, it is assumed that
     * the drawable has the following properties in common with the default X11
     * screen: depth, scan line pad, black pixel. This is a bug.
     *
     * @param p_mi the Media AudioPlayer
     * @param drawable the ID of the X window
     * @param p_e an initialized exception pointer
     * @since version 1.0.0
     * @deprecated VLC 1.1.0
     */
    @Deprecated
    void libvlc_media_player_set_xwindow(libvlc_media_player_t p_mi, int drawable,
            libvlc_exception_t p_e);

    /**
     * Get the X Window System window identifier previously set with
     * libvlc_media_player_set_xwindow(). Note that this will return the
     * identifier even if VLC is not currently using it (for instance if it is
     * playing an audio-only input).
     *
     * VLC_PUBLIC_API uint32_t libvlc_media_player_get_xwindow(libvlc_media_player_t *p_mi);
     *
     * @param p_mi the Media AudioPlayer
     * @return an X window ID, or 0 if none where set.
     * @since version 1.0.0
     */
    int libvlc_media_player_get_xwindow(libvlc_media_player_t p_mi);

    /**
     * Set a Win32/Win64 API window handle (HWND) where the media player should
     * render its video output. If LibVLC was built without Win32/Win64 API
     * output support, then this has no effects.
     *
     * VLC_PUBLIC_API void libvlc_media_player_set_hwnd (
     * libvlc_media_player_t *p_mi, void *drawable );
     *
     * @param p_mi the Media AudioPlayer
     * @param drawable windows handle of the drawable
     * @since version 1.1.0
     */
    void libvlc_media_player_set_hwnd(libvlc_media_player_t p_mi, Pointer drawable);
    /**
     * Set a Win32/Win64 API window handle (HWND) where the media player should
     * render its video output. If LibVLC was built without Win32/Win64 API
     * output support, then this has no effects.
     *
     * @param p_mi the Media AudioPlayer
     * @param drawable windows handle of the drawable
     * @param p_e an initialized exception pointer
     * @since version 1.0.0
     * @deprecated VLC 1.1.0
     */
    @Deprecated
    void libvlc_media_player_set_hwnd(libvlc_media_player_t p_mi, Pointer drawable,
            libvlc_exception_t p_e);

    /**
     * Get the Windows API window handle (HWND) previously set with
     * libvlc_media_player_set_hwnd(). The handle will be returned even if
     * LibVLC is not currently outputting any video to it.
     *
     * VLC_PUBLIC_API void *libvlc_media_player_get_hwnd(libvlc_media_player_t *p_mi);
     *
     * @param p_mi the Media AudioPlayer
     * @return a window handle or NULL if there are none.
     * @since version 1.0.0
     */
    Pointer libvlc_media_player_get_hwnd(libvlc_media_player_t p_mi);

    /**
     * Set the drawable where the media player should render its video output
     *
     * @param p_mi the Media AudioPlayer
     * @param drawable the libvlc_drawable_t where the media player should
     * render its video
     * @param p_e an initialized exception pointer
     * @since version 0.9.0
     * @deprecated VLC 1.0.0
     */
    @Deprecated
    void libvlc_media_player_set_drawable(libvlc_media_player_t p_mi, int drawable,
            libvlc_exception_t p_e);

    /**
     * Get the drawable where the media player should render its video output
     *
     * @param p_mi the Media AudioPlayer
     * @param p_e an initialized exception pointer
     * @return the libvlc_drawable_t where the media player should render its
     * video
     * @since version 0.9.0
     * @deprecated VLC 1.0.0
     */
    @Deprecated
    int libvlc_media_player_get_drawable(libvlc_media_player_t p_mi,
            libvlc_exception_t p_e);

    /* \bug This might go away ... to be replaced by a broader system */

    /**
     * Get the current movie length (in ms).
     *
     * VLC_PUBLIC_API libvlc_time_t libvlc_media_player_get_length (
     * libvlc_media_player_t *p_mi);
     *
     * @param p_mi the Media AudioPlayer
     * @return the movie length (in ms), or -1 if there is no media.
     * @since version 1.1.0
     */
    long libvlc_media_player_get_length(libvlc_media_player_t p_mi);
    /**
     * Get the current movie length (in ms).
     *
     * @param p_mi the Media AudioPlayer
     * @param p_e an initialized exception pointer
     * @return the movie length (in ms).
     * @since version 0.9.0
     * @deprecated VLC 1.1.0
     */
    @Deprecated
    long libvlc_media_player_get_length(libvlc_media_player_t p_mi, libvlc_exception_t p_e);

    /**
     * Get the current movie time (in ms).
     *
     * VLC_PUBLIC_API libvlc_time_t libvlc_media_player_get_time(libvlc_media_player_t *p_mi);
     *
     * @param p_mi the Media AudioPlayer
     * @return the movie time (in ms), or -1 if there is no media.
     * @since version 1.1.0
     */
    long libvlc_media_player_get_time(libvlc_media_player_t p_mi);
    /**
     * Get the current movie time (in ms).
     *
     * @param p_mi the Media AudioPlayer
     * @param p_e an initialized exception pointer
     * @return the movie time (in ms).
     * @since version 0.9.0
     * @deprecated VLC 1.1.0
     */
    @Deprecated
    long libvlc_media_player_get_time(libvlc_media_player_t p_mi, libvlc_exception_t p_e);

    /**
     * Set the movie time (in ms). This has no effect if no media is being
     * played. Not all formats and protocols support this.
     *
     * VLC_PUBLIC_API void libvlc_media_player_set_time(
     * libvlc_media_player_t *p_mi, libvlc_time_t i_time );
     *
     * @param p_mi the Media AudioPlayer
     * @param i_time the movie time (in ms).
     * @since version 1.1.0
     */
    void libvlc_media_player_set_time(libvlc_media_player_t p_mi, long i_time);
    /**
     * Set the movie time (in ms).
     *
     * @param p_mi the Media AudioPlayer
     * @param i_time the movie time (in ms).
     * @param p_e an initialized exception pointer
     * @since version 0.9.0
     * @deprecated VLC 1.1.0
     */
    @Deprecated
    void libvlc_media_player_set_time(libvlc_media_player_t p_mi, long i_time,
            libvlc_exception_t p_e);

    /**
     * Get movie position.
     *
     * VLC_PUBLIC_API float libvlc_media_player_get_position(libvlc_media_player_t *p_mi);
     *
     * @param p_mi the Media AudioPlayer
     * @return movie position, or -1. in case of error
     * @since version 1.1.0
     */
    float libvlc_media_player_get_position(libvlc_media_player_t p_mi);
    /**
     * Get movie position.
     *
     * @param p_mi the Media AudioPlayer
     * @param p_e an initialized exception pointer
     * @return movie position
     * @since version 0.9.0
     * @deprecated VLC 1.1.0
     */
    @Deprecated
    float libvlc_media_player_get_position(libvlc_media_player_t p_mi, libvlc_exception_t p_e);

    /**
     * Set movie position. This has no effect if playback is not enabled. This
     * might not work depending on the underlying input format and protocol.
     *
     * VLC_PUBLIC_API void libvlc_media_player_set_position(
     * libvlc_media_player_t *p_mi, float f_pos );
     *
     * @param p_mi the Media AudioPlayer
     * @param f_pos the position
     * @since version 1.1.0
     */
    void libvlc_media_player_set_position(libvlc_media_player_t p_mi, float f_pos);
    /**
     * Set movie position.
     *
     * @param p_mi the Media AudioPlayer
     * @param f_pos movie position
     * @param p_e an initialized exception pointer
     * @since version 0.9.0
     * @deprecated VLC 1.1.0
     */
    @Deprecated
    void libvlc_media_player_set_position(libvlc_media_player_t p_mi, float f_pos,
            libvlc_exception_t p_e);

    /**
     * Get current movie state
     *
     * VLC_PUBLIC_API libvlc_state_t libvlc_media_player_get_state(
     * libvlc_media_player_t *p_mi);
     *
     * @param p_mi the Media AudioPlayer
     * @return the current state of the media player (playing, paused, ...)
     * @see libvlc_state_t
     * @since version 1.1.0
     */
    int libvlc_media_player_get_state(libvlc_media_player_t p_mi);
    /**
     * Get current movie state
     *
     * @param p_mi the Media AudioPlayer
     * @param p_e an initialized exception pointer
     * @return current movie state as libvlc_state_t
     * @since version 0.9.0
     * @deprecated VLC 1.1.0
     */
    @Deprecated
    int libvlc_media_player_get_state(libvlc_media_player_t p_mi, libvlc_exception_t p_e);

    /* end bug */

    /**
     * How many video outputs does this media player have?
     *
     * VLC_PUBLIC_API unsigned libvlc_media_player_has_vout(
     * libvlc_media_player_t *p_mi);
     *
     * @param p_mi the media player
     * @return the number of video outputs
     * @since version 1.1.0
     */
    int libvlc_media_player_has_vout(libvlc_media_player_t p_mi);
    /**
     * Does this media player have a video output?
     *
     * @param p_mi the media player
     * @param p_e an initialized exception pointer
     * @return
     * @since version 0.9.0
     * @deprecated VLC 1.1.0
     */
    @Deprecated
    int libvlc_media_player_has_vout(libvlc_media_player_t p_mi, libvlc_exception_t p_e);

    /**
     * Enable or disable fullscreen.
     *
     * @warning With most window managers, only a top-level windows can be in
     * full-screen mode. Hence, this function will not operate properly if
     * libvlc_media_player_set_xid() was used to embed the video in a
     * non-top-level window. In that case, the embedding window must be
     * reparented to the root window <b>before</b> fullscreen mode is enabled.
     * You will want to reparent it back to its normal parent when disabling
     * fullscreen.
     *
     * VLC_PUBLIC_API void libvlc_set_fullscreen(
     * libvlc_media_player_t *p_mi, int b_fullscreen );
     *
     * @param p_mi the media player
     * @param b_fullscreen boolean for fullscreen status
     * @since version 1.1.0
     */
    void libvlc_set_fullscreen(libvlc_media_player_t p_mi, int b_fullscreen);
    /**
     * Enable or disable fullscreen on a video output.
     *
     * @param p_mi the media player
     * @param b_fullscreen boolean for fullscreen status
     * @param p_e an initialized exception pointer
     * @since version 0.9.0
     * @deprecated VLC 1.1.0
     */
    @Deprecated
    void libvlc_set_fullscreen(libvlc_media_player_t p_mi, int b_fullscreen,
            libvlc_exception_t p_e);

    /**
     * Get current fullscreen status.
     *
     * VLC_PUBLIC_API int libvlc_get_fullscreen( libvlc_media_player_t *p_mi );
     *
     * @param p_mi the media player
     * @return the fullscreen status (boolean)
     * @since version 1.1.0
     */
    int libvlc_get_fullscreen(libvlc_media_player_t p_mi);
    /**
     * Get current fullscreen status.
     *
     * @param p_mi the media player
     * @param p_e an initialized exception pointer
     * @return the fullscreen status (boolean)
     * @since version 0.9.0
     * @deprecated VLC 1.1.0
     */
    @Deprecated
    int libvlc_get_fullscreen(libvlc_media_player_t p_mi, libvlc_exception_t p_e);

    /**
     * Set new video subtitle file.
     *
     * VLC_PUBLIC_API int libvlc_video_set_subtitle_file(
     * libvlc_media_player_t *p_mi, const char *psz_subtitle );
     *
     * @param p_mi the media player
     * @param psz_subtitle new video subtitle file
     * @return the success status (boolean)
     * @since version 1.1.0
     */
    int libvlc_video_set_subtitle_file(libvlc_media_player_t p_mi, String psz_subtitle);
    /**
     * Set new video subtitle file.
     *
     * @param p_mi the media player
     * @param psz_subtitle new video subtitle file
     * @param p_e an initialized exception pointer
     * @return the success status (boolean)
     * @since version 0.9.0
     * @deprecated VLC 1.1.0
     */
    @Deprecated
    int libvlc_video_set_subtitle_file(libvlc_media_player_t p_mi, String psz_subtitle,
            libvlc_exception_t p_e);

    /**
     * Get current audio level.
     *
     * VLC_PUBLIC_API int libvlc_audio_get_volume(libvlc_media_player_t *p_mi);
     *
     * @param p_mi media player
     * @return the audio level (int)
     * @since version 1.1.0
     */
    int libvlc_audio_get_volume(libvlc_media_player_t p_mi);
    /**
     * Get current audio level.
     *
     * @param p_instance libvlc instance
     * @param p_e an initialized exception pointer
     * @return the audio level (int)
     * @since version 0.9.0
     * @deprecated VLC 1.1.0
     */
    @Deprecated
    int libvlc_audio_get_volume(libvlc_instance_t p_instance, libvlc_exception_t p_e);

    /**
     * Set current audio level.
     *
     * VLC_PUBLIC_API int libvlc_audio_set_volume( libvlc_media_player_t *p_mi,
     * int i_volume );
     *
     * @param p_mi media player
     * @param i_volume the volume (int)
     * @return 0 if the volume was set, -1 if it was out of range
     * @since version 1.1.0
     */
    int libvlc_audio_set_volume(libvlc_media_player_t p_mi, int i_volume);
    /**
     * Set current audio level.
     *
     * @param p_instance libvlc instance
     * @param i_volume the volume (int)
     * @param p_e an initialized exception pointer
     * @since version 0.9.0
     * @deprecated VLC 1.1.0
     */
    @Deprecated
    void libvlc_audio_set_volume(libvlc_instance_t p_instance, int i_volume,
            libvlc_exception_t p_e);
}
