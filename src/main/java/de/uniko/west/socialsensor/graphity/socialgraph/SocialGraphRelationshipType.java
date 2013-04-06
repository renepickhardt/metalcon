package de.uniko.west.socialsensor.graphity.socialgraph;

import org.neo4j.graphdb.RelationshipType;

/**
 * relationship types for social network graphs
 * 
 * @author Jonas Kunze, Rene Pickhardt, Sebastian Schlicht
 * 
 */
public enum SocialGraphRelationshipType implements RelationshipType {

	/**
	 * user's status update
	 */
	UPDATE,

	/**
	 * user's followers
	 */
	FOLLOW;

}