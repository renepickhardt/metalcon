/**
 * 
 */
package de.metalcon.haveInCommons;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

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
public class RetrievalOptimized implements HaveInCommons {
	protected Graph graph;
	protected HashMap<String, Vertex> vertices;
	protected HashMap<String, Set<String>> commonSet;

	/**
	 * constructor
	 */
	public RetrievalOptimized() {
		graph = new DirectedSparseGraph();
		vertices = new HashMap<String, Vertex>();
		commonSet = new HashMap<String, Set<String>>();
	}

	/**
	 * helper function to just store the newly created edge
	 * 
	 * @return returns true if the edge has been created and false if it already
	 *         existed
	 */
	protected boolean storeEdge(String from, String to) {
		Vertex f = vertices.get(from);
		if (f == null) {
			f = new DirectedSparseVertex();
			vertices.put(from, f);
			graph.addVertex(f);
		}
		Vertex t = vertices.get(to);
		if (t == null) {
			t = new DirectedSparseVertex();
			vertices.put(to, t);
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
	private void updateCommons(String from, String to) {
		if (from == null || to == null)
			throw new IllegalArgumentException();
		Vertex f = this.vertices.get(from);
		Vertex t = this.vertices.get(to);
		// include from to the commons set of (tmp and to)
		for (Object tmp : f.getInEdges()) {
			if (((DirectedSparseEdge) tmp).getSource() == t) {
				continue;
			}
			String key = ((DirectedSparseEdge) tmp).getSource().toString() + ":" + t.toString();
			saveCommonSetValue(key, from);
		}
		// include to to the commons set of (from and tmp)
		for (Object tmp : t.getOutEdges()) {
			if (((DirectedSparseEdge) tmp).getDest() == f) {
				continue;
			}
			String key = f.toString() + ":" + ((DirectedSparseEdge) tmp).getDest().toString();
			saveCommonSetValue(key, to);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.metalcon.haveInCommons.HaveInCommons#getCommonNodes(java.lang.String,
	 * java.lang.String)
	 */
	public Set<String> getCommonNodes(String uuid1, String uuid2) {
		Vertex f = vertices.get(uuid1);
		Vertex t = vertices.get(uuid2);
		String key = f.toString() + ":" + t.toString();
		return getCommonSetValue(key);
	}

	protected Set<String> getCommonSetValue(String key) {
		return commonSet.get(key);
	}

	protected void saveCommonSetValue(String key, String value) {
		Set<String> s = getCommonSetValue(key);
		if (s == null) {
			s = new HashSet<String>();
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
	public void putEdge(String from, String to) {
		if (storeEdge(from, to)) {
			//updateCommons(from, to);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.metalcon.haveInCommons.HaveInCommons#delegeEdge(java.lang.String,
	 * java.lang.String)
	 */
	public boolean delegeEdge(String from, String to) {
		return false;
	}

	@Override
	public void putFinished() {
		// TODO Auto-generated method stub

	}

}