package thot.exception;

import lombok.Getter;

/**
 * Code pour les exceptions de l'applacation.
 *
 * @author Fabrice Alleau
 * @version 1.8.4
 */
public enum ThotCodeException {

    SERVER(100),
    AUDIO(200),
    VIDEO(300);

    @Getter
    private int code;

    ThotCodeException(int code) {
        this.code = code;
    }
}
