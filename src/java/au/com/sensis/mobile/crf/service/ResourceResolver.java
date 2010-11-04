package au.com.sensis.mobile.crf.service;

import java.util.List;

import au.com.sensis.mobile.crf.config.Group;
import au.com.sensis.mobile.crf.exception.ResourceResolutionRuntimeException;

/**
 * Resolves requested resource paths to real resource paths.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public interface ResourceResolver {

    /**
     * Resolves a requested resource path to a real path that corresponds to the
     * given {@link Group}.
     *
     * @param requestedResourcePath
     *            Requested path. eg. /WEB-INF/view/jsp/detal/bdp.crf.
     * @param group
     *            {@link Group} to perform the path mapping for.
     * @param results
     *            The {@link ResourceAccumulator} to add the results to.
     * @return List of {@link Resource}s containing the results. If no resources
     *         can be resolved, an empty list is returned. May not be null.
     * @throws ResourceResolutionRuntimeException
     *             Thrown if any error occurs.
     */
    List<Resource> resolve(String requestedResourcePath, Group group, ResourceAccumulator results)
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
