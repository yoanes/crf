package au.com.sensis.mobile.crf.service;

import java.util.Properties;

import au.com.sensis.devicerepository.Device;
import au.com.sensis.mobile.crf.exception.ResourceResolutionRuntimeException;

/**
 * Allows callers to load a {@link Properties} object for a ({@link Device},
 * requested resource path) pair.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public interface PropertiesLoader {

    /**
     * Return the {@link Properties} for the requested abstract path. If none is
     * found, an empty {@link Properties} instance is returned.
     *
     * @param device
     *            {@link Device} for the request.
     * @param requestedResourcePath
     *            Path of the resource requested.
     * @return Return the {@link Properties} for the requested abstract path. If
     *         none is found, an empty {@link Properties} instance is returned.
     * @throws ResourceResolutionRuntimeException
     *             Thrown if an IO error occurs.
     */
    Properties loadProperties(Device device, String requestedResourcePath)
            throws ResourceResolutionRuntimeException;
}
