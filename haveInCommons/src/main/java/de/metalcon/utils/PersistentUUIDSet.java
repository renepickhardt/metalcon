package de.metalcon.utils;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class PersistentUUIDSet implements Set<Long> {
	private final String fileName;

	private RandomAccessFile file = null;

	/*
	 * This Map stores all uuids that exist in the file as keys and the position
	 * (nomber of longs) in the persistent file as value
	 */
	private HashMap<Long, Long> posByUUID = null;
	private HashMap<Long, Long> UUIDByPos = null;

	private long numberOfZerosInFile = 0;

	/*
	 * Number of longs in the file
	 */
	public long length = 0;

	/**
	 * 
	 * @return The number of empty slots in the array
	 */
	public float getFragmentationRatio() {
		return numberOfZerosInFile / ((float) length);
	}

	public PersistentUUIDSet(final String fileName) throws IOException {
		this.fileName = fileName;
		loadFile();
	}

	private void loadFile() throws IOException {
		if (file == null) {
			file = new RandomAccessFile(fileName, "rw");
		}

		posByUUID = new HashMap<Long, Long>();
		UUIDByPos = new HashMap<Long, Long>();

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
				UUIDByPos.put(pos, uuid);
			}
		}
	}

	/**
	 * Adds the given uuid to the end of the file
	 */
	public boolean add(long uuid) {
		try {
			file.seek(length);
			file.writeLong(uuid);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		posByUUID.put(uuid, length++);
		UUIDByPos.put(length, uuid);

		return true;
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
			UUIDByPos.remove(positionInFile);

			try {
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

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object[] toArray() {
		return posByUUID.keySet().toArray();
	}

	/**
	 * Writes all uuids into the given array
	 * 
	 * @param array
	 * @return
	 */
	public long[] toArray(long[] array) {
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
