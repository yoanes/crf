package au.com.sensis.mobile.crf.service;

import java.io.IOException;
import java.util.List;

import au.com.sensis.mobile.crf.config.Group;

/**
 * Maps requested resource paths to real resource paths.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public interface ResourcePathMapper {

    /**
     * Maps a requested resource path to a path that corresponds to the given
     * {@link Group}.
     *
     * @param requestedResourcePath
     *            Requested path. eg. /WEB-INF/view/jsp/detal/bdp.crf.
     * @param group
     *            {@link Group} to perform the path mapping for.
     * @return List of {@link MappedResourcePath} containing the results. If
     *         resources can be resolved, an empty list is returned. May not be
     *         null.
     * @throws IOException Thrown if an IO error occurs.
     */
    List<MappedResourcePath> resolve(String requestedResourcePath, Group group) throws IOException;

}
