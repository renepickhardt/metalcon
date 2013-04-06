package de.uniko.west.socialsensor.graphity.socialgraph.algorithms;

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
	public boolean createStatusUpdate(long timestamp, long userID,
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
		// loop through following users
		Node follower, lastPoster;
		DynamicRelationshipType egoType;
		Node prevUser, nextUser;

		for (Relationship relationship : user.getRelationships(
				SocialGraphRelationshipType.FOLLOW, Direction.INCOMING)) {
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
			// TODO: let certify correctness
			if (!lastPoster.equals(user)) {
				follower.getSingleRelationship(egoType, Direction.OUTGOING)
						.delete();
				follower.createRelationshipTo(user, egoType);
				user.createRelationshipTo(lastPoster, egoType);
			}
		}
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

}