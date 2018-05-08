package thot.video;

/**
 * Liste d'états possible pour le lecteur multimedia.
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
