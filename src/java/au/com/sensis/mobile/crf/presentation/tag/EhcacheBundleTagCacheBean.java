package au.com.sensis.mobile.crf.presentation.tag;

import net.sf.ehcache.Ehcache;
import au.com.sensis.mobile.crf.util.EhcacheCacheBean;

/**
 * Simple {@link BundleTagCache} implementation that uses ehcache.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class EhcacheBundleTagCacheBean
    extends EhcacheCacheBean<BundleTagCacheKey, String>
    implements BundleTagCache {

    /**
     * Constructor.
     *
     * @param ehcache {@link Ehcache} wrapped by this calss.
     * @param cacheEnabled
     *            True if the cache should be enabled.
     */
    public EhcacheBundleTagCacheBean(final Ehcache ehcache, final boolean cacheEnabled) {
        super(ehcache, cacheEnabled);
    }
}
