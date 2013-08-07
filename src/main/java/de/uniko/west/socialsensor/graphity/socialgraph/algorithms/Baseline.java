package de.uniko.west.socialsensor.graphity.socialgraph.algorithms;

import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.NotFoundException;
import org.neo4j.graphdb.Relationship;
import org.neo4j.kernel.AbstractGraphDatabase;

import de.uniko.west.socialsensor.graphity.server.statusupdates.StatusUpdate;
import de.uniko.west.socialsensor.graphity.socialgraph.NeoUtils;
import de.uniko.west.socialsensor.graphity.socialgraph.Properties;
import de.uniko.west.socialsensor.graphity.socialgraph.SocialGraph;
import de.uniko.west.socialsensor.graphity.socialgraph.SocialGraphRelationshipType;
import de.uniko.west.socialsensor.graphity.socialgraph.User;

/**
 * first social networking approach
 * 
 * @author Rene Pickhardt, Jonas Kunze, Sebastian Schlicht
 * 
 */
public class Baseline extends SocialGraph {

	public Baseline(final AbstractGraphDatabase graph) {
		super(graph);
	}

	@Override
	public boolean createFriendship(final long timestamp,
			final long followingId, final long followedId) {
		// find users first
		Node following, followed;
		try {
			following = NeoUtils.getNodeByIdentifier(this.graph, followingId);
			followed = NeoUtils.getNodeByIdentifier(this.graph, followedId);
		} catch (final NotFoundException e) {
			return false;
		}

		// create star topology
		following.createRelationshipTo(followed,
				SocialGraphRelationshipType.FOLLOW);

		return true;
	}

	@Override
	public long createStatusUpdate(long timestamp, long userID,
			StatusUpdate content) {
		// find user first
		Node user;
		try {
			user = NeoUtils.getNodeByIdentifier(this.graph, userID);
		} catch (final NotFoundException e) {
			return 0;
		}

		// get last recent status update
		final Node lastUpdate = NeoUtils.getNextSingleNode(user,
				SocialGraphRelationshipType.UPDATE);

		// create new status update node
		final Node crrUpdate = this.graph.createNode();

		// prepare status update for JSON parsing
		content.setTimestamp(timestamp);
		content.setId(crrUpdate.getId());
		content.setCreator(new User(user));

		// fill status update node
		crrUpdate.setProperty(Properties.TIMESTAMP, timestamp);
		crrUpdate.setProperty(Properties.CONTENT_TYPE, content.getType());
		crrUpdate.setProperty(Properties.CONTENT, content.toJSONString());

		// update references to previous status update (if existing)
		if (lastUpdate != null) {
			user.getSingleRelationship(SocialGraphRelationshipType.UPDATE,
					Direction.OUTGOING).delete();
			crrUpdate.createRelationshipTo(lastUpdate,
					SocialGraphRelationshipType.UPDATE);
		}

		// add reference from user to current update node
		user.createRelationshipTo(crrUpdate, SocialGraphRelationshipType.UPDATE);
		user.setProperty(Properties.LAST_UPDATE, timestamp);

		return crrUpdate.getId();
	}

	@Override
	public List<String> readStatusUpdates(long posterId, long readerId,
			int numItems, boolean ownUpdates) {
		// find users first
		Node poster, reader;
		try {
			poster = NeoUtils.getNodeByIdentifier(this.graph, posterId);
			if (posterId == readerId) {
				reader = poster;
			} else {
				ownUpdates = true;
				reader = NeoUtils.getNodeByIdentifier(this.graph, readerId);
			}
		} catch (final NotFoundException e) {
			return null;
		}

		final List<String> statusUpdates = new LinkedList<String>();

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
	public boolean removeFriendship(long followingId, long followedId) {
		// find users first
		Node following, followed;
		try {
			following = NeoUtils.getNodeByIdentifier(this.graph, followingId);
			followed = NeoUtils.getNodeByIdentifier(this.graph, followedId);
		} catch (final NotFoundException e) {
			return false;
		}

		// delete the followship if existing
		final Relationship followship = NeoUtils.getRelationshipBetween(
				following, followed, SocialGraphRelationshipType.FOLLOW,
				Direction.OUTGOING);
		if (followship != null) {
			followship.delete();
			return true;
		}

		// there is no such followship existing
		return false;
	}

	@Override
	public boolean removeStatusUpdate(long userId, long statusUpdateId) {
		// find user first
		Node user;
		try {
			user = NeoUtils.getNodeByIdentifier(this.graph, userId);
		} catch (final NotFoundException e) {
			return false;
		}

		final Node statusUpdate = NeoUtils
				.getStatusUpdate(user, statusUpdateId);

		// remove the status update if the ownership has been proved
		if (statusUpdate != null) {
			// remove reference from previous status update
			final Node previousUpdate = NeoUtils.getPrevSingleNode(
					statusUpdate, SocialGraphRelationshipType.UPDATE);
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

		// the status update is not owned by the user passed
		return false;
	}
}