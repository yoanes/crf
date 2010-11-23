package au.com.sensis.mobile.crf.config;

/**
 * Simple factory for a {@link GroupsCache}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public interface GroupsCacheFactory {

    /**
     * @return new {@link GroupsCache} instance.
     */
    GroupsCache createGroupsCache();
}
