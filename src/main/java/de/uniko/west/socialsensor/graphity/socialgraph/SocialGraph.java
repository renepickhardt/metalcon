package de.uniko.west.socialsensor.graphity.socialgraph;

import java.util.List;

import org.neo4j.graphdb.Transaction;
import org.neo4j.kernel.AbstractGraphDatabase;

import de.uniko.west.socialsensor.graphity.server.statusupdates.StatusUpdate;

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
	 * @return status update node identifier if created successfully<br>
	 *         zero - otherwise
	 */
	public abstract long createStatusUpdate(long timestamp, long userID,
			StatusUpdate content);

	/**
	 * create a new friendship from a user to another
	 * 
	 * @param followingId
	 *            identifier of the user following
	 * @param followedId
	 *            identifier of the user being followed
	 */
	public abstract void createFriendship(long timestamp, long followingId,
			long followedId);

	/**
	 * read some status updates for/from a user
	 * 
	 * @param posterId
	 *            identifier of the targeted stream's owner
	 * @param userId
	 *            identifier of the reading user
	 * @param numItems
	 *            number of items to be read
	 * @param ownUpdates
	 *            single stream flag
	 * @return list containing numItems status update Activities<br>
	 *         (Activitystrea.ms)
	 */
	public abstract List<String> readStatusUpdates(long posterId,
			long userId, int numItems, boolean ownUpdates);

	/**
	 * remove a friendship from one user to another
	 * 
	 * @param timestamp
	 *            time stamp of the friendship removal
	 * @param followingId
	 *            identifier of the user following, requesting the deletion
	 * @param followedId
	 *            identifier of the user followed
	 */
	public abstract void removeFriendship(long timestamp, long followingId,
			long followedId);

	/**
	 * remove a status update from a user
	 * 
	 * @param userId
	 *            identifier of the user owning the status update
	 * @param statusUpdateId
	 *            identifier of the status update
	 */
	public abstract void removeStatusUpdate(long userId, long statusUpdateId);

}