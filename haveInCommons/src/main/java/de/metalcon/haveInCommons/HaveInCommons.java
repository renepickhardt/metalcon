/**
 * 
 */
package de.metalcon.haveInCommons;

import java.util.Set;

/**
 * @author Rene Pickhardt
 *
 */
public interface HaveInCommons {
	/**
	 * Retrieves the list of common neighbors of 2 nodes in the graph with uuid1 and uuid2
	 * @param uuid1
	 * @param uuid2
	 * @return
	 */
	public Set<String> getCommonNodes(String uuid1, String uuid2);
	
	/**
	 * puts a new Edge to the data store.
	 * @param from
	 * @param to
	 */
	public void putEdge(String from, String to);

	/**
	 * deletes an Edge from the graph
	 * @param from
	 * @param to
	 * @return true if the edge was in the graph
	 */
	public boolean delegeEdge(String from, String to);
}
