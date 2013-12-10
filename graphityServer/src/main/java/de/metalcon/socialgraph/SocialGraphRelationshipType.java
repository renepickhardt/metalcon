package de.metalcon.socialgraph;

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
	FOLLOW,

	/**
	 * graphity ego network links
	 */
	GRAPHITY,

	/**
	 * replica layer links
	 */
	REPLICA;

	/**
	 * relationship types used for template management
	 * 
	 * @author sebschlicht
	 * 
	 */
	public enum Templates implements RelationshipType {

		/**
		 * previous template node
		 */
		PREVIOUS,

		/**
		 * template field information
		 */
		FIELD,

		/**
		 * template file information
		 */
		FILE,
	}

}