package au.com.sensis.mobile.crf.util;

import java.io.Serializable;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.Statistics;

import org.apache.log4j.Logger;

/**
 * {@link Cache} implementation that uses ehcache.
 *
 * @param <K>
 *            Key type.
 * @param <V>
 *            Value tyep.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class EhcacheCacheBean<K extends Serializable, V extends Serializable> implements
        Cache<K, V> {

    private static final Logger LOGGER = Logger.getLogger(EhcacheCacheBean.class);

    private boolean cacheEnabled = true;
    private final Ehcache ehcache;

    /**
     * Wrapper around static final fields in {@link Statistics}.
     */
    public static enum StatisticsAccuracy {
        /**
         * @see Statistics#STATISTICS_ACCURACY_NONE
         */
        NONE (Statistics.STATISTICS_ACCURACY_NONE),

        /**
         * @see Statistics#STATISTICS_ACCURACY_BEST_EFFORT
         */
        BEST_EFFORT(Statistics.STATISTICS_ACCURACY_BEST_EFFORT),

        /**
         * @see Statistics#STATISTICS_ACCURACY_GUARANTEED
         */
        GUARANTEED(Statistics.STATISTICS_ACCURACY_GUARANTEED);

        private int accuracy;

        private StatisticsAccuracy(final int accuracy) {
            this.accuracy = accuracy;
        }

        private int getAccuracy() {
            return accuracy;
        }
    }

    /**
     * Constructor.
     *
     * @param ehcache {@link Ehcache} wrapped by this calss.
     * @param cacheEnabled
     *            True if the cache should be enabled.
     */
    public EhcacheCacheBean(final Ehcache ehcache, final boolean cacheEnabled) {
        this.ehcache = ehcache;
        this.cacheEnabled = cacheEnabled;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean contains(final K key) {
        if (isEnabled()) {
            return getEhcache().isKeyInCache(key);
        } else {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public V get(final K key) {
        if (contains(key)) {
            return (V) getEhcache().get(key).getValue();
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void put(final K key, final V value) {
        if (isEnabled()) {
            getEhcache().put(new Element(key, value));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEnabled() {
        debugLogCacheEnabledState();

        return cacheEnabled;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeAll() {
        getEhcache().removeAll();
    }

    /**
     * @param enableStatistics
     *            Set whether statistics should be enabled on the cache.
     *            Defaults to false.
     */
    public void setEnableStatistics(final boolean enableStatistics) {
        getEhcache().setStatisticsEnabled(enableStatistics);
    }

    /**
     * @param accuracy
     *            Sets the accuracy of statistics. Only has an effect if
     *            {@link #setEnableStatistics(boolean)} was called with true.
     *            Defaults to the underlying ehcache default.
     */
    public void setStatisticsAccuracy(final StatisticsAccuracy accuracy) {
        getEhcache().setStatisticsAccuracy(accuracy.getAccuracy());
    }

    private void debugLogCacheEnabledState() {
        if (LOGGER.isDebugEnabled()) {
            if (cacheEnabled) {
                LOGGER.debug("cache is enabled so it will be consulted.");
            } else {
                LOGGER.debug("cache is disabled so it will be ignored.");
            }
        }
    }

    /**
     * @return the ehcache
     */
    private Ehcache getEhcache() {
        return ehcache;
    }
}
