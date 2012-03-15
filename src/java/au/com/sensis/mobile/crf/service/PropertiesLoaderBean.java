package au.com.sensis.mobile.crf.service;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.Validate;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import au.com.sensis.devicerepository.Device;
import au.com.sensis.mobile.crf.exception.ResourceResolutionRuntimeException;

/**
 * Default {@link PropertiesLoader}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class PropertiesLoaderBean implements PropertiesLoader {

    private final ResourceResolverEngine resourceResolverEngine;

    /**
     * Constructor.
     *
     * @param resourceResolverEngine
     *            {@link ResourceResolverEngine} to use to resolve the requested
     *            path.
     */
    public PropertiesLoaderBean(final ResourceResolverEngine resourceResolverEngine) {
        Validate.notNull(resourceResolverEngine, "resourceResolverEngine must not be null");
        this.resourceResolverEngine = resourceResolverEngine;
    }

    /**
     * Delegates to
     * {@link ResourceResolverEngine#getAllResources(Device, String)} then loads
     * all found resources into a single {@link Properties} object, with
     * properties from later resources overriding properties from earlier
     * resources.
     *
     * {@inheritDoc}
     */
    @Override
    public Properties loadProperties(final Device device, final String requestedResourcePath)
            throws ResourceResolutionRuntimeException {
        final List<Resource> foundResources =
                getResourceResolverEngine().getAllResources(device, requestedResourcePath);

        final Properties result = new Properties();
        for (final Resource resource : foundResources) {
            try {
                final Properties foundProperties =
                        PropertiesLoaderUtils.loadProperties(new FileSystemResource(resource
                                .getNewFile()));
                result.putAll(foundProperties);
            } catch (final IOException e) {
                throw new ResourceResolutionRuntimeException("Error loading properties file: '"
                        + resource.getNewFile().getPath() + "'", e);
            }
        }

        return result;
    }

    /**
     * @return the resourceResolverEngine
     */
    private ResourceResolverEngine getResourceResolverEngine() {
        return resourceResolverEngine;
    }

}
