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
import au.com.sensis.mobile.crf.exception.ContentRenderingFrameworkRuntimeException;
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
    private ResourceResolutionWarnLogger resourceResolutionWarnLogger;

    /**
     * Constructor.
     *
     * @param configurationFactory
     *            {@link ConfigurationFactory} to obtain
     *            the {@link au.com.sensis.mobile.crf.config.UiConfiguration} from.
     * @param resourcePathMapper
     *            {@link ResourcePathMapper} to map requested paths to real,
     *            device specific paths.
     */
    public ResourceSelectorBean(
            final ConfigurationFactory configurationFactory,
            final ResourcePathMapper resourcePathMapper) {
        Validate.notNull(configurationFactory, "configurationFactory must not be null");
        Validate.notNull(resourcePathMapper, "resourcePathMapper must not be null");

        this.configurationFactory = configurationFactory;
        this.resourcePathMapper = resourcePathMapper;
    }

    /**
     * {@inheritDoc}
     */
    public MappedResourcePath getResourcePath(final Device device,
            final String requestedResourcePath) {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Looking for resource '" + requestedResourcePath
                    + "' for device '" + device + "'.");
        }

        final Iterator<Group> matchingGroupIterator = getMatchingGroupIterator(device);
        while (matchingGroupIterator.hasNext()) {

            final Group currGroup = matchingGroupIterator.next();

            debugLogCheckingGroup(currGroup);

            final MappedResourcePath mappedResourcePath =
                    getMappedResourcePath(requestedResourcePath, currGroup);

            // TODO: looks weird and it is. One more step towards splitting MappedResourcePath
            // into two.
            final MappedResourcePath foundMappedResourcePath =
                    mappedResourcePath.resolveToSingle();
            if (foundMappedResourcePath != null) {
                debugLogSingleResourceFoundAndReturningIt(foundMappedResourcePath);

                return foundMappedResourcePath;
            }
        }

        // No resource found.
        return null;
    }

    private Iterator<Group> getMatchingGroupIterator(final Device device) {
        return getConfigurationFactory().getUiConfiguration()
                .matchingGroupIterator(device);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<MappedResourcePath> getAllResourcePaths(final Device device,
            final String requestedResourcePath) {

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

        return new ArrayList<MappedResourcePath>(allResourcePaths);
    }

    private void accumulateGroupResources(
            final MappedResourcePath mappedResourcePath,
            final Deque<MappedResourcePath> allResourcePaths) {

        try {
            final List<MappedResourcePath> resolvedPaths = mappedResourcePath.resolve();
            if (!resolvedPaths.isEmpty()) {

                debugLogResourceFoundAddingToList(resolvedPaths);

                Collections.reverse(resolvedPaths);

                for (final MappedResourcePath currPath : resolvedPaths) {
                    allResourcePaths.push(currPath);
                }
            }
        } catch (final IOException e) {
            throw new ContentRenderingFrameworkRuntimeException("TODO. Arrrghhh !!! ", e);
        }
    }

    private MappedResourcePath getMappedResourcePath(
            final String requestedResourcePath, final Group currGroup) {
        return getResourcePathMapper().mapResourcePath(
                requestedResourcePath, currGroup);
    }

    private void debugLogResourceFoundAddingToList(
            final List<MappedResourcePath> mappedResourcePaths) {

        if (LOGGER.isDebugEnabled()) {
            for (final MappedResourcePath mappedResourcePath : mappedResourcePaths) {
                LOGGER.debug("Resource found! Adding it to list: '"
                        + mappedResourcePath.getNewResourcePath() + "'");
            }
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
    public ResourceResolutionWarnLogger getResourceResolutionWarnLogger() {
        return resourceResolutionWarnLogger;
    }

    /**
     * @param resourceResolutionWarnLogger the resourceResolutionWarnLogger to set
     */
    // TODO: pass via constructor, not setter.
    public void setResourceResolutionWarnLogger(
            final ResourceResolutionWarnLogger resourceResolutionWarnLogger) {
        this.resourceResolutionWarnLogger = resourceResolutionWarnLogger;
    }

    private void debugLogCheckingGroup(final Group currGroup) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Looking for resource in matching group: " + currGroup);
        }
    }

    private void debugLogSingleResourceFoundAndReturningIt(
            final MappedResourcePath mappedResourcePath) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Resource found! Returning it: '"
                    + mappedResourcePath.getNewResourcePath() + "'");
        }
    }
}
