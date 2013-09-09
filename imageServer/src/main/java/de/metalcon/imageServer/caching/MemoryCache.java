package de.metalcon.imageServer.caching;

import java.util.HashMap;
import java.util.Map;

/**
 * memory caching layer
 * 
 * @author sebschlicht
 * 
 * @param <T>
 *            class of the objects you want to cache
 */
public class MemoryCache<T> {

	private final long expires;

	private final int numMaxElements;

	private int numStrongElements;

	/**
	 * cashing map
	 */
	private final Map<Object, CacheEntry<T>> cache;

	private final LinkedList<T> normal;
	private final LinkedList<T> strong;

	/**
	 * create a new memory cache
	 * 
	 * @param numMaxElements
	 *            maximum number of elements in the cache
	 */
	public MemoryCache(final int numMaxElements, final long expires) {
		this.expires = expires;
		this.numMaxElements = numMaxElements;

		final int capacity = (int) (numMaxElements / 0.75) + 1;
		this.cache = new HashMap<Object, CacheEntry<T>>(capacity);
		this.normal = new LinkedList<T>();
		this.strong = new LinkedList<T>();
	}

	public boolean cacheObject(final Object key, final T value,
			final boolean strong) {
		LinkedList<T> list = this.normal;
		ListItem<T> item = this.normal.getLast();

		// check if the cache is full
		if (this.numMaxElements == this.cache.size()) {
			final long now = System.currentTimeMillis();
			final ListItem<T> strongItem = this.strong.getLast();

			// check if a strong cached object has expired
			if (strongItem != null) {
				if ((now - strongItem.getValue().getLastAccess()) > this.expires) {
					list = this.strong;
					item = strongItem;
					this.numStrongElements -= 1;
				} else if (this.numMaxElements == this.numStrongElements) {
					// cache is full
					return false;
				}
			}

			if (item != null) {
				// remove the least relevant object
				this.cache.remove(item.getValue().getKey());
				list.removeLast();
			}
		} else if (this.numMaxElements < this.cache.size()) {
			System.out.println("cache run out of control...");
			return false;
		}

		final CacheEntry<T> entry = new CacheEntry<T>(key, value, strong);
		if (strong) {
			this.strong.add(entry);
			this.numStrongElements += 1;
		} else {
			this.normal.add(entry);
		}
		this.cache.put(key, entry);

		return true;
	}

	/**
	 * get a cached object
	 * 
	 * @param key
	 *            key used when the object has been added to the cache
	 * @return cached object<br>
	 *         <b>null</b> if the object is not cached anymore
	 */
	public T getCachedObject(final Object key) {
		final CacheEntry<T> entry = this.cache.get(key);
		if (entry != null) {
			if (entry.getStrong()) {
				this.strong.moveToFront(entry.getItem());
			} else {
				this.normal.moveToFront(entry.getItem());
			}
			entry.setLastAccess(System.currentTimeMillis());
			return entry.getValue();
		}

		return null;
	}

	public int getFreeSpace() {
		return this.numMaxElements - this.cache.size();
	}

	public int getUsableSpace() {
		return this.numMaxElements - this.numStrongElements;
	}
}