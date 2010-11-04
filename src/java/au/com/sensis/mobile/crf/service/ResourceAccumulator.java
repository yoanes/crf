package au.com.sensis.mobile.crf.service;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;


/**
 * @author Tony Filipe
 */
public class ResourceAccumulator {

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
     * Returns a String representing the content of this ResourceAccumulator.
     */
    @Override
    public String toString() {

        return allResourcePaths.toString();
    }

    /**
     * Delegates to the Object.hashCode() method.
     */
    @Override
    public int hashCode() {
        return super.hashCode();
    };

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
