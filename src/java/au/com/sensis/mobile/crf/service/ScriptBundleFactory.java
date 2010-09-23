package au.com.sensis.mobile.crf.service;

import java.util.List;


/**
 * Factory for getting Script bundles (ie. the result of concatenating constituent CSS files
 * and possibly compressing the result).
 *
 * @author Adrian.Koh2@sensis.com.au
 *
 */
public interface ScriptBundleFactory {

    /**
     * Gets a {@link Resource} formed from bundling/combining the
     * contents of all of the given files.
     *
     * @param resourcePathsToInclude
     *            {@link Resource} of each file to include in the
     *            bundle.
     * @return {@link Resource} of the bundle. If no bundle is created
     *         (because none of the given resourcePathsToInclude exists), null
     *         is returned.
     */
    Resource getBundle(List<Resource> resourcePathsToInclude);

}
