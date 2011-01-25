package au.com.sensis.mobile.crf.util;

/**
 * Simple implementation of {@link TimeGenerator} that generates the current
 * (system) time. ie. {@link System#currentTimeMillis()}.
 *
 * @author Adrian.Koh@sensis.com.au
 */
public class SystemTimeGeneratorBean implements TimeGenerator {

    /**
     * {@inheritDoc}
     */
    @Override
    public long getTimeInMillis() {

        return System.currentTimeMillis();
    }
}
