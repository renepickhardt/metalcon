package de.metalcon.storage;

import java.util.Set;

/**
 * @author Jonas Kunze
 */
public interface IPersistentUUIDSet extends Set<Long> {

	/**
	 * Deletes all entries from the Set
	 */
	public void delete();

	/**
	 * Close the file handle if too many are opened. It will be automatically
	 * reopened as soon as a disk access is needed.
	 */
	public void closeFileIfNecessary();

	/**
	 * Returns an array with all uuids
	 * 
	 * @param array
	 *            Used for method overloading
	 * @return An array with all uuids in the set
	 */
	public long[] toArray(long[] array);
}
