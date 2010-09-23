package au.com.sensis.mobile.crf.presentation.tag;

import au.com.sensis.mobile.crf.config.DeploymentVersion;
import au.com.sensis.mobile.crf.service.ResourceResolutionWarnLogger;
import au.com.sensis.mobile.crf.service.ResourceResolverEngine;
import au.com.sensis.mobile.crf.service.ScriptBundleFactory;

/**
 * Simple memento encapsulating the singleton collaborators of a {@link ScriptTag}.
 * The {@link ScriptTag} will retrieve this from the Spring context.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class ScriptTagDependencies extends TagDependencies {
    /**
     * Standard name of the {@link ScriptTagDependencies} in the Spring context.
     */
    public static final String BEAN_NAME = "crf.scriptTagDependencies";

    private final ScriptBundleFactory scriptBundleFactory;

    /**
     * @param resourceResolverEngine
     *            {@link ResourceResolverEngine} to use to map {@link #getHref()} to
     *            concrete resource(s).
     * @param deploymentVersion
     *            {@link DeploymentVersion} of the current deployment.
     * @param scriptBundleFactory
     *            {@link ScriptBundleFactory} to use to get Script Bundles.
     * @param clientPathPrefix
     *            Prefix to be used for the final paths that a client (web
     *            browser) will see.
     * @param resourceResolutionWarnLogger
     *            Special {@link ResourceResolutionWarnLogger} for logging
     *            warnings.
     */
    public ScriptTagDependencies(
            final ResourceResolverEngine resourceResolverEngine,
            final DeploymentVersion deploymentVersion,
            final ScriptBundleFactory scriptBundleFactory,
            final String clientPathPrefix,
            final ResourceResolutionWarnLogger resourceResolutionWarnLogger) {
        super(resourceResolverEngine, deploymentVersion, clientPathPrefix,
                resourceResolutionWarnLogger);

        this.scriptBundleFactory = scriptBundleFactory;
    }

    /**
     * @return {@link ScriptBundleFactory}
     */
    public ScriptBundleFactory getScriptBundleFactory() {
        return scriptBundleFactory;
    }

}