package au.com.sensis.mobile.crf.presentation.tag;

import au.com.sensis.mobile.crf.config.DeploymentVersion;
import au.com.sensis.mobile.crf.service.ResourceResolutionWarnLogger;
import au.com.sensis.mobile.crf.service.ResourceSelector;

/**
 * Simple memento encapsulating the singleton collaborators of a JSP tag.
 * The tags will retrieve this from the Spring context.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class TagCollaboratorsMemento {

    private final ResourceSelector
        resourceSelector;
    private final DeploymentVersion deploymentVersion;
    private final String clientPathPrefix;
    private final ResourceResolutionWarnLogger resourceResolutionWarnLogger;

    /**
     * @param resourceSelector
     *            {@link ResourceSelector} to use to
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
    public TagCollaboratorsMemento(
            final ResourceSelector
                resourceSelector,
            final DeploymentVersion deploymentVersion,
            final String clientPathPrefix,
            final ResourceResolutionWarnLogger resourceResolutionWarnLogger) {
        this.resourceSelector =
                resourceSelector;
        this.deploymentVersion = deploymentVersion;
        this.clientPathPrefix = clientPathPrefix;
        this.resourceResolutionWarnLogger = resourceResolutionWarnLogger;
    }

    /**
     * @return {@link ResourceSelector}
     */
    public ResourceSelector getResourceSelector() {
        return resourceSelector;
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
