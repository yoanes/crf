package au.com.sensis.mobile.crf.service;

import au.com.sensis.mobile.crf.util.Cache;

/**
 * Simple interface for storing a cache of (ResourceCacheKey, Resource []) pairs.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public interface ResourceCache extends Cache<ResourceCacheKey, ResourceCacheEntry> {

    /**
     * Default value for {@link #getResourcesNotFoundMaxRefreshCount()}.
     */
    int DEFAULT_RESOUCRES_NOT_FOUND_MAX_REFRESH_COUNT = 10;
    /**
     * Default value for {@link #getResourcesNotFoundRefreshCountUpdateMilliseconds()}.
     */
    int DEFAULT_RESOURCES_NOT_FOUND_REFRESH_COUNT_UPDATE_MILLISECONDS = 60000;

    /**
     * @return if a cache entry corresponds to no resources found, this is the
     *         maximum number of times that attempts will be made to refresh the
     *         entry (and thus conclude that the resources really, really cannot
     *         be found). This allows for some level of recovery if the
     *         original request could not be resolved due to a transient file
     *         system error.
     */
    int getResourcesNotFoundMaxRefreshCount();

    /**
     * @return Used in conjunction with
     *         {@link #getResourcesNotFoundMaxRefreshCount()}, this value is
     *         the minimum time delay to wait before updating the refresh count.
     *         For example, if this is set to 60000 milliseconds, we effectively
     *         allow a maximum of one refresh to be counted every 60 seconds,
     *         regardless of how many attempts were truly made. This ensures
     *         that the recovery attempts will span a non-trivial amount of
     *         time.
     */
    int getResourcesNotFoundRefreshCountUpdateMilliseconds();

}
