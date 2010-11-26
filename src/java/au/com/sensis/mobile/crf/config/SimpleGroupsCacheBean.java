package au.com.sensis.mobile.crf.config;

import au.com.sensis.mobile.crf.util.ConcurrentMapCacheBean;

/**
 * Simple {@link GroupsCache} implementation that stores all entries in memory
 * and does not replicate across a cluster.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class SimpleGroupsCacheBean extends ConcurrentMapCacheBean<GroupsCacheKey, Group> implements
        GroupsCache {

    /**
     * Constructor.
     *
     * @param cacheEnabled
     *            True if the cache should be enabled.
     */
    public SimpleGroupsCacheBean(final boolean cacheEnabled) {
        super(cacheEnabled);
    }

}
