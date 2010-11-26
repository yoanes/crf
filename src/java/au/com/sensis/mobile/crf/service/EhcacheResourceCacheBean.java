package au.com.sensis.mobile.crf.service;

import net.sf.ehcache.Ehcache;
import au.com.sensis.mobile.crf.util.EhcacheCacheBean;

/**
 * Simple {@link ResourceCache} implementation that stores all entries in memory
 * and does not replicate across a cluster.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class EhcacheResourceCacheBean extends EhcacheCacheBean<ResourceCacheKey, Resource>
    implements ResourceCache {

    /**
     * Constructor.
     *
     * @param ehcache
     *            {@link Ehcache} wrapped by this calss.
     * @param cacheEnabled
     *            True if the cache should be enabled.
     */
    public EhcacheResourceCacheBean(final Ehcache ehcache, final boolean cacheEnabled) {
        super(ehcache, cacheEnabled);
    }



}
