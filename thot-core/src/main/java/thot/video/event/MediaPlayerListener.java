package thot.video.event;

import java.util.EventListener;

/**
 * Notification des évènements du lecteur multimedia.
 *
 * @author Fabrice Alleau
 * @version 1.8.4
 */
public interface MediaPlayerListener extends EventListener {

    /**
     * Evènement de fin de lecture du média.
     *
     * @param event l'évènement.
     */
    void endReached(MediaPlayerEvent event);

    /**
     * Evènement d'une erreur.
     *
     * @param event l'évènement.
     */
    void encounteredError(MediaPlayerEvent event);
}
