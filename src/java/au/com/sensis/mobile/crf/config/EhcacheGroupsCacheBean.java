package au.com.sensis.mobile.crf.config;

import net.sf.ehcache.Ehcache;
import au.com.sensis.mobile.crf.util.LegacyEhcacheCacheBean;

/**
 * {@link GroupsCache} that uses ehcache.
 * <p>
 * Note that {@link Group} currently has a private, transient
 * {@link Groups} field. This is okay for the current usage of this
 * cache, since we only cache groups as the result of positive
 * {@link Group#match(au.com.sensis.wireless.common.volantis.devicerepository.api.Device)}
 * evaluation, after which the {@link Groups} is no longer needed.
 * </p>
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class EhcacheGroupsCacheBean extends LegacyEhcacheCacheBean<GroupsCacheKey, Group> implements
        GroupsCache {

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
