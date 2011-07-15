package au.com.sensis.mobile.crf.presentation.tag;

import au.com.sensis.mobile.crf.config.DeploymentMetadata;
import au.com.sensis.mobile.crf.service.ResourceResolutionWarnLogger;
import au.com.sensis.mobile.crf.service.ResourceResolverEngine;

/**
 * Simple encapsulation of the singleton collaborators of a {@link LinkTag}.
 * The {@link LinkTag} will retrieve this from the Spring context.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class LinkTagDependencies extends TagDependencies {
    /**
     * Standard name of the {@link LinkTagDependencies} in the Spring context.
     */
    public static final String BEAN_NAME = "crf.linkTagDependencies";

    private final JspContextBundleTagStack jspContextBundleTagStack;

    /**
     * @param resourceResolverEngine
     *            {@link ResourceResolverEngine} to use to resolve
     *            {@link #getHref()} to concrete resource(s).
     * @param deploymentMetadata
     *            {@link DeploymentMetadata} of the current deployment.
     * @param clientPathPrefix
     *            Prefix to be used for the final paths that a client (web
     *            browser) will see.
     * @param resourceResolutionWarnLogger
     *            Special {@link ResourceResolutionWarnLogger} for logging
     *            warnings.
     * @param jspContextBundleTagStack {@link JspContextBundleTagStack} that stores
     *            parent {@link BundleTag}s.
     */
    public LinkTagDependencies(
            final ResourceResolverEngine resourceResolverEngine,
            final DeploymentMetadata deploymentMetadata,
            final String clientPathPrefix,
            final ResourceResolutionWarnLogger resourceResolutionWarnLogger,
            final JspContextBundleTagStack jspContextBundleTagStack) {
        super(resourceResolverEngine, deploymentMetadata, clientPathPrefix,
                resourceResolutionWarnLogger);

        this.jspContextBundleTagStack = jspContextBundleTagStack;
    }

    /**
     * @return the jspContextBundleTagStack
     */
    public JspContextBundleTagStack getJspContextBundleTagStack() {
        return jspContextBundleTagStack;
    }

}
