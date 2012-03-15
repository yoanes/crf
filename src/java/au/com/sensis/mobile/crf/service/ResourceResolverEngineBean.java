package au.com.sensis.mobile.crf.service;

import java.util.List;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

import au.com.sensis.devicerepository.Device;
import au.com.sensis.mobile.crf.exception.ResourceResolutionRuntimeException;

/**
 * Standard implementation of {@link ResourceResolverEngine}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class ResourceResolverEngineBean implements
ResourceResolverEngine {

    private static final Logger LOGGER = Logger.getLogger(
            ResourceResolverEngineBean.class);


    private final ResourceResolver resourceResolver;
    private final ResourceResolutionWarnLogger resourceResolutionWarnLogger;

    /**
     * Constructor.
     *
     * @param resourceResolver
     *            {@link ResourceResolver} to resolve requested resource paths
     *            to real resource paths for a specific group.
     * @param resourceResolutionWarnLogger {@link ResourceResolutionWarnLogger} to
     *            use to log warnings.
     */
    public ResourceResolverEngineBean(
            final ResourceResolver resourceResolver,
            final ResourceResolutionWarnLogger resourceResolutionWarnLogger) {

        Validate.notNull(resourceResolver, "resourceResolver must not be null");
        Validate.notNull(resourceResolutionWarnLogger,
                "resourceResolutionWarnLogger must not be null");

        this.resourceResolver = resourceResolver;
        this.resourceResolutionWarnLogger = resourceResolutionWarnLogger;
    }

    /**
     * {@inheritDoc}
     */
    public Resource getResource(final Device device,
            final String requestedResourcePath) throws ResourceResolutionRuntimeException {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Looking for resource '" + requestedResourcePath
                    + "' for device '" + device + "'.");
        }


        //        final Iterator<Group> matchingGroupIterator = getMatchingGroupIterator(device,
        //                requestedResourcePath);
        //        while (matchingGroupIterator.hasNext()) {
        //
        //            final Group currGroup = matchingGroupIterator.next();

        //            debugLogCheckingGroup(requestedResourcePath, currGroup);

        final List<Resource> resources =
                //resolve(requestedResourcePath, currGroup, null);
                getResourceResolver().resolve(requestedResourcePath, device);

        if (!resources.isEmpty()) {
            debugLogResourcesFound(resources);

            warnIfMultipleResourcesFound(requestedResourcePath, resources);

            return resources.get(0);
        }
        //        }

        debugLogNoResourcesFound(requestedResourcePath);

        // No resource found.
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Resource> getAllResources(final Device device,
            final String requestedResourcePath) throws ResourceResolutionRuntimeException {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Looking for resource '" + requestedResourcePath
                    + "' for device '" + device + "'.");
        }

        final List<Resource> resources =
                getResourceResolver().resolve(requestedResourcePath, device);

        if (resources.isEmpty()) {
            debugLogNoResourcesFound(requestedResourcePath);
        }

        return resources;

        //        final ResourceAccumulator accumulatedResults =
        //            getResourceResolver().getResourceAccumulator(requestedResourcePath);

        //        if (LOGGER.isDebugEnabled()) {
        //            LOGGER.debug("Looking for resource '" + requestedResourcePath
        //                    + "' for device '" + device + "'.");
        //        }
        //
        //        final Iterator<Group> matchingGroupIterator = getMatchingGroupIterator(
        //                device, requestedResourcePath);
        //        while (matchingGroupIterator.hasNext()) {
        //
        //            final Group currGroup = matchingGroupIterator.next();
        //
        //            debugLogCheckingGroup(requestedResourcePath, currGroup);
        //
        //            resolve(requestedResourcePath, currGroup, accumulatedResults);
        //        }
        //
        //        if (accumulatedResults.getResources().isEmpty()) {
        //            debugLogNoResourcesFound(requestedResourcePath);
        //        }
        //
        //        return new ArrayList<Resource>(accumulatedResults.getResources());
    }

    //    private List<Resource> resolve(
    //            final String requestedResourcePath, final Group currGroup,
    //            final ResourceAccumulator results) {
    //
    //        return getResourceResolver().resolve(
    //                requestedResourcePath, currGroup, results);
    //    }


    private void warnIfMultipleResourcesFound(
            final String requestedResourcePath,
            final List<Resource> resources) {
        if ((resources.size() > 1)
                && getResourceResolutionWarnLogger().isWarnEnabled()) {
            getResourceResolutionWarnLogger().warn(
                    "Requested resource '"
                            + requestedResourcePath
                            + "' resolved to multiple resources when only one was requested. "
                            + "Will only return the first. Total found: "
                            + resources + ".");
        }
    }

    private void debugLogResourcesFound(
            final List<Resource> resources) {

        if (LOGGER.isDebugEnabled()) {
            for (final Resource resource : resources) {
                LOGGER.debug("Resource found: '"
                        + resource.getNewPath() + "'");
            }
        }
    }

    private void debugLogNoResourcesFound(final String requestedResourcePath) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("No resources found for requested path: '"
                    + requestedResourcePath + "'");
        }
    }

    private ResourceResolver getResourceResolver() {
        return resourceResolver;
    }

    private ResourceResolutionWarnLogger getResourceResolutionWarnLogger() {
        return resourceResolutionWarnLogger;
    }


}
