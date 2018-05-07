package thot.exception;

import lombok.Getter;

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
