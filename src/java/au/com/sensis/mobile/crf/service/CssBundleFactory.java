package au.com.sensis.mobile.crf.service;

import java.util.List;


/**
 * Factory for getting CSS bundles (ie. the result of concatenating constituent CSS files
 * and possibly compressing the result).
 *
 * @author Adrian.Koh2@sensis.com.au
 *
 */
public interface CssBundleFactory {

    /**
     * Gets a {@link MappedResourcePath} formed from bundling/combining the
     * contents of all of the given files.
     *
     * @param resourcePathsToInclude
     *            {@link MappedResourcePath} of each file to include in the
     *            bundle.
     * @return {@link MappedResourcePath} of the bundle. If no bundle is created
     *         (because none of the given resourcePathsToInclude exists), null
     *         is returned.
     */
    MappedResourcePath getBundle(List<MappedResourcePath> resourcePathsToInclude);

}
