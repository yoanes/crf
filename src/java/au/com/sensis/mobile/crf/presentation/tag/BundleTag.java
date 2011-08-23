package au.com.sensis.mobile.crf.presentation.tag;

import java.util.List;

import au.com.sensis.mobile.crf.service.Resource;

/**
 * Tag that bundles the output of any child tags that register {@link Resource}s with this
 * {@link BundleTag} via the {@link #addResourcesToBundle(List)} method.
 *
 * @author w12495
 *
 */
public interface BundleTag {

    /**
     * @param resources
     *            List of resources that a child tag wants to register with this
     *            {@link BundleTag} to be bundled into a single file.
     */
    void addResourcesToBundle(final List<Resource> resources);

    /**
     * @param href
     *            Absolute href that a child tag wants to register with this
     *            {@link BundleTag} to be written out before the bundle. (Note, not actually
     *            included in the bundle).
     */
    void rememberAbsoluteHref(String href);
}
