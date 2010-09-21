package au.com.sensis.mobile.crf.service;

import java.io.IOException;
import java.util.List;

import au.com.sensis.wireless.common.volantis.devicerepository.api.Device;

/**
 * Key interface for selecting concrete resources based on the current device
 * and requested abstract path.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
//TODO: I suspect that all methods should either declare a thrown IOException or some
// crf specific wrapper.
public interface ResourceSelector {

    /**
     * Return the first concrete resource path for the requested
     * abstract path.
     *
     * @param device
     *            {@link Device} for the request.
     * @param requestedResourcePath
     *            Path of the resource requested. This should be relative to the
     *            webapp's context root.
     * @return Return the first concrete resource path for the requested
     *         abstract path. If none is found, null is returned.
     * @throws IOException
     */
    // TODO: reconsider IOException
    MappedResourcePath getResourcePath(Device device, String requestedResourcePath) throws IOException;

    /**
     * List of all concrete resource paths (not just the first found)
     * that correspond to the requested abstract path. May not be null or empty.
     *
     *
     * @param device
     *            {@link Device} for the request.
     * @param requestedResourcePath
     *            Path of the resource requested. This should be relative to the
     *            webapp's context root.
     * @return List of all concrete resource paths that correspond to the
     *         requested path. May not be null. If no concrete resource paths are found,
     *         an empty {@link List} is returned.
     * @throws IOException
     */
    // TODO: reconsider IOException
    List<MappedResourcePath> getAllResourcePaths(Device device, String requestedResourcePath) throws IOException;
}
