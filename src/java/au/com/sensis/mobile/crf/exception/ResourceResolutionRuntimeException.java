package au.com.sensis.mobile.crf.exception;

/**
 * Represents an error in resolving a resource.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class ResourceResolutionRuntimeException extends ContentRenderingFrameworkRuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * @param message Describes the exception.
     * @param cause Cause of the exception.
     */
    public ResourceResolutionRuntimeException(final String message,
            final Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message Describes the exception.
     */
    public ResourceResolutionRuntimeException(final String message) {
        super(message);
    }


}
