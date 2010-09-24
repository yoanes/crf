package au.com.sensis.mobile.crf.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import au.com.sensis.mobile.crf.exception.ConfigurationRuntimeException;
import au.com.sensis.mobile.crf.util.FileIoFacade;
import au.com.sensis.mobile.crf.util.FileIoFacadeFactory;
import au.com.sensis.mobile.crf.util.PropertiesLoader;

/**
 * {@link JavaScriptFileFinder} for returning all JavaScript files for
 * a {@link Resource} that corresponds to a JavaScript package.
 *
 * @author Adrian.Koh2@sensis.com.au
 *
 */
public class JavaScriptFileFinderBean implements JavaScriptFileFinder {

    private static final String ORDER_PROPERTY_SPLIT_CHAR = ",";

    private static final String ORDER_PROPERTY_DEFAULT_VALUE = "*.js";

    private final PropertiesLoader propertiesLoader;
    private final String packagePropertiesFileName;
    private final String packageOrderPropertyName;

    /**
     * Constructor.
     *
     * @param propertiesLoader
     *            {@link PropertiesLoader} to use to load {@link Properties}
     *            from {@link File}s.
     * @param packagePropertiesFileName
     *            Name of the properties file for controlling packaging
     *            parameters.
     * @param packageOrderPropertyName
     *            Name of the property for controlling the order of files added
     *            to packages.
     */
    public JavaScriptFileFinderBean(
            final PropertiesLoader propertiesLoader,
            final String packagePropertiesFileName,
            final String packageOrderPropertyName) {
        this.propertiesLoader = propertiesLoader;
        this.packagePropertiesFileName = packagePropertiesFileName;
        this.packageOrderPropertyName = packageOrderPropertyName;
    }

    /**
     * All JavaScript files in the given dir are found. The order of the files
     * is controlled by the file that matches the
     * {@link #getPackageOrderPropertyName()} property found in the
     * {@link #getPackagePropertiesFileName()} properties file. If no such
     * property is found or no such file is found, then the order defaults to
     * {@link #ORDER_PROPERTY_DEFAULT_VALUE}. This default relies on the
     * JVM/platform default for file ordering.
     *
     * @return May not be null.
     *
     * {@inheritDoc}
     */
    @Override
    public List<File> findFiles(final File dir) throws IOException {
        final String bundleOrderProperty = getBundleOrderProperty(dir);

        final LinkedHashSet<File> foundFilesSet = new LinkedHashSet<File>();
        for (final String wildcard : createWildcards(bundleOrderProperty)) {
            // We treat each wildcard separately because the order of returned
            // files is significant.
            final File[] foundFiles =
                    getFileIoFacade().list(dir,
                            new String[] { wildcard.trim() });
            foundFilesSet.addAll(Arrays.asList(foundFiles));

        }
        return new ArrayList<File>(foundFilesSet);

    }


    private String getBundleOrderProperty(
            final File dir) throws IOException {
        return loadBundlesPropertiesWithDefaults(dir).getProperty(
                getPackageOrderPropertyName());
    }

    private String[] createWildcards(final String bundleOrderProperty) {
        return StringUtils.splitPreserveAllTokens(bundleOrderProperty,
                ORDER_PROPERTY_SPLIT_CHAR);
    }

    private FileIoFacade getFileIoFacade() {
        return FileIoFacadeFactory.getFileIoFacadeSingleton();
    }

    private Properties loadBundlesPropertiesWithDefaults(
            final File dir) throws IOException {

        final File packagePropertiesFile =
                createPackagePropertiesFile(dir);
        final Properties properties =
                getPropertiesLoader().loadPropertiesNotNull(
                        packagePropertiesFile);

        if (properties.getProperty(getPackageOrderPropertyName()) == null) {
            setDefaultOrderProperty(properties);
        } else {
            validateOrderProperty(properties
                    .getProperty(getPackageOrderPropertyName()),
                    packagePropertiesFile);
        }

        if (!properties.getProperty(getPackageOrderPropertyName()).trim()
                .endsWith(ORDER_PROPERTY_DEFAULT_VALUE)) {
            appendDefaultOrderPropertyValue(properties);
        }
        return properties;
    }

    private void validateOrderProperty(final String property,
            final File packagePropertiesFile) {
        final String[] splitProperty = createWildcards(property);
        for (final String wildcard : splitProperty) {
            if (StringUtils.isBlank(wildcard)) {
                throw new ConfigurationRuntimeException(
                        "Configuration file "
                                + packagePropertiesFile
                                + " contains an error. order property "
                                + "must be a comma separated list "
                                + "of file patterns (with optional * and ? wildcards). Was: '"
                                + property + "'");
            }
        }
    }

    private void appendDefaultOrderPropertyValue(final Properties properties) {
        final String orderProperty = properties.getProperty(getPackageOrderPropertyName());
        properties.setProperty(getPackageOrderPropertyName(),
                orderProperty + ORDER_PROPERTY_SPLIT_CHAR + ORDER_PROPERTY_DEFAULT_VALUE);

    }

    private void setDefaultOrderProperty(final Properties properties) {
        properties.setProperty(getPackageOrderPropertyName(),
                ORDER_PROPERTY_DEFAULT_VALUE);
    }

    private File createPackagePropertiesFile(final File dir) {
        return new File(dir, getPackagePropertiesFileName());
    }

    private PropertiesLoader getPropertiesLoader() {
        return propertiesLoader;
    }

    private String getPackagePropertiesFileName() {
        return packagePropertiesFileName;
    }

    private String getPackageOrderPropertyName() {
        return packageOrderPropertyName;
    }

}
