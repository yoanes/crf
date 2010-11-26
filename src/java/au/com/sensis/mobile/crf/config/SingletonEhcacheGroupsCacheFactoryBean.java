package au.com.sensis.mobile.crf.config;

/**
 * Factory for {@link SimpleGroupsCacheBean}s.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class SingletonEhcacheGroupsCacheFactoryBean implements GroupsCacheFactory {

    private final GroupsCache groupsCache;

    /**
     * Constructor.
     *
     * @param groupsCache Singleton for this factory to return.
     */
    public SingletonEhcacheGroupsCacheFactoryBean(final GroupsCache groupsCache) {
        this.groupsCache = groupsCache;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GroupsCache createGroupsCache() {
        return groupsCache;
    }
}
