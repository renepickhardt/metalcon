/**
 * @author Jonas Kunze
 * 
 */
package de.metalcon.haveInCommons;

import static org.fusesource.leveldbjni.JniDBFactory.bytes;
import static org.fusesource.leveldbjni.JniDBFactory.factory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.Set;

import org.iq80.leveldb.DB;
import org.iq80.leveldb.Options;

public class RetrievalOptimizedLevelDB extends RetrievalOptimized implements HaveInCommons {
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
	protected Set<String> getCommonSetValue(String key) {
		byte[] serializedSet = db.get(bytes(key));
		@SuppressWarnings("unchecked")
		HashSet<String> set = (HashSet<String>) DeSerialize(serializedSet);
		return set;
	}

	@Override
	protected void saveCommonSetValue(String key, String value) {
		Set<String> s = getCommonSetValue(key);
		if (s == null) {
			s = new HashSet<String>();
		}
		s.add(value);

		byte[] serializedSet = Serialize(s);
		db.put(bytes(key), serializedSet);
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