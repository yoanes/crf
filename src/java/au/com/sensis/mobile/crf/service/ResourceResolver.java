package au.com.sensis.mobile.crf.service;

import java.io.IOException;
import java.util.List;

import au.com.sensis.mobile.crf.config.Group;

/**
 * Resolves requested resource paths to real resource paths.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public interface ResourceResolver {

    /**
     * Resolves a requested resource path to a real path that corresponds to the given
     * {@link Group}.
     *
     * @param requestedResourcePath
     *            Requested path. eg. /WEB-INF/view/jsp/detal/bdp.crf.
     * @param group
     *            {@link Group} to perform the path mapping for.
     * @return List of {@link Resource}s containing the results. If no
     *         resources can be resolved, an empty list is returned. May not be
     *         null.
     * @throws IOException Thrown if an IO error occurs.
     */
    List<Resource> resolve(String requestedResourcePath, Group group) throws IOException;

}
