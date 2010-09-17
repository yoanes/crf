package au.com.sensis.mobile.crf.exception;

/**
 * Indicates that some configuration is malformed.
 *
 * @author Adrian.Koh2@sensis.com.au
 *
 */
public class ConfigurationRuntimeException extends
        ContentRenderingFrameworkRuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * @param message Describes the exception.
     * @param cause Cause of the exception.
     */
    public ConfigurationRuntimeException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message Describes the exception.
     */
    public ConfigurationRuntimeException(final String message) {
        super(message);
    }


}
