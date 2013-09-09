package de.metalcon.imageServer;

import de.metalcon.imageServer.caching.MemoryCache;

public class CacheTest {

	public static void main(String[] args) {
		MemoryCache<String> cache = new MemoryCache<String>(2, 1000 * 60 * 60
				* 24 * 3);
		cache.cacheObject("1", "huhu", false);
		if (cache.getCachedObject("1") == null) {
			System.out.println("not cached!");
		} else {
			System.out.println("cached.");
		}

		cache.cacheObject("2", "heyho", true);
		if (cache.getCachedObject("2") == null) {
			System.out.println("not cached!");
		} else {
			System.out.println("cached.");
		}

		cache.cacheObject("3", "wohoo", true);
		if (cache.getCachedObject("1") == null) {
			System.out.println("cache full, reorganizing...");
		} else {
			System.out.println("cache limitation ignored!");
		}
		if (cache.getCachedObject("3") == null) {
			System.out.println("not cached!");
		} else {
			System.out.println("cached.");
		}

		if (cache.cacheObject("4", "ohno", true)) {
			System.out.println("cache overwritten!");
		} else {
			System.out.println("cache full.");
		}
	}

}