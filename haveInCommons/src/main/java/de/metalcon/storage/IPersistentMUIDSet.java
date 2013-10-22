package de.metalcon.storage;

import de.metalcon.like.Node;

/**
 * @author Jonas Kunze
 */
public interface IPersistentMUIDSet extends Iterable<Long> {
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
	 * Returns an array with all muid
	 * 
	 * @return An array with all muid in the list
	 */
	public long[] toArray();

	public boolean contains(long muid);

	public boolean remove(long muid);

	public boolean remove(final Node n);

	public void add(long uuid);

	public long size();
}
