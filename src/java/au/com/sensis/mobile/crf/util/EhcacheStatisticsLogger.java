package au.com.sensis.mobile.crf.util;

import net.sf.ehcache.Ehcache;

import org.apache.log4j.Logger;

/**
 * Log stats associated with a given {@link Ehcache}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class EhcacheStatisticsLogger {

    // Not final so that we can inject a mock during unit testing.
    private static Logger logger = Logger.getLogger(EhcacheStatisticsLogger.class);

    private final Ehcache ehcache;

    /**
     * @param ehcache
     *            Associated cache to log stats for.
     */
    public EhcacheStatisticsLogger(final Ehcache ehcache) {
        this.ehcache = ehcache;
    }

    /**
     * Log stats associated with {@link #getEhcache()}.
     */
    public void logStats() {
        if (logger.isInfoEnabled()) {
            logger.info("Statistics: " + getEhcache().getStatistics());
        }
    }

    /**
     * @return the ehcache
     */
    private Ehcache getEhcache() {
        return ehcache;
    }
}
