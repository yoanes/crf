package au.com.sensis.mobile.crf.service;

import java.io.IOException;
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
import au.com.sensis.wireless.common.volantis.devicerepository.api.Device;

/**
 * Standard implementation of {@link ResourceSelector}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class ResourceSelectorBean implements
        ResourceSelector {

    private static final Logger LOGGER = Logger.getLogger(
            ResourceSelectorBean.class);

    private final ConfigurationFactory configurationFactory;
    private final ResourcePathMapper resourcePathMapper;
    private final ResourceResolutionWarnLogger resourceResolutionWarnLogger;

    /**
     * Constructor.
     *
     * @param configurationFactory
     *            {@link ConfigurationFactory} to obtain
     *            the {@link au.com.sensis.mobile.crf.config.UiConfiguration} from.
     * @param resourcePathMapper
     *            {@link ResourcePathMapper} to map requested paths to real,
     *            device specific paths.
     * @param resourceResolutionWarnLogger {@link ResourceResolutionWarnLogger} to
     *            use to log warnings.
     */
    public ResourceSelectorBean(
            final ConfigurationFactory configurationFactory,
            final ResourcePathMapper resourcePathMapper,
            final ResourceResolutionWarnLogger resourceResolutionWarnLogger) {

        Validate.notNull(configurationFactory, "configurationFactory must not be null");
        Validate.notNull(resourcePathMapper, "resourcePathMapper must not be null");
        Validate.notNull(resourceResolutionWarnLogger,
                "resourceResolutionWarnLogger must not be null");

        this.configurationFactory = configurationFactory;
        this.resourcePathMapper = resourcePathMapper;
        this.resourceResolutionWarnLogger = resourceResolutionWarnLogger;
    }

    /**
     * {@inheritDoc}
     * @throws IOException
     */
    public MappedResourcePath getResourcePath(final Device device,
            final String requestedResourcePath) throws IOException {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Looking for resource '" + requestedResourcePath
                    + "' for device '" + device + "'.");
        }

        final Iterator<Group> matchingGroupIterator = getMatchingGroupIterator(device);
        while (matchingGroupIterator.hasNext()) {

            final Group currGroup = matchingGroupIterator.next();

            debugLogCheckingGroup(currGroup);

            final List<MappedResourcePath> mappedResourcePaths =
                    getMappedResourcePath(requestedResourcePath, currGroup);

            if (!mappedResourcePaths.isEmpty()) {
                debugLogResourcesFound(mappedResourcePaths);

                warnIfMultipleResourcesFound(requestedResourcePath, mappedResourcePaths);

                return mappedResourcePaths.get(0);
            }
        }

        debugLogNoResourcesFound(requestedResourcePath);

        // No resource found.
        return null;
    }

    private Iterator<Group> getMatchingGroupIterator(final Device device) {
        return getConfigurationFactory().getUiConfiguration()
                .matchingGroupIterator(device);
    }

    private void warnIfMultipleResourcesFound(
            final String requestedResourcePath,
            final List<MappedResourcePath> mappedResourcePaths) {
        if ((mappedResourcePaths.size() > 1)
                && getResourceResolutionWarnLogger().isWarnEnabled()) {
            getResourceResolutionWarnLogger().warn(
                    "Requested resource '"
                    + requestedResourcePath
                    + "' resolved to multiple resources when only one was requested. "
                    + "Will only return the first. Total found: "
                    + mappedResourcePaths + ".");
        }
    }

    /**
     * {@inheritDoc}
     * @throws IOException
     */
    @Override
    public List<MappedResourcePath> getAllResourcePaths(final Device device,
            final String requestedResourcePath) throws IOException {

        final Deque<MappedResourcePath> allResourcePaths = new ArrayDeque<MappedResourcePath>();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Looking for resource '" + requestedResourcePath
                    + "' for device '" + device + "'.");
        }

        final Iterator<Group> matchingGroupIterator = getMatchingGroupIterator(device);
        while (matchingGroupIterator.hasNext()) {

            final Group currGroup = matchingGroupIterator.next();

            debugLogCheckingGroup(currGroup);

            accumulateGroupResources(
                    getMappedResourcePath(requestedResourcePath, currGroup),
                    allResourcePaths);
        }

        debugLogNoResourcesFound(requestedResourcePath);

        return new ArrayList<MappedResourcePath>(allResourcePaths);
    }

    private void accumulateGroupResources(
            final List<MappedResourcePath> resolvedPaths,
            final Deque<MappedResourcePath> allResourcePaths) {

        if (!resolvedPaths.isEmpty()) {

            debugLogResourcesFound(resolvedPaths);

            Collections.reverse(resolvedPaths);

            for (final MappedResourcePath currPath : resolvedPaths) {
                allResourcePaths.push(currPath);
            }
        }
    }

    private List<MappedResourcePath> getMappedResourcePath(
            final String requestedResourcePath, final Group currGroup) throws IOException {
        return getResourcePathMapper().resolve(
                requestedResourcePath, currGroup);
    }

    private void debugLogResourcesFound(
            final List<MappedResourcePath> mappedResourcePaths) {

        if (LOGGER.isDebugEnabled()) {
            for (final MappedResourcePath mappedResourcePath : mappedResourcePaths) {
                LOGGER.debug("Resource found: '"
                        + mappedResourcePath.getNewResourcePath() + "'");
            }
        }
    }

    private void debugLogNoResourcesFound(final String requestedResourcePath) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("No resources found for requsted path: '"
                    + requestedResourcePath + "'");
        }
    }

    /**
     * @return the configurationFactory
     */
    private ConfigurationFactory getConfigurationFactory() {
        return configurationFactory;
    }

    /**
     * @return the resourcePathMapper
     */
    private ResourcePathMapper getResourcePathMapper() {
        return resourcePathMapper;
    }

    /**
     * @return the resourceResolutionWarnLogger
     */
    private ResourceResolutionWarnLogger getResourceResolutionWarnLogger() {
        return resourceResolutionWarnLogger;
    }

    private void debugLogCheckingGroup(final Group currGroup) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Looking for resource in matching group: " + currGroup);
        }
    }
}
