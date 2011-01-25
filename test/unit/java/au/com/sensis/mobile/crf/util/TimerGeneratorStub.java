package au.com.sensis.mobile.crf.util;

import java.util.Date;

/**
 * Stubbed {@link TimeGenerator} so that we can return a predictable timestamp.
 */
public class TimerGeneratorStub implements TimeGenerator {

    private static final Date DEFAULT_TIMESTAMP = new Date();

    private Date timestamp;

    /**
     * Default constructor.
     */
    public TimerGeneratorStub() {
        timestamp = DEFAULT_TIMESTAMP;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getTimeInMillis() {
        return timestamp.getTime();
    }

    /**
     * @param timestamp the timestamp to set
     */
    public void setTimestamp(final Date timestamp) {
        this.timestamp = timestamp;
    }
}
