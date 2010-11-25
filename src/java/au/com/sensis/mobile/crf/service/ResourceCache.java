package au.com.sensis.mobile.crf.service;

import au.com.sensis.mobile.crf.util.Cache;

/**
 * Simple interface for storing a cache of (ResourceCacheKey, Resource []) pairs.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public interface ResourceCache extends Cache<ResourceCacheKey, Resource> {

}
