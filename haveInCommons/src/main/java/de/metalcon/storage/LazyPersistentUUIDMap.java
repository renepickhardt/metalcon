package de.metalcon.storage;

import java.util.HashMap;

/**
 * @author Jonas Kunze
 */
public class LazyPersistentUUIDMap extends HashMap<Long, long[]> implements
		IPersistentUUIDArrayMap {
	private static final long serialVersionUID = 6847864014049339099L;

	private static final int InitialArrayLength = 4;
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
	public void save() {
		Serialization.writeObjectToFile(this, fileName);
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

	@Override
	public void append(long keyUUID, long valueUUID) {
		long[] valueArray = get(keyUUID);

		int lastEmptyPointer = 0;
		if (valueArray == null) {
			valueArray = new long[InitialArrayLength];
			this.put(keyUUID, valueArray);
		} else {

			/*
			 * Seek the last 0 element or return if addUUID is already in the
			 * array
			 * 
			 * If neither any 0-element nor addUUID have been found
			 * lastEmptyPointer will be commons.length after this loop
			 */
			while (lastEmptyPointer != valueArray.length) {
				long current = valueArray[lastEmptyPointer];
				if (current == valueUUID) {
					return;
				}
				if (current == 0) {
					break;
				}
				lastEmptyPointer++;
			}
			/*
			 * No empty position found. Array has to be extended
			 */
			if (lastEmptyPointer == valueArray.length) {
				int newLength = (int) (2 * valueArray.length + 1);
				int oldLength = valueArray.length;
				long[] tmp = new long[newLength];
				System.arraycopy(valueArray, 0, tmp, 0, oldLength);
				valueArray = tmp;
				this.put(keyUUID, valueArray);
			}
		}
		valueArray[lastEmptyPointer] = valueUUID;
	}

	@Override
	public long[] get(long keyUUID) {
		return super.get(keyUUID);
	}

	@Override
	public void removeKey(long keyUUID) {
		super.remove(keyUUID);
	}

	@Override
	public void remove(long keyUUID, long valueUUID) {
		// TODO To be implemented
		// for (int i = 0; i < commons.length; i++) {
		// if (commons[i] == UUID) {
		// System.arraycopy(commons, i + 1, commons, i, commons.length - i);
		// commons[commons.length - 1] = 0; // remove duplicate last
		// // element
		// return commons;
		// }
		// }

	}

	@Override
	public void removeAll() {
		// TODO To be implemented

	}
}
