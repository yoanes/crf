package au.com.sensis.mobile.crf.config;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.log4j.Logger;

/**
 * Simple {@link GroupsCache} implementation that stores all entries in memory
 * and does not replicate across a cluster.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class SimpleGroupsCacheBean implements GroupsCache {

    private static final Logger LOGGER = Logger.getLogger(SimpleGroupsCacheBean.class);

    private boolean cacheEnabled = true;
    private ConcurrentMap<String, Group[]> groupsMap;

    /**
     * Constructor.
     *
     * @param cacheEnabled
     *            True if the cache should be enabled.
     */
    public SimpleGroupsCacheBean(final boolean cacheEnabled) {
        this.cacheEnabled = cacheEnabled;
        setGroupsMap(new ConcurrentHashMap<String, Group[]>());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean contains(final String userAgent) {
        if (isEnabled()) {
            return getGroupsMap().containsKey(userAgent);
        } else {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Group[] get(final String userAgent) {
        if (isEnabled()) {
            return getGroupsMap().get(userAgent);
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void put(final String userAgent, final Group[] groups) {
        if (isEnabled()) {
            getGroupsMap().put(userAgent, groups);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEnabled() {
        debugLogCacheEnabledState();

        return cacheEnabled;
    }

    private void debugLogCacheEnabledState() {
        if (LOGGER.isDebugEnabled()) {
            if (cacheEnabled) {
                LOGGER.debug("cache is enabled so it will be consulted.");
            } else {
                LOGGER.debug("cache is disabled so it will be ignored.");
            }
        }
    }

    /**
     * @return the groupsMap
     */
    private ConcurrentMap<String, Group[]> getGroupsMap() {
        return groupsMap;
    }

    /**
     * @param groupsMap the groupsMap to set
     */
    private void setGroupsMap(final ConcurrentMap<String, Group[]> groupsMap) {
        this.groupsMap = groupsMap;
    }

}
