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
	 * template field information
	 */
	FIELD,

	/**
	 * template file information
	 */
	FILE,

	/**
	 * user's status update
	 */
	UPDATE,

	/**
	 * user's followers
	 */
	FOLLOW,

	/**
	 * graphity ego network links
	 */
	GRAPHITY,

	/**
	 * replica layer links
	 */
	REPLICA;

}