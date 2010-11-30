package au.com.sensis.mobile.crf.exception;

/**
 * Represents an error in minifying a JavaScript or CSS file.
 *
 * @author Tony Filipe
 *
 */
public class MinificationException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * @param message Describes the exception.
     * @param cause Cause of the exception.
     */
    public MinificationException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message Describes the exception.
     */
    public MinificationException(final String message) {
        super(message);
    }


}
