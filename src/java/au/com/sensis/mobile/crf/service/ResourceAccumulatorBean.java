package au.com.sensis.mobile.crf.service;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;


/**
 * Accumulates all resolved {@link Resource}s, ordering them so that the most-specific is
 * output last (and thus overrides more generic versions of the same resources).
 *
 * @author Tony Filipe
 */
public class ResourceAccumulatorBean implements ResourceAccumulator {

    /**
     * An initial value for a <code>hashCode</code>, to which is added contributions
     * from fields. Using a non-zero value decreases collisions of <code>hashCode</code>
     * values.
     */
    private static final int SEED = 21;

    private final Deque<Resource> allResourcePaths;


    /**
     * Constructs an initialised ResourceAccumulator.
     */
    public ResourceAccumulatorBean() {

        allResourcePaths = new ArrayDeque<Resource>();
    }


    /**
     * Accumulates all of the given {@link Resource}s together.
     * @param resolvedPaths to be added to the combined list
     */
    public void accumulate(final List<Resource> resolvedPaths) {

        if (!resolvedPaths.isEmpty()) {

            Collections.reverse(resolvedPaths);

            for (final Resource currPath : resolvedPaths) {
                allResourcePaths.push(currPath);
            }
        }
    }

    /**
     * Compares this {@link ResourceAccumulatorBean} with the given Object.
     *
     * @param obj the Object being compared to this.
     * @return true if the given Object is a {@link ResourceAccumulatorBean} and contains
     *  the same content this one
     */
    @Override
    public boolean equals(final Object obj) {

        if (obj instanceof ResourceAccumulatorBean) {

            final ResourceAccumulatorBean compareObj = (ResourceAccumulatorBean) obj;

            final ArrayList<Resource> theseResourcePaths =
                new ArrayList<Resource>(allResourcePaths);

            final ArrayList<Resource> compareToResourcePaths =
                new ArrayList<Resource>(compareObj.getResources());

            return theseResourcePaths.equals(compareToResourcePaths);
        }

        return false;
    }

    /**
     * Provides a hash code for this {@link ResourceAccumulatorBean} so that when one
     * resourceAccumulator.equals(this) then resourceAccumulator.hashCode() also equals
     * this.hashCode().
     *
     * @return a hash code for this {@link ResourceAccumulatorBean}
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

    private List<Resource> doGetResources() {
        return new ArrayList<Resource>(allResourcePaths);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isBundlingEnabled() {
        return false;
    }
}
