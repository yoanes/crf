package au.com.sensis.mobile.crf.service;

import java.util.List;

import au.com.sensis.mobile.crf.exception.ResourceResolutionRuntimeException;
import au.com.sensis.wireless.common.volantis.devicerepository.api.Device;

/**
 * Key interface for selecting concrete resources based on the current device
 * and requested abstract path.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public interface ResourceResolverEngine {

    /**
     * Return the first concrete resource path for the requested
     * abstract path.
     *
     * @param device
     *            {@link Device} for the request.
     * @param requestedResourcePath
     *            Path of the resource requested.
     * @return Return the first concrete resource path for the requested
     *         abstract path. If none is found, null is returned.
     * @throws ResourceResolutionRuntimeException Thrown if an IO error occurs.
     */
    Resource getResource(Device device, String requestedResourcePath)
        throws ResourceResolutionRuntimeException;

    /**
     * List of all concrete resource paths (not just the first found)
     * that correspond to the requested abstract path. May not be null.
     *
     * @param device
     *            {@link Device} for the request.
     * @param requestedResourcePath
     *            Path of the resource requested.
     * @return List of all concrete resource paths that correspond to the
     *         requested path. May not be null. If no concrete resource paths are found,
     *         an empty {@link List} is returned.
     * @throws ResourceResolutionRuntimeException Thrown if an IO error occurs.
     */
    List<Resource> getAllResources(Device device, String requestedResourcePath)
        throws ResourceResolutionRuntimeException;
}
