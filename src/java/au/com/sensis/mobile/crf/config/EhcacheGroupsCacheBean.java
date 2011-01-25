package au.com.sensis.mobile.crf.config;

import net.sf.ehcache.Ehcache;
import au.com.sensis.mobile.crf.util.LegacyEhcacheCacheBean;

/**
 * {@link GroupsCache} that uses ehcache.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class EhcacheGroupsCacheBean extends LegacyEhcacheCacheBean<GroupsCacheKey, Group>
    implements GroupsCache {

    /**
     * Constructor.
     *
     * @param ehcache
     *            {@link Ehcache} wrapped by this calss.
     * @param cacheEnabled
     *            True if the cache should be enabled.
     */
    public EhcacheGroupsCacheBean(final Ehcache ehcache, final boolean cacheEnabled) {
        super(ehcache, cacheEnabled);
    }

}
