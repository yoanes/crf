package au.com.sensis.mobile.crf.service;

import java.util.List;

import au.com.sensis.mobile.crf.exception.ResourceResolutionRuntimeException;
import au.com.sensis.wireless.common.volantis.devicerepository.api.Device;

/**
 * Resolves requested resource paths to real resource paths.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public interface ResourceResolver {

    /**
     * Resolves a requested resource path to a real path that corresponds to the
     * given {@link au.com.sensis.mobile.crf.config.Group}.
     *
     * @param requestedResourcePath
     *            Requested path. eg. /WEB-INF/view/jsp/detal/bdp.crf.
     * @param device
     *            {@link Device} to perform the path mapping for.
     * @return List of {@link Resource}s containing the results. If no resources
     *         can be resolved, an empty list is returned. May not be null.
     * @throws ResourceResolutionRuntimeException
     *             Thrown if any error occurs.
     */
    List<Resource> resolve(String requestedResourcePath, Device device)
    throws ResourceResolutionRuntimeException;

    /**
     * Returns true if this {@link ResourceResolver} supports resolution of the
     * requested resource.
     *
     * @param requestedResourcePath
     *            Requested path. eg. /WEB-INF/view/jsp/detal/bdp.crf.
     * @return true if this {@link ResourceResolver} supports resolution of the
     *         requested resource.
     */
    boolean supports(String requestedResourcePath);

}
