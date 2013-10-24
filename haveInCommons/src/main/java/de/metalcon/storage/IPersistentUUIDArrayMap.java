package de.metalcon.storage;

import java.util.HashMap;

/**
 * @author Jonas Kunze
 */
public interface IPersistentUUIDArrayMap {
	/**
	 * @return The timestamp of the last update
	 */
	public int getLastUpdateTimeStamp();

	/**
	 * 
	 * @param updateTimeStamp
	 *            The timestamp of the last update
	 */
	public void setUpdateTimeStamp(final int updateTimeStamp);

	/**
	 * Adds the given valueUUID to the long[] mapped to keyUUID if and only if
	 * it did not yet exist in the array
	 * 
	 * @param keyUUID
	 *            The key whose associated array should be appended
	 * @param valueUUID
	 *            The value to be added to the array
	 */
	public void append(final long keyUUID, final long valueUUID);

	/**
	 * Returns the long[] mapped to the keyUUID
	 * 
	 * @param keyUUID
	 *            The key whose associated value is to be returned
	 * @return the value to which the specified key is mapped, or null if this
	 *         map contains no mapping for the key
	 * @see HashMap#get(Object)
	 */
	public long[] get(final long keyUUID);

	/**
	 * Removes the element valueUUID from the list associated with the given
	 * keyUUID
	 * 
	 * @param keyUUID
	 *            The key of the list
	 * @param valueUUID
	 *            The element to be deleted from the list
	 */
	public void removeKey(final long keyUUID);

	/**
	 * Removes the element valueUUID from the list associated with the given
	 * keyUUID
	 * 
	 * @param keyUUID
	 *            The key of the list
	 * @param valueUUID
	 *            The element to be deleted from the list
	 */
	public void remove(final long keyUUID, final long valueUUID);

	/**
	 * Will delete all elements in this map
	 */
	public void removeAll();

	public void save();
}
