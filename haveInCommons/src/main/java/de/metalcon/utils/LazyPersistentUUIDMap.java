package de.metalcon.utils;

import java.util.HashMap;

/**
 * @author Jonas Kunze
 */
public class LazyPersistentUUIDMap extends HashMap<Long, long[]> {
	private static final long serialVersionUID = 6847864014049339099L;

	/*
	 * Path to the file to be used to store this Set
	 */
	private final String fileName;

	/*
	 * The timestamp when this Map has been updated
	 */
	private int lastUpdateTS = 0;

	/**
	 * Factory method
	 * 
	 * @param fileName
	 *            Path to the file storing the requested Map. If the file
	 *            doesn't exist, a new Instance will be initialized and returned
	 * @return The PersistentUUIDMap mapping the given file
	 */
	public static LazyPersistentUUIDMap getPersistentUUIDMap(
			final String fileName) {
		LazyPersistentUUIDMap map = (LazyPersistentUUIDMap) Serialization
				.readObjectFromFile(fileName);
		if (map == null) {
			map = new LazyPersistentUUIDMap(fileName);
		}
		return map;
	}

	private LazyPersistentUUIDMap(final String fileName) {
		this.fileName = fileName;

	}

	/**
	 * Serializes the given commonsMap and writes it into the file with the
	 * given path
	 * 
	 * @return <code>true</code> in case of success
	 */
	public boolean save() {
		return Serialization.writeObjectToFile(this, fileName);
	}

	/**
	 * @return The timestamp of the last update
	 */
	public int getLastUpdateTimeStamp() {
		return lastUpdateTS;
	}

	/**
	 * 
	 * @param updateTimeStamp
	 *            The timestamp of the last update
	 */
	public void setUpdateTimeStamp(int updateTimeStamp) {
		this.lastUpdateTS = updateTimeStamp;
	}
}
