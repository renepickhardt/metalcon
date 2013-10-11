package de.metalcon.utils;

import java.util.HashMap;
import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author Jonas Kunze
 */
public class PersistentUUIDArrayMapRedis {

	private final static JedisPool pool = new JedisPool(new JedisPoolConfig(),
			"localhost");
	private final static Jedis jedis = pool.getResource();

	private final String prefix;

	public PersistentUUIDArrayMapRedis(final String prefix) {
		this.prefix = prefix;
	}

	/**
	 * @return The timestamp of the last update
	 */
	public int getLastUpdateTimeStamp() {
		try {
			return Integer.parseInt(jedis.get(prefix + "UpdateTS"));
		} catch (final NumberFormatException e) {
			return 0;
		}
	}

	/**
	 * 
	 * @param updateTimeStamp
	 *            The timestamp of the last update
	 */
	public void setUpdateTimeStamp(final int updateTimeStamp) {
		jedis.set(prefix + "UpdateTS", Integer.toString(updateTimeStamp));
	}

	/**
	 * 
	 * @param keyUUID
	 * @param valueUUID
	 */
	public void append(final long keyUUID, final long valueUUID) {
		jedis.zadd(prefix + Long.toString(keyUUID), 0, Long.toString(valueUUID));
	}

	/**
	 * Returns the long[] to which the keyUUID is mapped
	 * 
	 * @param keyUUID
	 *            The key whose associated value is to be returned
	 * @return the value to which the specified key is mapped, or null if this
	 *         map contains no mapping for the key
	 * @see HashMap#get(Object)
	 */
	public long[] get(final long keyUUID) {
		Set<String> strings = jedis.zrange(prefix + Long.toString(keyUUID), 0,
				-1);
		long[] result = new long[strings.size()];
		int pointer = 0;
		for (String s : strings) {
			result[pointer++] = Long.valueOf(s);
		}
		return result;
	}

	/**
	 * Removes the element valueUUID from the list associated with the given
	 * keyUUID
	 * 
	 * @param keyUUID
	 *            The key of the list
	 * @param valueUUID
	 *            The element to be deleted from the list
	 */
	public void removeKey(final long keyUUID) {
		jedis.del(prefix + keyUUID);
	}

	/**
	 * Removes the element valueUUID from the list associated with the given
	 * keyUUID
	 * 
	 * @param keyUUID
	 *            The key of the list
	 * @param valueUUID
	 *            The element to be deleted from the list
	 */
	public void remove(final long keyUUID, final long valueUUID) {
		jedis.zrem(prefix + keyUUID, "" + valueUUID);
	}

	/**
	 * Will delete all elements in this map
	 */
	public void removeAll() {
		for (String key : jedis.keys(prefix + "*")) {
			jedis.del(key);
		}
	}
}
