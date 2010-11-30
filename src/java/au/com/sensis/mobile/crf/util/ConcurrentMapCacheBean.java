package au.com.sensis.mobile.crf.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.log4j.Logger;

/**
 * Simple {@link Cache} implementation that stores all entries in a {@link ConcurrentMap}
 * in memory and does not replicate across a cluster.
 *
 * @param <K> Key type.
 * @param <V> Value tyep.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class ConcurrentMapCacheBean<K, V> implements Cache<K, V> {

    private static final Logger LOGGER = Logger.getLogger(ConcurrentMapCacheBean.class);

    private boolean cacheEnabled = true;
    private ConcurrentMap<K, V []> cacheMap;

    /**
     * Constructor.
     *
     * @param cacheEnabled
     *            True if the cache should be enabled.
     */
    public ConcurrentMapCacheBean(final boolean cacheEnabled) {
        this.cacheEnabled = cacheEnabled;
        setCacheMap(new ConcurrentHashMap<K, V[]>());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean contains(final K key) {
        if (isEnabled()) {
            return getCacheMap().containsKey(key);
        } else {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V [] get(final K key) {
        if (isEnabled()) {
            return getCacheMap().get(key);
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void put(final K key, final V[] value) {
        if (isEnabled()) {
            getCacheMap().put(key, value);
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
        getCacheMap().clear();
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
     * @return the cacheMap
     */
    private ConcurrentMap<K, V[]> getCacheMap() {
        return cacheMap;
    }

    /**
     * @param cacheMap the cacheMap to set
     */
    private void setCacheMap(final ConcurrentMap<K, V[]> cacheMap) {
        this.cacheMap = cacheMap;
    }
}
