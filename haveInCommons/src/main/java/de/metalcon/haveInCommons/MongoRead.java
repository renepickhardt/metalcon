package de.metalcon.haveInCommons;

import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;

import org.iq80.leveldb.DB;

public class MongoRead {

	private MongoClient mongoClient = null;
	private DB db = null;
	private DBCollection collection = null;

	/**
	 * Setup connection to MongoDB server with appropriate parameters For
	 * debugging on local server instance all parameters are optional, just pass
	 * an empty string. Otherwise provide as many parameters as possible in the
	 * order described below.
	 * 
	 * @param connectionString
	 *            - (host;[port][;database][;collection])
	 * @throws UnknownHostException
	 */
	public MongoRead(String connectionString) throws UnknownHostException {
		if (connectionString.isEmpty()) {
			this.mongoClient = new MongoClient("localhost");
		} else {
			String[] args = connectionString.split(";");
			if (args.length == 2) {
				this.mongoClient = new MongoClient(new ServerAddress(args[0],
						Integer.parseInt(args[1])));
			} else if (args.length == 3) {
				this.mongoClient = new MongoClient(new ServerAddress(args[0],
						Integer.parseInt(args[1])));
				this.db = this.mongoClient.getDB(args[2]);
			} else if (args.length == 4) {
				this.mongoClient = new MongoClient(new ServerAddress(args[0],
						Integer.parseInt(args[1])));
				this.db = this.mongoClient.getDB(args[2]);
				this.collection = this.db.getCollection(args[3]);
			} else {
				throw new IllegalArgumentException();
			}
			this.mongoClient.setWriteConcern(WriteConcern.NORMAL);
		}
	}

	private void setDatabase(String database) {
		this.db = this.mongoClient.getDB(database);
	}

	private Set<String> getCollectionNames() {
		return this.db.getCollectionNames();
	}

	private void createCollection(String collectionName) {
		this.db.createCollection(collectionName, null);
	}

	/**
	 * 
	 * Insert a new value to given key as common entity. See wiki for
	 * documentation of underlying datamodel (pending).
	 * 
	 * @param key
	 * @param value
	 * @return true if insert was successful, otherwise false
	 */
	public boolean insert(String key, String value) {

		BasicDBObject query = new BasicDBObject("key", key);
		DBCursor cursor = this.collection.find(query);

		// TODO:update documents first
		if (cursor.size() < 0) {
			if (!updateCommons(key, value))
				throw new MongoException("update of common entities failed!");
		}

		BasicDBList dbl = new BasicDBList();
		dbl.add(value);

		BasicDBObject dbo = new BasicDBObject("key", key)
				.append("commons", dbl);
		WriteResult result = this.collection.insert(dbo);

		// testing for errors during insert
		if (result.getN() == 0)
			return true;
		else
			return false;
	}

	/**
	 * 
	 * Updates all matching entries which identified by key. If a entry with key
	 * does not exist, it will not be created.
	 * 
	 * @param key
	 *            to update
	 * @param value
	 *            to insert
	 * @return true if update was successful, otherwise false
	 */
	private boolean updateCommons(String key, String value) {
		BasicDBObject pushQuery = new BasicDBObject("key", key);
		BasicDBObject pushCommand = new BasicDBObject("$push",
				new BasicDBObject("commons", value));

		// create new document if it does not exist: false
		// update all matching documents : true
		WriteResult result = this.collection.update(pushQuery, pushCommand,
				false, true);
		if (result.getN() == 0)
			return true;
		else
			return false;
	}

	public Set<String> getCommons(String key) {
		BasicDBObject query = new BasicDBObject("key", key);
		DBCursor cursor = this.collection.find(query);

		Set<String> items = new HashSet<String>(
				(int) (cursor.size() / 0.75) + 1);

		DBObject item = null;
		BasicDBList list = null;

		try {
			while (cursor.hasNext()) {

				item = cursor.next();
				list = (BasicDBList) item.get("commons");

				for (Object itemid : list) {
					items.add((String) itemid);
				}
			}
		} finally {
			cursor.close();
		}

		return items;
	}

	/**
	 * deletes all occurences of value in commons array
	 * 
	 * @param value
	 *            to delete
	 */
	public void delete(String value) {
		// TODO: Test if all items with occurences within key specifier will be
		// matched and deleted!
		this.collection.remove(new BasicDBObject("key", value));

		BasicDBObject pullquery = new BasicDBObject("commons", value);
		this.collection
				.update(pullquery, new BasicDBObject("$pull", pullquery));
	}
}
