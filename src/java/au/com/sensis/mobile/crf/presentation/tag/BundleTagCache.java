package au.com.sensis.mobile.crf.presentation.tag;

import au.com.sensis.mobile.crf.util.Cache;

/**
 * Cache mapping {@link BundleTagCacheKey}s to the bundle client path to render to the page.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public interface BundleTagCache extends Cache<BundleTagCacheKey, String> {
}
