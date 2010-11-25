package au.com.sensis.mobile.crf.service;

import org.apache.commons.lang.Validate;

import au.com.sensis.mobile.crf.config.ConfigurationFactory;
import au.com.sensis.mobile.crf.config.DeploymentMetadata;


/**
 * A convenience class that holds the common parameters used in constructing
 * {@link ResourceResolver}s.
 *
 * @author Tony Filipe
 */
public class ResourceResolverCommonParamHolder {

    private final ResourceResolutionWarnLogger resourceResolutionWarnLogger;
    private final DeploymentMetadata deploymentMetadata;
    private final ConfigurationFactory configurationFactory;
    private final ResourceCache resourceCache;

    /**
     * Constructs a CommonResourceResolverParamHolder with the common parameters required
     * for constructing {@link ResourceResolver}s.
     *
     * @param resourceResolutionWarnLogger
     *            {@link ResourceResolutionWarnLogger} to use to log warnings.
     * @param deploymentMetadata {@link DeploymentMetadata} of the deployed app.
     * @param configurationFactory
     *            For accessing the {@link au.com.sensis.mobile.crf.config.UiConfiguration}.
     * @param resourceCache {@link ResourceCache} for caching {@link Resource}s.
     */
    public ResourceResolverCommonParamHolder(
            final ResourceResolutionWarnLogger resourceResolutionWarnLogger,
            final DeploymentMetadata deploymentMetadata,
            final ConfigurationFactory configurationFactory,
            final ResourceCache resourceCache) {

        validateResourceResolutionWarnLogger(resourceResolutionWarnLogger);
        validateDeploymentMetadata(deploymentMetadata);
        validateConfigurationFactory(configurationFactory);
        validateResourceCache(resourceCache);

        this.resourceResolutionWarnLogger = resourceResolutionWarnLogger;
        this.deploymentMetadata = deploymentMetadata;
        this.configurationFactory = configurationFactory;
        this.resourceCache = resourceCache;
    }

    private void validateResourceCache(final ResourceCache resourceCache) {

        if (resourceCache == null) {
            throw new IllegalArgumentException("resourceCache must not be null");
        }
    }
    private void validateConfigurationFactory(final ConfigurationFactory configurationFactory) {

        if (configurationFactory == null) {
            throw new IllegalArgumentException("configurationFactory must not be null");
        }
    }

    private void validateResourceResolutionWarnLogger(
            final ResourceResolutionWarnLogger resourceResolutionWarnLogger) {
        Validate.notNull(resourceResolutionWarnLogger,
        "resourceResolutionWarnLogger must not be null");
    }

    private void validateDeploymentMetadata(final DeploymentMetadata deploymentMetadata) {
        Validate.notNull(deploymentMetadata, "deploymentMetadata must not be null");
    }

    /**
     * @return the resourceResolutionWarnLogger
     */
    public ResourceResolutionWarnLogger getResourceResolutionWarnLogger() {

        return resourceResolutionWarnLogger;
    }

    /**
     * @return the deploymentMetadata
     */
    public DeploymentMetadata getDeploymentMetadata() {

        return deploymentMetadata;
    }

    /**
     * @return the configurationFactory
     */
    public ConfigurationFactory getConfigurationFactory() {

        return configurationFactory;
    }

    /**
     * @return the resourceCache
     */
    public ResourceCache getResourceCache() {
        return resourceCache;
    }
}
