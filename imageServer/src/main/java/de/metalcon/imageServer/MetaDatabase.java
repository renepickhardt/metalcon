package de.metalcon.imageServer;

/**
 * database for meta data
 * 
 * @author sebschlicht
 * 
 */
public class MetaDatabase {

	/**
	 * add an entry to the database
	 * 
	 * @param identifier
	 *            database entry identifier
	 * @param metaData
	 *            meta data for the entry
	 * @return true - if the entry has been added successfully<br>
	 *         false - if there is already an entry with such identifier
	 */
	public boolean addDatabaseEntry(final String identifier,
			final String metaData) {
		// TODO
		return true;
	}

	/**
	 * read the meta data for an entry
	 * 
	 * @param identifier
	 *            database entry identifier
	 * @return meta data for the entry specified<br>
	 *         <b>null</b> if there is no entry with such identifier
	 */
	public String getMetadata(final String identifier) {
		// TODO
		return null;
	}

	/**
	 * append a key value pair to the meta data for an entry
	 * 
	 * @param identifier
	 *            database entry identifier
	 * @param key
	 *            key of the meta data value<br>
	 *            &emsp;<b>if the meta already has a value for this key the old
	 *            value will get overridden</b>
	 * @param value
	 *            meta data value that shall be appended
	 * @return true - if the meta data has been appended successfully<br>
	 *         false - if there is no entry with such identifier
	 */
	public boolean appendMetadata(final String identifier, final String key,
			final String value) {
		// TODO
		return false;
	}

	/**
	 * delete an entry from the database
	 * 
	 * @param identifier
	 *            database entry identifier
	 * @return true - if the entry has been added successfully<br>
	 *         false - if there is no entry with such identifier
	 */
	public boolean deleteDatabaseEntry(final String identifier) {
		// TODO
		return false;
	}

}