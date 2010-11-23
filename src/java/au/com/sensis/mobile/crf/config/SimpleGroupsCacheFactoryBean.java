package au.com.sensis.mobile.crf.config;

/**
 * Factory for {@link SimpleGroupsCacheBean}s.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class SimpleGroupsCacheFactoryBean implements GroupsCacheFactory {

    private boolean cacheEnabled = true;

    /**
     * Constructor.
     *
     * @param cacheEnabled True if the created cache objects should be enabled.
     */
    public SimpleGroupsCacheFactoryBean(final boolean cacheEnabled) {
        this.cacheEnabled = cacheEnabled;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GroupsCache createGroupsCache() {
        return new SimpleGroupsCacheBean(isCacheEnabled());
    }

    private boolean isCacheEnabled() {
        return cacheEnabled;
    }

}
