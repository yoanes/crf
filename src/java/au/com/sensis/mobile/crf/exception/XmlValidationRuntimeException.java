package au.com.sensis.mobile.crf.exception;

/**
 * Indicates that an error occurred when validating XML.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class XmlValidationRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * @param message Describes the exception.
     * @param cause Cause of the exception.
     */
    public XmlValidationRuntimeException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message Describes the exception.
     */
    public XmlValidationRuntimeException(final String message) {
        super(message);
    }



}
