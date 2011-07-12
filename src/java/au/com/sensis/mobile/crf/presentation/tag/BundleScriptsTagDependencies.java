package au.com.sensis.mobile.crf.presentation.tag;

import java.io.File;

import au.com.sensis.mobile.crf.config.DeploymentMetadata;
import au.com.sensis.mobile.crf.service.ResourceResolutionWarnLogger;

/**
 * Simple encapsulation of the singleton collaborators of a {@link BundleScriptsTag}.
 * The {@link BundleScriptsTag} will retrieve this from the Spring context.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
// NOTE: we don't extend TagDependencies since we don't and should never need the inherited
// ResourceResolverEngine. The BundleScriptsTag is layered on top of the ScriptsTag as a
// postprocessing step.
// TODO: TagDependencies should probably be renamed to indicate it's purpose better.
// ResourceResolverTagDependencies? Alternatively, remove ResourceResolverEngine from
// TagDependencies and push it down into the relevant classes.
public class BundleScriptsTagDependencies {

    /**
     * Standard name of the {@link BundleScriptsTagDependencies} in the Spring context.
     */
    public static final String BEAN_NAME = "crf.bundleScriptsTagDependencies";

    private final DeploymentMetadata deploymentMetadata;
    private final String clientPathPrefix;
    private final ResourceResolutionWarnLogger resourceResolutionWarnLogger;
    private final BundleScriptsTagCache bundleScriptsTagCache;
    private final File rootResourcesDir;

    /**
     * @param deploymentMetadata
     *            {@link DeploymentMetadata} of the current deployment.
     * @param clientPathPrefix
     *            Prefix to be used for the final paths that a client (web browser) will see.
     * @param resourceResolutionWarnLogger
     *            Special {@link ResourceRe,solutionWarnLogger} for logging warnings.
     * @param bundleScriptsTagCache
     *            {@link BundleScriptsTagCache} to store the results of bundle creation.
     * @param rootResourcesDir
     *            Root directory where the bundles that this tag creates should be stored.
     */
    public BundleScriptsTagDependencies(final DeploymentMetadata deploymentMetadata,
            final String clientPathPrefix,
            final ResourceResolutionWarnLogger resourceResolutionWarnLogger,
            final BundleScriptsTagCache bundleScriptsTagCache,
            final File rootResourcesDir) {

        this.deploymentMetadata = deploymentMetadata;
        this.clientPathPrefix = clientPathPrefix;
        this.resourceResolutionWarnLogger = resourceResolutionWarnLogger;
        this.bundleScriptsTagCache = bundleScriptsTagCache;
        this.rootResourcesDir = rootResourcesDir;
    }

    /**
     * @return the deploymentMetadata
     */
    public DeploymentMetadata getDeploymentMetadata() {
        return deploymentMetadata;
    }

    /**
     * @return the clientPathPrefix
     */
    public String getClientPathPrefix() {
        return clientPathPrefix;
    }

    /**
     * @return the resourceResolutionWarnLogger
     */
    public ResourceResolutionWarnLogger getResourceResolutionWarnLogger() {
        return resourceResolutionWarnLogger;
    }

    /**
     * @return the rootResourcesDir
     */
    public File getRootResourcesDir() {
        return rootResourcesDir;
    }

    /**
     * @return the bundleScriptsTagCache
     */
    public BundleScriptsTagCache getBundleScriptsTagCache() {
        return bundleScriptsTagCache;
    }

}
