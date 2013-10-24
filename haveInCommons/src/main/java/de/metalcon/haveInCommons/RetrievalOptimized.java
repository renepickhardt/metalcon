/**
 * 
 */
package de.metalcon.haveInCommons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.metalcon.like.Like.Vote;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import edu.uci.ics.jung.graph.impl.DirectedSparseVertex;

/**
 * @author Jonas Kunze
 */
public class RetrievalOptimized implements HaveInCommons {
	protected Graph graph;
	protected HashMap<Long, Vertex> vertices;
	protected HashMap<Vertex, Long> reverseIndex;
	protected HashMap<byte[], ArrayList<Long>> commonSet;

	/**
	 * constructor
	 */
	public RetrievalOptimized() {
		graph = new DirectedSparseGraph();
		vertices = new HashMap<Long, Vertex>();
		reverseIndex = new HashMap<Vertex, Long>();
		commonSet = new HashMap<byte[], ArrayList<Long>>();
	}

	/**
	 * helper function to just store the newly created edge
	 * 
	 * @return returns true if the edge has been created and false if it already
	 *         existed
	 */
	protected boolean storeEdge(long from, long to) {
		Vertex f = vertices.get(from);
		if (f == null) {
			f = new DirectedSparseVertex();
			// f.setUserDatum("ID", from, UserData.REMOVE);
			vertices.put(from, f);
			reverseIndex.put(f, from);
			graph.addVertex(f);
		}
		Vertex t = vertices.get(to);
		if (t == null) {
			t = new DirectedSparseVertex();
			// f.setUserDatum("ID", to, UserData.REMOVE);
			vertices.put(to, t);
			reverseIndex.put(t, to);
			graph.addVertex(t);
		}
		Edge edge = new DirectedSparseEdge(f, t);
		if (graph.getEdges().contains(edge)) {
			return false;
		} else {
			try {
				graph.addEdge(edge);
			} catch (Exception e) {
				System.out.println("could not add edge");
				return false;
			}
		}
		return true;
	}

	/**
	 * adds the key of the vertices that are in common to the hashset. always
	 * assumes that vertices from and to exist in the graph and that from and to
	 * are never null
	 * 
	 * @param from
	 * @param to
	 */
	protected void updateCommons(long from, long to) {
		Vertex f = this.vertices.get(from);
		Vertex t = this.vertices.get(to);
		// include from to the commons set of (tmp and to)
		for (Object tmp : f.getInEdges()) {
			if (((DirectedSparseEdge) tmp).getSource() == t) {
				continue;
			}

			// long fromKey = (long) ((DirectedSparseEdge) tmp).getSource()
			// .getUserDatum("ID");
			long fromKey = reverseIndex.get(((DirectedSparseEdge) tmp)
					.getSource());
			saveCommonSetValue(fromKey, to, from);
		}
		// include to to the commons set of (from and tmp)
		for (Object tmp : t.getOutEdges()) {
			if (((DirectedSparseEdge) tmp).getDest() == f) {
				continue;
			}
			// long toKey = (long) ((DirectedSparseEdge) tmp).getDest()
			// .getUserDatum("ID");
			long toKey = reverseIndex.get(((DirectedSparseEdge) tmp).getDest());
			saveCommonSetValue(from, toKey, to);
		}
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
		return toPrimitive(getCommonSetValue(generateKey(uuid1, uuid2)));
	}

	protected ArrayList<Long> getCommonSetValue(byte[] key) {
		return commonSet.get(key);
	}

	protected void saveCommonSetValue(long from, long to, long value) {
		byte[] key = generateKey(from, to);
		ArrayList<Long> s = getCommonSetValue(key);
		if (s == null) {
			s = new ArrayList<Long>();
		}
		s.add(value);

		commonSet.put(key, s);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.metalcon.haveInCommons.HaveInCommons#putEdge(java.lang.String,
	 * java.lang.String)
	 */
	public void putEdge(long from, long to, final Vote vote) {
		if (storeEdge(from, to)) {
			updateCommons(from, to);
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

	public byte[] generateKey(long from, long to) {
		byte[] result = new byte[8];

		result[0] = (byte) (from >> 24);
		result[1] = (byte) (from >> 16);
		result[2] = (byte) (from >> 8);
		result[3] = (byte) (from);

		result[4] = (byte) (to >> 24);
		result[5] = (byte) (to >> 16);
		result[6] = (byte) (to >> 8);
		result[7] = (byte) (to);

		return result;
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