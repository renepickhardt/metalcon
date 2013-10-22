package de.metalcon.storage;

import static org.fusesource.leveldbjni.JniDBFactory.factory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.iq80.leveldb.DB;
import org.iq80.leveldb.Options;

public class LevelDBHandler {
	private static DB db = null;

	private static int InitialArrayLength = 4;

	private final byte[] keyPrefix;

	public static void initialize(final String DBPath) {
		if (db == null) {
			try {
				Options options = new Options();
				options.createIfMissing(true);

				// options.logger(new Logger() {
				// public void log(String message) {
				// System.out.println(message);
				// }
				// });

				db = factory.open(new File(DBPath), options);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	public LevelDBHandler(final long keyPrefix) {
		this.keyPrefix = new byte[16];
		this.keyPrefix[0] = (byte) (keyPrefix >> 56);
		this.keyPrefix[1] = (byte) (keyPrefix >> 48);
		this.keyPrefix[2] = (byte) (keyPrefix >> 40);
		this.keyPrefix[3] = (byte) (keyPrefix >> 32);
		this.keyPrefix[4] = (byte) (keyPrefix >> 24);
		this.keyPrefix[5] = (byte) (keyPrefix >> 16);
		this.keyPrefix[6] = (byte) (keyPrefix >> 8);
		this.keyPrefix[7] = (byte) (keyPrefix);
	}

	public LevelDBHandler(final String keyPrefix) {
		long hash = keyPrefix.hashCode() + 0xFFFFFFFFL * keyPrefix.hashCode();
		this.keyPrefix = new byte[16];
		this.keyPrefix[0] = (byte) (hash >> 56);
		this.keyPrefix[1] = (byte) (hash >> 48);
		this.keyPrefix[2] = (byte) (hash >> 40);
		this.keyPrefix[3] = (byte) (hash >> 32);
		this.keyPrefix[4] = (byte) (hash >> 24);
		this.keyPrefix[5] = (byte) (hash >> 16);
		this.keyPrefix[6] = (byte) (hash >> 8);
		this.keyPrefix[7] = (byte) (hash);
	}

	/**
	 * Associates the specified value with the specified key in the DB. If the
	 * DB previously contained a mapping for the key, the old value is replaced.
	 * 
	 * @param key
	 *            key with which the specified value is to be associated
	 * @param value
	 *            value to be associated with the specified key
	 */
	public void put(final String key, final int value) {
		db.put(generateKey(key), Serialize(value));
	}

	/**
	 * Associates the specified value with the specified key in the DB. If the
	 * DB previously contained a mapping for the key, the old value is replaced.
	 * 
	 * @param key
	 *            key with which the specified value is to be associated
	 * @param value
	 *            value to be associated with the specified key
	 */
	public void put(final long key, final long value) {
		db.put(generateKey(key), Serialize(value));
	}

	/**
	 * Adds value to the array associated with the specified key in the DB if it
	 * is not already included.
	 * 
	 * @param key
	 *            key associated with the array to which the specified value is
	 *            to be added
	 * @param value
	 *            value to be added to the array
	 */
	public void setAdd(final long key, final long value) {
		long[] valueArray = getLongs(key);

		int lastEmptyPointer = 0;
		if (valueArray == null) {
			valueArray = new long[InitialArrayLength];
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
				if (current == value) {
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
			}
		}
		valueArray[lastEmptyPointer] = value;
		put(key, valueArray);
	}

	/**
	 * Removes value from the array associated with the specified key in the DB
	 * 
	 * @param key
	 *            key associated with the array from which the specified value
	 *            is to be removed
	 * @param value
	 *            value to be removed from the array
	 */
	public void removeFromSet(final long key, final long value) {
		long[] valueArray = getLongs(key);

		if (valueArray == null) {
			return;
		}

		int elementPointer = 0;

		/*
		 * Seek the last 0 element or return if addUUID is already in the array
		 * 
		 * If neither any 0-element nor addUUID have been found lastEmptyPointer
		 * will be commons.length after this loop
		 */
		while (elementPointer != valueArray.length) {
			long current = valueArray[elementPointer];
			if (current == value) {
				break;
			}
			elementPointer++;
		}
		/*
		 * No empty position found. Array has to be extended
		 */
		if (elementPointer != valueArray.length) {
			System.arraycopy(valueArray, elementPointer + 1, valueArray,
					elementPointer, valueArray.length - elementPointer - 1);
		}
		put(key, valueArray);
	}

	/**
	 * Associates the specified value with the specified key in the DB. If the
	 * DB previously contained a mapping for the key, the old value is replaced.
	 * 
	 * @param key
	 *            key with which the specified value is to be associated
	 * @param value
	 *            value to be associated with the specified key
	 */
	public void put(final long key, final long[] value) {
		db.put(generateKey(key), Serialize(value));
	}

	/**
	 * Returns the integer to which the specified key is mapped, or
	 * Integer.MIN_VALUE if the DB contains no mapping for the key.
	 * 
	 * @param key
	 *            The key whose associated value is to be returned
	 * @return The integer to which the specified key is mapped, or
	 *         Integer.MIN_VALUE if the DB contains no mapping for the key.
	 */
	public int getInt(final String key) {
		try {
			byte[] bytes = db.get(generateKey(key.hashCode()));
			if (bytes == null) {
				return Integer.MIN_VALUE;
			}
			return (int) DeSerialize(bytes);
		} catch (final NumberFormatException e) {
			return 0;
		}
	}

	/**
	 * Returns the long[] to which the specified key is mapped, or null if the
	 * DB contains no mapping for the key.
	 * 
	 * @param key
	 *            The key whose associated value is to be returned
	 * @return The long[] to which the specified key is mapped, or null if the
	 *         DB contains no mapping for the key.
	 */
	public long[] getLongs(final long key) {
		byte[] bytes = db.get(generateKey(key));
		if (bytes == null) {
			return null;
		}
		return (long[]) DeSerialize(bytes);
	}

	/**
	 * Removes the mapping for a key from this DB if it is present
	 * 
	 * @param keyUUID
	 *            The key to be removed
	 */
	public void removeKey(final long keyUUID) {
		db.delete(generateKey(keyUUID));
	}

	/**
	 * Try to avoid using this method and use get() instead!
	 * 
	 * @param keyUUID
	 * @return
	 */
	public boolean containsKey(final long keyUUID) {
		return db.get(generateKey(keyUUID)) != null;
	}

	/**
	 * Try to avoid using this method and use get() instead!
	 * 
	 * FIXME: Sort the Set and use binary search
	 * 
	 * @param keyUUID
	 * @return
	 */
	public boolean setContainsElement(final long keyUUID, final long valueUUID) {
		if (getLongs(keyUUID) == null) {
			return false;
		}
		for (long l : getLongs(keyUUID)) {
			if (l == valueUUID) {
				return true;
			}
		}
		return false;
	}

	private byte[] generateKey(final String keySuffix) {
		return generateKey(keySuffix.hashCode());
	}

	private byte[] generateKey(final long keySuffix) {
		byte[] key = keyPrefix;
		key[8] = (byte) (keySuffix >> 56);
		key[9] = (byte) (keySuffix >> 48);
		key[10] = (byte) (keySuffix >> 40);
		key[11] = (byte) (keySuffix >> 32);
		key[12] = (byte) (keySuffix >> 24);
		key[13] = (byte) (keySuffix >> 16);
		key[14] = (byte) (keySuffix >> 8);
		key[15] = (byte) (keySuffix);

		return key;
	}

	private static byte[] Serialize(Object obj) {
		byte[] out = null;
		if (obj != null) {
			try {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(baos);
				oos.writeObject(obj);
				out = baos.toByteArray();
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
		return out;
	}

	private static Object DeSerialize(byte[] str) {
		Object out = null;
		if (str != null) {
			try {
				ByteArrayInputStream bios = new ByteArrayInputStream(str);
				ObjectInputStream ois = new ObjectInputStream(bios);
				out = ois.readObject();
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				return null;
			}
		}
		return out;
	}

}
