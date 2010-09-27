package au.com.sensis.mobile.crf.presentation.tag;

import au.com.sensis.mobile.crf.config.DeploymentMetadata;
import au.com.sensis.mobile.crf.service.ResourceResolutionWarnLogger;
import au.com.sensis.mobile.crf.service.ResourceResolverEngine;

/**
 * Simple encapsulation of the singleton collaborators of an {@link ImageTag}.
 * The {@link LinkTag} will retrieve this from the Spring context.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class ImageTagDependencies extends TagDependencies {
    /**
     * Standard name of the {@link ImageTagDependencies} in the Spring context.
     */
    public static final String BEAN_NAME = "crf.imageTagDependencies";

    /**
     * @param resourceResolverEngine
     *            {@link ResourceResolverEngine} to use to
     *            resolve {@link #getHref()} to concrete resource(s).
     * @param deploymentMetadata
     *            {@link DeploymentMetadata} of the current deployment.
     * @param clientPathPrefix Prefix to be used for the final paths
     *            that a client (web browser) will see.
     * @param resourceResolutionWarnLogger
     *            Special {@link ResourceResolutionWarnLogger} for logging
     *            warnings.
     */
    public ImageTagDependencies(
            final ResourceResolverEngine
                resourceResolverEngine,
            final DeploymentMetadata deploymentMetadata,
            final String clientPathPrefix,
            final ResourceResolutionWarnLogger resourceResolutionWarnLogger) {
        super(resourceResolverEngine, deploymentMetadata, clientPathPrefix,
                resourceResolutionWarnLogger);
    }

}
