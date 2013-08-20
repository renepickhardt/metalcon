package de.metalcon.socialgraph.algorithms;

import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import org.json.simple.JSONObject;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.kernel.AbstractGraphDatabase;

import de.metalcon.server.statusupdates.StatusUpdate;
import de.metalcon.socialgraph.NeoUtils;
import de.metalcon.socialgraph.Properties;
import de.metalcon.socialgraph.SocialGraph;
import de.metalcon.socialgraph.SocialGraphRelationshipType;
import de.metalcon.socialgraph.User;

/**
 * first social networking approach
 * 
 * @author Rene Pickhardt, Jonas Kunze, Sebastian Schlicht
 * 
 */
public class WriteOptimizedGraphity extends SocialGraph {

	public WriteOptimizedGraphity(final AbstractGraphDatabase graph) {
		super(graph);
	}

	@Override
	public void createFriendship(final Node following, final Node followed) {
		// try to find the node of the user followed
		for (Relationship followship : following.getRelationships(
				SocialGraphRelationshipType.FOLLOW, Direction.OUTGOING)) {
			if (followship.getEndNode().equals(followed)) {
				return;
			}
		}

		// create star topology
		following.createRelationshipTo(followed,
				SocialGraphRelationshipType.FOLLOW);
	}

	@Override
	public void createStatusUpdate(final long timestamp, final Node user,
			StatusUpdate content) {
		// get last recent status update
		final Node lastUpdate = NeoUtils.getNextSingleNode(user,
				SocialGraphRelationshipType.UPDATE);

		// create new status update node
		final Node crrUpdate = NeoUtils.createStatusUpdateNode(content.getId());

		// prepare status update for JSON parsing
		content.setTimestamp(timestamp);
		content.setCreator(new User(user));

		// fill status update node
		crrUpdate.setProperty(Properties.StatusUpdate.TIMESTAMP, timestamp);
		crrUpdate.setProperty(Properties.StatusUpdate.CONTENT_TYPE,
				content.getType());
		crrUpdate.setProperty(Properties.StatusUpdate.CONTENT, content
				.toJSONObject().toJSONString());

		// update references to previous status update (if existing)
		if (lastUpdate != null) {
			user.getSingleRelationship(SocialGraphRelationshipType.UPDATE,
					Direction.OUTGOING).delete();
			crrUpdate.createRelationshipTo(lastUpdate,
					SocialGraphRelationshipType.UPDATE);
		}

		// add reference from user to current update node
		user.createRelationshipTo(crrUpdate, SocialGraphRelationshipType.UPDATE);
		user.setProperty(Properties.User.LAST_UPDATE, timestamp);
	}

	@Override
	public List<JSONObject> readStatusUpdates(final Node poster,
			final Node user, final int numItems, boolean ownUpdates) {
		if (!poster.equals(user)) {
			ownUpdates = true;
		}

		final List<JSONObject> statusUpdates = new LinkedList<JSONObject>();

		// check if ego network stream is being accessed
		if (!ownUpdates) {
			final TreeSet<StatusUpdateUser> users = new TreeSet<StatusUpdateUser>(
					new StatusUpdateUserComparator());

			// loop through users followed
			Node userNode;
			StatusUpdateUser crrUser;
			for (Relationship relationship : poster.getRelationships(
					SocialGraphRelationshipType.FOLLOW, Direction.OUTGOING)) {
				userNode = relationship.getEndNode();

				// add users having status updates
				crrUser = new StatusUpdateUser(userNode);
				if (crrUser.hasStatusUpdate()) {
					users.add(crrUser);
				}
			}

			// handle user queue
			while ((statusUpdates.size() < numItems) && !users.isEmpty()) {
				crrUser = users.pollLast();

				// add last recent status update of current user
				statusUpdates.add(crrUser.getStatusUpdate());

				// re-add current user if more status updates available
				if (crrUser.hasStatusUpdate()) {
					users.add(crrUser);
				}
			}
		} else {
			// access single stream only
			final StatusUpdateUser posterNode = new StatusUpdateUser(poster);
			while ((statusUpdates.size() < numItems)
					&& posterNode.hasStatusUpdate()) {
				statusUpdates.add(posterNode.getStatusUpdate());
			}
		}

		return statusUpdates;
	}

	@Override
	public boolean removeFriendship(final Node following, final Node followed) {
		// delete the followship if existing
		final Relationship followship = NeoUtils.getRelationshipBetween(
				following, followed, SocialGraphRelationshipType.FOLLOW,
				Direction.OUTGOING);

		// there is no such followship existing
		if (followship == null) {
			return false;
		}

		followship.delete();
		return true;
	}

	@Override
	public boolean deleteStatusUpdate(final Node user, final Node statusUpdate) {
		// get the status update owner
		final Node statusUpdateAuthor = NeoUtils.getPrevSingleNode(
				statusUpdate, SocialGraphRelationshipType.UPDATE);

		// the status update is not owned by the user passed
		if (!user.equals(statusUpdateAuthor)) {
			return false;
		}

		// remove reference from previous status update
		final Node previousUpdate = NeoUtils.getPrevSingleNode(statusUpdate,
				SocialGraphRelationshipType.UPDATE);
		previousUpdate.getSingleRelationship(
				SocialGraphRelationshipType.UPDATE, Direction.OUTGOING)
				.delete();

		// update references to the next status update (if existing)
		final Node nextUpdate = NeoUtils.getNextSingleNode(statusUpdate,
				SocialGraphRelationshipType.UPDATE);
		if (nextUpdate != null) {
			statusUpdate.getSingleRelationship(
					SocialGraphRelationshipType.UPDATE, Direction.OUTGOING)
					.delete();
			previousUpdate.createRelationshipTo(nextUpdate,
					SocialGraphRelationshipType.UPDATE);
		}

		// delete the status update node
		statusUpdate.delete();
		return true;
	}
}