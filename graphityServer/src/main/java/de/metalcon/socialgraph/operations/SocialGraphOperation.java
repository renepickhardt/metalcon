package de.metalcon.socialgraph.operations;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

import de.metalcon.server.tomcat.GraphityHttpServlet;
import de.metalcon.socialgraph.SocialGraph;

/**
 * basic class for smallest possible operations on social graphs
 * 
 * @author sebschlicht
 * 
 */
public abstract class SocialGraphOperation {

	/**
	 * request servlet
	 */
	protected final GraphityHttpServlet servlet;

	/**
	 * executing user
	 */
	protected final Node user;

	/**
	 * create a new basic social graph operation
	 * 
	 * @param servlet
	 *            request servlet
	 * @param user
	 *            user executing
	 */
	public SocialGraphOperation(final GraphityHttpServlet servlet,
			final Node user) {
		this.servlet = servlet;
		this.user = user;
	}

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
		this.servlet.finish();
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