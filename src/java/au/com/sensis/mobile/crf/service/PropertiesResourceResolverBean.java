package au.com.sensis.mobile.crf.service;

import java.io.File;

import org.apache.log4j.Logger;

import au.com.sensis.mobile.crf.config.DeploymentMetadata;
import au.com.sensis.mobile.crf.config.Group;

/**
 * {@link ResourceResolver} that resolves abstract property file paths to real property file paths.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class PropertiesResourceResolverBean extends AbstractResourceResolver {

    private static final Logger LOGGER = Logger.getLogger(PropertiesResourceResolverBean.class);

    /**
     * Constructor.
     *
     * @param abstractResourceExtension
     *            Extension of resources (eg. "css" or "crf") that this class
     *            knows how to resolve.
     * @param rootResourcesDir
     *            Root directory where the real resources that this resolver
     *            handles are stored.
     * @param resourceResolutionWarnLogger
     *            {@link ResourceResolutionWarnLogger} to use to log warnings.
     * @param deploymentMetadata
     *            {@link DeploymentMetadata} of the deployed app.
     */
    public PropertiesResourceResolverBean(final String abstractResourceExtension,
            final File rootResourcesDir,
            final ResourceResolutionWarnLogger resourceResolutionWarnLogger,
            final DeploymentMetadata deploymentMetadata) {
        super(abstractResourceExtension, rootResourcesDir, resourceResolutionWarnLogger,
                deploymentMetadata);
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
