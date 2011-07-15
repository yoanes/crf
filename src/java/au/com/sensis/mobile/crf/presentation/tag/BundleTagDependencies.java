package au.com.sensis.mobile.crf.presentation.tag;

import java.io.File;

import au.com.sensis.mobile.crf.config.DeploymentMetadata;
import au.com.sensis.mobile.crf.service.ResourceResolutionWarnLogger;

/**
 * Simple encapsulation of the singleton collaborators of a {@link BundleTag}.
 * The {@link BundleTag} will retrieve this from the Spring context.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
// NOTE: we don't extend TagDependencies since we don't and should never need the inherited
// ResourceResolverEngine. The BundleScriptsTag is layered on top of the ScriptsTag as an
// optional postprocessing step.
// TODO: TagDependencies should probably be renamed to indicate it's purpose better.
// ResourceResolverTagDependencies? Alternatively, remove ResourceResolverEngine from
// TagDependencies and push it down into the relevant classes.
public class BundleTagDependencies {

    private final DeploymentMetadata deploymentMetadata;
    private final String clientPathPrefix;
    private final ResourceResolutionWarnLogger resourceResolutionWarnLogger;
    private final BundleTagCache bundleTagCache;
    private final File rootResourcesDir;
    private final JspContextBundleTagStack jspContextBundleTagStack;


    /**
     * @param deploymentMetadata
     *            {@link DeploymentMetadata} of the current deployment.
     * @param clientPathPrefix
     *            Prefix to be used for the final paths that a client (web browser) will see.
     * @param resourceResolutionWarnLogger
     *            Special {@link ResourceResolutionWarnLogger} for logging warnings.
     * @param bundleTagCache
     *            {@link BundleTagCache} to store the results of bundle creation.
     * @param rootResourcesDir
     *            Root directory where the bundles that this tag creates should be stored.
     * @param jspContextBundleTagStack {@link JspContextBundleTagStack} used for storing
     *            {@link BundleTag}s to be exposed to child tags.
     */
    public BundleTagDependencies(final DeploymentMetadata deploymentMetadata,
            final String clientPathPrefix,
            final ResourceResolutionWarnLogger resourceResolutionWarnLogger,
            final BundleTagCache bundleTagCache,
            final File rootResourcesDir,
            final JspContextBundleTagStack jspContextBundleTagStack) {

        this.deploymentMetadata = deploymentMetadata;
        this.clientPathPrefix = clientPathPrefix;
        this.resourceResolutionWarnLogger = resourceResolutionWarnLogger;
        this.bundleTagCache = bundleTagCache;
        this.rootResourcesDir = rootResourcesDir;
        this.jspContextBundleTagStack = jspContextBundleTagStack;

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
     * @return the bundleTagCache
     */
    public BundleTagCache getBundleTagCache() {
        return bundleTagCache;
    }

    /**
     * @return the jspContextBundleTagStack
     */
    public JspContextBundleTagStack getJspContextBundleTagStack() {
        return jspContextBundleTagStack;
    }

}
