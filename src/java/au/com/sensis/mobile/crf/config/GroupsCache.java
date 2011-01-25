package au.com.sensis.mobile.crf.config;

import au.com.sensis.mobile.crf.util.LegacyCache;


/**
 * Simple interface for storing a cache of (String userAgent, Group []) pairs.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public interface GroupsCache extends LegacyCache<GroupsCacheKey, Group> {

}
