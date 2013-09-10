package de.metalcon.imageServer;

import de.metalcon.imageServer.caching.MemoryCache;

public class CacheTest {

	public static void main(String[] args) {
		MemoryCache<String> cache = new MemoryCache<String>(2, 1000 * 60 * 60
				* 24 * 3);
		final String id1 = "1";
		cache.cacheObject(id1, "huhu", false);
		if (cache.getCachedObject(id1) == null) {
			System.out.println("not cached!");
		} else {
			System.out.println("cached.");
		}

		final String id2 = "2";
		cache.cacheObject(id2, "heyho", true);
		if (cache.getCachedObject(id2) == null) {
			System.out.println("not cached!");
		} else {
			System.out.println("cached.");
		}

		cache.cacheObject("3", "wohoo", true);
		if (cache.getCachedObject(id1) == null) {
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