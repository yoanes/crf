package au.com.sensis.mobile.crf.util;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;


/**
 * Default {@link PropertiesLoader} implementation.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class PropertiesLoaderBean implements PropertiesLoader {

    /**
     * {@inheritDoc}
     */
    @Override
    public Properties loadPropertiesNotNull(final File file) throws IOException {
        Properties properties = null;
        if (file.exists()) {
            properties =
                    PropertiesLoaderUtils
                            .loadProperties(new FileSystemResource(file));
        } else {
            properties = new Properties();
        }

        return properties;
    }
}
