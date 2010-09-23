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

/**
 * {@link JavaScriptFileFinder} for returning all JavaScript files for
 * a {@link Resource} that corresponds to a JavaScript bundle.
 *
 * @author Adrian.Koh2@sensis.com.au
 *
 */
// TODO: will be merged into JavaScriptGroupResourceResolver (once JavaScriptResourceBean
// is split into two. See comments in Resource).
public class JavaScriptBundlePathExpander implements JavaScriptFileFinder {

    private static final String ORDER_PROPERTY_SPLIT_CHAR = ",";

    private static final String ORDER_PROPERTY_DEFAULT_VALUE = "*.js";

    private final PropertiesLoader propertiesLoader;
    private final String bundlesPropertiesFileName;
    private final String bundleOrderPropertyName;

    /**
     * Constructor.
     *
     * @param propertiesLoader
     *            {@link PropertiesLoader} to use to load {@link Properties}
     *            from {@link File}s.
     * @param bundlesPropertiesFileName
     *            Name of the properties file for controlling bundling
     *            parameters.
     * @param bundleOrderPropertyName
     *            Name of the property for controlling the order of files added
     *            to bundles.
     */
    public JavaScriptBundlePathExpander(
            final PropertiesLoader propertiesLoader,
            final String bundlesPropertiesFileName,
            final String bundleOrderPropertyName) {
        this.propertiesLoader = propertiesLoader;
        this.bundlesPropertiesFileName = bundlesPropertiesFileName;
        this.bundleOrderPropertyName = bundleOrderPropertyName;
    }

    /**
     * If {@link Resource#isBundlePath()}, then the path is expanded
     * to a list of files that correspond to the bundle. Otherwise,
     * {@link Resource#getNewFile()} is returned.
     * <p>
     * Bundle expansion occurs by finding all files in
     * {@link Resource#getBundleParentDirFile()} that match the
     * {@link #getBundleOrderPropertyName()} property found in the
     * {@link #getBundlesPropertiesFileName()} properties file. If no such
     * property is found or no such file is found, then the order defaults to
     * {@link #ORDER_PROPERTY_DEFAULT_VALUE}. This default relies on the
     * JVM/platform default for file ordering.
     * </p>
     *
     * @return May not be null.
     * {@inheritDoc}
     */
    @Override
    public List<File> findJavaScriptFiles(final File dir) throws IOException {
        final String bundleOrderProperty = getBundleOrderProperty(dir);

        final LinkedHashSet<File> foundFilesSet = new LinkedHashSet<File>();
        for (final String wildcard : createWildcards(bundleOrderProperty)) {
            // We treat each wildcard separately because the order of returned
            // files
            // is significant.
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
                getBundleOrderPropertyName());
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

        final File bundlePropertiesFile =
                createBundlePropertiesFile(dir);
        final Properties properties =
                getPropertiesLoader().loadPropertiesNotNull(
                        bundlePropertiesFile);

        if (properties.getProperty(getBundleOrderPropertyName()) == null) {
            setDefaultOrderProperty(properties);
        } else {
            validateOrderProperty(properties
                    .getProperty(getBundleOrderPropertyName()),
                    bundlePropertiesFile);
        }

        if (!properties.getProperty(getBundleOrderPropertyName()).trim()
                .endsWith(ORDER_PROPERTY_DEFAULT_VALUE)) {
            appendDefaultOrderPropertyValue(properties);
        }
        return properties;
    }

    private void validateOrderProperty(final String property,
            final File bundlePropertiesFile) {
        final String[] splitProperty = createWildcards(property);
        for (final String wildcard : splitProperty) {
            if (StringUtils.isBlank(wildcard)) {
                throw new ConfigurationRuntimeException(
                        "Configuration file "
                                + bundlePropertiesFile
                                + " contains an error. order property "
                                + "must be a comma separated list "
                                + "of file patterns (with optional * and ? wildcards). Was: '"
                                + property + "'");
            }
        }
    }

    private void appendDefaultOrderPropertyValue(final Properties properties) {
        final String orderProperty = properties.getProperty(getBundleOrderPropertyName());
        properties.setProperty(getBundleOrderPropertyName(),
                orderProperty + ORDER_PROPERTY_SPLIT_CHAR + ORDER_PROPERTY_DEFAULT_VALUE);

    }

    private void setDefaultOrderProperty(final Properties properties) {
        properties.setProperty(getBundleOrderPropertyName(),
                ORDER_PROPERTY_DEFAULT_VALUE);
    }

    private File createBundlePropertiesFile(final File dir) {
        return new File(dir, getBundlesPropertiesFileName());
    }

    private PropertiesLoader getPropertiesLoader() {
        return propertiesLoader;
    }

    private String getBundlesPropertiesFileName() {
        return bundlesPropertiesFileName;
    }

    private String getBundleOrderPropertyName() {
        return bundleOrderPropertyName;
    }

}
