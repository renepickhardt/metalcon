/**
 * 
 */
package de.metalcon.haveInCommons;


/**
 * @author Rene Pickhardt
 * 
 */
public interface HaveInCommons {
	/**
	 * Retrieves the list of common neighbors of 2 nodes in the graph with uuid1
	 * and uuid2
	 * 
	 * @param uuid1
	 * @param uuid2
	 * @return
	 */
	public long[] getCommonNodes(long uuid1, long uuid2);
<<<<<<< HEAD

=======
	
>>>>>>> 4b22e8f0ec7b54aadba2014f738d766e1cfe6d0e
	/**
	 * puts a new Edge to the data store.
	 * 
	 * @param from
	 * @param to
	 */
	public void putEdge(long from, long to);

	/**
	 * deletes an Edge from the graph
	 * 
	 * @param from
	 * @param to
	 * @return true if the edge was in the graph
	 */
<<<<<<< HEAD
	public boolean deleteEdge(long from, long to);
=======
	public boolean delegeEdge(long from, long to);

>>>>>>> 4b22e8f0ec7b54aadba2014f738d766e1cfe6d0e
}
