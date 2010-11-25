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

    // TODO: refactor ResourceResolutionWarnLogger out of here since only
    // AbstractMultipleResourceResolver uses it now.
    private final ResourceAccumulatorFactory resourceAccumulatorFactory;
    private final ConfigurationFactory configurationFactory;

    /**
     * Constructs a CommonResourceResolverParamHolder with the common parameters required
     * for constructing {@link ResourceResolver}s.
     *
     * @param resourceResolutionWarnLogger
     *            {@link ResourceResolutionWarnLogger} to use to log warnings.
     * @param deploymentMetadata {@link DeploymentMetadata} of the deployed app.
     * @param resourceAccumulatorFactory
     *            Provides a {@link ResourceAccumulator} for this {@link ResourceResolver}.
     * @param configurationFactory
     *            For accessing the {@link au.com.sensis.mobile.crf.config.UiConfiguration}.
     */
    public ResourceResolverCommonParamHolder(
            final ResourceResolutionWarnLogger resourceResolutionWarnLogger,
            final DeploymentMetadata deploymentMetadata,
            final ResourceAccumulatorFactory resourceAccumulatorFactory,
            final ConfigurationFactory configurationFactory) {

        validateResourceResolutionWarnLogger(resourceResolutionWarnLogger);
        validateDeploymentMetadata(deploymentMetadata);
        validateResourceAccumulatorFactory(resourceAccumulatorFactory);
        validateConfigurationFactory(configurationFactory);

        this.resourceResolutionWarnLogger = resourceResolutionWarnLogger;
        this.deploymentMetadata = deploymentMetadata;
        this.resourceAccumulatorFactory = resourceAccumulatorFactory;
        this.configurationFactory = configurationFactory;
    }

    private void validateResourceAccumulatorFactory(
            final ResourceAccumulatorFactory resourceAccumulatorFactory) {

        if (resourceAccumulatorFactory == null) {
            throw new IllegalArgumentException("resourceAccumulatorFactory must not be null");
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
     * @return the resourceAccumulatorFactory
     */
    public ResourceAccumulatorFactory getResourceAccumulatorFactory() {

        return resourceAccumulatorFactory;
    }

    /**
     * @return the configurationFactory
     */
    public ConfigurationFactory getConfigurationFactory() {

        return configurationFactory;
    }
}
