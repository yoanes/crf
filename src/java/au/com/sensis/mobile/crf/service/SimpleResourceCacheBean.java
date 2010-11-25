package au.com.sensis.mobile.crf.service;

import au.com.sensis.mobile.crf.util.ConcurrentMapCacheBean;

/**
 * Simple {@link ResourceCache} implementation that stores all entries in memory
 * and does not replicate across a cluster.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class SimpleResourceCacheBean extends ConcurrentMapCacheBean<ResourceCacheKey, Resource>
    implements ResourceCache {

    /**
     * Constructor.
     *
     * @param cacheEnabled
     *            True if the cache should be enabled.
     */
    public SimpleResourceCacheBean(final boolean cacheEnabled) {
        super(cacheEnabled);
    }


}
