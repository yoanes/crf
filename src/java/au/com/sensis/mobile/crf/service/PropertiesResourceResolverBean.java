package au.com.sensis.mobile.crf.service;

import java.io.File;

import org.apache.log4j.Logger;

import au.com.sensis.mobile.crf.config.Group;

/**
 * {@link ResourceResolver} that resolves abstract property file paths to real property file paths.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class PropertiesResourceResolverBean extends AbstractMultipleResourceResolver {

    private static final Logger LOGGER = Logger.getLogger(PropertiesResourceResolverBean.class);

    /**
     * Constructor.
     *
     * @param commonParams
     *            Holds the common parameters used in constructing all {@link ResourceResolver}s.
     * @param abstractResourceExtension
     *            Extension of resources (eg. "css" or "crf") that this class
     *            knows how to resolve.
     * @param rootResourcesDir
     *            Root directory where the real resources that this resolver
     *            handles are stored.
     * @param resourceAccumulatorFactory
     *            Provides a {@link ResourceAccumulator} for this
     *            {@link ResourceResolver}.
     */
    public PropertiesResourceResolverBean(final ResourceResolverCommonParamHolder commonParams,
            final String abstractResourceExtension,
            final File rootResourcesDir,
            final ResourceAccumulatorFactory resourceAccumulatorFactory) {

        super(commonParams, abstractResourceExtension, rootResourcesDir,
               resourceAccumulatorFactory);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getRealResourcePathExtension() {
        return ".properties";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Logger getLogger() {
        return LOGGER;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ResourceAccumulator createResourceAccumulator() {

        return getResourceAccumulatorFactory().getPropertiesResourceAccumulator();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    protected String getDebugResourceTypeName() {
        return "properties";
    }

    /**
     * Overrides the standard implementation to ensure that version is not inserted into the
     * path, given that properties files are server side resources and will never be visible
     * to the clients (and hence client side caching will never be an issue).
     *
     * {@inheritDoc}
     */
    @Override
    protected String insertGroupNameAndDeploymentVersionIntoPath(
            final String requestedResourcePath, final Group group) {
        return group.getName()
        + RESOURCE_SEPARATOR + requestedResourcePath;
    }

}
