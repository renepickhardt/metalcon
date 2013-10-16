package de.metalcon.storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel.MapMode;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Jonas Kunze
 * 
 *         This class can be used to store a HashMap<Long,long[]> on the disk an
 *         access it in a performant way. It uses two memory mapped files to
 *         store keys and values separated.
 */
class ValueArrayPointer {
	/*
	 * Pointer to the first byte of the value array in the value file
	 */
	long pointer;
	/*
	 * Number of Longs in the array
	 */
	int length;

	ValueArrayPointer(long pointer, int length) {
		this.pointer = pointer;
		this.length = length;
	}
}

/**
 * @author Jonas Kunze
 * 
 */
public class PersistentUUIDArrayMap implements IPersistentUUIDArrayMap {
	private HashMap<Long, long[]> mainMap;

	/*
	 * Number of bytes per key entry in the key file: long keyUUID long
	 * valueArrayPointer int valueArrayLength
	 */
	private static final int BytesPerKeyEntry = 20;

	private static final int InitialValueArrayLength = 4;
	private static final float ValueArrayGrowthFactor = 1.2f;

	/*
	 * The number of currently opened file handles
	 */
	private static final AtomicInteger NumberOfOpenFileHandles = new AtomicInteger(
			0);

	/*
	 * The maximum number of open file handles allowed.
	 */
	private static final int MaximumOpenFileHandles = 300;

	/*
	 * Path to the file storing the keys
	 */
	private final String keyFileName;

	/*
	 * Path to the file storing the values
	 */
	private final String valueFileName;

	/*
	 * The timestamp when this Map has been updated
	 */
	private int lastUpdateTS = 0;

	/*
	 * Stores the position p of the key k in keyMap.put(k,p). The position is
	 * the number of key-lines in the key file. Keys are ValueArrayPointer
	 */
	private HashMap<Long, Long> keyPositions = null;

	/*
	 * All pointers to the value arrays by the uuid key
	 */
	private HashMap<Long, ValueArrayPointer> arrayPointers = null;

	/*
	 * Number of bytes in the key file
	 */
	private long keyFileSize = 0;

	/*
	 * Number of bytes in the key file
	 */
	private long valueFileSize = 0;

	/*
	 * Number of zero elements in the key file (fragmentation)
	 */
	private int numberOfZerosInKeyFile;

	/*
	 * The first long of each files is the length of the file in bytes
	 */
	private RandomAccessFile keyRAFile = null;
	private RandomAccessFile valueRAFile = null;
	private MappedByteBuffer valueFile = null;
	private MappedByteBuffer keyFile = null;
	private long keyFileBufferSize = 0;
	private long valueFileBufferSize = 0;

	/**
	 * 
	 * @param fileName
	 *            Path to the file storing the requested Map. If the file
	 *            doesn't exist, a new Instance will be initialized and returned
	 * 
	 */
	public PersistentUUIDArrayMap(final String fileName) throws IOException {
		this.keyFileName = fileName + "_keys";
		this.valueFileName = fileName + "_values";

		mainMap = new HashMap<Long, long[]>();

		loadFile();
		closeFileIfNecessary();
	}

	/**
	 * Opens the file handle if it was closed
	 * 
	 * @throws FileNotFoundException
	 */
	private void openFilesIfClosed() {
		if (keyFile == null) {
			openFiles();
		}
	}

	/**
	 * Creates the memory mapped files
	 */
	private void openFiles() {
		try {
			NumberOfOpenFileHandles.addAndGet(2);
			keyRAFile = new RandomAccessFile(keyFileName, "rw");
			updateMemoryMappedKeyBuffer(keyRAFile.length());
			valueRAFile = new RandomAccessFile(valueFileName, "rw");
			updateMemoryMappedValueBuffer(valueRAFile.length());
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	private void updateMemoryMappedKeyBuffer(final long minimumBufferSize) {
		try {
			if (minimumBufferSize < 4096)
				keyFileBufferSize = 4096;
			else
				keyFileBufferSize = minimumBufferSize;

			keyFile = keyRAFile.getChannel().map(MapMode.READ_WRITE, 0,
					keyFileBufferSize);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	private void updateMemoryMappedValueBuffer(final long minimumBufferSize) {
		try {
			if (minimumBufferSize < 4096)
				valueFileBufferSize = 4096;
			else
				valueFileBufferSize = minimumBufferSize;

			valueFile = valueRAFile.getChannel().map(MapMode.READ_WRITE, 0,
					valueFileBufferSize);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * Reads the file content and loads it to the index maps
	 * 
	 * @throws IOException
	 * 
	 */
	private void loadFile() throws IOException {
		openFiles();

		keyPositions = new HashMap<Long, Long>();
		arrayPointers = new HashMap<Long, ValueArrayPointer>();

		keyFileSize = keyFile.getLong();
		if (keyFileSize == 0) {
			/*
			 * Whenever we want to append something, we should start at byte 8
			 * as the first 8 bytes are reserved for the file length
			 */
			keyFileSize = 8;
			valueFileSize = 8;
			return;
		}
		valueFileSize = valueFile.getLong();

		numberOfZerosInKeyFile = 0;

		/*
		 * Read each key file line
		 */
		for (long elementNum = 0; elementNum < keyFileSize / BytesPerKeyEntry; elementNum += BytesPerKeyEntry) {
			long uuid = keyFile.getLong();
			long pointer = keyFile.getLong();
			int length = keyFile.getInt();
			if (uuid == 0) {
				numberOfZerosInKeyFile++;
			} else {
				keyPositions.put(uuid, 8 + elementNum * BytesPerKeyEntry);
				arrayPointers.put(uuid, new ValueArrayPointer(pointer, length));
				/*
				 * Now read the value file
				 */

				valueFile.position((int) pointer);
				long[] valueUUIDs = new long[length];
				for (int i = 0; i < length; ++i) {
					valueUUIDs[i] = valueFile.getLong();
				}
				mainMap.put(uuid, valueUUIDs);
			}
		}
	}

	/**
	 * Close the file handle. It will be automatically reopened as soon as a
	 * disk access is needed.
	 * 
	 * @return true if the handles were opened and we were able to close it
	 */
	public boolean closeFile() {
		boolean success = false;
		try {
			if (keyFile != null) {
				NumberOfOpenFileHandles.decrementAndGet();
				keyRAFile.close();
				keyFile = null;
				success = true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			if (valueFile != null) {
				NumberOfOpenFileHandles.decrementAndGet();
				valueRAFile.close();
				valueFile = null;
			}
		} catch (IOException e) {
			e.printStackTrace();
			success = false;
		}

		return success;
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

	/**
	 * 
	 * @param keyUUID
	 * @param valueUUID
	 */
	public void append(final long keyUUID, final long valueUUID) {
		openFilesIfClosed();
		try {
			if (!writeIntoValueArray(keyUUID, valueUUID)) {
				/*
				 * Create a new long array, put the valueUUID to the first slot
				 * and put the whole array into this map with the given uuid as
				 * key
				 */
				long[] valueArray = new long[InitialValueArrayLength];
				valueArray[0] = valueUUID;
				mainMap.put(keyUUID, valueArray);

				/*
				 * Append the new value array to the end of the value file
				 */
				writeArrayToValueFile(valueArray, valueFileSize);

				/*
				 * The new array has been appended to the end of the value file.
				 * The pointer to the array therefore is the old valueFilesSize
				 */
				ValueArrayPointer pointer = new ValueArrayPointer(
						valueFileSize, valueArray.length);
				valueFileSize += valueArray.length * 8;

				updateMemoryMappedValueBuffer(valueFileSize);

				valueFile.putLong(0, valueFileSize); // udpate length

				writePointerToKeyFile(keyUUID, pointer);
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * Seeks the first 0 element in the value array and puts addUUID into that
	 * position. If the array is already full its length will be extended by a
	 * factor of ValueArrayGrowthFactor
	 * 
	 * @param array
	 *            The array addUUID will be added to
	 * @param valueUUID
	 *            The uuid to be added
	 * @return true if the keySet already contained keyUUID
	 */
	static int i = 0;

	private boolean writeIntoValueArray(final long keyUUID, final long valueUUID) {
		int firstEmtpyLongPointer = 0;
		ValueArrayPointer pointer = arrayPointers.get(keyUUID);
		if (pointer == null) {
			return false;
		}

		long[] array = mainMap.get(keyUUID);
		/*
		 * Seek the last 0 element or return if addUUID is already in the array
		 * 
		 * If neither any 0-element nor addUUID have been found lastEmptyPointer
		 * will be commons.length after this loop
		 */
		while (firstEmtpyLongPointer != array.length) {
			long current = array[firstEmtpyLongPointer];
			if (current == valueUUID) {
				return true;
			}
			if (current == 0) {
				break;
			}
			firstEmtpyLongPointer++;
		}

		if (firstEmtpyLongPointer != array.length) {
			/*
			 * Still space in the array. Just jump to the beginning of the array
			 * (pointer.pointer) plus the relative position of the first empty
			 * element in the array (lastEmptyPointer)
			 */
			valueFile.putLong(
					(int) pointer.pointer + firstEmtpyLongPointer * 8,
					valueUUID);
			/*
			 * Now update the array in the cache
			 */
			array[firstEmtpyLongPointer] = valueUUID;
		} else {
			/*
			 * No empty position found. Array has to be extended
			 */
			int newLength = (int) (ValueArrayGrowthFactor * array.length + 1);
			int oldLength = array.length;
			long[] tmp = new long[newLength];
			System.arraycopy(array, 0, tmp, 0, oldLength);
			array = tmp;

			array[firstEmtpyLongPointer] = valueUUID;
			/*
			 * Append the new array to the end of the value file
			 */
			try {
				pointer.length = array.length;
				pointer.pointer = valueFileSize;
				valueFileSize += writeArrayToValueFile(array, valueFileSize);
				valueFile.putLong(0, valueFileSize);

				writePointerToKeyFile(keyUUID, pointer);
			} catch (IOException e) {
				e.printStackTrace();
			}
			mainMap.put(keyUUID, array);
		}

		return true;
	}

	/**
	 * 
	 * @param keyUUID
	 * @param valueUUID
	 */
	public void remove(final long keyUUID, final long valueUUID) {
		/*
		 * Seek the element in commons with the value UUID and move all elements
		 * behind on to the left
		 */
		// for (int i = 0; i < commons.length; i++) {
		// if (commons[i] == UUID) {
		// System.arraycopy(commons, i + 1, commons, i, commons.length - i);
		// commons[commons.length - 1] = 0; // remove duplicate last
		// // element
		// return commons;
		// }
		// }
	}

	/**
	 * Overwrites the key in the given uuid in the key file
	 * 
	 * @param uuid
	 *            The key to be overwritten
	 * @return true if the key existed
	 * @throws IOException
	 */
	private boolean eraseKey(final long uuid) throws IOException {
		Long pos = keyPositions.get(uuid);
		if (pos != null) {
			keyFile.position(pos.intValue());
			keyFile.putLong(0);
			keyFile.putLong(0);
			keyFile.putInt(0);
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @param p
	 *            The Pointer to be written to file
	 * @param position
	 *            The position (number of bytes) in the file where p should be
	 *            written to
	 * @return The number of bytes written to file
	 * @throws IOException
	 */
	private int writePointerToKeyFile(final long uuid, final ValueArrayPointer p)
			throws IOException {
		Long position = keyPositions.get(uuid);
		if (position == null) {
			position = keyFileSize;
			if (keyFileBufferSize <= keyFileSize + BytesPerKeyEntry) {
				updateMemoryMappedKeyBuffer(keyFileSize + BytesPerKeyEntry);
			}
			keyFileSize += BytesPerKeyEntry;
			keyFile.putLong(0, keyFileSize);
			keyPositions.put(uuid, position);
		}

		keyFile.position(position.intValue());
		keyFile.putLong(uuid);
		keyFile.putLong(p.pointer);
		keyFile.putInt(p.length);

		arrayPointers.put(uuid, p);
		return BytesPerKeyEntry;
	}

	/**
	 * 
	 * @param array
	 *            The array to be written
	 * @param position
	 *            The position (number of bytes) in the file where array should
	 *            be written to
	 * @return The number of bytes written to file
	 * @throws IOException
	 */
	private int writeArrayToValueFile(final long[] array, final long position)
			throws IOException {
		if (valueFileBufferSize <= valueFileSize + array.length * 8) {
			updateMemoryMappedValueBuffer(valueFileSize + array.length * 8);
		}

		valueFile.position((int) position);
		for (long l : array) {
			valueFile.putLong(l);
		}
		return array.length * 8;
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
		return mainMap.get(keyUUID);
	}

	public void print() {
		System.out.println(valueFileName + ":");
		for (Long key : mainMap.keySet()) {
			System.out.print("\t" + key + "\n\t\t");
			for (long l : mainMap.get(key)) {
				System.out.print(l + "\t");
			}
			System.out.println();
		}
	}

	@Override
	public void removeKey(long keyUUID) {
		// TODO To be implemented
	}

	@Override
	public void removeAll() {
		/**
		 * Delete the corresponding file
		 */
		File file = new File(keyFileName);
		file.renameTo(new File(keyFileName + ".deleted"));

		file = new File(valueFileName);
		file.renameTo(new File(valueFileName + ".deleted"));

		mainMap.clear();
		keyPositions.clear();
		arrayPointers.clear();

		valueFileSize = 8;
		keyFileSize = 8;
	}

	@Override
	public void save() {
		// TODO Auto-generated method stub

	}
}
