package de.metalcon.imageStorageServer.caching;

/**
 * single value within the cache
 * 
 * @author sebschlicht
 * 
 * @param <T>
 *            class of the objects cached
 */
public class CacheEntry<T> {

	/**
	 * list item representing this entry
	 */
	private ListItem<T> item;

	/**
	 * timestamp of the last access
	 */
	private long lastAccess;

	/**
	 * caching key
	 */
	private final Object key;

	/**
	 * cached object
	 */
	private final T value;

	/**
	 * caching type
	 */
	private boolean strong;

	/**
	 * create a new cache entry
	 * 
	 * @param key
	 *            caching key
	 * @param value
	 *            cached object
	 * @param strong
	 *            caching type
	 */
	public CacheEntry(final Object key, final T value, final boolean strong) {
		this.lastAccess = 0;
		this.key = key;
		this.value = value;
		this.strong = strong;
	}

	/**
	 * @return list item representing this entry
	 */
	public ListItem<T> getItem() {
		return this.item;
	}

	/**
	 * set the list item
	 * 
	 * @param item
	 *            list item representing this entry
	 */
	public void setItem(final ListItem<T> item) {
		this.item = item;
	}

	/**
	 * @return caching key
	 */
	public Object getKey() {
		return this.key;
	}

	/**
	 * @return timestamp of the last access
	 */
	public long getLastAccess() {
		return this.lastAccess;
	}

	/**
	 * update the access timestamp
	 * 
	 * @param lastAccess
	 *            timestamp of the last access
	 */
	public void setLastAccess(final long lastAccess) {
		this.lastAccess = lastAccess;
	}

	/**
	 * @return cached object
	 */
	public T getValue() {
		return this.value;
	}

	/**
	 * @return caching type
	 */
	public boolean getStrong() {
		return this.strong;
	}

}