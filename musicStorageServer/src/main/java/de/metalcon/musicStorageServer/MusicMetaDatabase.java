package de.metalcon.musicStorageServer;

import java.net.UnknownHostException;

/**
 * database for music meta data
 * 
 * @author sebschlicht
 * 
 */
public class MusicMetaDatabase extends MetaDatabase {

	/**
	 * create a new database for music meta data
	 * 
	 * @param hostAddress
	 *            host address of the server the database runs at
	 * @param port
	 *            port to connect to the database
	 * @param database
	 *            name of the database used
	 * @throws UnknownHostException
	 *             if the database server could not be reached
	 */
	public MusicMetaDatabase(final String hostAddress, final int port,
			final String database) throws UnknownHostException {
		super(hostAddress, port, database);
	}

}