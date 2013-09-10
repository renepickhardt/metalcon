package de.metalcon.haveInCommons;

import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;
import com.mongodb.WriteResult;

public class MongoDBStore {

	private MongoClient mongoClient = null;
	private DB db = null;

	public MongoDBStore(String connectionString) throws UnknownHostException {
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

	public void insert(int from, int to, Set<String> neighbours,
			DBCollection collection) {
		BasicDBList dbl = new BasicDBList();

		for (String s : neighbours) {
			dbl.add(new BasicDBObject("id", s));
		}

		BasicDBObject dbo = new BasicDBObject("from", from).append("to", to)
				.append("neighbours", dbl);
		WriteResult result = collection.insert(dbo);
		// TODO: Testing for errors
	}

	public Set<String> getPreferenceSet(int from, int to, String collection) {
		BasicDBObject query = new BasicDBObject("from", from).append("to", to);
		DBCursor cursor = this.db.getCollection(collection).find(query);

		Set<String> items = new HashSet<String>((int)(cursor.size()/0.75)+1);

		DBObject item = null;
		BasicDBList list = null;
		
		try {
			while(cursor.hasNext()){
				
				item = cursor.next();
				list = (BasicDBList)item.get("neighbours");
				
				for(Object itemid : list){
					items.add((String)itemid);
				}
			}
		} finally {
			cursor.close();
		}

		return null;
	}

	/**
	 * deletes all occurences of id "from"
	 * 
	 * @param id to delete
	 * @param collection
	 */
	public void delete(int id, String collection) {
		db.getCollection(collection).remove(new BasicDBObject("from", id));
		
		BasicDBObject pullquery = new BasicDBObject("neighbours", id);
		db.getCollection(collection).update(pullquery, new BasicDBObject("$pull", pullquery));
	}
}
