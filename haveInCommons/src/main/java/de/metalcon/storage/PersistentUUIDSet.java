package de.metalcon.storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

import de.metalcon.like.Node;

/**
 * @author Jonas Kunze
 */
public class PersistentUUIDSet implements IPersistentMUIDSet {
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
	private long length = 0;

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
	public void add(long uuid) {
		try {
			openFileIfClosed();
			file.seek(length++ * 8);
			file.writeLong(uuid);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		posByUUID.put(uuid, length);
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
	public boolean contains(long uuid) {
		return posByUUID.containsKey(uuid);
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
	@Override
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
	public boolean remove(final Node n) {
		return remove(n.getUUID());
	}

	/**
	 * Returns an array with all uuids
	 * 
	 * @param array
	 * @return
	 */
	public long[] toArray() {
		long[] array = new long[(int) size()];
		int i = 0;
		for (Long n : this) {
			array[i++] = n;
		}
		return array;
	}

	/**
	 * 
	 * @return The number of elements in this set
	 */
	@Override
	public long size() {
		return length - numberOfZerosInFile;
	}
}
