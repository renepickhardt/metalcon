package de.uniko.west.socialsensor.graphity.socialgraph;

import org.neo4j.graphdb.Transaction;
import org.neo4j.kernel.AbstractGraphDatabase;

/**
 * basic social graph algorithm implementation
 * 
 * @author sebschlicht
 * 
 */
public abstract class SocialGraph {

	/**
	 * social graph targeted by transactions
	 */
	protected final AbstractGraphDatabase graph;

	/**
	 * create a new social graph
	 * 
	 * @param graph
	 *            graph database to operate on
	 */
	public SocialGraph(final AbstractGraphDatabase graph) {
		this.graph = graph;
	}

	/**
	 * start a new graph transaction
	 * 
	 * @return transaction object
	 */
	public Transaction beginTx() {
		return this.graph.beginTx();
	}

	/**
	 * create a new status update for a user
	 * 
	 * @param timestamp
	 *            time stamp of the status update
	 * @param userID
	 *            user identifier
	 * @param content
	 *            status update content object
	 * @return true - status update has been added successfully<br>
	 *         false otherwise
	 */
	public abstract boolean createStatusUpdate(long timestamp, long userID,
			StatusUpdate content);

	/**
	 * create a new friendship from a user to another
	 * 
	 * @param timestamp
	 *            time stamp of the friendship
	 * @param followingId
	 *            identifier of the user following
	 * @param followedId
	 *            identifier of the user being followed
	 * @return true - friendship has been added successfully<br>
	 *         false otherwise
	 */
	public abstract boolean createFriendship(long timestamp, long followingId,
			long followedId);

}