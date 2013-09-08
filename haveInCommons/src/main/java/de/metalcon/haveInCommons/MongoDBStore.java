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

		Set<String> items = new HashSet<String>();

		try {
			for (DBObject obj : cursor.toArray()) {
				String item = (String) obj.get("neighbours");
				item = item.substring(1, item.length() - 1);
				String[] val = item.split(" ");

				for (String s : val) {
					items.add(s);
				}
			}
		} finally {
			cursor.close();
		}

		return null;
	}

	public void delete(int id, DBCollection collection) {
		collection.remove(new BasicDBObject("from", id));
		// TODO: Remove all occourences of "id" in arrays of other
		// elements/documents
	}
}
