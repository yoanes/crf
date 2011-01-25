package au.com.sensis.mobile.crf.service;

import java.io.Serializable;
import java.util.List;

/**
 * Entries in a cache of {@link Resource}s.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public interface ResourceCacheEntry extends Serializable {

    /**
     * @return the {@link Resource}s being cached.
     */
    Resource [] getResources();

    /**
     * @param resources the {@link Resource}s being cached.
     */
    void setResources(Resource [] resources);

    /**
     * @return {@link #getResources()} as a List.
     */
    List<Resource> getResourcesAsList();

    /**
     * @return true if the cached resources are null or empty.
     */
    boolean isEmptyResources();

    /**
     * @return true if this cache entry has been refreshed the maximum number of allowed times.
     */
    boolean maxRefreshCountReached();

    /**
     * @return Number of times that this entry has been refreshed.
     */
    int getRefreshCount();

    /**
     * Increment the refresh count used by {@link #maxRefreshCountReached()} but limit the increment
     * rate to a sane limit. eg. at most once every 60 seconds.
     */
    void incrementRefreshCountRateLimited();
}
