package thot.exception;

import lombok.Getter;

/**
 * Code pour les exceptions de l'applacation.
 *
 * @author Fabrice Alleau
 * @version 1.8.4
 */
public enum ThotCodeException {

    FILE_NOT_FOUND(100),
    EMPTY_DIRECTORY(101),
    BINARY_NOT_FOUND(110),
    VLC_NOT_FOUND(111),
    MPLAYER_NOT_FOUND(112),
    MPENCODER_NOT_FOUND(113),

    NOT_YET_IMPLEMENTED(200),
    CONVESRION_ERROR(201),

    SERVER(700),
    AUDIO(800),
    VIDEO(900);

    @Getter
    private int code;


    ThotCodeException(int code) {
        this.code = code;
    }
}
