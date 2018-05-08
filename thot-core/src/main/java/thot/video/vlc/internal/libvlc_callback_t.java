package thot.video.vlc.internal;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;

/**
 * Callback function notification.
 *
 * @author Fabrice Alleau
 * @version 1.8.4 (VLC 1.1.0 à 3.0.x et compatible JET)
 */
public interface libvlc_callback_t extends Callback {

    /**
     * Callback function notification.
     *
     * @param p_event the event triggering the callback
     * @param userData
     */
    void callback(libvlc_event_t p_event, Pointer userData);
}
