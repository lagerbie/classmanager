package thot.video.vlc.internal;

import com.sun.jna.Pointer;
import lombok.Getter;
import thot.video.event.MediaPlayerEvent;
import thot.video.event.MediaPlayerListener;
import thot.video.vlc.VLCMediaPlayer;

/**
 * Callback pour les évènements de la librairie VLC.
 *
 * @author Fabrice Alleau
 * @version 1.8.4 (VLC 1.1.0 à 3.0.x et compatible JET)
 */
public class MediaPlayerCallback implements libvlc_callback_t {

    /**
     * Lecteur multimédia.
     */
    private VLCMediaPlayer mediaPlayer;
    /**
     * Référence du listener.
     */
    @Getter
    private MediaPlayerListener listener;

    /**
     * Initialisation du callback.
     *
     * @param mediaPlayer le lecteur multimédia.
     * @param listener le listener.
     */
    public MediaPlayerCallback(VLCMediaPlayer mediaPlayer, MediaPlayerListener listener) {
        this.mediaPlayer = mediaPlayer;
        this.listener = listener;
    }

    /**
     * Appel du callback.
     *
     * @param libvlc_event l'évènement.
     * @param userData les données de l'évènement.
     */
    @Override
    public void callback(libvlc_event_t libvlc_event, Pointer userData) {
        int type = libvlc_event.type;
        MediaPlayerEvent event = new MediaPlayerEvent(mediaPlayer);

        switch (libvlc_event_e.event(type)) {
            case libvlc_MediaPlayerEndReached:
                listener.endReached(event);
                break;
            case libvlc_MediaPlayerEncounteredError:
                listener.encounteredError(event);
                break;
        }
    }
}
