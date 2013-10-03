/**
 * 
 */
package de.metalcon.haveInCommons;

import java.util.HashSet;
import java.util.Set;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.index.lucene.QueryContext;

/**
 * @author Rene Pickhardt
 * 
 */
public class PersistentReadOptimized implements HaveInCommons {
	private GraphDatabaseService graphDB;

	/**
	 * 
	 */
	public PersistentReadOptimized(String arg) {
		graphDB = new GraphDatabaseFactory().newEmbeddedDatabase(arg);
	}

	private void updateCommons(Node from, Node to) {
		if (from == null || to == null)
			throw new IllegalArgumentException();

		String fromId = (String) from.getProperty("id");
		for (Relationship r : to.getRelationships(Direction.BOTH)) {
			Node dest = r.getEndNode();
			String id = (String) dest.getProperty("id");
			addCommonNode(to, fromId, id);
		}

		String toId = (String) to.getProperty("id");
		for (Relationship r : from.getRelationships(Direction.BOTH)) {
			Node src = r.getStartNode();
			String id = (String) src.getProperty("id");
			addCommonNode(from, id, toId);
		}
	}

	/**
	 * @param to
	 * @param fromId
	 * @param id
	 */
	private void addCommonNode(Node n, String from, String to) {
		Index<Node> ix = graphDB.index().forNodes("commons");
		ix.add(n, "key", from + ":" + to);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.metalcon.haveInCommons.HaveInCommons#getCommonNodes(java.lang.String,
	 * java.lang.String)
	 */
	public Set<String> getCommonNodes(String uuid1, String uuid2) {
//		Index<Node> ix = graphDB.index().forNodes("commons");
//		//IndexHits<Node> hits = ix.query("key", new QueryContext("*"));
//		IndexHits<Node> hits = ix.get("key", uuid1 + "\\:" + uuid2);
		Index<Node> ix = graphDB.index().forNodes("nodes");
		Node from = ix.get("id", uuid1).getSingle();
		Node to = ix.get("id", uuid2).getSingle();
		if (to == null || from == null)return null;
		Set<String> s = new HashSet<String>();
		Set<String> res = new HashSet<String>();
		for (Relationship r : from.getRelationships()){
			Node tmp = r.getOtherNode(from);
			s.add((String)tmp.getProperty("id"));
		}
		for (Relationship r : to.getRelationships()){
			Node tmp = r.getOtherNode(to);
			String key = (String)tmp.getProperty("id"); 
			if (s.contains(key))res.add(key);
		}
		return res;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.metalcon.haveInCommons.HaveInCommons#putEdge(java.lang.String,
	 * java.lang.String)
	 */
	public void putEdge(String from, String to) {
		Transaction tx = graphDB.beginTx();
		try {
			storeEdge(from, to);
			tx.success();
		} catch (Exception e) {
			e.printStackTrace();
			tx.failure();
			System.exit(-1);
			return;
		} finally {
			tx.finish();
		}
	}

	/**
	 * @param from
	 * @param to
	 * @return
	 */
	private void storeEdge(String from, String to) {
		Node f = null;
		Node t = null;
		Index<Node> nodeIndex = graphDB.index().forNodes("nodes");
		f = nodeIndex.get("id", from).getSingle();
		if (f == null) {
			f = graphDB.createNode();
			f.setProperty("id", from);
			nodeIndex.add(f, "id", from);
		}
		t = nodeIndex.get("id", to).getSingle();
		if (t == null) {
			t = graphDB.createNode();
			t.setProperty("id", to);
			nodeIndex.add(t, "id", to);
		}

		Index<Relationship> relIndex = graphDB.index()
				.forRelationships("edges");
		Relationship r = null;
		r = relIndex.get("id", from + ":" + to).getSingle();
		if (r==null){
			r = f.createRelationshipTo(t,
					DynamicRelationshipType.withName("follows"));
			relIndex.add(r, "id", from + ":" + to);
		}
//		updateCommons(f, t);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.metalcon.haveInCommons.HaveInCommons#delegeEdge(java.lang.String,
	 * java.lang.String)
	 */
	public boolean delegeEdge(String from, String to) {
		// TODO Auto-generated method stub
		return false;
	}

}
