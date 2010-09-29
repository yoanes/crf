package au.com.sensis.mobile.crf.service;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

import au.com.sensis.mobile.crf.config.ConfigurationFactory;
import au.com.sensis.mobile.crf.config.Group;
import au.com.sensis.mobile.crf.exception.ResourceResolutionRuntimeException;
import au.com.sensis.wireless.common.volantis.devicerepository.api.Device;

/**
 * Standard implementation of {@link ResourceResolverEngine}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class ResourceResolverEngineBean implements
        ResourceResolverEngine {

    private static final Logger LOGGER = Logger.getLogger(
            ResourceResolverEngineBean.class);

    private final ConfigurationFactory configurationFactory;
    private final ResourceResolver resourceResolver;
    private final ResourceResolutionWarnLogger resourceResolutionWarnLogger;

    /**
     * Constructor.
     *
     * @param configurationFactory
     *            {@link ConfigurationFactory} to obtain
     *            the {@link au.com.sensis.mobile.crf.config.UiConfiguration} from.
     * @param resourceResolver
     *            {@link ResourceResolver} to resolve requested resource paths
     *            to real resource paths for a specific group.
     * @param resourceResolutionWarnLogger {@link ResourceResolutionWarnLogger} to
     *            use to log warnings.
     */
    public ResourceResolverEngineBean(
            final ConfigurationFactory configurationFactory,
            final ResourceResolver resourceResolver,
            final ResourceResolutionWarnLogger resourceResolutionWarnLogger) {

        Validate.notNull(configurationFactory, "configurationFactory must not be null");
        Validate.notNull(resourceResolver, "resourceResolver must not be null");
        Validate.notNull(resourceResolutionWarnLogger,
                "resourceResolutionWarnLogger must not be null");

        this.configurationFactory = configurationFactory;
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

        final Iterator<Group> matchingGroupIterator = getMatchingGroupIterator(device,
                requestedResourcePath);
        while (matchingGroupIterator.hasNext()) {

            final Group currGroup = matchingGroupIterator.next();

            debugLogCheckingGroup(requestedResourcePath, currGroup);

            final List<Resource> resources =
                    resolve(requestedResourcePath, currGroup);

            if (!resources.isEmpty()) {
                debugLogResourcesFound(resources);

                warnIfMultipleResourcesFound(requestedResourcePath, resources);

                return resources.get(0);
            }
        }

        debugLogNoResourcesFound(requestedResourcePath);

        // No resource found.
        return null;
    }

    private Iterator<Group> getMatchingGroupIterator(final Device device,
            final String requestedResourcePath) {
        return getConfigurationFactory().getUiConfiguration(requestedResourcePath)
                .matchingGroupIterator(device);
    }

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

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Resource> getAllResources(final Device device,
            final String requestedResourcePath) throws ResourceResolutionRuntimeException {

        final Deque<Resource> allResourcePaths = new ArrayDeque<Resource>();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Looking for resource '" + requestedResourcePath
                    + "' for device '" + device + "'.");
        }

        final Iterator<Group> matchingGroupIterator = getMatchingGroupIterator(
                device, requestedResourcePath);
        while (matchingGroupIterator.hasNext()) {

            final Group currGroup = matchingGroupIterator.next();

            debugLogCheckingGroup(requestedResourcePath, currGroup);

            accumulateGroupResources(
                    resolve(requestedResourcePath, currGroup),
                    allResourcePaths);
        }

        if (allResourcePaths.isEmpty()) {
            debugLogNoResourcesFound(requestedResourcePath);
        }

        return new ArrayList<Resource>(allResourcePaths);
    }

    private void accumulateGroupResources(
            final List<Resource> resolvedPaths,
            final Deque<Resource> allResourcePaths) {

        if (!resolvedPaths.isEmpty()) {

            debugLogResourcesFound(resolvedPaths);

            Collections.reverse(resolvedPaths);

            for (final Resource currPath : resolvedPaths) {
                allResourcePaths.push(currPath);
            }
        }
    }

    private List<Resource> resolve(
            final String requestedResourcePath, final Group currGroup) {
        return getResourceResolver().resolve(
                requestedResourcePath, currGroup);
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

    private ConfigurationFactory getConfigurationFactory() {
        return configurationFactory;
    }

    private ResourceResolver getResourceResolver() {
        return resourceResolver;
    }

    private ResourceResolutionWarnLogger getResourceResolutionWarnLogger() {
        return resourceResolutionWarnLogger;
    }

    private void debugLogCheckingGroup(final String requestedResourcePath,
            final Group currGroup) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Looking for '" + requestedResourcePath
                    + "' in matching group: " + currGroup);
        }
    }
}
