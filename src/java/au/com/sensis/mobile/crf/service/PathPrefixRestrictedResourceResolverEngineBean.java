package au.com.sensis.mobile.crf.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;

import au.com.sensis.mobile.crf.exception.ResourceResolutionRuntimeException;
import au.com.sensis.wireless.common.volantis.devicerepository.api.Device;

/**
 * Implementation of {@link PathRestrictedResourceResolverEngine}
 * that is restricted to paths that have a prefix matching {@link #getPathPrefix()}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class PathPrefixRestrictedResourceResolverEngineBean
        implements PathRestrictedResourceResolverEngine {

    private static final Logger LOGGER
        = Logger.getLogger(PathPrefixRestrictedResourceResolverEngineBean.class);

    private final String pathPrefix;
    private final ResourceResolverEngine resourceResolverEngine;

    /**
     * Constructor.
     *
     * @param pathPrefix
     *            Prefix that requested resource paths must have for this
     *            {@link ResourceResolverEngine} to be
     *            interested in handling the request.
     * @param resourceResolverEngine
     *            {@link ResourceResolverEngine} to delegate
     *            requests to if the requested resource starts with the
     *            pathPrefix.
     */
    public PathPrefixRestrictedResourceResolverEngineBean(final String pathPrefix,
            final ResourceResolverEngine resourceResolverEngine) {
        if (StringUtils.isBlank(pathPrefix)) {
            throw new IllegalArgumentException(
                    "pathPrefix must not be blank: '" + pathPrefix + "'");
        }
        Validate.notNull(resourceResolverEngine, "resourceResolverEngine must not be null");
        this.pathPrefix = pathPrefix;
        this.resourceResolverEngine = resourceResolverEngine;
    }

    /**
     * If {@link #isInterestedIn(String)} returns true, then delegates to the
     * {@link ResourceResolverEngine} that was passed to the
     * constructor. Else an empty {@link List} is returned.
     *
     * {@inheritDoc}
     */
    @Override
    public List<Resource> getAllResources(final Device device,
            final String requestedResourcePath) throws ResourceResolutionRuntimeException {

        if (isInterestedIn(requestedResourcePath)) {
            debugLogInterest(requestedResourcePath);

            return getResourceResolverEngine().getAllResources(device,
                    requestedResourcePath);
        } else {
            debugLogDisinterestReturningEmptyList(requestedResourcePath);

            return new ArrayList<Resource>();
        }

    }

    /**
     * If {@link #isInterestedIn(String)} returns true, then delegates to the
     * {@link ResourceResolverEngine} that was passed to the
     * constructor. Else null is returned.
     *
     * {@inheritDoc}
     */
    @Override
    public Resource getResource(final Device device,
            final String requestedResourcePath) throws ResourceResolutionRuntimeException {

        if (isInterestedIn(requestedResourcePath)) {
            debugLogInterest(requestedResourcePath);

            return getResourceResolverEngine().getResource(device,
                    requestedResourcePath);
        } else {
            debugLogDisinterestReturningNull(requestedResourcePath);

            return null;
        }
    }

    private ResourceResolverEngine getResourceResolverEngine() {
        return resourceResolverEngine;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isInterestedIn(final String requestedResourcePath) {
        return (requestedResourcePath != null)
                && requestedResourcePath.startsWith(getPathPrefix());
    }

    /**
     * @return the path prefix that requested resource paths must have for
     *         {@link #isInterestedIn(String)} to return true.
     */
    public String getPathPrefix() {
        return pathPrefix;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final ToStringBuilder toStringBuilder = new ToStringBuilder(this);
        toStringBuilder.append("pathPrefix", getPathPrefix());
        toStringBuilder.append("resourceResolverEngine", getResourceResolverEngine());
        return toStringBuilder.toString();
    }

    private void debugLogInterest(final String requestedResourcePath) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(requestedResourcePath
                    + " is of interest. Delegating to wrapped resourceResolverEngine.");
        }
    }

    private void debugLogDisinterestReturningNull(final String requestedResourcePath) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(requestedResourcePath
                    + " is NOT of interest. Returning null.");
        }
    }

    private void debugLogDisinterestReturningEmptyList(final String requestedResourcePath) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(requestedResourcePath
                    + " is NOT of interest. Returning empty list.");
        }
    }
}
