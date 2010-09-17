package au.com.sensis.mobile.crf.service;

import au.com.sensis.mobile.crf.config.Group;

/**
 * Maps requested resource paths to real resource paths.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public interface ResourcePathMapper {

    /**
     * Maps a requested resource path to a path that corresponds to the given {@link Group}.
     *
     * @param requestedResourcePath Requested path. eg. /WEB-INF/view/jsp/detal/bdp.crf.
     * @param group {@link Group} to perform the path mapping for.
     * @return {@link MappedResourcePath} containing the results of the mapping. If no mapping
     * is found, {@link NullMappedResourcePath} is returned.
     */
    MappedResourcePath mapResourcePath(String requestedResourcePath, Group group);

}
