package au.com.sensis.mobile.crf.config;

import au.com.sensis.mobile.crf.util.Cache;


/**
 * Simple interface for storing a cache of (String userAgent, Group []) pairs.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public interface GroupsCache extends Cache<GroupsCacheKey, Group> {

}
