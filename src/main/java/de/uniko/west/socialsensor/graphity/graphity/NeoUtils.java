package de.uniko.west.socialsensor.graphity.graphity;

import java.nio.channels.NonWritableChannelException;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;

/**
 * collection of useful functions for neo4j
 * 
 * @author sebschlicht
 * 
 */
public class NeoUtils {

	/**
	 * find an outgoing relation from the user passed of the specified type
	 * 
	 * @param user
	 *            source user node
	 * @param relType
	 *            relationship type of the relation being searched
	 * @return node targeted by the relation<br>
	 *         null - if there is no relation of the type specified
	 */
	public static Node getNextSingleNode(final Node user,
			RelationshipType relType) {
		// find an outgoing relation of the type specified
		Relationship rel = null;
		try {
			rel = user.getSingleRelationship(relType, Direction.OUTGOING);
		} catch (NonWritableChannelException e) {
			// TODO: why is this here? Bug for read-only databases in previous
			// version?
		}

		return (rel == null) ? (null) : (rel.getEndNode());
	}

}