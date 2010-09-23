package au.com.sensis.mobile.crf.service;

/**
 * {@link ResourceResolverEngine} that will only handle a
 * restricted set of requested resource paths that satisfy
 * {@link #isInterestedIn(String)}. If {@link #isInterestedIn(String)} returns
 * false, all {@link ResourceResolverEngine} methods will
 * return a null/empty List as appropriate.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public interface PathRestrictedResourceResolverEngine extends
        ResourceResolverEngine {
    /**
     * Returns true if this {@link PathRestrictedResourceResolverEngine} is interested
     * in the given requested resource path.
     *
     * @param requestedResourcePath
     *            Requested resource path to be tested.
     * @return True if this {@link PathRestrictedResourceResolverEngine} is interested
     *         in the given requested resource path.
     */
    boolean isInterestedIn(String requestedResourcePath);
}
