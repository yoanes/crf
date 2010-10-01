package au.com.sensis.mobile.crf.config;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;

import au.com.sensis.mobile.crf.exception.ConfigurationRuntimeException;
import au.com.sensis.mobile.crf.exception.GroupEvaluationRuntimeException;
import au.com.sensis.mobile.crf.exception.XmlValidationRuntimeException;
import au.com.sensis.mobile.crf.util.XmlBinder;
import au.com.sensis.mobile.crf.util.XmlValidator;
import au.com.sensis.wireless.common.volantis.devicerepository.api.DefaultDevice;
import au.com.sensis.wireless.common.volantis.devicerepository.api.Device;

/**
 * Factory for returning a {@link UiConfiguration}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class ConfigurationFactoryBean implements ConfigurationFactory {

    // Not final to allow injection of a mock during unit testing.
    private static Logger logger = Logger.getLogger(ConfigurationFactoryBean.class);

    private static final String CONFIG_CLASSPATH_PATTERNS_SEPARATOR = ",";

    private static final String SCHEMA_CLASSPATH_LOCATION =
            "/au/com/sensis/mobile/crf/config/crf-config.xsd";

    /**
     * Classpath of the file containing the configuration.
     */
    private final String mappingConfigurationClasspathPattern;

    private final DeploymentMetadata deploymentMetadata;
    private final XmlBinder xmlBinder;
    private final XmlValidator xmlValidator;
    private final ResourcePatternResolver resourcePatternResolver;
    private List<UiConfiguration> uiConfigurations;

    /**
     * The constructor which sets up the mappingConfiguration.
     *
     * @param deploymentMetadata
     *            {@link DeploymentMetadata}.
     * @param resourcePatternResolver
     *            {@link ResourcePatternResolver} used to resolve
     *            mappingConfigurationClasspathPattern to resources.
     * @param xmlBinder
     *            {@link XmlBinder} to use to load the XML configuration file.
     * @param xmlValidator
     *            {@link XmlValidator} to use to validate the XML configuration
     *            file.
     * @param mappingConfigurationClasspathPattern
     *            Classpath of the file containing the configuration.
     */
    // TODO: validate args.
    public ConfigurationFactoryBean(final DeploymentMetadata deploymentMetadata,
            final ResourcePatternResolver resourcePatternResolver, final XmlBinder xmlBinder,
            final XmlValidator xmlValidator, final String mappingConfigurationClasspathPattern) {
        this.deploymentMetadata = deploymentMetadata;
        this.resourcePatternResolver = resourcePatternResolver;
        this.mappingConfigurationClasspathPattern = mappingConfigurationClasspathPattern;
        this.xmlBinder = xmlBinder;
        this.xmlValidator = xmlValidator;

        initUiConfigurations();
    }

    private void initUiConfigurations() {
        setUiConfigurations(new ArrayList<UiConfiguration>());
        loadConfigurationFilesWithSchemaValidation();
        validateLoadedConfigurationData();
    }

    private void loadConfigurationFilesWithSchemaValidation() {

        try {
            doLoadConfigurationFilesWithSchemaValidation();

        } catch (final XmlValidationRuntimeException e) {
            throw e;
        } catch (final ConfigurationRuntimeException e) {
            throw e;
        } catch (final Exception e) {
            // Wrap all other types of exceptions.
            throw new ConfigurationRuntimeException("Error loading config from classpath: '"
                    + getMappingConfigurationClasspathPattern() + "'", e);
        }
    }

    private void doLoadConfigurationFilesWithSchemaValidation() throws IOException {
        final List<UiConfiguration> defaultUiConfigurations = new ArrayList<UiConfiguration>();

        for (final Resource resource : getConfigurationResources()) {

            getXmlValidator().validate(resource.getURL(), getConfigSchemaUrl());

            final UiConfiguration uiConfiguration = unmarshallToUiConfiguration(resource);

            addToCorrectList(uiConfiguration, defaultUiConfigurations, getUiConfigurations());
        }

        validateOneAndOnlyOneDefaultUiConfiguration(defaultUiConfigurations);

        getUiConfigurations().addAll(defaultUiConfigurations);
    }

    private Device createDefaultDevice() {
        return new DefaultDevice();
    }

    private void validateOneAndOnlyOneDefaultUiConfiguration(
            final List<UiConfiguration> defaultUiConfigurations) {

        if (defaultUiConfigurations.isEmpty()) {
            throw new ConfigurationRuntimeException(
                    "No configuration file with a default (empty) config path was found.");
        }

        if (defaultUiConfigurations.size() > 1) {
            final List<URL> defaultConfigurationUrls = extractUrls(defaultUiConfigurations);
            throw new ConfigurationRuntimeException(
                    "Multiple configurations with a default (empty) "
                            + "config path were found. Only one is allowed: "
                            + defaultConfigurationUrls);
        }
    }

    private List<URL> extractUrls(final List<UiConfiguration> uiConfigurations) {
        final List<URL> defaultConfigurationUrls = new ArrayList<URL>();
        for (final UiConfiguration uiConfiguration : uiConfigurations) {
            defaultConfigurationUrls.add(uiConfiguration.getSourceUrl());
        }
        return defaultConfigurationUrls;
    }

    private void addToCorrectList(final UiConfiguration uiConfiguration,
            final List<UiConfiguration> defaultUiConfigurations,
            final List<UiConfiguration> uiConfigurations) {

        if (uiConfiguration.hasDefaultConfigPath()) {
            defaultUiConfigurations.add(uiConfiguration);
        } else {
            uiConfigurations.add(uiConfiguration);
        }
    }

    private UiConfiguration unmarshallToUiConfiguration(final Resource resource)
        throws IOException {

        final UiConfiguration uiConfiguration =
                (UiConfiguration) getXmlBinder().unmarshall(resource.getURL());
        uiConfiguration.setSourceUrl(resource.getURL());
        uiConfiguration.setSourceTimestamp(resource.lastModified());

        if (logger.isInfoEnabled()) {
            logger.info("Loaded configuration: " + uiConfiguration);
        }

        return uiConfiguration;
    }

    private Resource[] getConfigurationResources() throws IOException {
        final List<Resource> resources = new ArrayList<Resource>();

        final String[] patterns =
                getMappingConfigurationClasspathPattern()
                        .split(CONFIG_CLASSPATH_PATTERNS_SEPARATOR);
        for (final String pattern : patterns) {
            addArrayToList(getResourcePatternResolver().getResources(pattern.trim()), resources);
        }

        return resources.toArray(new Resource[] {});
    }

    private <T> void addArrayToList(final T[] resourcesArray, final List<T> resourcesList) {
        for (final T resource : resourcesArray) {
            resourcesList.add(resource);
        }
    }

    private URL getConfigSchemaUrl() throws IOException {
        return new ClassPathResource(SCHEMA_CLASSPATH_LOCATION).getURL();
    }

    private void validateLoadedConfigurationData() {

        for (final UiConfiguration uiConfiguration : getUiConfigurations()) {
            final Iterator<Group> groupIterator = uiConfiguration.groupIterator();
            while (groupIterator.hasNext()) {
                try {
                    groupIterator.next().validate(createDefaultDevice());
                } catch (final GroupEvaluationRuntimeException e) {
                    throw new ConfigurationRuntimeException("Config at '"
                            + uiConfiguration.getSourceUrl() + "' is invalid.", e);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public UiConfiguration getUiConfiguration(final String requestedResourcePath)
            throws ConfigurationRuntimeException {

        if (configurationRefreshRequired()) {
            infoLogReloadingConfiguration();
            initUiConfigurations();
        }

        for (final UiConfiguration uiConfiguration : getUiConfigurations()) {
            if (uiConfiguration.appliesToPath(requestedResourcePath)) {
                return uiConfiguration;
            }
        }

        throw new IllegalStateException(
                "Reached end of UiConfiguration list without finding one that "
                        + "applies to the requested path: '" + requestedResourcePath
                        + "'. This should never happen !!!");
    }

    private void infoLogReloadingConfiguration() {
        if (logger.isInfoEnabled()) {
            logger.info("Updated configuration detected and UiConfiguration caching disabled. "
                    + "Will reload configuration.");
        }
    }

    private boolean configurationRefreshRequired() {
        return !getDeploymentMetadata().isCacheUiConfiguration()
                && loadedUiConfigurationOutOfDate();
    }

    private boolean loadedUiConfigurationOutOfDate() {
        try {
            final Resource[] resources = getConfigurationResources();
            if (resources.length != getUiConfigurations().size()) {
                return true;
            }

            final List<UrlAndTimestamp> previousUrlAndTimestampsLoaded =
                    createUrlAndTimestamps(getUiConfigurations());
            final List<UrlAndTimestamp> newUrlAndTimestamps = createUrlAndTimestamps(resources);

            if (!previousUrlAndTimestampsLoaded.equals(newUrlAndTimestamps)) {
                return true;
            }
        } catch (final IOException e) {
            throw new ConfigurationRuntimeException(
                    "Error checking if loaded UiConfiguraton is out of date using pattern: '"
                            + getMappingConfigurationClasspathPattern() + "'", e);
        }

        return false;
    }

    private List<UrlAndTimestamp> createUrlAndTimestamps(final Resource[] resources)
            throws IOException {
        final List<UrlAndTimestamp> result = new ArrayList<UrlAndTimestamp>();
        for (final Resource resource : resources) {
            result.add(new UrlAndTimestamp(resource.getURL(), resource.lastModified()));
        }
        Collections.sort(result);
        return result;
    }

    private List<UrlAndTimestamp> createUrlAndTimestamps(
            final List<UiConfiguration> uiConfigurations) {
        final List<UrlAndTimestamp> result = new ArrayList<UrlAndTimestamp>();
        for (final UiConfiguration uiConfiguration : uiConfigurations) {
            result.add(new UrlAndTimestamp(uiConfiguration.getSourceUrl(), uiConfiguration
                    .getSourceTimestamp()));
        }
        Collections.sort(result);
        return result;
    }

    /**
     * @return the mappingConfigurationClasspathPattern
     */
    private String getMappingConfigurationClasspathPattern() {
        return mappingConfigurationClasspathPattern;
    }

    private XmlBinder getXmlBinder() {
        return xmlBinder;
    }

    private XmlValidator getXmlValidator() {
        return xmlValidator;
    }

    private ResourcePatternResolver getResourcePatternResolver() {
        return resourcePatternResolver;
    }

    private List<UiConfiguration> getUiConfigurations() {
        return uiConfigurations;
    }

    private void setUiConfigurations(final List<UiConfiguration> uiConfigurations) {
        this.uiConfigurations = uiConfigurations;
    }

    private DeploymentMetadata getDeploymentMetadata() {
        return deploymentMetadata;
    }


    private class UrlAndTimestamp implements Comparable<UrlAndTimestamp> {
        private final URL url;
        private final long timestamp;

        public UrlAndTimestamp(final URL url, final long timestamp) {
            super();
            this.url = url;
            this.timestamp = timestamp;
        }

        @Override
        public int compareTo(final UrlAndTimestamp urlAndTimestamp) {
            return getUrl().toString().compareTo(urlAndTimestamp.getUrl().toString());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }

            if ((obj == null) || !this.getClass().equals(obj.getClass())) {
                return false;
            }

            final UrlAndTimestamp rhs = (UrlAndTimestamp) obj;
            final EqualsBuilder equalsBuilder = new EqualsBuilder();

            equalsBuilder.append(getUrl(), rhs.getUrl());
            equalsBuilder.append(getTimestamp(), rhs.getTimestamp());
            return equalsBuilder.isEquals();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            final HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
            hashCodeBuilder.append(getUrl());
            hashCodeBuilder.append(getTimestamp());
            return hashCodeBuilder.toHashCode();
        }

        public URL getUrl() {
            return url;
        }

        public long getTimestamp() {
            return timestamp;
        }
    }
}
