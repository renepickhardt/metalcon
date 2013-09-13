package de.metalcon.musicStorageServer;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Properties;

/**
 * configuration class for the music storage server
 * 
 * @author Jonas Kunze, Rene Pickhardt, sebschlicht
 * 
 */
public class MSSConfig extends Properties {

	/**
	 * root directory for the music storage server
	 */
	public String music_directory;

	/**
	 * temporary directory for resampling
	 */
	public String temporary_directory;

	/**
	 * sample rate targeted (in kbit/s)
	 */
	public int sample_rate_average;

	/**
	 * minimum sample rate (in kbit/s)
	 */
	public int sample_rate_min;

	/**
	 * maximum sample rate (in kbit/s)
	 */
	public int sample_rate_max;

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

	private static MSSConfig instance = null;

	public MSSConfig(final String configPath) {
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

	public static MSSConfig get(final String configPath) {
		if (instance == null) {
			instance = new MSSConfig(configPath);
		}
		return instance;
	}

	/**
	 * @return root directory for the music storage server
	 */
	public String getMusicDirectory() {
		return this.music_directory;
	}

	/**
	 * @return temporary directory for resampling
	 */
	public String getTemporaryDirectory() {
		return this.temporary_directory;
	}

	/**
	 * @return sample rate targeted (in kbit/s)
	 */
	public int getSampleRateAverage() {
		return this.sample_rate_average;
	}

	/**
	 * @return minimum sample rate (in kbit/s)
	 */
	public int getSampleRateMin() {
		return this.sample_rate_min;
	}

	/**
	 * @return maximum sample rate (in kbit/s)
	 */
	public int getSampleRateMax() {
		return this.sample_rate_max;
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