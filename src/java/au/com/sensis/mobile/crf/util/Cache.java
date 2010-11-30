package au.com.sensis.mobile.crf.util;

/**
 * Simple interface for storing a cache of (K, V) pairs.
 *
 * @param <K> Key type.
 * @param <V> Value tyep.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public interface Cache<K, V> {

    /**
     * Put element into this cache.
     *
     * @param key Key of the item to add.
     * @param values Values  to be put into this cache.
     */
    void put(K key, V [] values);

    /**
     *
     * @param key Key to lookup in this cache.
     * @return Values corresponding to the key.
     */
    V [] get(K key);

    /**
     * @param key Key to lookup in this cache.
     * @return true if this cache contains an entry for the key.
     */
    boolean contains(K key);

    /**
     * @return true if this cache is enabled.
     */
    boolean isEnabled();

    /**
     * Remove all elements from the cache.
     */
    void removeAll();

}
