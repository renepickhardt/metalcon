package de.uniko.west.socialsensor.graphity.socialgraph;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.neo4j.graphdb.Node;

/**
 * user class representing a user node
 * 
 * @author Sebastian Schlicht
 * 
 */
public class User {

	/**
	 * user identifier
	 */
	private final long id;

	/**
	 * create a new user instance from Neo4j user node
	 * 
	 * @param userNode
	 *            Neo4j node representing the creator
	 */
	public User(final Node userNode) {
		this.id = userNode.getId();
	}

	/**
	 * parse the user to an Actor (Object) JSON map
	 * 
	 * @return Actor (Object) JSON map representing this user<br>
	 *         (Activitystrea.ms)
	 */
	public Map<String, Object> toActorJSON() {
		final HashMap<String, Object> actor = new LinkedHashMap<String, Object>();
		actor.put("objectType", "person");
		actor.put("id", this.id);
		return actor;
	}

}