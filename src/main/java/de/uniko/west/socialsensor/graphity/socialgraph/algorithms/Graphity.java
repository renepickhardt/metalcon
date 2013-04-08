package de.uniko.west.socialsensor.graphity.socialgraph.algorithms;

import java.util.LinkedList;
import java.util.TreeSet;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.NotFoundException;
import org.neo4j.graphdb.Relationship;
import org.neo4j.kernel.AbstractGraphDatabase;

import de.uniko.west.socialsensor.graphity.socialgraph.NeoUtils;
import de.uniko.west.socialsensor.graphity.socialgraph.Properties;
import de.uniko.west.socialsensor.graphity.socialgraph.SocialGraph;
import de.uniko.west.socialsensor.graphity.socialgraph.SocialGraphRelationshipType;
import de.uniko.west.socialsensor.graphity.socialgraph.StatusUpdate;
import de.uniko.west.socialsensor.graphity.socialgraph.StatusUpdateWrapper;

/**
 * social graph implementation 'Graphity'
 * 
 * @author Rene Pickhardt, Jonas Kunze, Sebastian Schlicht
 * 
 */
public class Graphity extends SocialGraph {

	/**
	 * create a new graphity social graph
	 * 
	 * @param graph
	 *            graph database to operate on
	 */
	public Graphity(final AbstractGraphDatabase graph) {
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

		// create start topology
		following.createRelationshipTo(followed,
				SocialGraphRelationshipType.FOLLOW);

		// load user's Graphity ego type
		final DynamicRelationshipType egoType = getEgoType(following);

		// check if followed user is the first in following's ego network
		if (NeoUtils.getNextSingleNode(following, egoType) == null) {
			following.createRelationshipTo(followed, egoType);
		} else {
			// search for insertion index within following's ego network
			final long followedTimestamp = getLastUpdate(followed);
			long crrTimestamp;
			Node prev = following;
			Node next = null;
			while (true) {
				// get next user
				next = NeoUtils.getNextSingleNode(prev, egoType);
				if (next != null) {
					crrTimestamp = getLastUpdate(next);

					// step on if current user has newer status updates
					if (crrTimestamp > followedTimestamp) {
						prev = next;
						continue;
					}
				}

				// insertion position has been found
				break;
			}

			// insert followed user into following's ego network
			if (next != null) {
				prev.getSingleRelationship(egoType, Direction.OUTGOING)
						.delete();
			}
			prev.createRelationshipTo(followed, egoType);
			if (next != null) {
				followed.createRelationshipTo(next, egoType);
			}
		}

		return true;
	}

	@Override
	public boolean createStatusUpdate(final long timestamp, final long userID,
			StatusUpdate content) {
		// find user first
		Node user;
		try {
			user = this.graph.getNodeById(userID);
		} catch (final NotFoundException e) {
			return false;
		}

		// get last recent status update
		final Node lastUpdate = NeoUtils.getNextSingleNode(user,
				SocialGraphRelationshipType.UPDATE);

		// create new status update
		final Node crrUpdate = this.graph.createNode();
		crrUpdate.setProperty(Properties.Timestamp, timestamp);
		crrUpdate.setProperty(Properties.ContentType, content.getType());
		crrUpdate.setProperty(Properties.Content, content);

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

		// update ego network for this user
		this.UpdateEgoNetwork(user);

		return true;
	}

	/**
	 * update the ego network of a user
	 * 
	 * @param user
	 *            user where changes have occurred
	 */
	private void UpdateEgoNetwork(final Node user) {
		Node follower, lastPoster;
		DynamicRelationshipType egoType;
		Node prevUser, nextUser;

		// loop through users following
		for (Relationship relationship : user.getRelationships(
				SocialGraphRelationshipType.FOLLOW, Direction.INCOMING)) {
			// load each user and its Graphity ego type
			follower = relationship.getStartNode();
			egoType = getEgoType(follower);

			// bridge user node
			prevUser = NeoUtils.getPrevSingleNode(user, egoType);
			if (!prevUser.equals(follower)) {
				user.getSingleRelationship(egoType, Direction.INCOMING)
						.delete();

				nextUser = NeoUtils.getNextSingleNode(user, egoType);
				if (nextUser != null) {
					user.getSingleRelationship(egoType, Direction.OUTGOING)
							.delete();
					prevUser.createRelationshipTo(nextUser, egoType);
				}
			}

			// insert user node at its new position
			lastPoster = NeoUtils.getNextSingleNode(follower, egoType);
			if (!lastPoster.equals(user)) {
				follower.getSingleRelationship(egoType, Direction.OUTGOING)
						.delete();
				follower.createRelationshipTo(user, egoType);
				user.createRelationshipTo(lastPoster, egoType);
			}
		}
	}

	@Override
	public LinkedList<StatusUpdateWrapper> readStatusUpdates(
			final long posterId, final long readerId, final int numItems,
			boolean ownUpdates) {
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

		final LinkedList<StatusUpdateWrapper> statusUpdates = new LinkedList<StatusUpdateWrapper>();

		// check if ego network stream is being accessed
		if (!ownUpdates) {
			final DynamicRelationshipType egoType = getEgoType(poster);
			final TreeSet<GraphityUserNode> users = new TreeSet<GraphityUserNode>(
					new GraphityUserNodeComparator());

			// load first user
			Node newUser = NeoUtils.getNextSingleNode(poster, egoType);
			GraphityUserNode crrUser, lastUser = null;
			if (newUser != null) {
				crrUser = new GraphityUserNode(newUser);
				if (crrUser.hasStatusUpdate()) {
					lastUser = crrUser;
					users.add(crrUser);
				}
			}

			// handle user queue
			while ((statusUpdates.size() < numItems) && !users.isEmpty()) {
				crrUser = users.pollFirst();

				// add last recent status update of current user
				statusUpdates.add(crrUser.getStatusUpdateWrapper());

				// re-add current user if more status updates available
				if (crrUser.hasStatusUpdate()) {
					users.add(crrUser);
				}

				// // load additional user if necessary and existing
				if (crrUser == lastUser) {
					newUser = NeoUtils.getNextSingleNode(lastUser.getUser(),
							egoType);
					if (newUser != null) {
						lastUser = new GraphityUserNode(newUser);
						users.add(lastUser);
					}
				}
			}
		} else {
			// access single stream only
			final GraphityUserNode posterNode = new GraphityUserNode(poster);
			while (posterNode.hasStatusUpdate()) {
				statusUpdates.add(posterNode.getStatusUpdateWrapper());
			}
		}

		return statusUpdates;
	}

	/**
	 * generate ego network relationship type
	 * 
	 * @param user
	 *            user to generate the type for
	 * @return ego network relationship type of the user passed
	 */
	private static DynamicRelationshipType getEgoType(final Node user) {
		return DynamicRelationshipType.withName("ego:" + user.getId());
	}

	/**
	 * get a user's last recent status update's time stamp
	 * 
	 * @param user
	 *            user targeted
	 * @return last recent status update's time stamp
	 */
	private static long getLastUpdate(final Node user) {
		if (user.hasProperty(Properties.LastUpdate)) {
			return (long) user.getProperty(Properties.LastUpdate);
		}
		return 0;
	}

}