package au.com.sensis.mobile.crf.util;

/**
 * Generic interface for generating a time in milliseconds, where this value
 * typically has the same semantics as
 * {@link java.util.Calendar#setTimeInMillis(long)} or
 * {@link System#currentTimeMillis()}. This simple interface is used by classes
 * that would otherwise use {@link java.util.Calendar} or
 * {@link java.lang.System} directly for generating a timestamp. Using this
 * TimeGenerator interface instead allows such classes to be more easily tested.
 * In otherwords, during unit tests, an implementation that always produces the
 * same time can be used.
 *
 * @author Adrian.Koh@sensis.com.au
 */
public interface TimeGenerator {

    /**
     * Returns a time in milliseconds, where the exact semantics of the returned
     * value is implementation dependent. For example, the implementation may
     * call {@link java.util.Calendar#setTimeInMillis(long)} or
     * {@link System#currentTimeMillis()}.
     *
     * @return a time in milliseconds, where the exact semantics of the returned
     *         value is implementation dependent.
     */
    long getTimeInMillis();
}
