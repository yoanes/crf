package au.com.sensis.mobile.crf.service;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;


/**
 * @author Tony Filipe
 */
public class ResourceAccumulator {

    /**
     * An initial value for a <code>hashCode</code>, to which is added contributions
     * from fields. Using a non-zero value decreases collisons of <code>hashCode</code>
     * values.
     */
    private static final int SEED = 23;


    private Deque<Resource> allResourcePaths;


    /**
     * Constructs an initialised ResourceAccumulator.
     */
    public ResourceAccumulator() {

        allResourcePaths = new ArrayDeque<Resource>();
    }


    /**
     * Compares this {@link ResourceAccumulator} with the given Object.
     *
     * @param obj the Object being compared to this.
     * @return true if the given Object is a {@link ResourceAccumulator} and contains
     *  the same content this one
     */
    @Override
    public boolean equals(final Object obj) {

        if (obj instanceof ResourceAccumulator) {

            final ResourceAccumulator compareObj = (ResourceAccumulator) obj;

            final ArrayList<Resource> theseResourcePaths =
                new ArrayList<Resource>(allResourcePaths);

            final ArrayList<Resource> compareToResourcePaths =
                new ArrayList<Resource>(compareObj.getAllResourcePaths());

            return theseResourcePaths.equals(compareToResourcePaths);
        }

        return false;
    }

    /**
     * Provides a hash code for this {@link ResourceAccumulator} so that when one
     * resourceAccumulator.equals(this) then resourceAccumulator.hashCode() also equals
     * this.hashCode().
     *
     * @return a hash code for this {@link ResourceAccumulator}
     */
    @Override
    public int hashCode() {
        return SEED + getAllResourcePaths().hashCode();
    }

    /**
     * @return a String representing the content of this ResourceAccumulator.
     */
    @Override
    public String toString() {

        return allResourcePaths.toString();
    }

    /**
     * @return the allResourcePaths
     */
    protected Deque<Resource> getAllResourcePaths() {

        return allResourcePaths;
    }

    /**
     * @param allResourcePaths  the allResourcePaths to set
     */
    protected void setAllResourcePaths(final Deque<Resource> allResourcePaths) {

        this.allResourcePaths = allResourcePaths;
    }

}
