package thot.exception;

import lombok.Getter;

/**
 * Exception de l'applacation.
 *
 * @author Fabrice Alleau
 * @version 1.8.4
 */
public class ThotException extends Exception {

    @Getter
    private ThotCodeException code;

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param code the exception type.
     * @param message the detail message (all '{}' are replace by '%s')..
     * @param parameters the message parameters.
     */
    public ThotException(ThotCodeException code, String message, Object... parameters) {
        super(getMessage(message, parameters));
        this.code = code;
    }

    /**
     * Constructs a new exception with the specified detail message and cause.
     *
     * @param code the exception type.
     * @param message the detail message.
     * @param cause the cause (which is saved for later retrieval by the {@link #getCause()} method).  (A
     *         <tt>null</tt> value is permitted, and indicates that the cause is nonexistent or unknown.)
     * @param parameters the message parameters.
     */
    public ThotException(ThotCodeException code, String message, Throwable cause, Object... parameters) {
        super(getMessage(message, parameters), cause);
        this.code = code;
    }

    /**
     * Constructs the message of the exception.
     *
     * @param message the detail message (all '{}' are replace by '%s').
     * @param parameters the message parameters.
     *
     * @return the detail message.
     */
    private static String getMessage(String message, Object... parameters) {
        return String.format(message.replace("{}", "%s"), parameters);
    }
}
