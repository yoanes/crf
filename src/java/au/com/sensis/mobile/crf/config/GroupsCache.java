package au.com.sensis.mobile.crf.config;


/**
 * Simple interface for storing a cache of (String userAgent, Group []) pairs.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public interface GroupsCache {

    /**
     * Put element into this cache.
     *
     * @param userAgent User agent key of the item to add.
     * @param groups Groups to be put into this cache.
     */
    void put(String userAgent, Group [] groups);

    /**
     *
     * @param userAgent User agent key to lookup in this cache.
     * @return Groups corresponding to the user agent.
     */
    Group [] get(String userAgent);

    /**
     * @param userAgent User agent key to lookup in this cache.
     * @return true if this cache contains an entry for the userAgent.
     */
    boolean contains(String userAgent);

    /**
     * @return true if this cache is enabled.
     */
    boolean isEnabled();
}
