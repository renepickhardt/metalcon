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
 * social graph implementation 'Graphity'
 * 
 * @author Rene Pickhardt, Jonas Kunze, Sebastian Schlicht
 * 
 */
public class ReadOptimizedGraphity extends SocialGraph {

	/**
	 * create a new graphity social graph
	 * 
	 * @param graph
	 *            graph database to operate on
	 */
	public ReadOptimizedGraphity(final AbstractGraphDatabase graph) {
		super(graph);
	}

	@Override
	public void createFriendship(final Node following, final Node followed) {
		// try to find the replica node of the user followed
		Node followedReplica = null;
		for (Relationship followship : following.getRelationships(
				SocialGraphRelationshipType.FOLLOW, Direction.OUTGOING)) {
			followedReplica = followship.getEndNode();
			if (NeoUtils.getNextSingleNode(followedReplica,
					SocialGraphRelationshipType.REPLICA).equals(followed)) {
				break;
			}
			followedReplica = null;
		}

		// user is following already
		if (followedReplica != null) {
			return;
		}

		// create replica
		final Node newReplica = this.graph.createNode();
		following.createRelationshipTo(newReplica,
				SocialGraphRelationshipType.FOLLOW);
		newReplica.createRelationshipTo(followed,
				SocialGraphRelationshipType.REPLICA);

		// check if followed user is the first in following's ego network
		if (NeoUtils.getNextSingleNode(following,
				SocialGraphRelationshipType.GRAPHITY) == null) {
			following.createRelationshipTo(newReplica,
					SocialGraphRelationshipType.GRAPHITY);
		} else {
			// search for insertion index within following replica layer
			final long followedTimestamp = getLastUpdateByReplica(newReplica);
			long crrTimestamp;
			Node prevReplica = following;
			Node nextReplica = null;
			while (true) {
				// get next user
				nextReplica = NeoUtils.getNextSingleNode(prevReplica,
						SocialGraphRelationshipType.GRAPHITY);
				if (nextReplica != null) {
					crrTimestamp = getLastUpdateByReplica(nextReplica);

					// step on if current user has newer status updates
					if (crrTimestamp > followedTimestamp) {
						prevReplica = nextReplica;
						continue;
					}
				}

				// insertion position has been found
				break;
			}

			// insert followed user's replica into following's ego network
			if (nextReplica != null) {
				prevReplica.getSingleRelationship(
						SocialGraphRelationshipType.GRAPHITY,
						Direction.OUTGOING).delete();
				newReplica.createRelationshipTo(nextReplica,
						SocialGraphRelationshipType.GRAPHITY);
			}
			prevReplica.createRelationshipTo(newReplica,
					SocialGraphRelationshipType.GRAPHITY);
		}
	}

	@Override
	public void createStatusUpdate(final long timestamp, final Node user,
			final StatusUpdate content) {
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

		// update ego network for this user
		this.updateEgoNetwork(user);
	}

	/**
	 * update the ego network of a user
	 * 
	 * @param user
	 *            user where changes have occurred
	 */
	private void updateEgoNetwork(final Node user) {
		Node followedReplica, followingUser, lastPosterReplica;
		Node prevReplica, nextReplica;

		// loop through users following
		for (Relationship relationship : user.getRelationships(
				SocialGraphRelationshipType.REPLICA, Direction.INCOMING)) {
			// load each replica and the user corresponding
			followedReplica = relationship.getStartNode();
			followingUser = NeoUtils.getPrevSingleNode(followedReplica,
					SocialGraphRelationshipType.FOLLOW);

			// bridge user node
			prevReplica = NeoUtils.getPrevSingleNode(followedReplica,
					SocialGraphRelationshipType.GRAPHITY);
			if (!prevReplica.equals(followingUser)) {
				followedReplica.getSingleRelationship(
						SocialGraphRelationshipType.GRAPHITY,
						Direction.INCOMING).delete();

				nextReplica = NeoUtils.getNextSingleNode(followedReplica,
						SocialGraphRelationshipType.GRAPHITY);
				if (nextReplica != null) {
					followedReplica.getSingleRelationship(
							SocialGraphRelationshipType.GRAPHITY,
							Direction.OUTGOING).delete();
					prevReplica.createRelationshipTo(nextReplica,
							SocialGraphRelationshipType.GRAPHITY);
				}
			}

			// insert user's replica at its new position
			lastPosterReplica = NeoUtils.getNextSingleNode(followingUser,
					SocialGraphRelationshipType.GRAPHITY);
			if (!lastPosterReplica.equals(followedReplica)) {
				followingUser.getSingleRelationship(
						SocialGraphRelationshipType.GRAPHITY,
						Direction.OUTGOING).delete();
				followingUser.createRelationshipTo(followedReplica,
						SocialGraphRelationshipType.GRAPHITY);
				followedReplica.createRelationshipTo(lastPosterReplica,
						SocialGraphRelationshipType.GRAPHITY);
			}
		}
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
			final TreeSet<GraphityUser> users = new TreeSet<GraphityUser>(
					new StatusUpdateUserComparator());

			// load first user by the replica
			Node replicaAdded = NeoUtils.getNextSingleNode(poster,
					SocialGraphRelationshipType.GRAPHITY);
			Node userAdded;
			GraphityUser crrUser, lastUser = null;
			if (replicaAdded != null) {
				userAdded = NeoUtils.getNextSingleNode(replicaAdded,
						SocialGraphRelationshipType.REPLICA);
				crrUser = new GraphityUser(userAdded, replicaAdded);
				if (crrUser.hasStatusUpdate()) {
					lastUser = crrUser;
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

				// load additional user if necessary
				if (crrUser == lastUser) {
					replicaAdded = NeoUtils.getNextSingleNode(
							lastUser.getUserReplica(),
							SocialGraphRelationshipType.GRAPHITY);
					if (replicaAdded != null) {
						userAdded = NeoUtils.getNextSingleNode(replicaAdded,
								SocialGraphRelationshipType.REPLICA);
						lastUser = new GraphityUser(userAdded, replicaAdded);

						// add new user if updates available only
						if (lastUser.hasStatusUpdate()) {
							users.add(lastUser);
							continue;
						}
					}

					// further users do not need to be loaded
					lastUser = null;
				}
			}

		} else {
			// access single stream only
			final GraphityUser posterUser = new GraphityUser(poster, null);
			while ((statusUpdates.size() < numItems)
					&& posterUser.hasStatusUpdate()) {
				statusUpdates.add(posterUser.getStatusUpdate());
			}
		}

		return statusUpdates;
	}

	/**
	 * remove a followed user from the replica layer
	 * 
	 * @param followedReplica
	 *            replica of the user that will be removed
	 */
	private void removeFromReplicaLayer(final Node followedReplica) {
		final Node prev = NeoUtils.getPrevSingleNode(followedReplica,
				SocialGraphRelationshipType.GRAPHITY);
		final Node next = NeoUtils.getNextSingleNode(followedReplica,
				SocialGraphRelationshipType.GRAPHITY);

		// bridge the user replica in the replica layer
		prev.getSingleRelationship(SocialGraphRelationshipType.GRAPHITY,
				Direction.OUTGOING).delete();
		if (next != null) {
			next.getSingleRelationship(SocialGraphRelationshipType.GRAPHITY,
					Direction.INCOMING).delete();
			prev.createRelationshipTo(next,
					SocialGraphRelationshipType.GRAPHITY);
		}

		// remove the followship
		followedReplica.getSingleRelationship(
				SocialGraphRelationshipType.FOLLOW, Direction.INCOMING)
				.delete();

		// remove the replica node itself
		followedReplica.getSingleRelationship(
				SocialGraphRelationshipType.REPLICA, Direction.OUTGOING)
				.delete();
		followedReplica.delete();
	}

	@Override
	public boolean removeFriendship(final Node following, final Node followed) {
		// find the replica node of the user followed
		Node followedReplica = null;
		for (Relationship followship : following.getRelationships(
				SocialGraphRelationshipType.FOLLOW, Direction.OUTGOING)) {
			followedReplica = followship.getEndNode();
			if (NeoUtils.getNextSingleNode(followedReplica,
					SocialGraphRelationshipType.REPLICA).equals(followed)) {
				break;
			}
			followedReplica = null;
		}

		// there is no such followship existing
		if (followedReplica == null) {
			return false;
		}

		this.removeFromReplicaLayer(followedReplica);
		return true;
	}

	/**
	 * update the replica layer for status update deletion
	 * 
	 * @param user
	 *            owner of the status update being deleted
	 * @param statusUpdate
	 *            status update being deleted
	 */
	private void updateReplicaLayerStatusUpdateDeletion(final Node user,
			final Node statusUpdate) {
		final Node lastUpdate = NeoUtils.getNextSingleNode(user,
				SocialGraphRelationshipType.UPDATE);

		// update the ego network if the removal targets the last recent status
		// update
		if (statusUpdate.equals(lastUpdate)) {
			// get timestamp of the last recent status update in future
			long newTimestamp = 0;
			final Node nextStatusUpdate = NeoUtils.getNextSingleNode(
					statusUpdate, SocialGraphRelationshipType.UPDATE);
			if (nextStatusUpdate != null) {
				newTimestamp = (long) nextStatusUpdate
						.getProperty(Properties.StatusUpdate.TIMESTAMP);
			}

			// loop through followers
			Node replicaNode, following;
			for (Relationship replicated : user.getRelationships(
					SocialGraphRelationshipType.REPLICA, Direction.INCOMING)) {
				replicaNode = replicated.getEndNode();
				following = NeoUtils.getPrevSingleNode(replicaNode,
						SocialGraphRelationshipType.FOLLOW);

				// search for insertion index within following replica layer
				long crrTimestamp;
				Node prevReplica = following;
				Node nextReplica = null;
				while (true) {
					// get next user
					nextReplica = NeoUtils.getNextSingleNode(prevReplica,
							SocialGraphRelationshipType.GRAPHITY);
					if (nextReplica != null) {
						// ignore replica of the status update owner
						if (nextReplica.equals(replicaNode)) {
							prevReplica = nextReplica;
							continue;
						}

						crrTimestamp = getLastUpdateByReplica(nextReplica);

						// step on if current user has newer status updates
						if (crrTimestamp > newTimestamp) {
							prevReplica = nextReplica;
							continue;
						}
					}

					// insertion position has been found
					break;
				}

				// insert the replica
				if (nextReplica != null) {
					// bride the replica node
					final Node oldPrevReplica = NeoUtils.getNextSingleNode(
							replicaNode, SocialGraphRelationshipType.GRAPHITY);
					final Node oldNextReplica = NeoUtils.getNextSingleNode(
							replicaNode, SocialGraphRelationshipType.GRAPHITY);
					replicaNode.getSingleRelationship(
							SocialGraphRelationshipType.GRAPHITY,
							Direction.INCOMING).delete();

					if (oldNextReplica != null) {
						oldNextReplica.getSingleRelationship(
								SocialGraphRelationshipType.GRAPHITY,
								Direction.INCOMING).delete();
						oldPrevReplica.createRelationshipTo(oldNextReplica,
								SocialGraphRelationshipType.GRAPHITY);
					}

					// link to new neighbored nodes
					if (nextReplica != null) {
						replicaNode.createRelationshipTo(nextReplica,
								SocialGraphRelationshipType.GRAPHITY);
						prevReplica.getSingleRelationship(
								SocialGraphRelationshipType.GRAPHITY,
								Direction.OUTGOING);
					}
					prevReplica.createRelationshipTo(replicaNode,
							SocialGraphRelationshipType.GRAPHITY);
				}
			}

		}
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

		// update ego network
		this.updateReplicaLayerStatusUpdateDeletion(user, statusUpdate);

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

	/**
	 * get a user's last recent status update's time stamp
	 * 
	 * @param userReplica
	 *            replica of the user targeted
	 * @return last recent status update's time stamp
	 */
	private static long getLastUpdateByReplica(final Node userReplica) {
		final Node user = NeoUtils.getNextSingleNode(userReplica,
				SocialGraphRelationshipType.REPLICA);
		if (user.hasProperty(Properties.User.LAST_UPDATE)) {
			return (long) user.getProperty(Properties.User.LAST_UPDATE);
		}
		return 0;
	}

}