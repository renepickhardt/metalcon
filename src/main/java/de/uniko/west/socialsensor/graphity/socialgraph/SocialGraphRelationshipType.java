package de.uniko.west.socialsensor.graphity.socialgraph;

import org.neo4j.graphdb.RelationshipType;

/**
 * relationship types for social network graphs
 * 
 * @author sebschlicht
 * 
 */
public enum SocialGraphRelationshipType implements RelationshipType {

	/**
	 * user's status update
	 */
	UPDATE;

}