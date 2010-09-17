package au.com.sensis.mobile.crf.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;

import au.com.sensis.wireless.common.volantis.devicerepository.api.Device;

/**
 * Implementation of {@link PathRestrictedResourceSelector}
 * that is restricted to paths that have a prefix matching {@link #getPathPrefix()}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class PathPrefixRestrictedResourceSelectorBean
        implements PathRestrictedResourceSelector {

    private static final Logger LOGGER
        = Logger.getLogger(PathPrefixRestrictedResourceSelectorBean.class);

    private final String pathPrefix;
    private final ResourceSelector resourceSelector;

    /**
     * Constructor.
     *
     * @param pathPrefix
     *            Prefix that requested resource paths must have for this
     *            {@link ResourceSelector} to be
     *            interested in handling the request.
     * @param resourceSelector
     *            {@link ResourceSelector} to delegate
     *            requests to if the requested resource starts with the
     *            pathPrefix.
     */
    public PathPrefixRestrictedResourceSelectorBean(final String pathPrefix,
            final ResourceSelector resourceSelector) {
        if (StringUtils.isBlank(pathPrefix)) {
            throw new IllegalArgumentException(
                    "pathPrefix must not be blank: '" + pathPrefix + "'");
        }
        Validate.notNull(resourceSelector, "resourceSelector must not be null");
        this.pathPrefix = pathPrefix;
        this.resourceSelector = resourceSelector;
    }

    /**
     * If {@link #isInterestedIn(String)} returns true, then delegates to the
     * {@link ResourceSelector} that was passed to the
     * constructor. Else a {@link NullMappedResourcePath} is returned.
     *
     * {@inheritDoc}
     */
    @Override
    public List<MappedResourcePath> getAllResourcePaths(final Device device,
            final String requestedResourcePath) {

        if (isInterestedIn(requestedResourcePath)) {
            debugLogInterest(requestedResourcePath);

            return getResourceSelector().getAllResourcePaths(device,
                    requestedResourcePath);
        } else {
            debugLogDisinterest(requestedResourcePath);

            final List<MappedResourcePath> result = new ArrayList<MappedResourcePath>();
            result.add(new NullMappedResourcePath(requestedResourcePath));
            return result;
        }

    }

    /**
     * If {@link #isInterestedIn(String)} returns true, then delegates to the
     * {@link ResourceSelector} that was passed to the
     * constructor. Else a {@link NullMappedResourcePath} is returned.
     *
     * {@inheritDoc}
     */
    @Override
    public MappedResourcePath getResourcePath(final Device device,
            final String requestedResourcePath) {

        if (isInterestedIn(requestedResourcePath)) {
            debugLogInterest(requestedResourcePath);

            return getResourceSelector().getResourcePath(device,
                    requestedResourcePath);
        } else {
            debugLogDisinterest(requestedResourcePath);

            return new NullMappedResourcePath(requestedResourcePath);
        }
    }

    /**
     * If {@link #isInterestedIn(String)} returns true, then delegates to the
     * {@link ResourceSelector} that was passed to the
     * constructor. Else a {@link NullMappedResourcePath} is returned.
     *
     * {@inheritDoc}
     */
    @Override
    public MappedResourcePath getResourcePathWithExtensions(
            final Device device, final String requestedResourcePath,
            final String[] wildcardExtensions) throws IllegalArgumentException {

        if (isInterestedIn(requestedResourcePath)) {
            debugLogInterest(requestedResourcePath);

            return getResourceSelector().getResourcePathWithExtensions(device,
                    requestedResourcePath, wildcardExtensions);
        } else {
            debugLogDisinterest(requestedResourcePath);

            return new NullMappedResourcePath(requestedResourcePath);
        }

    }

    private ResourceSelector getResourceSelector() {
        return resourceSelector;
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
        toStringBuilder.append("resourceSelector", getResourceSelector());
        return toStringBuilder.toString();
    }

    private void debugLogInterest(final String requestedResourcePath) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(requestedResourcePath
                    + " is of interest. Delegating to wrapped resourceSelector.");
        }
    }

    private void debugLogDisinterest(final String requestedResourcePath) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(requestedResourcePath
                    + " is NOT of interest. Returning NullMappedResourcePath.");
        }
    }
}
