package au.com.sensis.mobile.crf.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import au.com.sensis.mobile.crf.config.Group;

/**
 * {@link ResourceResolver} that delegates to a list of other
 * {@link ResourceResolver}s.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class DelegatingResourceResolverBean implements ResourceResolver {

    // logger not final to allow it to be swapped out during unit testing.
    private static Logger logger = Logger.getLogger(DelegatingResourceResolverBean.class);

    private final List<ResourceResolver> resourceResolvers;

    /**
     * @param resourceResolvers
     *            List of {@link ResourceResolver}s to delegate to.
     */
    public DelegatingResourceResolverBean(
            final List<ResourceResolver> resourceResolvers) {
        if (resourceResolvers == null) {
            throw new IllegalArgumentException(
                    "resourceResolvers must not be null");
        }

        if (resourceResolvers.isEmpty() && logger.isEnabledFor(Level.WARN)) {
            logger.warn("resourceResolvers is empty. "
                    + "This DelegatingResourceResolverBean will always "
                    + "return an empty list of resources.");
        }
        this.resourceResolvers = resourceResolvers;
    }


    /**
     * Delegates to each {@link ResourceResolver} from the List that was passed
     * to the constructor. The process stops when either of the following occurs:
     * <ol>
     * <li>A non-empty list of {@link Resource}s is found.</li>
     * <li>The end of the list is reached without the previous condition being
     * reached. In this case, an empty list is returned.</li>
     * </ol>
     *
     * {@inheritDoc}
     */
    @Override
    public List<Resource> resolve(
            final String requestedResourcePath, final Group group) throws IOException {
        for (final ResourceResolver resourceResolver : getResourceResolvers()) {
            // TODO: needs to check if resourceResolver is interested instead of checking
            // foundResourcePaths.isEmpty?.
            final List<Resource> foundResourcePaths
                = resourceResolver.resolve(requestedResourcePath, group);
            if (!foundResourcePaths.isEmpty()) {
                return foundResourcePaths;
            }
        }
        return new ArrayList<Resource>();
    }


    /**
     * @return the resourceResolvers passed to the constructor.
     */
    private List<ResourceResolver> getResourceResolvers() {
        return resourceResolvers;
    }
}
