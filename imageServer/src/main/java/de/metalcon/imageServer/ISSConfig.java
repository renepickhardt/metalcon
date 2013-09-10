package de.metalcon.imageServer;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Properties;

/**
 * This is an interface class to the Config file for this project. For each
 * class field on java property must be defined int config.txt. The fields will
 * be automatically filled! Allowed Types are String, int, String[] and long[]
 * where arrays are defined by semicolon-separated Strings like "array=a;b;c"
 * 
 * @author Jonas Kunze, Rene Pickhardt, sebschlicht
 * 
 */
public class ISSConfig extends Properties {

	/**
	 * root directory for the image storage server
	 */
	public String image_directory;

	/**
	 * temporary directory for image magic
	 */
	public String temporary_directory;

	/**
	 * host address of the server the database runs at
	 */
	public String database_host;

	/**
	 * port to connect to the database
	 */
	public int database_port;

	/**
	 * name of the database used
	 */
	public String database_name;

	private static ISSConfig instance = null;

	public ISSConfig(final String configPath) {
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

	public static ISSConfig get(final String configPath) {
		if (instance == null) {
			instance = new ISSConfig(configPath);
		}
		return instance;
	}

	/**
	 * @return root directory for the image storage server
	 */
	public String getImageDirectory() {
		return this.image_directory;
	}

	/**
	 * @return temporary directory for image magic
	 */
	public String getTemporaryDirectory() {
		return this.temporary_directory;
	}

	/**
	 * @return host address of the server the database runs at
	 */
	public String getDatabaseHost() {
		return this.database_host;
	}

	/**
	 * @return port to connect to the database
	 */
	public int getDatabasePort() {
		return this.database_port;
	}

	/**
	 * @return name of the database used
	 */
	public String getDatabaseName() {
		return this.database_name;
	}

}