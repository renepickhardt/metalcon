package de.metalcon.storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Jonas Kunze
 */
public class PersistentUUIDSet implements IPersistentUUIDSet {
	/*
	 * The number of currently opened file handles
	 */
	private static final AtomicInteger NumberOfOpenFileHandles = new AtomicInteger(
			0);

	/*
	 * The maximum number of open file handles allowed.
	 */
	private static final int MaximumOpenFileHandles = 500;

	/*
	 * Path to the file to be used to store this Set
	 */
	private final String fileName;

	private RandomAccessFile file = null;

	/*
	 * This Map stores all uuids that exist in the file as keys and the position
	 * (number of longs) in the persistent file as value
	 */
	private HashMap<Long, Long> posByUUID = null;

	private long numberOfZerosInFile = 0;

	/*
	 * Number of longs in the file (incuding zeros from fragmentation)
	 */
	public long length = 0;

	/**
	 * 
	 * @param fileName
	 *            The file this Set will be stored at
	 * @throws IOException
	 *             if the file could not be read
	 */
	public PersistentUUIDSet(final String fileName) throws IOException {
		this.fileName = fileName;
		loadFile();
		closeFileIfNecessary();
	}

	/**
	 * 
	 * @return The number of empty slots in the array
	 */
	public float getFragmentationRatio() {
		if (length == 0) {
			return 0;
		}
		return numberOfZerosInFile / ((float) length);
	}

	/**
	 * Reads the file content and loads it to the index maps
	 * 
	 * @throws IOException
	 */
	private void loadFile() throws IOException {
		openFileIfClosed();

		posByUUID = new HashMap<Long, Long>();

		length = file.length() / 8;
		if (length == 0) {
			return;
		}

		numberOfZerosInFile = 0;
		for (long pos = 0; pos < length; pos++) {
			long uuid = file.readLong();
			if (uuid == 0) {
				numberOfZerosInFile++;
			} else {
				posByUUID.put(uuid, pos);
			}
		}
	}

	/**
	 * Opens the file handle if it was closed
	 * 
	 * @throws FileNotFoundException
	 */
	private void openFileIfClosed() throws FileNotFoundException {
		if (file == null) {
			NumberOfOpenFileHandles.incrementAndGet();
			file = new RandomAccessFile(fileName, "rw");
		}
	}

	/**
	 * Close the file handle. It will be automatically reopened as soon as a
	 * disk access is needed.
	 * 
	 * @return true if the handle was opened and we were able to close it
	 */
	private boolean closeFile() {
		if (file != null) {
			try {
				NumberOfOpenFileHandles.decrementAndGet();
				file.close();
				file = null;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
			return true;
		}
		return false;
	}

	/**
	 * Close the file handle if too many are opened. It will be automatically
	 * reopened as soon as a disk access is needed.
	 */
	public void closeFileIfNecessary() {
		if (NumberOfOpenFileHandles.get() > MaximumOpenFileHandles) {
			closeFile();
		}
	}

	/**
	 * Adds the given uuid to the end of the file
	 */
	public boolean add(long uuid) {
		try {
			openFileIfClosed();
			file.seek(length++ * 8);
			file.writeLong(uuid);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		posByUUID.put(uuid, length);

		return true;
	}

	/**
	 * Deletes the persistent file
	 */
	public void delete() {
		closeFile();
		File f = new File(fileName);
		f.renameTo(new File(fileName + ".deleted"));
	}

	@Override
	public boolean add(Long uuid) {
		return add((long) uuid);
	}

	@Override
	public boolean addAll(Collection<? extends Long> arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean contains(Object obj) {
		return posByUUID.containsKey(obj);
	}

	@Override
	public boolean containsAll(Collection<?> arg0) {
		return posByUUID.keySet().containsAll(arg0);
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Iterator<Long> iterator() {
		return posByUUID.keySet().iterator();
	}

	/**
	 * 
	 * @param uuid
	 *            Thee uuid to be removed
	 * @return Returns true if this set contained the uuid
	 */
	public boolean remove(long uuid) {
		Long positionInFile = posByUUID.get((Long) uuid);
		if (positionInFile != null) {
			posByUUID.remove(uuid);

			try {
				openFileIfClosed();
				file.seek(positionInFile * 8);
				file.writeLong(0);
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean remove(Object uuid) {
		return remove((long) uuid);
	}

	@Override
	public boolean removeAll(Collection<?> arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean retainAll(Collection<?> arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * 
	 * @return The number of elements in this set
	 */
	public long getSize() {
		return length - numberOfZerosInFile;
	}

	/*
	 * @deprecated Use getSize() to have full long prezision
	 * 
	 * @see java.util.Set#size()
	 */
	@Override
	@Deprecated
	public int size() {
		long elements = length - numberOfZerosInFile;
		if (elements > Integer.MAX_VALUE) {
			return Integer.MAX_VALUE;
		}
		return (int) (elements);
	}

	@Override
	public Object[] toArray() {
		return posByUUID.keySet().toArray();
	}

	/**
	 * Returns an array with all uuids
	 * 
	 * @param array
	 * @return
	 */
	public long[] toArray(long[] array) {
		array = new long[(int) getSize()];
		int i = 0;
		for (Long n : this) {
			array[i++] = n;
		}
		return array;
	}

	@Override
	public <T> T[] toArray(T[] obj) {
		return posByUUID.keySet().toArray(obj);
	}
}
