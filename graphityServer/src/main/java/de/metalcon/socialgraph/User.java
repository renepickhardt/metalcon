package de.metalcon.socialgraph;

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
	private final String id;

	/**
	 * display name
	 */
	private final String displayName;

	/**
	 * create a new user instance from Neo4j user node
	 * 
	 * @param userNode
	 *            Neo4j node representing the creator
	 */
	public User(final Node userNode) {
		this.id = (String) userNode.getProperty(Properties.User.IDENTIFIER);
		this.displayName = (String) userNode
				.getProperty(Properties.User.DISPLAY_NAME);
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
		actor.put("displayName", this.displayName);
		return actor;
	}

}