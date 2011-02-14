package au.com.sensis.mobile.crf.util;

/**
 * Indicates that there was an error in creating an image.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class ImageCreationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * @param message Error message.
     * @param throwable Cause.
     */
    public ImageCreationException(final String message, final Throwable throwable) {
        super(message, throwable);
    }

    /**
     * @param message Error message.
     */
    public ImageCreationException(final String message) {
        super(message);
    }
}
