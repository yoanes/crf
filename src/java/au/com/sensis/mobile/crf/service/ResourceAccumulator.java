package au.com.sensis.mobile.crf.service;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;


/**
 * @author Tony Filipe
 */
public class ResourceAccumulator {

    private Deque<Resource> allResourcePaths;


    public ResourceAccumulator() {

        allResourcePaths = new ArrayDeque<Resource>();
    }


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
