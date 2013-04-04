package de.uniko.west.socialsensor.graphity.socialgraph.operations;

import org.neo4j.graphdb.Transaction;

import de.uniko.west.socialsensor.graphity.socialgraph.SocialGraph;

/**
 * basic class for smallest possible operations on social graphs
 * 
 * @author sebschlicht
 * 
 */
public abstract class SocialGraphOperation {

	/**
	 * execute the transaction<br>
	 * for impacts see concrete graph operations
	 * 
	 * @param graph
	 *            social graph targeted
	 */
	public void run(final SocialGraph graph) {
		final Transaction transaction = graph.beginTx();
		if (this.execute(graph)) {
			transaction.success();
		}
		transaction.finish();
	}

	/**
	 * operation execution to be implemented by the social graph
	 * 
	 * @param graph
	 *            social graph targeted
	 * @return transaction success flag
	 */
	protected abstract boolean execute(final SocialGraph graph);

}