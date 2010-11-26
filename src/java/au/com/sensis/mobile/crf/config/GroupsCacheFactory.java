package au.com.sensis.mobile.crf.config;

/**
 * Simple factory for a {@link GroupsCache}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public interface GroupsCacheFactory {

    /**
     * @return {@link GroupsCache} instance. Whether this factory creates a
     *         singleton cache or a new one per invocation is implementation
     *         dependent.
     */
    GroupsCache createGroupsCache();
}
