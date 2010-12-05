package au.com.sensis.mobile.crf.service;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

import org.apache.log4j.Logger;


/**
 * A {@link ResourceAccumulator} that accumulates Javascript {@link Resource}s into a list ordered
 * from most-generic to most-specific. If bundling is enabled then a single bundle of the
 * combined ordered {@link Resource}s is output.
 *
 * @author Tony Filipe
 */
public class JavaScriptResourceAccumulatorBean implements ResourceAccumulator {

    private static final Logger LOGGER = Logger.getLogger(JavaScriptResourceAccumulatorBean.class);
    private final String javascriptPackageKeyword;
    private final boolean bundlingEnabled;
    private JavaScriptBundleFactory bundleFactory;

    /**
     * An initial value for a <code>hashCode</code>, to which is added contributions
     * from fields. Using a non-zero value decreases collisions of <code>hashCode</code>
     * values.
     */
    private static final int SEED = 23;


    private Deque<Resource> allResourcePaths;


    /**
     * Constructs an initialised ResourceAccumulator.
     *
     * @param javascriptPackageKeyword special keyword used to represent a Javascript package.
     * @param javascriptPackageFilename the name to use for a Javascript package bundle
     * @param bundlingEnabled whether or not Javascript bundling is enabled
     */
    public JavaScriptResourceAccumulatorBean(final String javascriptPackageKeyword,
            final String javascriptPackageFilename, final boolean bundlingEnabled) {

        this.javascriptPackageKeyword = javascriptPackageKeyword;
        this.bundlingEnabled = bundlingEnabled;

        if (bundlingEnabled) {
            bundleFactory = new JavaScriptBundleFactory(javascriptPackageKeyword,
                    javascriptPackageFilename);
        }

        allResourcePaths = new ArrayDeque<Resource>();
    }


    /**
     * Compares this {@link JavaScriptResourceAccumulatorBean} with the given Object.
     *
     * @param obj the Object being compared to this.
     * @return true if the given Object is a {@link JavaScriptResourceAccumulatorBean} and contains
     *  the same content this one
     */
    @Override
    public boolean equals(final Object obj) {

        if (obj instanceof JavaScriptResourceAccumulatorBean) {

            final JavaScriptResourceAccumulatorBean compareObj =
                (JavaScriptResourceAccumulatorBean) obj;

            final ArrayList<Resource> theseResourcePaths =
                new ArrayList<Resource>(allResourcePaths);

            final ArrayList<Resource> compareToResourcePaths =
                new ArrayList<Resource>(compareObj.getResources());

            return theseResourcePaths.equals(compareToResourcePaths);
        }

        return false;
    }

    /**
     * Provides a hash code for this {@link JavaScriptResourceAccumulatorBean} so that when one
     * resourceAccumulator.equals(this) then resourceAccumulator.hashCode() also equals
     * this.hashCode().
     *
     * @return a hash code for this {@link JavaScriptResourceAccumulatorBean}
     */
    @Override
    public int hashCode() {
        return SEED + getResources().hashCode();
    }

    /**
     * @return a String representing the content of this ResourceAccumulator.
     */
    @Override
    public String toString() {

        return allResourcePaths.toString();
    }

    /**
     * If bundling is enabled then {@link Resource}s are combined and returned as a single
     * {@link Resource}.
     * If bundling is disabled then a list of ordered {@link Resource}s is returned.
     *
     * @return a list containing either a single {@link Resource} bundle or the complete set of
     * ordered {@link Resource}s.
     */
    public List<Resource> getResources() {

        final List<Resource> result = doGetResources();

        return result;
    }

    private List<Resource> doGetResources() {
        List<Resource> allResources = new ArrayList<Resource>(allResourcePaths);

        if (isBundlingEnabled() && (allResources.size() > 1)) {

            try {
                final Resource bundle = getBundleFactory().getBundle(allResources);
                LOGGER.debug("bundle: " + bundle);
                allResources = Collections.singletonList(bundle);

            } catch (final IOException e) {
                // If we can't bundle we continue on and return the unbundled list of resources.
                LOGGER.error("Couldn't read file to perform bundling, "
                        + "returning unbundled resources. " + e);
            }
        }

        return allResources;
    }

    /**
     * @param allResourcePaths  the allResourcePaths to set
     */
    protected void setAllResourcePaths(final Deque<Resource> allResourcePaths) {

        this.allResourcePaths = allResourcePaths;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void accumulate(final List<Resource> resolvedPaths) {

        if (!resolvedPaths.isEmpty()) {

            // We reverse the files so that when they're output in the HTML page
            // they're still in the original order.
            Collections.reverse(resolvedPaths);

            // for each found resource decide if we want to add it to the final list
            for (final Resource newResource : resolvedPaths) {

                LOGGER.debug("looking at: " + newResource.getNewPath());

                if (!hasAVariantAlreadyBeenStored(newResource, allResourcePaths)) {

                    LOGGER.debug("Adding " + newResource.getNewPath() + " to the list.");
                    allResourcePaths.push(newResource);
                }
            }
        }

    }

    private boolean isPartOfAPackage(final Resource requestedResource) {
        return requestedResource.getOriginalPath().endsWith(getJavascriptPackageKeyword());
    }

    private boolean hasAVariantAlreadyBeenStored(final Resource newResource,
            final Deque<Resource> accumulatedResources) {

        boolean alreadyStoredAVariant = false;

        // loop through the final list to see if our new ones are already there
        for (final Resource existingResource : accumulatedResources) {

            LOGGER.debug("Comparing with: " + existingResource.getNewPath());

            if (hasPackageResourceBeenStored(newResource, existingResource)
                    || hasSingleResourceBeenStored(newResource, existingResource)) {

                alreadyStoredAVariant = true;
                break;
            }
        }

        return alreadyStoredAVariant;
    }

    private boolean hasSingleResourceBeenStored(final Resource newResource,
            final Resource existingResource) {

        if (isPartOfAPackage(newResource)) {
            return false;
        }

        // It's not a Javascript package request
        return existingResource.getOriginalPath().equals(newResource.getOriginalPath());
    }

    private boolean hasPackageResourceBeenStored(final Resource newResource,
            final Resource existingResource) {

        if (!isPartOfAPackage(newResource)) {
            return false;
        }

        // compare on filename
        final int lastSlashPos = existingResource.getNewPath().lastIndexOf("/");
        final String existingFilename = existingResource.getNewPath().substring(
                lastSlashPos);

        // new request is for same filename as already in the list
        return newResource.getNewPath().endsWith(existingFilename);
    }

    /**
     * @return the javascriptPackageKeyword
     */
    public String getJavascriptPackageKeyword() {

        return javascriptPackageKeyword;
    }

    /**
     * @return true if the {@link ResourceAccumulator}s returned by the factory
     *         should support bundling. False otherwise.
     */
    @Override
    public boolean isBundlingEnabled() {
        return bundlingEnabled;
    }

    /**
     * @return the bundleFactory
     */
    protected JavaScriptBundleFactory getBundleFactory() {

        return bundleFactory;
    }

}
