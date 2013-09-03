package de.metalcon.imageServer;

/**
 * database for meta data
 * 
 * @author sebschlicht
 * 
 */
public class MetaDatabase {

	/**
	 * check if the database contains an entry with an identifier
	 * 
	 * @param identifier
	 *            identifier to be searched for
	 * @return true - if there is an entry with such identifier<br>
	 *         false - otherwise
	 */
	public boolean hasEntryWithIdentifier(final String identifier) {
		// TODO
		return true;
	}

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

	/**
	 * get the path to the smallest image larger than the dimension passed
	 * 
	 * @param width
	 *            minimum width
	 * @param height
	 *            minimum height
	 * @return path to the smallest image larger than the dimension passed<br>
	 *         <b>null</b> if there is no such version
	 */
	public String getSmallestImagePath(final int width, final int height) {
		// TODO
		return null;
	}

	/**
	 * check if there is a certain size for an image
	 * 
	 * @param imageIdentifier
	 *            image identifier
	 * @param width
	 *            requested width
	 * @param height
	 *            requested height
	 * @return true - if the size is registered for the image passed<br>
	 *         false - otherwise
	 */
	public boolean imageHasSizeRegistered(final String imageIdentifier,
			final int width, final int height) {
		// TODO
		return false;
	}

}