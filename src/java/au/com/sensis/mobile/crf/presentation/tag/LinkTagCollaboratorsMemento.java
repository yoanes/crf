package au.com.sensis.mobile.crf.presentation.tag;

import au.com.sensis.mobile.crf.config.DeploymentVersion;
import au.com.sensis.mobile.crf.service.ResourceSelector;
import au.com.sensis.mobile.crf.service.CssBundleFactory;
import au.com.sensis.mobile.crf.service.ResourceResolutionWarnLogger;

/**
 * Simple memento encapsulating the singleton collaborators of a {@link LinkTag}.
 * The {@link LinkTag} will retrieve this from the Spring context.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class LinkTagCollaboratorsMemento extends TagCollaboratorsMemento {
    /**
     * Standard name of the {@link LinkTagCollaboratorsMemento} in the Spring context.
     */
    public static final String BEAN_NAME = "crf.linkTagCollaboratorsMemento";

    private final CssBundleFactory cssBundleFactory;
    /**
     * @param resourceSelector
     * {@link ResourceSelector} to use to map
     * {@link #getHref()} to concrete resource(s).
     *
     * @param deploymentVersion
     *            {@link DeploymentVersion} of the current deployment.
     * @param cssBundleFactory
     *            {@link CssBundleFactory} to use to get CSS Bundles.
     * @param clientPathPrefix Prefix to be used for the final paths
     *            that a client (web browser) will see.
     * @param resourceResolutionWarnLogger
     *            Special {@link ResourceResolutionWarnLogger} for logging
     *            warnings.
     */
    public LinkTagCollaboratorsMemento(
            final ResourceSelector
                resourceSelector,
                final DeploymentVersion deploymentVersion,
            final CssBundleFactory cssBundleFactory,
            final String clientPathPrefix,
            final ResourceResolutionWarnLogger resourceResolutionWarnLogger) {
        super(resourceSelector, deploymentVersion, clientPathPrefix,
                resourceResolutionWarnLogger);

        this.cssBundleFactory = cssBundleFactory;
    }

    /**
     * @return {@link CssBundleFactory}
     */
    public CssBundleFactory getCssBundleFactory() {
        return cssBundleFactory;
    }
}
