/**
 *
 */
package de.metalcon.haveInCommons;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.neo4j.tooling.GlobalGraphOperations;

import de.metalcon.like.Like.Vote;

/**
 * @author Rene Pickhardt
 */
public class PersistentReadOptimizedHased implements HaveInCommons {
	private GraphDatabaseService graphDB;

	// Index<Node> ix;
	// Index<Relationship> relIndex;

	HashMap<Long, Node> nodeIx = new HashMap<Long, Node>();
	HashMap<Pair, Relationship> relationIx = new HashMap<Pair, Relationship>();

	class Pair {
		long s;
		long t;

		public Pair(long s, long t) {
			this.s = s;
			this.t = t;
		}
	}

	/**
     *
     */
	public PersistentReadOptimizedHased() {
		// Delete Database
		try {
			FileUtils
					.deleteDirectory(new File("/home/hartmann/LikeButtonNeo4j"));
		} catch (IOException e) {
			e.printStackTrace(); // To change body of catch statement use File |
									// Settings | File Templates.
		}

		graphDB = new EmbeddedGraphDatabase("/home/hartmann/LikeButtonNeo4j");
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
		Node from = nodeIx.get(uuid1);
		Node to = nodeIx.get(uuid2);

		if (to == null || from == null)
			return null;

		Set<Long> followedByFrom = new HashSet<Long>();
		ArrayList<Long> resultSet = new ArrayList<Long>();

		for (Relationship r : from.getRelationships()) {
			Node tmp = r.getOtherNode(from);
			followedByFrom.add((Long) tmp.getProperty("id"));
		}

		for (Relationship r : to.getRelationships()) {
			Node tmp = r.getOtherNode(to);
			long key = (long) tmp.getProperty("id");
			if (followedByFrom.contains(key))
				resultSet.add(key);
		}

		return toPrimitive(resultSet);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.metalcon.haveInCommons.HaveInCommons#putEdge(java.lang.String,
	 * java.lang.String)
	 */

	ArrayList<Long> putBuffer_1 = new ArrayList<Long>();
	ArrayList<Long> putBuffer_2 = new ArrayList<Long>();
	static final int bufSize = 5000;

	@Override
	public void putEdge(long from, long to, final Vote vote) {
		if (putBuffer_1.size() < bufSize) {
			putBuffer_1.add(from);
			putBuffer_2.add(to);
		} else {
			Transaction tx = graphDB.beginTx();

			try {

				for (int i = 0; i < bufSize; i++) {

					storeEdge(putBuffer_1.get(i), putBuffer_2.get(i));

				}

				tx.success();

				putBuffer_1.clear();
				putBuffer_2.clear();

			} catch (Exception e) {
				e.printStackTrace();
				tx.failure();
				System.exit(-1);
				return;
			} finally {
				tx.finish();
			}
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
		// Index<Node> nodeIndex = graphDB.index().forNodes("nodes");

		f = nodeIx.get(from);
		if (f == null) {
			f = graphDB.createNode();
			f.setProperty("id", from);
			nodeIx.put(from, f);
		}

		t = nodeIx.get(to);
		if (t == null) {
			t = graphDB.createNode();
			t.setProperty("id", to);
			nodeIx.put(to, t);
		}

		Relationship r = relationIx.get(new Pair(from, to));
		if (r == null) {
			r = f.createRelationshipTo(t,
					DynamicRelationshipType.withName("follows"));
			relationIx.put(new Pair(from, to), r);
		}
	}

	private long mash(long from, long to) {
		return ((new Long(from)).hashCode() ^ (new Long(to)).hashCode());
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

	public int getNodeCount() {
		int i = 0;
		for (Node n : GlobalGraphOperations.at(graphDB).getAllNodes()) {
			i++;
		}
		return i;
	}

	public int getRelCount() {
		int i = 0;
		for (Relationship r : GlobalGraphOperations.at(graphDB)
				.getAllRelationships()) {
			i++;
		}
		return i;
	}
}
