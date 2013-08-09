package de.uniko.west.socialsensor.graphity.socialgraph.algorithms;

import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.kernel.AbstractGraphDatabase;

import de.uniko.west.socialsensor.graphity.server.exceptions.InvalidUserIdentifierException;
import de.uniko.west.socialsensor.graphity.server.exceptions.create.follow.FollowEdgeExistingException;
import de.uniko.west.socialsensor.graphity.server.exceptions.create.follow.InvalidCreateFollowedIdentifier;
import de.uniko.west.socialsensor.graphity.server.exceptions.delete.follow.FollowEdgeNotExistingException;
import de.uniko.west.socialsensor.graphity.server.exceptions.delete.follow.InvalidDeleteFollowedIdentifier;
import de.uniko.west.socialsensor.graphity.server.exceptions.delete.statusupdate.InvalidStatusUpdateIdentifierException;
import de.uniko.west.socialsensor.graphity.server.exceptions.delete.statusupdate.StatusUpdateNotOwnedException;
import de.uniko.west.socialsensor.graphity.server.exceptions.read.InvalidPosterIdentifierException;
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
	public void createFriendship(final long timestamp, final long followingId,
			final long followedId) {
		// find users first
		Node following, followed;
		try {
			following = NeoUtils.getNodeByIdentifier(this.graph, followingId);

		} catch (final IllegalArgumentException e) {
			throw new InvalidUserIdentifierException("user with id \""
					+ followingId + "\" is not existing.");
		}

		try {
			followed = NeoUtils.getNodeByIdentifier(this.graph, followedId);
		} catch (final IllegalArgumentException e) {
			throw new InvalidCreateFollowedIdentifier("user with id \""
					+ followedId + "\" is not existing.");
		}

		// try to find the node of the user followed
		for (Relationship followship : following.getRelationships(
				SocialGraphRelationshipType.FOLLOW, Direction.OUTGOING)) {
			if (followship.getEndNode().equals(followed)) {
				final String followedName = (String) followed
						.getProperty(Properties.User.DISPLAY_NAME);
				throw new FollowEdgeExistingException("you are following \""
						+ followedName + "\" already.");
			}
		}

		// create star topology
		following.createRelationshipTo(followed,
				SocialGraphRelationshipType.FOLLOW);
	}

	@Override
	public long createStatusUpdate(final long timestamp, final long userID,
			StatusUpdate content) {
		// find user first
		Node user;
		try {
			user = NeoUtils.getNodeByIdentifier(this.graph, userID);
		} catch (final IllegalArgumentException e) {
			throw new InvalidUserIdentifierException("user with id \"" + userID
					+ "\" is not existing.");
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
		crrUpdate.setProperty(Properties.StatusUpdate.TIMESTAMP, timestamp);
		crrUpdate.setProperty(Properties.StatusUpdate.CONTENT_TYPE,
				content.getType());
		crrUpdate.setProperty(Properties.StatusUpdate.CONTENT,
				content.toJSONString());

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

		return crrUpdate.getId();
	}

	@Override
	public List<String> readStatusUpdates(final long posterId,
			final long userId, final int numItems, boolean ownUpdates) {
		// find users first
		Node user, poster;
		try {
			user = NeoUtils.getNodeByIdentifier(this.graph, userId);
		} catch (final IllegalArgumentException e) {
			throw new InvalidUserIdentifierException("user with id \"" + userId
					+ "\" is not existing.");
		}

		if (userId == posterId) {
			poster = user;
		} else {
			ownUpdates = true;
			try {
				poster = NeoUtils.getNodeByIdentifier(this.graph, posterId);
			} catch (final IllegalArgumentException e) {
				throw new InvalidPosterIdentifierException("user with id \""
						+ posterId + "\" is not existing.");
			}
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
	public void removeFriendship(final long timestamp, final long followingId,
			final long followedId) {
		// find users first
		Node following, followed;
		try {
			following = NeoUtils.getNodeByIdentifier(this.graph, followingId);
		} catch (final IllegalArgumentException e) {
			throw new InvalidUserIdentifierException("user with id \""
					+ followingId + "\" is not existing.");
		}

		try {
			followed = NeoUtils.getNodeByIdentifier(this.graph, followedId);
		} catch (final IllegalArgumentException e) {
			throw new InvalidDeleteFollowedIdentifier("user with id \""
					+ followedId + "\" is not existing.");
		}

		// delete the followship if existing
		final Relationship followship = NeoUtils.getRelationshipBetween(
				following, followed, SocialGraphRelationshipType.FOLLOW,
				Direction.OUTGOING);

		// there is no such followship existing
		if (followship == null) {
			final String followedName = (String) followed
					.getProperty(Properties.User.DISPLAY_NAME);
			throw new FollowEdgeNotExistingException("you are not following \""
					+ followedName + "\".");
		}

		followship.delete();
	}

	@Override
	public void removeStatusUpdate(final long userId, final long statusUpdateId) {
		// find user and status update first
		Node user, statusUpdate;
		try {
			user = NeoUtils.getNodeByIdentifier(this.graph, userId);
		} catch (final IllegalArgumentException e) {
			throw new InvalidUserIdentifierException("user with id \"" + userId
					+ "\" is not existing.");
		}

		try {
			statusUpdate = NeoUtils.getNodeByIdentifier(this.graph,
					statusUpdateId);
		} catch (final IllegalArgumentException e) {
			throw new InvalidStatusUpdateIdentifierException(
					"status update with id \"" + statusUpdateId
							+ "\" is not existing.");
		}

		// get the status update owner
		final Node statusUpdateAuthor = NeoUtils.getPrevSingleNode(
				statusUpdate, SocialGraphRelationshipType.UPDATE);

		// the status update is not owned by the user passed
		if (!user.equals(statusUpdateAuthor)) {
			throw new StatusUpdateNotOwnedException(
					"you do not own the status update with the id \""
							+ statusUpdateId + "\".");
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
	}
}