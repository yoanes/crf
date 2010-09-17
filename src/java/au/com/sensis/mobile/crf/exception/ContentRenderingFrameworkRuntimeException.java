package au.com.sensis.mobile.crf.exception;

/**
 * Top level runtime exception for the Content Rendering Framework.
 * @author Adrian.Koh2@sensis.com.au
 */
public class ContentRenderingFrameworkRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * @param message Describes the exception.
     * @param cause Cause of the exception.
     */
    public ContentRenderingFrameworkRuntimeException(final String message,
            final Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message Describes the exception.
     */
    public ContentRenderingFrameworkRuntimeException(final String message) {
        super(message);
    }

}
