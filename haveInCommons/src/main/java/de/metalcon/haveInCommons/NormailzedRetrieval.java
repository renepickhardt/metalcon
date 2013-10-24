/**
 * 
 */
package de.metalcon.haveInCommons;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import de.metalcon.like.Like.Vote;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import edu.uci.ics.jung.graph.impl.DirectedSparseVertex;

/**
 * @author Rene Pickhardt
 * @author Jonas Kunze
 */
public class NormailzedRetrieval implements HaveInCommons {
	protected Graph graph;

	protected HashMap<Long, Vertex> vertexIndex;
	protected HashMap<Vertex, Long> reverseVertexIndex;

	/**
	 * constructor
	 */
	public NormailzedRetrieval() {
		graph = new DirectedSparseGraph();
		vertexIndex = new HashMap<Long, Vertex>();
		reverseVertexIndex = new HashMap<Vertex, Long>();
	}

	/**
	 * helper function to just store the newly created edge
	 * 
	 * @return returns true if the edge has been created and false if it already
	 *         existed
	 */
	protected boolean storeEdge(long from, long to) {

		Vertex f = vertexIndex.get(from);
		if (f == null) {
			f = new DirectedSparseVertex();

			vertexIndex.put(from, f);
			reverseVertexIndex.put(f, from);

			graph.addVertex(f);
		}

		Vertex t = vertexIndex.get(to);
		if (t == null) {
			t = new DirectedSparseVertex();

			vertexIndex.put(to, t);
			reverseVertexIndex.put(t, to);

			graph.addVertex(t);
		}

		Edge edge = new DirectedSparseEdge(f, t);

		if (graph.getEdges().contains(edge)) {
			return false;
		} else {
			try {
				graph.addEdge(edge);
			} catch (Exception e) {
				// System.out.println("could not add edge");
				return false;
			}
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.metalcon.haveInCommons.HaveInCommons#getCommonNodes(java.lang.String,
	 * java.lang.String)
	 */
	public long[] getCommonNodes(long uuid1, long uuid2) {
		Vertex f = vertexIndex.get(uuid1);
		Vertex t = vertexIndex.get(uuid2);
		Set<Vertex> intersection = new HashSet<Vertex>(f.getOutEdges());
		intersection.retainAll(t.getInEdges());
		long[] result = new long[intersection.size()];
		int i = 0;
		for (Vertex v : intersection) {
			result[i++] = reverseVertexIndex.get(v).longValue();
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.metalcon.haveInCommons.HaveInCommons#putEdge(java.lang.String,
	 * java.lang.String)
	 */
	public void putEdge(long from, long to, final Vote vote) {
		if (storeEdge(from, to)) {
			// updateCommons(from, to);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.metalcon.haveInCommons.HaveInCommons#delegeEdge(java.lang.String,
	 * java.lang.String)
	 */
	public void deleteEdge(long from, long to) {
	}
}