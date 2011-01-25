package au.com.sensis.mobile.crf.service;

import net.sf.ehcache.Ehcache;
import au.com.sensis.mobile.crf.util.EhcacheCacheBean;

/**
 * Simple {@link ResourceCache} implementation that stores all entries in memory
 * and does not replicate across a cluster.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class EhcacheResourceCacheBean extends EhcacheCacheBean<ResourceCacheKey, ResourceCacheEntry>
    implements ResourceCache {

    private int resourcesNotFoundMaxRefreshCount = DEFAULT_RESOUCRES_NOT_FOUND_MAX_REFRESH_COUNT;
    private int resourcesNotFoundRefreshCountUpdateMilliseconds
        = DEFAULT_RESOURCES_NOT_FOUND_REFRESH_COUNT_UPDATE_MILLISECONDS;

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

    /**
     * {@inheritDoc}
     */
    @Override
    public int getResourcesNotFoundMaxRefreshCount() {
        return resourcesNotFoundMaxRefreshCount;
    }

    /**
     * @param resourcesNotFoundMaxRefreshCount
     *            the resourcesNotFoundMaxRefreshCount to set
     */
    public void setResourcesNotFoundMaxRefreshCount(
            final int resourcesNotFoundMaxRefreshCount) {
        this.resourcesNotFoundMaxRefreshCount = resourcesNotFoundMaxRefreshCount;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getResourcesNotFoundRefreshCountUpdateMilliseconds() {
        return resourcesNotFoundRefreshCountUpdateMilliseconds;
    }

    /**
     * @param resourcesNotFoundRefreshCountUpdateMilliseconds
     *            the resourcesNotFoundRefreshCountUpdateMilliseconds to set
     */
    public void setResourcesNotFoundRefreshCountUpdateMilliseconds(
            final int resourcesNotFoundRefreshCountUpdateMilliseconds) {
        this.resourcesNotFoundRefreshCountUpdateMilliseconds =
                resourcesNotFoundRefreshCountUpdateMilliseconds;
    }
}
