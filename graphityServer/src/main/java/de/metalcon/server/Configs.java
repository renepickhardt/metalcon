package de.metalcon.server;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Properties;

import de.metalcon.socialgraph.Configuration;

/**
 * This is an interface class to the Config file for this project. For each
 * class field on java property must be defined int config.txt. The fields will
 * be automatically filled! Allowed Types are String, int, String[] and long[]
 * where arrays are defined by semicolon-separated Strings like "array=a;b;c"
 * 
 * @author Jonas Kunze, Rene Pickhardt
 * 
 */
public class Configs extends Properties implements Configuration {

	/**
	 * social graph database path
	 */
	public String database_path;

	/**
	 * path for status update templates (XML files)
	 */
	public String templates_path;

	/**
	 * path including picture files
	 */
	public String picture_path;

	/**
	 * database read only flag<br>
	 * TODO: necessary or specified somewhere else?
	 */
	public boolean read_only;

	/**
	 * Graphity algorithm used
	 */
	public String algorithm;

	/**
	 * access control HTTP header
	 */
	public String headerAccessControl;

	/**
	 * cache type
	 */
	public String cache_type;

	/**
	 * use memory mapped buffers flag<br>
	 * TODO: boolean value?
	 */
	public String use_memory_mapped_buffers;

	private static final long serialVersionUID = -4439565094382127683L;

	private static Configs instance = null;

	public Configs(final String configPath) {
		try {
			BufferedInputStream stream = new BufferedInputStream(
					new FileInputStream(configPath));
			this.load(stream);
			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			this.initialize();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Fills all fields with the data defined in the config file.
	 * 
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	private void initialize() throws IllegalArgumentException,
			IllegalAccessException {
		Field[] fields = this.getClass().getFields();
		for (Field f : fields) {
			if (this.getProperty(f.getName()) == null) {
				System.err.print("Property '" + f.getName()
						+ "' not defined in config file");
			}
			if (f.getType().equals(String.class)) {
				f.set(this, this.getProperty(f.getName()));
			} else if (f.getType().equals(long.class)) {
				f.setLong(this, Long.valueOf(this.getProperty(f.getName())));
			} else if (f.getType().equals(int.class)) {
				f.setInt(this, Integer.valueOf(this.getProperty(f.getName())));
			} else if (f.getType().equals(boolean.class)) {
				f.setBoolean(this,
						Boolean.valueOf(this.getProperty(f.getName())));
			} else if (f.getType().equals(String[].class)) {
				f.set(this, this.getProperty(f.getName()).split(";"));
			} else if (f.getType().equals(int[].class)) {
				String[] tmp = this.getProperty(f.getName()).split(";");
				int[] ints = new int[tmp.length];
				for (int i = 0; i < tmp.length; i++) {
					ints[i] = Integer.parseInt(tmp[i]);
				}
				f.set(this, ints);
			} else if (f.getType().equals(long[].class)) {
				String[] tmp = this.getProperty(f.getName()).split(";");
				long[] longs = new long[tmp.length];
				for (int i = 0; i < tmp.length; i++) {
					longs[i] = Long.parseLong(tmp[i]);
				}
				f.set(this, longs);
			}
		}
	}

	public static Configs get(final String configPath) {
		if (instance == null) {
			instance = new Configs(configPath);
		}
		return instance;
	}

	@Override
	public String getDatabasePath() {
		return this.database_path;
	}

	@Override
	public String getTemplatesPath() {
		return this.templates_path;
	}

	@Override
	public boolean getReadOnly() {
		return this.read_only;
	}

	@Override
	public String getAlgorithm() {
		return this.algorithm;
	}

	@Override
	public String getUseMemoryMappedBuffers() {
		return this.use_memory_mapped_buffers;
	}

	@Override
	public String getCacheType() {
		return this.cache_type;
	}

	/**
	 * @return access control HTTP header
	 */
	public String getHeaderAccessControl() {
		return this.headerAccessControl;
	}

}