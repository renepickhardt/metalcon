/**
 * @author Jonas Kunze
 * 
 */
package de.metalcon.haveInCommons;

import java.util.HashSet;
import java.util.Set;

import com.udpwork.ssdb.Response;
import com.udpwork.ssdb.SSDB;

import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;

public class RetrievalOptimizedSSDB extends RetrievalOptimized implements
		HaveInCommons {
	private SSDB db = null;
	private Response resp = null;

	protected Set<String> getCommonSetValue(String key) {
		HashSet<String> commons = new HashSet<String>();

		try {
			resp = db.zscan(key, "", null, null, 100);

			for (byte[] bytes : resp.items.keySet()) {
				commons.add(new String(bytes, "UTF-8"));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return commons;
	}

	protected void saveCommonSetValue(String key, String value) {
		try {
			db.zset(key, value, 1);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * constructor
	 */
	public RetrievalOptimizedSSDB() {
		super();
		try {
			db = new SSDB("127.0.0.1", 8888);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * adds the key of the vertices that are in common to the hashset. always
	 * assumes that vertices from and to exist in the graph and that from and to
	 * are never null
	 * 
	 * @param from
	 * @param to
	 * @throws Exception
	 */
	private void updateCommons(String from, String to) throws Exception {
		if (from == null || to == null)
			throw new IllegalArgumentException();
		Vertex f = this.vertices.get(from);
		Vertex t = this.vertices.get(to);
		// include from to the commons set of (tmp and to)
		for (Object tmp : f.getInEdges()) {
			if (((DirectedSparseEdge) tmp).getSource() == t) {
				continue;
			}
			String key = ((DirectedSparseEdge) tmp).getSource().toString()
					+ ":" + t.toString();
			saveCommonSetValue(key, from);
		}
		// include to to the commons set of (from and tmp)
		for (Object tmp : t.getOutEdges()) {
			if (((DirectedSparseEdge) tmp).getDest() == f) {
				continue;
			}
			String key = f.toString() + ":"
					+ ((DirectedSparseEdge) tmp).getDest().toString();
			saveCommonSetValue(key, to);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.metalcon.haveInCommons.HaveInCommons#putEdge(java.lang.String,
	 * java.lang.String)
	 */
	public void putEdge(int from, int to) {
		if (storeEdge(from, to)) {
			try {
				updateCommons(from, to);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}