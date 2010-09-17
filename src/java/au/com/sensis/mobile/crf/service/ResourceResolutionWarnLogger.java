package au.com.sensis.mobile.crf.service;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * Simple class for specifically logging resource resolution warnings. This
 * abstracts clients from having to know what the specific log4j logger is
 * called.
 *
 * @author Adrian.Koh2@sensis.com.au
 *
 */
public class ResourceResolutionWarnLogger {

    private static final Logger LOG =
            Logger.getLogger(ResourceResolutionWarnLogger.class);

    /**
     * @param message
     *            Message to be logged into the resource resolution warning log.
     */
    public void warn(final Object message) {
        LOG.warn(message);
    }

    /**
     * @param message
     *            Message to be logged into the resource resolution warning log.
     * @param t
     *            Throwable that caused the warning.
     */
    public void warn(final Object message, final Throwable t) {
        LOG.warn(message, t);
    }

    /**
     * @return true if warn level logging is enabled.
     */
    public boolean isWarnEnabled() {
        return LOG.isEnabledFor(Level.WARN);
    }
}
