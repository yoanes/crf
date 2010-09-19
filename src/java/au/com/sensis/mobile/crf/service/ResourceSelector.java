package au.com.sensis.mobile.crf.service;

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
     *         abstract path. If none is found, a {@link NullMappedResourcePath}
     *         is returned.
     */
    MappedResourcePath getResourcePath(Device device, String requestedResourcePath);

    /**
     * Return the first concrete resource path for the requested abstract path
     * and allowed wildcard extensions.
     *
     * @param device
     *            {@link Device} for the request.
     * @param requestedResourcePath
     *            Path of the resource requested. This should be relative to the
     *            webapp's context root.
     * @param wildcardExtensions
     *            Array of allowed wildcard extensions. eg. "png", "*".
     * @return Return the first concrete resource path for the requested
     *         abstract path. If none is found, a {@link NullMappedResourcePath}
     *         is returned.
     * @throws IllegalArgumentException
     *             Thrown if the requestedResourcePath already has an extension.
     */
    // TODO: merge with the simpler interface getResourcePath above.
    MappedResourcePath getResourcePathWithExtensions(Device device,
            String requestedResourcePath, String[] wildcardExtensions)
            throws IllegalArgumentException;

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
     *         requested path. May not be null or empty. If no concrete resource paths are found,
     *         a {@link List} with a single {@link NullMappedResourcePath} is returned.
     */
    List<MappedResourcePath> getAllResourcePaths(Device device, String requestedResourcePath);
}