package de.metalcon.haveInCommons;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Set;

import de.metalcon.haveInCommons.LuceneRead;
import de.metalcon.haveInCommons.MongoDBStore;

public class PersistendMongoDBIndex {
	
	LuceneRead luceneIndex = null;
	MongoDBStore mongoDBStore = null;
	String collection = null;
	
	public PersistendMongoDBIndex(){
		try{
			luceneIndex = new LuceneRead();
			MongoDBStore mongoDBStore = new MongoDBStore("");
		} catch (UnknownHostException ue) {
			ue.printStackTrace(); 
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} 
		
		collection = "commonEntities";
	}
	
	public void buildIndex(String filepath) {
		
		String from = null;
		String to = null;
		
		luceneIndex.putEdge(from, to);
		
		Set<String> commonEntities = luceneIndex.getCommonNodes(from, to);
		mongoDBStore.insert(Integer.parseInt(from), Integer.parseInt(to), commonEntities, this.collection);
	}
	
	public void queryMongoDB(int from, int to) {
		Set<String> commonEntities = null;
		commonEntities = mongoDBStore.getPreferenceSet(from, to, this.collection);
		
		for(String item : commonEntities){
			System.out.println(item);
		}
	}
	
	public void deleteEdge(String from, String to) {
		mongoDBStore.delete(Integer.parseInt(from), this.collection);
	}
}
