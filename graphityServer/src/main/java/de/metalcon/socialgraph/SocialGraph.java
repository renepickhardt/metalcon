package de.metalcon.socialgraph;

import java.util.List;

import org.json.simple.JSONObject;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
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
	 * create a new user
	 * 
	 * @param userId
	 *            user identifier
	 * @param displayName
	 *            user display name
	 * @param profilePicturePath
	 *            path to the profile picture of the user
	 */
	public void createUser(final String userId, final String displayName,
			final String profilePicturePath) {
		final Node user = NeoUtils.createUserNode(userId);
		user.setProperty(Properties.User.DISPLAY_NAME, displayName);
		user.setProperty(Properties.User.PROFILE_PICTURE_PATH,
				profilePicturePath);
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
	public abstract void createFriendship(Node following, Node followed);

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
	public abstract List<JSONObject> readStatusUpdates(Node poster, Node user,
			int numItems, boolean ownUpdates);

	/**
	 * delete a user removing it from all replica layers of following users
	 * 
	 * @param user
	 *            user that shall be deleted
	 */
	public void deleteUser(final Node user) {
		Node userReplica, following;
		for (Relationship replica : user.getRelationships(
				SocialGraphRelationshipType.REPLICA, Direction.INCOMING)) {
			userReplica = replica.getEndNode();
			following = NeoUtils.getPrevSingleNode(userReplica,
					SocialGraphRelationshipType.FOLLOW);
			this.removeFriendship(following, user);
		}
	}

	/**
	 * remove a follow edge from one user to another
	 * 
	 * @param following
	 *            user following, requesting the deletion
	 * @param followed
	 *            user followed
	 * @return true - if the follow edge has been removed<br>
	 *         false - if there is no follow edge between the users
	 */
	public abstract boolean removeFriendship(Node following, Node followed);

	/**
	 * delete a status update from a user
	 * 
	 * @param user
	 *            user requesting
	 * @param statusUpdate
	 *            status update
	 * @return true - if the status update has been removed<br>
	 *         false - if the status update is not owned by the user requesting
	 */
	public abstract boolean deleteStatusUpdate(Node user, Node statusUpdate);

}