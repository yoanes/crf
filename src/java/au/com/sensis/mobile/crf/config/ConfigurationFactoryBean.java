package au.com.sensis.mobile.crf.config;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.filefilter.AndFileFilter;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;

import au.com.sensis.mobile.crf.exception.ConfigurationRuntimeException;
import au.com.sensis.mobile.crf.exception.GroupEvaluationRuntimeException;
import au.com.sensis.mobile.crf.exception.XmlValidationRuntimeException;
import au.com.sensis.mobile.crf.service.ResourceResolutionWarnLogger;
import au.com.sensis.mobile.crf.util.FileIoFacadeFactory;
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

    private final ConfigurationPaths configurationPaths;

    private final DeploymentMetadata deploymentMetadata;
    private final XmlBinder xmlBinder;
    private final XmlValidator xmlValidator;
    private final ResourcePatternResolver resourcePatternResolver;

    /**
     * {@link UiConfiguration}s that are candidates to be returned by
     * {@link #getUiConfiguration(String).
     */
    private List<UiConfiguration> uiConfigurations;

    /**
     * {@link UiConfiguration}s that are only used to resolve imports from
     * the {@link UiConfiguration}s returned by {@link #getUiConfiguration(String)}.
     */
    private List<UiConfiguration> globalConfigPathUiConfigurations;

    private final ResourceResolutionWarnLogger resourceResolutionWarnLogger;
    private final GroupsCacheFactory groupsCacheFactory;

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
     * @param resourceResolutionWarnLogger {@link ResourceResolutionWarnLogger}.
     * @param configurationPaths {@link ConfigurationPaths}.
     * @param groupsCacheFactory {@link GroupsCacheFactory} to use to create caches to be injected
     *            into the loaded {@link UiConfiguration} objects. Whether this factory creates
     *            a singleton cache or a new one per invocation is implementation dependent.
     */
    // TODO: validate args.
    public ConfigurationFactoryBean(final DeploymentMetadata deploymentMetadata,
            final ResourcePatternResolver resourcePatternResolver, final XmlBinder xmlBinder,
            final XmlValidator xmlValidator,
            final ResourceResolutionWarnLogger resourceResolutionWarnLogger,
            final ConfigurationPaths configurationPaths,
            final GroupsCacheFactory groupsCacheFactory) {
        this.deploymentMetadata = deploymentMetadata;
        this.resourcePatternResolver = resourcePatternResolver;
        this.xmlBinder = xmlBinder;
        this.xmlValidator = xmlValidator;
        this.resourceResolutionWarnLogger = resourceResolutionWarnLogger;
        this.configurationPaths = configurationPaths;
        this.groupsCacheFactory = groupsCacheFactory;

        initUiConfigurations();
    }

    private void initUiConfigurations() {
        setUiConfigurations(new ArrayList<UiConfiguration>());
        setGlobalConfigPathUiConfigurations(new ArrayList<UiConfiguration>());
        loadConfigurationFilesWithSchemaValidation();
        finaliseGroupsAndImports();
        validateLoadedConfigurationData();
        infoLogLoadingDone();
    }

    private void infoLogLoadingDone() {
        if (logger.isInfoEnabled()) {

            for (final UiConfiguration uiConfiguration : getGlobalConfigPathUiConfigurations()) {
                logger.info("Loaded configuration: " + uiConfiguration);
            }

            for (final UiConfiguration uiConfiguration : getUiConfigurations()) {
                logger.info("Loaded configuration: " + uiConfiguration);
            }

        }
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

            addToCorrectList(uiConfiguration, defaultUiConfigurations, getUiConfigurations(),
                    getGlobalConfigPathUiConfigurations());
        }

        validateOneAndOnlyOneDefaultUiConfiguration(defaultUiConfigurations);

        getUiConfigurations().addAll(defaultUiConfigurations);
    }

    private void finaliseGroupsAndImports() {
        for (final UiConfiguration uiConfiguration : getGlobalConfigPathUiConfigurations()) {
            finaliseGroupsAndImports(uiConfiguration);
        }

        for (final UiConfiguration uiConfiguration : getUiConfigurations()) {
            finaliseGroupsAndImports(uiConfiguration);
        }
    }

    private void finaliseGroupsAndImports(final UiConfiguration uiConfiguration) {
        final List<Group> finalisedGroups = new ArrayList<Group>();

        for (final GroupOrImport groupOrImport : uiConfiguration.getGroupsAndImports()
                .getGroupOrImport()) {

            finaliseGroupOrImport(uiConfiguration, groupOrImport, finalisedGroups);
        }

        final Groups groups = new Groups();
        groups.setGroups(finalisedGroups.toArray(new Group[] {}));
        groups.setDefaultGroup(uiConfiguration.getGroupsAndImports().getDefaultGroup());
        uiConfiguration.setGroups(groups);

        // Throw away the intermediate groupsAndImports because we don't need them anymore.
        uiConfiguration.setGroupsAndImports(null);
    }

    private void finaliseGroupOrImport(final UiConfiguration parentUiConfiguration,
            final GroupOrImport groupOrImport, final List<Group> finalisedGroups) {

        if (groupOrImport.isGroup()) {
            finalisedGroups.add(groupOrImport.getGroup());

        } else if (groupOrImport.isGroupImport()) {

            validateLegalImportScope(parentUiConfiguration, groupOrImport.getGroupImport());

            final List<Group> importedGroups = importGroups(parentUiConfiguration,
                    groupOrImport.getGroupImport());
            finalisedGroups.addAll(importedGroups);
        } else {
            throw new IllegalStateException("GroupOrImport has neither a Group or a GroupImport: "
                    + groupOrImport + "This should never happen.");
        }
    }

    private void validateLegalImportScope(final UiConfiguration parentUiConfiguration,
            final GroupImport groupImport) {

        if (globalImportsGlobal(parentUiConfiguration, groupImport)) {

            throw new ConfigurationRuntimeException("Illegal for global UiConfiguration at '"
                    + parentUiConfiguration.getSourceUrl()
                    + "' to import from another global config path of '"
                    + groupImport.getFromConfigPath()
                    + "'. Note that global configs have a config path " + "starting with "
                    + UiConfiguration.GLOBAL_CONFIG_PATH_PREFIX);

        }

        if (nonGlobalImportsNonGlobal(parentUiConfiguration, groupImport)) {

            throw new ConfigurationRuntimeException("Illegal for non-global UiConfiguration at '"
                    + parentUiConfiguration.getSourceUrl()
                    + "' to import from another non-global config path of '"
                    + groupImport.getFromConfigPath()
                    + "'. Note that global configs have a config path " + "starting with "
                    + UiConfiguration.GLOBAL_CONFIG_PATH_PREFIX);
        }
    }

    private boolean nonGlobalImportsNonGlobal(final UiConfiguration parentUiConfiguration,
            final GroupImport groupImport) {
        return !parentUiConfiguration.hasGlobalConfigPath()
                && !groupImport.hasGlobalConfigPath();
    }

    private boolean globalImportsGlobal(final UiConfiguration parentUiConfiguration,
            final GroupImport groupImport) {
        return parentUiConfiguration.hasGlobalConfigPath() && groupImport.hasGlobalConfigPath();
    }

    private List<Group> importGroups(final UiConfiguration parentUiConfiguration,
            final GroupImport groupImport) {

        if (logger.isInfoEnabled()) {
            logger.info("Resolving import for " + parentUiConfiguration.getSourceUrl() + ": "
                    + groupImport);
        }

        final List<Group> importedGroups = new ArrayList<Group>();

        final UiConfiguration importedUiConfiguration =
                getGlobalUiConfigurationByExactConfigPath(groupImport.getFromConfigPath());

        if (StringUtils.isNotBlank(groupImport.getGroupName())) {
            final Group groupToImport =
                    importedUiConfiguration.getGroups().getGroupByName(groupImport.getGroupName());
            importedGroups.add(creatNewGroup(groupToImport));
        } else {
            importedGroups.addAll(createNewGroups(importedUiConfiguration.getGroups().getGroups()));
        }

        return importedGroups;
    }

    private List<Group> createNewGroups(final Group[] groups) {
        final List<Group> newGroups = new ArrayList<Group>();

        for (final Group currGroup : groups) {
            newGroups.add(creatNewGroup(currGroup));
        }

        return newGroups;
    }

    private Group creatNewGroup(final Group groupToImport) {
        final Group newGroup = new Group();
        newGroup.setName(groupToImport.getName());
        newGroup.setExpr(groupToImport.getExpr());
        return newGroup;
    }

    private UiConfiguration getGlobalUiConfigurationByExactConfigPath(final String fromConfigPath) {
        for (final UiConfiguration uiConfiguration : getGlobalConfigPathUiConfigurations()) {
            if (uiConfiguration.getConfigPath().equals(fromConfigPath)) {
                return uiConfiguration;
            }
        }
        throw new ConfigurationRuntimeException(
                "No global UiConfiguration found with a configPath "
                        + "matching requested import path: '" + fromConfigPath + "'");
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
            final List<UiConfiguration> uiConfigurations,
            final List<UiConfiguration> globalConfigPathUiConfigurations) {

        if (uiConfiguration.hasDefaultConfigPath()) {
            defaultUiConfigurations.add(uiConfiguration);
        } else if (uiConfiguration.hasGlobalConfigPath()) {
            globalConfigPathUiConfigurations.add(uiConfiguration);
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
        uiConfiguration.setMatchingGroupsCache(getGroupsCacheFactory().createGroupsCache());

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

        validateGroupNamesUnique();

        validateConfigPathsUnique();

        validateGroupsByDelegation();

        validateGroupDirsExist();

        validateUiResourceDirsExistAsGroups();

        // TODO: validate no duplicate uiconfiguration config-paths and no duplicate group names
        // within a ui configuration.

    }

    private void validateConfigPathsUnique() {
        final Map<String, UiConfiguration> seenConfigPathsMap =
                new HashMap<String, UiConfiguration>();

        for (final UiConfiguration uiConfiguration : getUiConfigurations()) {

            if (seenConfigPathsMap.containsKey(uiConfiguration.getConfigPath())) {
                handleDuplicateConfigPathFound(seenConfigPathsMap, uiConfiguration);

            } else {
                seenConfigPathsMap.put(uiConfiguration.getConfigPath(), uiConfiguration);
            }
        }
    }

    private void handleDuplicateConfigPathFound(
            final Map<String, UiConfiguration> seenConfigPathsMap,
            final UiConfiguration uiConfiguration) {

        final Set<URL> duplicateConfigPathUrls = new HashSet<URL>();
        duplicateConfigPathUrls.add(uiConfiguration.getSourceUrl());
        duplicateConfigPathUrls.add(seenConfigPathsMap.get(uiConfiguration.getConfigPath())
                .getSourceUrl());

        throw new ConfigurationRuntimeException("Duplicate config path of '"
                + uiConfiguration.getConfigPath() + "' found in: "
                + duplicateConfigPathUrls);
    }

    private void validateGroupNamesUnique() {
        for (final UiConfiguration uiConfiguration : getUiConfigurations()) {
            validateGroupNamesUnique(uiConfiguration);
        }

    }

    private void validateGroupNamesUnique(final UiConfiguration uiConfiguration) {

        final Set<String> seenGroupNames = new HashSet<String>();
        final Set<String> duplicateGroupNames = new HashSet<String>();

        final Iterator<Group> groupIterator = uiConfiguration.groupIterator();
        while (groupIterator.hasNext()) {
            final Group currGroup = groupIterator.next();

            if (seenGroupNames.contains(currGroup.getName())) {
                duplicateGroupNames.add(currGroup.getName());

            } else {
                seenGroupNames.add(currGroup.getName());
            }
        }

        if (!duplicateGroupNames.isEmpty()) {
            throw new ConfigurationRuntimeException("Config at '" + uiConfiguration.getSourceUrl()
                    + "' has duplicate group names: " + duplicateGroupNames + ". "
                    + "Some may have been via imports.");
        }
    }

    private void validateGroupsByDelegation() {
        for (final UiConfiguration uiConfiguration : getUiConfigurations()) {
            final Iterator<Group> groupIterator = uiConfiguration.groupIterator();
            while (groupIterator.hasNext()) {
                try {
                    groupIterator.next().validate(createDefaultDevice());
                } catch (final GroupEvaluationRuntimeException e) {
                    // Only log a warning. This leniency is a consequence of CRF allowing callers to
                    // set arbitrary Jexl context objects via ThreadLocalContextObjectsHolder -
                    // at start up time, CRF has no easy way to fabricate a valid context for
                    // group validation.
                    logWarning(uiConfiguration, e);
                }
            }
        }
    }

    private void logWarning(final UiConfiguration uiConfiguration,
            final GroupEvaluationRuntimeException e) {
        if (getResourceResolutionWarnLogger().isWarnEnabled()) {
            getResourceResolutionWarnLogger().warn(
                    "Config at '" + uiConfiguration.getSourceUrl() + "' is possibly invalid.", e);
        }

    }

    private void validateGroupDirsExist() {
        final Set<String> allUiResourceGroupDirNames = findAllUiResourceGroupDirNames();

        for (final UiConfiguration uiConfiguration : getUiConfigurations()) {
            final Iterator<Group> groupIterator = uiConfiguration.groupIterator();

            final Set<String> groupsMissingADir =
                    getGroupsMissingADir(allUiResourceGroupDirNames, groupIterator);

            logMissingGroupDirsWarningIfRequired(uiConfiguration, groupsMissingADir);

        }

    }

    private void logMissingGroupDirsWarningIfRequired(final UiConfiguration uiConfiguration,
            final Set<String> groupsMissingADir) {
        if (!groupsMissingADir.isEmpty() && getResourceResolutionWarnLogger().isWarnEnabled()) {
            getResourceResolutionWarnLogger().warn(
                    "No group directories found for groups: " + groupsMissingADir
                            + " for UiConfiguration loaded from: "
                            + uiConfiguration.getSourceUrl() + ". Searched directories: "
                            + getUiResourceRootDirectories());
        }
    }

    private Set<String> getGroupsMissingADir(final Set<String> allUiResourceGroupDirNames,
            final Iterator<Group> groupIterator) {
        final Set<String> groupsMissingADir = new LinkedHashSet<String>();

        while (groupIterator.hasNext()) {
            final Group currentGroup = groupIterator.next();
            if (!currentGroup.isDefault()
                    && !allUiResourceGroupDirNames.contains(currentGroup.getName())) {
                groupsMissingADir.add(currentGroup.getName());
            }
        }
        return groupsMissingADir;
    }

    private void validateUiResourceDirsExistAsGroups() {
        final Set<String> allGroupNames = findAllGroupNames();
        final Set<File> dirsWithoutGroup = new LinkedHashSet<File>();

        for (final File uiResourceRootDir : getUiResourceRootDirectories()) {
            accumulateDirsWithoutGroup(allGroupNames, uiResourceRootDir, dirsWithoutGroup);
        }

        logDirsWithoutGroupWarningIfRequired(dirsWithoutGroup);

    }

    private void logDirsWithoutGroupWarningIfRequired(final Set<File> dirsWithoutGroup) {
        if (!dirsWithoutGroup.isEmpty() && getResourceResolutionWarnLogger().isWarnEnabled()) {
            getResourceResolutionWarnLogger().warn(
                    "Found group directories that are not configured in any config file: "
                            + dirsWithoutGroup);
        }
    }

    private void accumulateDirsWithoutGroup(final Set<String> allGroupNames,
            final File uiResourceRootDir, final Set<File> dirsWithoutGroup) {
        for (final File dir : getDirs(uiResourceRootDir)) {
            if (!allGroupNames.contains(dir.getName())) {
                dirsWithoutGroup.add(dir);
            }
        }
    }

    private Set<String> findAllGroupNames() {
        final Set<String> allGroupNames = new HashSet<String>();

        for (final UiConfiguration uiConfiguration : getUiConfigurations()) {
            final Iterator<Group> groupIterator = uiConfiguration.groupIterator();
            while (groupIterator.hasNext()) {
                allGroupNames.add(groupIterator.next().getName());
            }
        }

        return allGroupNames;
    }

    private Set<String> findAllUiResourceGroupDirNames() {
        final Set<String> allGroupDirNames = new HashSet<String>();

        for (final File uiResourceRootDir : getUiResourceRootDirectories()) {
            final String [] dirNames = getDirNames(uiResourceRootDir);
            addDirNamesToSet(dirNames, allGroupDirNames);
        }

        return allGroupDirNames;
    }

    private String[] getDirNames(final File uiResourceRootDir) {
        final List<String> dirNamess = new ArrayList<String>();
        for (final File fileOrDir : getDirs(uiResourceRootDir)) {
            dirNamess.add(fileOrDir.getName());
        }

        return dirNamess.toArray(new String[] {});
    }

    private File[] getDirs(final File uiResourceRootDir) {
        final File[] allFilesAndDirs =
            FileIoFacadeFactory.getFileIoFacadeSingleton().list(uiResourceRootDir,
                    createDirFileFilter());

        final List<File> dirs = new ArrayList<File>();
        for (final File fileOrDir : allFilesAndDirs) {
            if (fileOrDir.isDirectory()) {
                dirs.add(fileOrDir);
            }
        }

        return dirs.toArray(new File[] {});
    }

    private FileFilter createDirFileFilter() {
        final WildcardFileFilter allDirs = new WildcardFileFilter("*");

        // We hard code ignoring of CVS dirs since we'd never want them and yet they
        // can be there during test case runs.
        final NotFileFilter notCvsDir = new NotFileFilter(new NameFileFilter("CVS"));

        return new AndFileFilter(allDirs, notCvsDir);
    }

    private void addDirNamesToSet(final String[] dirNames, final Set<String> allGroupDirNames) {
        for (final String dirName : dirNames) {
            allGroupDirNames.add(dirName);
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
                debugLogUiConfigurationFound(uiConfiguration);
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
            if (resources.length != getUiConfigurations().size()
                    + getGlobalConfigPathUiConfigurations().size()) {
                return true;
            }


            final List<UiConfiguration> allUiConfigurations = new ArrayList<UiConfiguration>();
            allUiConfigurations.addAll(getUiConfigurations());
            allUiConfigurations.addAll(getGlobalConfigPathUiConfigurations());

            final List<UrlAndTimestamp> previousUrlAndTimestampsLoaded =
                    createUrlAndTimestamps(allUiConfigurations);
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
        return getConfigurationPaths().getMappingConfigurationClasspathPattern();
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

    /**
     * @return the globalConfigPathUiConfigurations
     */
    private List<UiConfiguration> getGlobalConfigPathUiConfigurations() {
        return globalConfigPathUiConfigurations;
    }

    /**
     * @param globalConfigPathUiConfigurations the globalConfigPathUiConfigurations to set
     */
    private void setGlobalConfigPathUiConfigurations(
            final List<UiConfiguration> globalConfigPathUiConfigurations) {
        this.globalConfigPathUiConfigurations = globalConfigPathUiConfigurations;
    }

    /**
     * @return the configurationPaths
     */
    public ConfigurationPaths getConfigurationPaths() {
        return configurationPaths;
    }

    private DeploymentMetadata getDeploymentMetadata() {
        return deploymentMetadata;
    }

    /**
     * @return the uiResourceRootDirectories
     */
    private List<File> getUiResourceRootDirectories() {
        return getConfigurationPaths().getUiResourceRootDirectories();
    }

    /**
     * @return the resourceResolutionWarnLogger
     */
    private ResourceResolutionWarnLogger getResourceResolutionWarnLogger() {
        return resourceResolutionWarnLogger;
    }

    /**
     * @return the groupsCacheFactory
     */
    private GroupsCacheFactory getGroupsCacheFactory() {
        return groupsCacheFactory;
    }

    private void debugLogUiConfigurationFound(final UiConfiguration uiConfiguration) {

        if (logger.isDebugEnabled()) {
            logger.debug("UiConfiguration found with configPath: '"
                    + uiConfiguration.getConfigPath() + "'");
        }
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
