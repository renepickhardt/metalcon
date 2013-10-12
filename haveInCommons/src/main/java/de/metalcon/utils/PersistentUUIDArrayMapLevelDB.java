package de.metalcon.utils;

import static org.fusesource.leveldbjni.JniDBFactory.factory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;

import org.iq80.leveldb.DB;
import org.iq80.leveldb.Options;

/**
 * @author Jonas Kunze
 */
public class PersistentUUIDArrayMapLevelDB implements IPersistentUUIDArrayMap {
	private static DB db = null;

	private static final int InitialArrayLength = 4;

	private byte[] keyPrefix;

	public static PersistentUUIDArrayMapLevelDB generateMap(
			final long keyPrefix, final String DBPath) {
		if (db == null) {
			try {
				Options options = new Options();
				options.createIfMissing(true);

				db = factory.open(new File(DBPath), options);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}

		return new PersistentUUIDArrayMapLevelDB(keyPrefix, DBPath);
	}

	private PersistentUUIDArrayMapLevelDB(final long keyPrefix,
			final String DBPath) {

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

	/**
	 * @return The timestamp of the last update
	 */
	@Override
	public int getLastUpdateTimeStamp() {
		try {
			byte[] bytes = db.get(generateKey("UpdateTS"));
			return (int) DeSerialize(bytes);
		} catch (final NumberFormatException e) {
			return 0;
		}
	}

	/**
	 * 
	 * @param updateTimeStamp
	 *            The timestamp of the last update
	 */
	@Override
	public void setUpdateTimeStamp(final int updateTimeStamp) {
		db.put(generateKey("UpdateTS"), Serialize(updateTimeStamp));
	}

	/**
	 * 
	 * @param keyUUID
	 * @param valueUUID
	 */
	@Override
	public void append(final long keyUUID, final long valueUUID) {
		final byte[] key = generateKey(keyUUID);
		long[] valueArray = (long[]) DeSerialize(db.get(key));

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
			}
		}
		valueArray[lastEmptyPointer] = valueUUID;
		db.put(key, Serialize(valueArray));
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
		final byte[] key = generateKey(keyUUID);
		long[] set = (long[]) DeSerialize(db.get(key));
		return set;
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
	public void removeKey(final long keyUUID) {
		db.delete(generateKey(keyUUID));
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
		final byte[] key = generateKey(keyUUID);
		HashSet<Long> set = (HashSet<Long>) DeSerialize(db.get(key));
		if (set == null) {
			return;
		}
		set.remove(valueUUID);
		db.put(key, Serialize(set));
	}

	/**
	 * Will delete all elements in this map
	 */
	@Override
	public void removeAll() {
		db.get
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

	@Override
	public void save() {
		// TODO Auto-generated method stub
	}

	static public byte[] Serialize(Object obj) {
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

	static public Object DeSerialize(byte[] str) {
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
