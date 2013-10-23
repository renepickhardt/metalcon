/**
 *
 */
package de.metalcon.haveInCommons;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.Index;

import de.metalcon.like.Like.Vote;

/**
 * @author Rene Pickhardt
 */
public class PersistentReadOptimized implements HaveInCommons {
	private GraphDatabaseService graphDB;
	Index<Node> ix;
	Index<Relationship> relIndex;

	/**
     *
     */
	public PersistentReadOptimized() {
		graphDB = new GraphDatabaseFactory().newEmbeddedDatabase("neo4j");
		ix = graphDB.index().forNodes("nodes");
		relIndex = graphDB.index().forRelationships("edges");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.metalcon.haveInCommons.HaveInCommons#getCommonNodes(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public long[] getCommonNodes(long uuid1, long uuid2) {
		Node from = ix.get("id", uuid1).getSingle();
		Node to = ix.get("id", uuid2).getSingle();

		if (to == null || from == null)
			return null;

		Set<Long> s = new HashSet<Long>();
		ArrayList<Long> res = new ArrayList<Long>();
		for (Relationship r : from.getRelationships(Direction.OUTGOING)) {
			Node tmp = r.getOtherNode(from);
			s.add((long) tmp.getProperty("id"));
		}

		for (Relationship r : to.getRelationships(Direction.INCOMING)) {
			Node tmp = r.getOtherNode(to);
			long key = (long) tmp.getProperty("id");
			if (s.contains(key))
				res.add(key);
		}
		return toPrimitive(res);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.metalcon.haveInCommons.HaveInCommons#putEdge(java.lang.String,
	 * java.lang.String)
	 */

	ArrayList<Long> putBuffer_from = new ArrayList<Long>();
	ArrayList<Long> putBuffer_to = new ArrayList<Long>();
	static final int bufSize = 5000;

	@Override
	public void putEdge(long from, long to, final Vote vote) {
		putBuffer_from.add(from);
		putBuffer_to.add(to);
		if (putBuffer_from.size() == bufSize) {
			flush();
		}
	}

	public void flush() {
		Transaction tx = graphDB.beginTx();
		try {

			for (int i = 0; i < putBuffer_from.size(); i++) {
				storeEdge(putBuffer_from.get(i), putBuffer_to.get(i));
			}

			tx.success();

			putBuffer_from.clear();
			putBuffer_to.clear();

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
	private void storeEdge(long from, long to) {
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

		Relationship r = relIndex.get("id", from + ":" + to).getSingle();
		if (r == null) {
			r = f.createRelationshipTo(t,
					DynamicRelationshipType.withName("follows"));
			relIndex.add(r, "id", from + ":" + to);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.metalcon.haveInCommons.HaveInCommons#delegeEdge(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public void deleteEdge(long from, long to) {
	}

	/*
	 * Converts a List<Long> to an array of primitive longs
	 */
	protected long[] toPrimitive(List<Long> list) {
		long[] ints = new long[list.size()];
		int i = 0;
		for (Long n : list) {
			ints[i++] = n;
		}
		return ints;
	}
}
