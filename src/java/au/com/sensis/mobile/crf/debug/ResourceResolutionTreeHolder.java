package au.com.sensis.mobile.crf.debug;

/**
 * Holds a {@link ThreadLocal} storing a {@link ResourceResolutionTree}.
 */
public final class ResourceResolutionTreeHolder {

    private static ThreadLocal<ResourceResolutionTree> resourceResolutionTreeThreadLocal =
            new ThreadLocal<ResourceResolutionTree>();

    /**
     * Private constructor to prevent construction.
     */
    private ResourceResolutionTreeHolder() {

    }

    private static ThreadLocal<ResourceResolutionTree> getResourceResolutionTreeThreadLocal() {
        return resourceResolutionTreeThreadLocal;
    }

    /**
     * @return {@link ResourceResolutionTree} for the current thread that was
     *         previously set via
     *         {@link #setResourceResolutionTree(ResourceResolutionTree)}.
     */
    public static ResourceResolutionTree getResourceResolutionTree() {
        if (getResourceResolutionTreeThreadLocal().get() == null) {
            setResourceResolutionTree(new ResourceResolutionTree());
        }

        return getResourceResolutionTreeThreadLocal().get();
    }

    /**
     * Set {@link ResourceResolutionTree} for the current thread.
     *
     * @param resourceResolutionTree {@link ResourceResolutionTree} for the current thread.
     */
    public static void setResourceResolutionTree(
            final ResourceResolutionTree resourceResolutionTree) {
        getResourceResolutionTreeThreadLocal().set(resourceResolutionTree);
    }

    /**
     * Remove the {@link ResourceResolutionTree} for the current thread.
     */
    public static void removeResourceResolutionTree() {
        getResourceResolutionTreeThreadLocal().remove();
    }
}
