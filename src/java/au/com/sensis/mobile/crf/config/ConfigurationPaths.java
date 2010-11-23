package au.com.sensis.mobile.crf.config;

import java.io.File;
import java.util.List;

/**
 * Configuration path data.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class ConfigurationPaths {

    /**
     * Classpath of the file containing the configuration.
     */
    private final String mappingConfigurationClasspathPattern;

    private final List<File> uiResourceRootDirectories;

    /**
     * Constructor.
     *
     * @param mappingConfigurationClasspathPattern
     *            Classpath of the file containing the configuration.
     * @param uiResourceRootDirectories List of root directories where UI resources
     *            are expected to be found. Each group in the configuration should correspond
     *            to a directory directly under one of these directories.
     */
    public ConfigurationPaths(final String mappingConfigurationClasspathPattern,
            final List<File> uiResourceRootDirectories) {
        this.mappingConfigurationClasspathPattern = mappingConfigurationClasspathPattern;
        this.uiResourceRootDirectories = uiResourceRootDirectories;
    }

    /**
     * @return the mappingConfigurationClasspathPattern
     */
    public String getMappingConfigurationClasspathPattern() {
        return mappingConfigurationClasspathPattern;
    }

    /**
     * @return the uiResourceRootDirectories
     */
    public List<File> getUiResourceRootDirectories() {
        return uiResourceRootDirectories;
    }
}
