package au.com.sensis.mobile.crf.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import au.com.sensis.mobile.crf.config.Group;

/**
 * {@link ResourceResolver} that delegates to a chain of responsibility list of other
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
     *            Chain of responsibility list of {@link ResourceResolver}s.
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
     * Delegates to each {@link ResourceResolver} from a chain of
     * responsibility. The chain stops when either of the following occurs:
     * <ol>
     * <li>A non-null {@link Resource}is found.</li>
     * <li>The end of the chain is reached without the previous condition being
     * reached. In this case, null is returned.</li>
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
     * @return the resourceResolvers
     */
    private List<ResourceResolver> getResourceResolvers() {
        return resourceResolvers;
    }
}
