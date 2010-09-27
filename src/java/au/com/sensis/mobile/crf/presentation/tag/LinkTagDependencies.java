package au.com.sensis.mobile.crf.presentation.tag;

import au.com.sensis.mobile.crf.config.DeploymentMetadata;
import au.com.sensis.mobile.crf.service.CssBundleFactory;
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

    private final CssBundleFactory cssBundleFactory;
    /**
     * @param resourceResolverEngine
     *            {@link ResourceResolverEngine} to use to resolve
     *            {@link #getHref()} to concrete resource(s).
     * @param deploymentMetadata
     *            {@link DeploymentMetadata} of the current deployment.
     * @param cssBundleFactory
     *            {@link CssBundleFactory} to use to get CSS Bundles.
     * @param clientPathPrefix
     *            Prefix to be used for the final paths that a client (web
     *            browser) will see.
     * @param resourceResolutionWarnLogger
     *            Special {@link ResourceResolutionWarnLogger} for logging
     *            warnings.
     */
    public LinkTagDependencies(
            final ResourceResolverEngine resourceResolverEngine,
            final DeploymentMetadata deploymentMetadata,
            final CssBundleFactory cssBundleFactory,
            final String clientPathPrefix,
            final ResourceResolutionWarnLogger resourceResolutionWarnLogger) {
        super(resourceResolverEngine, deploymentMetadata, clientPathPrefix,
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
