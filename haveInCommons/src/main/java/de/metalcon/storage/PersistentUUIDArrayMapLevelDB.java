package de.metalcon.storage;

import java.util.HashMap;
import java.util.HashSet;

/**
 * @author Jonas Kunze
 */
public class PersistentUUIDArrayMapLevelDB implements IPersistentUUIDArrayMap {
	private static final int InitialArrayLength = 4;

	LevelDBHandler dbHandler;

	public PersistentUUIDArrayMapLevelDB(final long keyPrefix) {
		dbHandler = new LevelDBHandler(keyPrefix);
	}

	/**
	 * @return The timestamp of the last update
	 */
	@Override
	public int getLastUpdateTimeStamp() {
		return dbHandler.getInt("UpdateTS");
	}

	/**
	 * 
	 * @param updateTimeStamp
	 *            The timestamp of the last update
	 */
	@Override
	public void setUpdateTimeStamp(final int updateTimeStamp) {
		dbHandler.put("UpdateTS", updateTimeStamp);
	}

	/**
	 * 
	 * @param keyUUID
	 * @param valueUUID
	 */
	@Override
	public void append(final long keyUUID, final long valueUUID) {
		dbHandler.setAdd(keyUUID, valueUUID);
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
	@Override
	public long[] get(final long keyUUID) {
		return dbHandler.getLongs(keyUUID);
	}

	/**
	 * Removes all elements associated with the given keyUUID
	 * 
	 * @param keyUUID
	 *            The key of the list
	 */
	@Override
	public void removeKey(final long keyUUID) {
		dbHandler.removeKey(keyUUID);
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
	@Override
	public void remove(final long keyUUID, final long valueUUID) {
		/*
		 * TODO To be implemented
		 */
	}

	/**
	 * Will delete all elements in this map
	 */
	@Override
	public void removeAll() {
		/*
		 * TODO To be implemented
		 */
	}

	@Override
	public void save() {
		// TODO Auto-generated method stub
	}
}
