package au.com.sensis.mobile.crf.util;

import java.io.Serializable;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

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
    @Override
    public V[] get(final K key) {
        if (contains(key)) {
            return (V[]) getEhcache().get(key).getValue();
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
