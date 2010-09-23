package au.com.sensis.mobile.crf.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;

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
     * @throws IOException
     */
    @Override
    public List<Resource> getAllResourcePaths(final Device device,
            final String requestedResourcePath) throws IOException {

        if (isInterestedIn(requestedResourcePath)) {
            debugLogInterest(requestedResourcePath);

            return getResourceResolverEngine().getAllResourcePaths(device,
                    requestedResourcePath);
        } else {
            debugLogDisinterest(requestedResourcePath);

            return new ArrayList<Resource>();
        }

    }

    /**
     * If {@link #isInterestedIn(String)} returns true, then delegates to the
     * {@link ResourceResolverEngine} that was passed to the
     * constructor. Else null is returned.
     *
     * {@inheritDoc}
     * @throws IOException
     */
    @Override
    public Resource getResourcePath(final Device device,
            final String requestedResourcePath) throws IOException {

        if (isInterestedIn(requestedResourcePath)) {
            debugLogInterest(requestedResourcePath);

            return getResourceResolverEngine().getResourcePath(device,
                    requestedResourcePath);
        } else {
            debugLogDisinterest(requestedResourcePath);

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

    private void debugLogDisinterest(final String requestedResourcePath) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(requestedResourcePath
                    + " is NOT of interest. Returning null.");
        }
    }
}
