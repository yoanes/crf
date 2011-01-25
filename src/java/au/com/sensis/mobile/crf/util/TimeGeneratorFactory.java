package au.com.sensis.mobile.crf.util;

/**
 * Factory to return a {@link TimeGenerator} singleton.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public final class TimeGeneratorFactory {
    private static TimeGenerator timeGeneratorSingleton;

    static {
        restoreDefaultTimeGeneratorSingleton();
    }

    private TimeGeneratorFactory() {

    }

    /**
     * @return the timeGeneratorSingleton
     */
    public static TimeGenerator getTimeGeneratorSingleton() {
        return timeGeneratorSingleton;
    }

    /**
     * Change the default {@link TimeGenerator} singleton. Only to be called
     * during unit testing.
     *
     * @param timeGeneratorSingleton
     *            the timeGeneratorSingleton to use.
     */
    public static void changeDefaultTimeGeneratorSingleton(
            final TimeGenerator timeGeneratorSingleton) {
        TimeGeneratorFactory.timeGeneratorSingleton = timeGeneratorSingleton;
    }

    /**
     * Restore the default {@link TimeGenerator} singleton. Only to be called
     * during unit testing.
     */
    public static void restoreDefaultTimeGeneratorSingleton() {
        // We actually instantiate a new instance since it contains no state.
        TimeGeneratorFactory.timeGeneratorSingleton = new SystemTimeGeneratorBean();
    }
}
