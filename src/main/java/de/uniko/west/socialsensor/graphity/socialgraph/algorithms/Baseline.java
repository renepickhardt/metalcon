package de.uniko.west.socialsensor.graphity.socialgraph.algorithms;

import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.NotFoundException;
import org.neo4j.graphdb.Relationship;
import org.neo4j.kernel.AbstractGraphDatabase;

import de.uniko.west.socialsensor.graphity.socialgraph.NeoUtils;
import de.uniko.west.socialsensor.graphity.socialgraph.Properties;
import de.uniko.west.socialsensor.graphity.socialgraph.SocialGraph;
import de.uniko.west.socialsensor.graphity.socialgraph.SocialGraphRelationshipType;
import de.uniko.west.socialsensor.graphity.socialgraph.User;
import de.uniko.west.socialsensor.graphity.socialgraph.statusupdates.StatusUpdate;

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
			following = this.graph.getNodeById(followingId);
			followed = this.graph.getNodeById(followedId);
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
			user = this.graph.getNodeById(userID);
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
		crrUpdate.setProperty(Properties.Timestamp, timestamp);
		crrUpdate.setProperty(Properties.ContentType, content.getType());
		crrUpdate.setProperty(Properties.Content, content.toJSONString());

		// update references to previous status update (if existing)
		if (lastUpdate != null) {
			user.getSingleRelationship(SocialGraphRelationshipType.UPDATE,
					Direction.OUTGOING).delete();
			crrUpdate.createRelationshipTo(lastUpdate,
					SocialGraphRelationshipType.UPDATE);
		}

		// add reference from user to current update node
		user.createRelationshipTo(crrUpdate, SocialGraphRelationshipType.UPDATE);
		user.setProperty(Properties.LastUpdate, timestamp);

		return crrUpdate.getId();
	}

	@Override
	public List<String> readStatusUpdates(long posterId, long readerId,
			int numItems, boolean ownUpdates) {
		// find users first
		Node poster, reader;
		try {
			poster = this.graph.getNodeById(posterId);
			if (posterId == readerId) {
				reader = poster;
			} else {
				ownUpdates = true;
				reader = this.graph.getNodeById(readerId);
			}
		} catch (final NotFoundException e) {
			// TODO: catch all exceptions in network responder
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

}