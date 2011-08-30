package au.com.sensis.mobile.crf.presentation.tag;


/**
 * Tag that bundles the output of any child {@link ScriptTag}s that register
 * {@link au.com.sensis.mobile.crf.service.Resource}s with
 * this {@link BundleScriptsTag} via the {@link #addResourcesToBundle(java.util.List)} method.
 *
 * @author w12495
 */
public class BundleScriptsTag
        extends AbstractBundleTag<BundleScriptsTagDelegate> {

    /**
     * {@inheritDoc}
     */
    @Override
    protected BundleScriptsTagDelegate createBundleTagDelegate() {

        return new BundleScriptsTagDelegate(getJspContext(), getBundleTagData());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getTagStackBeanName() {

        return "crf.bundleScriptsTagStackBean";
    }
}
