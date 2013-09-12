package de.utils;

import java.net.UnknownHostException;

import org.json.simple.JSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.WriteResult;

/**
 * database for meta data
 * 
 * @author sebschlicht
 * 
 */
public class MetaDatabase {

	/**
	 * name of the database table for meta data
	 */
	private static final String TABLE_METADATA = "meta_data";

	/**
	 * BSON key for the identifier in a meta data entry
	 */
	protected static final String ENTRY_IDENTIFIER = "id";

	/**
	 * BSON key for meta data in a meta data entry
	 */
	private static final String META_DATA_META_DATA = "meta_data";

	/**
	 * mongo database
	 */
	protected final DB mongoDB;

	/**
	 * database table for meta data
	 */
	private final DBCollection tableMetaData;

	/**
	 * create a new database for meta data
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
	public MetaDatabase(final String hostAddress, final int port,
			final String database) throws UnknownHostException {
		final MongoClient client = new MongoClient(hostAddress, port);
		this.mongoDB = client.getDB(database);
		this.tableMetaData = this.mongoDB.getCollection(TABLE_METADATA);
	}

	/**
	 * check if the database contains an entry with an identifier
	 * 
	 * @param identifier
	 *            identifier to be searched for
	 * @return true - if there is an entry with such identifier<br>
	 *         false - otherwise
	 */
	public boolean hasEntryWithIdentifier(final String identifier) {
		final BasicDBObject searchQuery = new BasicDBObject();
		searchQuery.put(ENTRY_IDENTIFIER, identifier);

		return (this.tableMetaData.findOne(searchQuery) != null);
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
			final JSONObject metaData) {
		final BasicDBObject entry = new BasicDBObject();
		entry.put(ENTRY_IDENTIFIER, identifier);

		if (this.tableMetaData.findOne(entry) == null) {
			// add the meta data if passed
			final BasicDBObject metaDataEntry = new BasicDBObject();
			if (metaData != null) {
				for (Object key : metaData.keySet()) {
					metaDataEntry.put((String) key, metaData.get(key));
				}
			}
			entry.put(META_DATA_META_DATA, metaDataEntry);

			// add the image to the database
			this.tableMetaData.insert(entry);

			return true;
		}

		return false;
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
		final BasicDBObject searchQuery = new BasicDBObject();
		searchQuery.put(ENTRY_IDENTIFIER, identifier);

		final DBObject entry = this.tableMetaData.findOne(searchQuery);
		if (entry != null) {
			final BasicDBObject metaDataEntry = (BasicDBObject) entry
					.get(META_DATA_META_DATA);

			return metaDataEntry.toString();
		}

		return null;
	}

	/**
	 * append a key value pair to the meta data for an entry
	 * 
	 * @param identifier
	 *            database entry identifier
	 * @param metaData
	 *            meta data that shall be appended/updated
	 * @return true - if the meta data has been appended/updated successfully<br>
	 *         false - if there is no entry with such identifier
	 */
	public boolean appendMetadata(final String identifier,
			final JSONObject metaData) {
		final BasicDBObject searchQuery = new BasicDBObject();
		searchQuery.put(ENTRY_IDENTIFIER, identifier);

		final DBObject entry = this.tableMetaData.findOne(searchQuery);
		if (entry != null) {
			final BasicDBObject metaDataEntry = (BasicDBObject) entry
					.get(META_DATA_META_DATA);
			for (Object key : metaData.keySet()) {
				metaDataEntry.put((String) key, metaData.get(key));
			}
			entry.put(META_DATA_META_DATA, metaDataEntry);
			this.tableMetaData.update(searchQuery, entry);
			return true;
		}

		return false;
	}

	/**
	 * delete an entry from the database
	 * 
	 * @param identifier
	 *            database entry identifier
	 * @return true - if the entry has been deleted successfully<br>
	 *         false - if there is no entry with such identifier
	 */
	public boolean deleteDatabaseEntry(final String identifier) {
		final BasicDBObject searchQuery = new BasicDBObject();
		searchQuery.put(ENTRY_IDENTIFIER, identifier);

		final WriteResult deleteResult = this.tableMetaData.remove(searchQuery);
		return (deleteResult.getN() == 1);
	}

	/**
	 * clear the meta data table<br>
	 * <b>removes all existing data!</b>
	 */
	public void clear() {
		this.tableMetaData.drop();
	}

}