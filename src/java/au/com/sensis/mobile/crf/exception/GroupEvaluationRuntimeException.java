package au.com.sensis.mobile.crf.exception;


/**
 * Indicates that an error occurred when evaluating a group, either during validation of the group
 * or runtime comparison to the current device.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class GroupEvaluationRuntimeException extends ConfigurationRuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * @param message Describes the exception.
     * @param cause Cause of the exception.
     */
    public GroupEvaluationRuntimeException(final String message,
            final Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message Describes the exception.
     */
    public GroupEvaluationRuntimeException(final String message) {
        super(message);
    }

}
