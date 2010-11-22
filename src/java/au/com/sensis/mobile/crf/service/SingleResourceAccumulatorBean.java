package au.com.sensis.mobile.crf.service;

import java.util.ArrayList;
import java.util.List;


/**
 * Used for single {@link Resource}s, such as images and JSPs, which don't require
 * accumulation of multiple {@link Resource}s.
 *
 * See {@link ResourceAccumulatorBean} if you require accumulation of multiple {@link Resource}s.
 *
 * @author Tony Filipe
 */
public class SingleResourceAccumulatorBean extends AbstractResourceAccumulatorBean {

    /**
     * An initial value for a <code>hashCode</code>, to which is added contributions
     * from fields. Using a non-zero value decreases collisons of <code>hashCode</code>
     * values.
     */
    private static final int SEED = 257;


    private List<Resource> allResources;


    /**
     * Constructs an initialised ResourceAccumulator.
     */
    public SingleResourceAccumulatorBean() {

        allResources = new ArrayList<Resource>();
    }


    /**
     * Will only retain the first list of {@link Resource}s passed in via a call to this
     * accumulate method. Subsequent calls to accumulate will be ignored, as for single
     * {@link Resource}s we only want the first most-specific match.
     *
     * @param resolvedResourcesForGroup to be added to the list.
     */
    public void accumulate(final List<Resource> resolvedResourcesForGroup) {

        if (allResources.isEmpty() && !resolvedResourcesForGroup.isEmpty()) {
            allResources = resolvedResourcesForGroup;
        }
    }

    /**
     * Compares this {@link SingleResourceAccumulatorBean} with the given Object.
     *
     * @param obj the Object being compared to this.
     * @return true if the given Object is a {@link SingleResourceAccumulatorBean} and contains
     *  the same content this one
     */
    @Override
    public boolean equals(final Object obj) {

        if (obj instanceof SingleResourceAccumulatorBean) {

            final SingleResourceAccumulatorBean compareObj = (SingleResourceAccumulatorBean) obj;

            return allResources.equals(compareObj.getResources());
        }

        return false;
    }

    /**
     * Provides a hash code for this {@link SingleResourceAccumulatorBean} so that when one
     * resourceAccumulator.equals(this) then resourceAccumulator.hashCode() also equals
     * this.hashCode().
     *
     * @return a hash code for this {@link SingleResourceAccumulatorBean}
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

        return allResources.toString();
    }

    /**
     * @return the combined list of {@link Resource}s.
     */
    public List<Resource> getResources() {
        final List<Resource> result = doGetResources();

        addResourcesToResourceResolutionTreeIfEnabled(result);

        return result;
    }

    /**
     * Work horse method for {@link #getResources()}.
     *
     * @return the combined list of {@link Resource}s.
     */
    protected List<Resource> doGetResources() {

        return allResources;
    }

}
