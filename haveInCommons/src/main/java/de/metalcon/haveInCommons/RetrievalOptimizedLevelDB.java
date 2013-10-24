/**
 * @author Jonas Kunze
 * 
 */
package de.metalcon.haveInCommons;

import static org.fusesource.leveldbjni.JniDBFactory.factory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import org.iq80.leveldb.DB;
import org.iq80.leveldb.Options;

public class RetrievalOptimizedLevelDB extends RetrievalOptimized implements
		HaveInCommons {
	private DB db = null;

	/**
	 * constructor
	 */
	public RetrievalOptimizedLevelDB() {
		super();

		try {
			Options options = new Options();
			options.createIfMissing(true);

			this.db = factory.open(new File("levelDB"), options);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected ArrayList<Long> getCommonSetValue(byte[] key) {
		byte[] serializedSet = db.get(key);
		@SuppressWarnings("unchecked")
		ArrayList<Long> set = (ArrayList<Long>) DeSerialize(serializedSet);
		return set;
	}

	@Override
	protected void saveCommonSetValue(long from, long to, long value) {
		byte[] key = generateKey(from, to);
		ArrayList<Long> s = getCommonSetValue(key);
		if (s == null) {
			s = new ArrayList<Long>();
		}
		s.add(value);

		db.put(key, Serialize(s));
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