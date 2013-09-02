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
 *
 */
public class RetrievalOptimized implements HaveInCommons {
	private Graph graph;
	private HashMap<String, Vertex> vertices;
	private HashMap<String, Set<String>> commonSet;
	
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
	 * @return returns true if the edge has been created and false if it already existed
	 */
	private boolean storeEdge(String from, String to){
		Vertex f = vertices.get(from);
		if (f == null){
			f = new DirectedSparseVertex();
			vertices.put(from, f);
			graph.addVertex(f);
		}
		Vertex t = vertices.get(to);
		if (t == null){
			t = new DirectedSparseVertex();
			vertices.put(to, t);
			graph.addVertex(t);
		}
		Edge edge = new DirectedSparseEdge(f, t);
		if (graph.getEdges().contains(edge)){
			return false;
		}
		else {
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
	 * adds the key of the vertices that are in common to the hashset.
	 * always assumes that vertices from and to exist in the graph
	 * and that from and to are never null
	 * @param from
	 * @param to
	 */
	private void updateCommons(String from, String to) {
		if (from==null || to == null)throw new IllegalArgumentException();
		Vertex f = vertices.get(from);
		Vertex t = vertices.get(to);
		// include from to the commons set of (tmp and to)
		for (Object tmp : f.getInEdges()){
			String key = ((DirectedSparseEdge)tmp).getSource().toString()+":"+t.toString();
			Set<String> s = commonSet.get(key);
			if (s==null){
				s=new HashSet<String>();
			}
			s.add(from);
			commonSet.put(key, s);
		}
		// include to to the commons set of (from and tmp)
		for (Object tmp : t.getOutEdges()){
			String key = from.toString()+":"+((DirectedSparseEdge)tmp).getDest().toString();
			Set<String> s = commonSet.get(key);
			if (s==null){
				s=new HashSet<String>();
			}
			s.add(to);
			commonSet.put(key, s);
		}
	}
	
	/* (non-Javadoc)
	 * @see de.metalcon.haveInCommons.HaveInCommons#getCommonNodes(java.lang.String, java.lang.String)
	 */
	public Set<String> getCommonNodes(String uuid1, String uuid2) {
		Vertex f = vertices.get(uuid1);
		Vertex t = vertices.get(uuid2);
		String key = f.toString()+":"+t.toString();
		return commonSet.get(key);
	}

	/* (non-Javadoc)
	 * @see de.metalcon.haveInCommons.HaveInCommons#putEdge(java.lang.String, java.lang.String)
	 */
	public void putEdge(String from, String to) {
		if (storeEdge(from, to)){
			updateCommons(from, to);
		}
	}



	/* (non-Javadoc)
	 * @see de.metalcon.haveInCommons.HaveInCommons#delegeEdge(java.lang.String, java.lang.String)
	 */
	public boolean delegeEdge(String from, String to) {
		return false;
	}

}
