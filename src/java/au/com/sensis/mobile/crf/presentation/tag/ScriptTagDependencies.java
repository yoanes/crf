package au.com.sensis.mobile.crf.presentation.tag;

import au.com.sensis.mobile.crf.config.DeploymentMetadata;
import au.com.sensis.mobile.crf.service.ResourceResolutionWarnLogger;
import au.com.sensis.mobile.crf.service.ResourceResolverEngine;

/**
 * Simple encapsulation of the singleton collaborators of a {@link ScriptTag}.
 * The {@link ScriptTag} will retrieve this from the Spring context.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class ScriptTagDependencies extends TagDependencies {
    /**
     * Standard name of the {@link ScriptTagDependencies} in the Spring context.
     */
    public static final String BEAN_NAME = "crf.scriptTagDependencies";

    private final JspContextBundleTagStack jspContextBundleTagStack;

    /**
     * @param resourceResolverEngine
     *            {@link ResourceResolverEngine} to use to resolve {@link #getHref()} to
     *            concrete resource(s).
     * @param deploymentMetadata
     *            {@link DeploymentMetadata} of the current deployment.
     * @param clientPathPrefix
     *            Prefix to be used for the final paths that a client (web
     *            browser) will see.
     * @param resourceResolutionWarnLogger
     *            Special {@link ResourceResolutionWarnLogger} for logging
     *            warnings.
     * @param jspContextBundleTagStack {@link JspContextBundleTagStack} that stores
     *            parent {@link AbstractBundleTag}s.
     */
    public ScriptTagDependencies(
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
