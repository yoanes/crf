package au.com.sensis.mobile.crf.service;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;


/**
 * @author Tony Filipe
 */
public class BundleResourceAccumulatorBean extends AbstractResourceAccumulatorBean {

    /**
     * An initial value for a <code>hashCode</code>, to which is added contributions
     * from fields. Using a non-zero value decreases collisons of <code>hashCode</code>
     * values.
     */
    private static final int SEED = 24;


    private Deque<Resource> allResourcePaths;


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

        addResourcesToResourceResolutionTreeIfEnabled(result);

        return result;
    }

    private List<Resource> doGetResources() {

        return new ArrayList<Resource>(allResourcePaths);
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

            Collections.reverse(resolvedPaths);

            for (final Resource currPath : resolvedPaths) {
                allResourcePaths.push(currPath);
            }
        }
    }

}
