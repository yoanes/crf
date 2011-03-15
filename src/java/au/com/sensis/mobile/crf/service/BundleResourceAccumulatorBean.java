package au.com.sensis.mobile.crf.service;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

import org.apache.log4j.Logger;


/**
 * Resolves multiple matching {@link Resource}s, combining them into a single
 * {@link Resource} bundle.
 *
 * @author Tony Filipe
 */
public class BundleResourceAccumulatorBean implements ResourceAccumulator {

    private static final Logger LOGGER = Logger.getLogger(BundleResourceAccumulatorBean.class);

    private final BundleFactory bundleFactory = new BundleFactory();
    /**
     * An initial value for a <code>hashCode</code>, to which is added contributions
     * from fields. Using a non-zero value decreases collisions of <code>hashCode</code>
     * values.
     */
    private static final int SEED = 24;


    private final Deque<Resource> allResourcePaths;


    /**
     * Constructs an initialised ResourceAccumulator.
     */
    public BundleResourceAccumulatorBean() {

        allResourcePaths = new ArrayDeque<Resource>();
    }


    /**
     * Compares this {@link BundleResourceAccumulatorBean} with the given Object.
     *
     * @param obj the Object being compared to this.
     * @return true if the given Object is a {@link BundleResourceAccumulatorBean} and contains
     *  the same content this one
     */
    @Override
    public boolean equals(final Object obj) {

        if (obj instanceof BundleResourceAccumulatorBean) {

            final BundleResourceAccumulatorBean compareObj = (BundleResourceAccumulatorBean) obj;

            final ArrayList<Resource> theseResourcePaths =
                new ArrayList<Resource>(allResourcePaths);

            final ArrayList<Resource> compareToResourcePaths =
                new ArrayList<Resource>(compareObj.getResources());

            return theseResourcePaths.equals(compareToResourcePaths);
        }

        return false;
    }

    /**
     * Provides a hash code for this {@link BundleResourceAccumulatorBean} so that when one
     * resourceAccumulator.equals(this) then resourceAccumulator.hashCode() also equals
     * this.hashCode().
     *
     * @return a hash code for this {@link BundleResourceAccumulatorBean}
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
     * @return the combined list of {@link Resource}s.
     */
    public List<Resource> getResources() {
        final List<Resource> result = doGetResources();

        return result;
    }

    /**
     * @return a single {@link Resource} bundle of all the combined resources.
     */
    private List<Resource> doGetResources() {

        List<Resource> allResources = new ArrayList<Resource>(allResourcePaths);

        if (!allResources.isEmpty()) {
            try {
                final Resource bundle = getBundleFactory().getBundle(allResources);

                allResources = Collections.singletonList(bundle);

            } catch (final Exception e) {
                // If we can't bundle we continue on and return the unbundled list of resources.
                LOGGER.error("Couldn't create bundle for resources: " + allResources
                        + ". Returning unbundled resources. ", e);
            }
        }
        return allResources;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void accumulate(final List<Resource> resolvedPaths) {

        if (!resolvedPaths.isEmpty()) {

            Collections.reverse(resolvedPaths);

            for (final Resource currPath : resolvedPaths) {
                allResourcePaths.push(currPath);
            }
        }
    }

    /**
     * @return the bundleFactory
     */
    private BundleFactory getBundleFactory() {

        return bundleFactory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isBundlingEnabled() {
        return true;
    }
}
