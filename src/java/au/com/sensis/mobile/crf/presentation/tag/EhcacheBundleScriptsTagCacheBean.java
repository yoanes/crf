package au.com.sensis.mobile.crf.presentation.tag;

import net.sf.ehcache.Ehcache;
import au.com.sensis.mobile.crf.util.EhcacheCacheBean;

/**
 * Simple {@link BundleScriptsTagCache} implementation that stores all entries in memory
 * and does not replicate across a cluster.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class EhcacheBundleScriptsTagCacheBean
    extends EhcacheCacheBean<BundleScriptsTagCacheKey, String>
    implements BundleScriptsTagCache {

    /**
     * Constructor.
     *
     * @param ehcache {@link Ehcache} wrapped by this calss.
     * @param cacheEnabled
     *            True if the cache should be enabled.
     */
    public EhcacheBundleScriptsTagCacheBean(final Ehcache ehcache, final boolean cacheEnabled) {
        super(ehcache, cacheEnabled);
    }
}
