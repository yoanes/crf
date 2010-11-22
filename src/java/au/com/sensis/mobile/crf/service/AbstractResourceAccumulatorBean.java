package au.com.sensis.mobile.crf.service;

import java.util.List;

import au.com.sensis.mobile.crf.debug.ResourceTreeNodeBean;
import au.com.sensis.mobile.crf.debug.ResourceResolutionTree;
import au.com.sensis.mobile.crf.debug.ResourceResolutionTreeHolder;

/**
 * Base {@link ResourceAccumulator}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public abstract class AbstractResourceAccumulatorBean implements ResourceAccumulator {

    private ResourceResolutionTree getResourceResolutionTree() {
        return ResourceResolutionTreeHolder.getResourceResolutionTree();
    }

    /**
     * Add the Resources to the {@link ResourceResolutionTree} for the current
     * thread.
     *
     * @param resources
     *            Resources to add to the {@link ResourceResolutionTree} for the
     *            current thread.
     */
    protected final void addResourcesToResourceResolutionTreeIfEnabled(
            final List<Resource> resources) {
        if (getResourceResolutionTree().isEnabled()) {
            for (final Resource currResource : resources) {
                getResourceResolutionTree().addChildToCurrentNode(
                        new ResourceTreeNodeBean(currResource));
            }
        }
    }
}
