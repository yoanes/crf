package au.com.sensis.mobile.crf.presentation.tag;

import au.com.sensis.mobile.crf.config.DeploymentVersion;
import au.com.sensis.mobile.crf.service.ResourceResolutionWarnLogger;
import au.com.sensis.mobile.crf.service.ResourceResolverEngine;

/**
 * Simple encapsulation of the singleton collaborators of a JSP tag.
 * The tags will programmatically retrieve this from the Spring context.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class TagDependencies {

    private final ResourceResolverEngine
        resourceResolverEngine;
    private final DeploymentVersion deploymentVersion;
    private final String clientPathPrefix;
    private final ResourceResolutionWarnLogger resourceResolutionWarnLogger;

    /**
     * @param resourceResolverEngine
     *            {@link ResourceResolverEngine} to use to
     *            map {@link #getHref()} to concrete resource(s).
     *
     * @param deploymentVersion
     *            {@link DeploymentVersion} of the current deployment.
     * @param clientPathPrefix
     *            Prefix to be used for the final paths that a client (web
     *            browser) will see.
     * @param resourceResolutionWarnLogger
     *            Special {@link ResourceResolutionWarnLogger} for logging
     *            warnings.
     */
    public TagDependencies(
            final ResourceResolverEngine
                resourceResolverEngine,
            final DeploymentVersion deploymentVersion,
            final String clientPathPrefix,
            final ResourceResolutionWarnLogger resourceResolutionWarnLogger) {
        this.resourceResolverEngine =
                resourceResolverEngine;
        this.deploymentVersion = deploymentVersion;
        this.clientPathPrefix = clientPathPrefix;
        this.resourceResolutionWarnLogger = resourceResolutionWarnLogger;
    }

    /**
     * @return {@link ResourceResolverEngine}
     */
    public ResourceResolverEngine getResourceResolverEngine() {
        return resourceResolverEngine;
    }

    /**
     * @return {@link DeploymentVersion}
     */
    public DeploymentVersion getDeploymentVersion() {
        return deploymentVersion;
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

}
