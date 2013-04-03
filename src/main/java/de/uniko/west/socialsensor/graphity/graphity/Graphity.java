package de.uniko.west.socialsensor.graphity.graphity;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.NotFoundException;
import org.neo4j.kernel.AbstractGraphDatabase;

import de.uniko.west.socialsensor.graphity.socialgraph.Properties;
import de.uniko.west.socialsensor.graphity.socialgraph.SocialGraph;
import de.uniko.west.socialsensor.graphity.socialgraph.SocialGraphRelationshipType;
import de.uniko.west.socialsensor.graphity.socialgraph.StatusUpdate;

/**
 * social graph implementation 'Graphity'
 * 
 * @author sebschlicht
 * 
 */
public class Graphity implements SocialGraph {

	/**
	 * graph database in Graphity format
	 */
	private AbstractGraphDatabase graphity;

	@Override
	public boolean addStatusUpdate(long timestamp, long userID,
			StatusUpdate content) {
		// find user first
		Node user;
		try {
			user = this.graphity.getNodeById(userID);
		} catch (NotFoundException e) {
			return false;
		}

		// get last recent status update
		Node lastUpdate = NeoUtils.getNextSingleNode(user,
				SocialGraphRelationshipType.UPDATE);
		// TODO: ensure performance of having two node variables created at each
		// call

		// create new status update
		Node crrUpdate = this.graphity.createNode();
		crrUpdate.setProperty(Properties.Timestamp, timestamp);
		crrUpdate.setProperty(Properties.ContentType, content.getType());
		crrUpdate.setProperty(Properties.Content, content);
		user.createRelationshipTo(crrUpdate, SocialGraphRelationshipType.UPDATE);

		// update references to last recent status update if existing
		if (lastUpdate != null) {
			user.getSingleRelationship(SocialGraphRelationshipType.UPDATE,
					Direction.OUTGOING).delete();
			crrUpdate.createRelationshipTo(lastUpdate,
					SocialGraphRelationshipType.UPDATE);
		}

		// add reference from user to current update node
		user.createRelationshipTo(crrUpdate, SocialGraphRelationshipType.UPDATE);

		user.setProperty(Properties.LastUpdate, timestamp);
		// TODO: update ego network for this user

		return true;

	}

}
