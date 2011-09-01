package au.com.sensis.mobile.crf.presentation.tag;

/**
 * Tag that bundles the output of any child {@link LinkTag}s that register
 * {@link au.com.sensis.mobile.crf.service.Resource}s with
 * this {@link BundleLinksTag} via the {@link #addResourcesToBundle(java.util.List)} method.
 *
 * @author w12495
 */
public class BundleLinksTag
        extends AbstractBundleTag<BundleLinksTagDelegate> {

    /**
     * {@inheritDoc}
     */
    @Override
    protected BundleLinksTagDelegate createBundleTagDelegate() {

        return new BundleLinksTagDelegate(getJspContext(), getBundleTagData());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getTagStackBeanName() {

        return "crf.bundleLinksTagStackBean";
    }
}
