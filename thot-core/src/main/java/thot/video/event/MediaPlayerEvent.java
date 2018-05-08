package thot.video.event;

import java.util.EventObject;

import thot.video.MediaPlayer;

/**
 * Evènement pour le lecteur multimedia.
 *
 * @author Fabrice Alleau
 * @version 1.8.4
 */
public class MediaPlayerEvent extends EventObject {
    private static final long serialVersionUID = 90000L;

    /**
     * Initiallisation de l'évenement du lectuer multimédia.
     *
     * @param source la source de l'évènement.
     */
    public MediaPlayerEvent(MediaPlayer source) {
        super(source);
    }

}
