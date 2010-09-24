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
     * Delegates to the first {@link ResourceResolver} from the List for which
     * {@link ResourceResolver#supports(String)} returns true.
     *
     * {@inheritDoc}
     */
    @Override
    public List<Resource> resolve(final String requestedResourcePath,
            final Group group) throws IOException {
        for (final ResourceResolver resourceResolver : getResourceResolvers()) {
            if (resourceResolver.supports(requestedResourcePath)) {
                return resourceResolver.resolve(requestedResourcePath, group);
            }
        }
        return new ArrayList<Resource>();
    }

    /**
     * Returns true if any of the resolvers passed to the constructor support the requested
     * path.
     *
     * {@inheritDoc}
     */
    @Override
    public boolean supports(final String requestedResourcePath) {
        for (final ResourceResolver resourceResolver : getResourceResolvers()) {
            if (resourceResolver.supports(requestedResourcePath)) {
                return true;
            }
        }

        return false;
    }

    /**
     * @return the resourceResolvers passed to the constructor.
     */
    private List<ResourceResolver> getResourceResolvers() {
        return resourceResolvers;
    }
}
