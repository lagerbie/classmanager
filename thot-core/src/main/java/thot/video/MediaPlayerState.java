package thot.video;

/**
 * Liste d'états possible pour le lecteur multimedia.
 *
 * @author Fabrice Alleau
 * @version 1.8.4
 */
public enum MediaPlayerState {
    IDLE_CLOSE,
    OPENING,
    BUFFERING,
    PLAYING,
    PAUSED,
    STOPPING,
    ENDED,
    ERROR
}
