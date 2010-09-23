package au.com.sensis.mobile.crf.presentation.tag;

import au.com.sensis.mobile.crf.config.DeploymentVersion;
import au.com.sensis.mobile.crf.service.ResourceResolverEngine;
import au.com.sensis.mobile.crf.service.ResourceResolutionWarnLogger;

/**
 * Simple memento encapsulating the singleton collaborators of a {@link LinkTag}.
 * The {@link LinkTag} will retrieve this from the Spring context.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class ImageTagCollaboratorsMemento extends TagCollaboratorsMemento {
    /**
     * Standard name of the {@link ImageTagCollaboratorsMemento} in the Spring context.
     */
    public static final String BEAN_NAME = "crf.imageTagCollaboratorsMemento";

    /**
     * @param resourceResolverEngine
     *            {@link ResourceResolverEngine} to use to
     *            map {@link #getHref()} to concrete resource(s).
     *
     * @param deploymentVersion
     *            {@link DeploymentVersion} of the current deployment.
     * @param clientPathPrefix Prefix to be used for the final paths
     *            that a client (web browser) will see.
     * @param resourceResolutionWarnLogger
     *            Special {@link ResourceResolutionWarnLogger} for logging
     *            warnings.
     */
    public ImageTagCollaboratorsMemento(
            final ResourceResolverEngine
                resourceResolverEngine,
            final DeploymentVersion deploymentVersion,
            final String clientPathPrefix,
            final ResourceResolutionWarnLogger resourceResolutionWarnLogger) {
        super(resourceResolverEngine, deploymentVersion, clientPathPrefix,
                resourceResolutionWarnLogger);
    }

}
