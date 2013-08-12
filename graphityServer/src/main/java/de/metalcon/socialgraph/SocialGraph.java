package de.metalcon.socialgraph;

import java.util.List;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.kernel.AbstractGraphDatabase;

import de.metalcon.server.statusupdates.StatusUpdate;

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
	 * @param user
	 *            user
	 * @param content
	 *            status update content object
	 */
	public abstract void createStatusUpdate(long timestamp, Node user,
			StatusUpdate content);

	/**
	 * create a new friendship from a user to another
	 * 
	 * @param following
	 *            user following
	 * @param followed
	 *            user being followed
	 */
	public abstract void createFriendship(long timestamp, Node following,
			Node followed);

	/**
	 * read some status updates for/from a user
	 * 
	 * @param poster
	 *            owner of the stream targeted
	 * @param user
	 *            reading user
	 * @param numItems
	 *            number of items to be read
	 * @param ownUpdates
	 *            single stream flag
	 * @return list containing numItems status update Activities<br>
	 *         (Activitystrea.ms)
	 */
	public abstract List<String> readStatusUpdates(Node poster, Node user,
			int numItems, boolean ownUpdates);

	/**
	 * remove a friendship from one user to another
	 * 
	 * @param timestamp
	 *            time stamp of the friendship removal
	 * @param following
	 *            user following, requesting the deletion
	 * @param followed
	 *            user followed
	 */
	public abstract void removeFriendship(long timestamp, Node following,
			Node followed);

	/**
	 * remove a status update from a user
	 * 
	 * @param user
	 *            user requesting
	 * @param statusUpdate
	 *            status update
	 */
	public abstract void removeStatusUpdate(Node user, Node statusUpdate);

}