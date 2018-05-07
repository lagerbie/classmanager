package thot.exception;

import lombok.Getter;

public enum ThotCodeException {

    AUDIO(100);

    @Getter
    private int code;

    ThotCodeException(int code) {
        this.code = code;
    }
}
