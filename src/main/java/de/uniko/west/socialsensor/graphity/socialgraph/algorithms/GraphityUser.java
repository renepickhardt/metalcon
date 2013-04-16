package de.uniko.west.socialsensor.graphity.socialgraph.algorithms;

import org.neo4j.graphdb.Node;

/**
 * user used within Gravity reading process
 * 
 * @author Sebastian Schlicht
 * 
 */
public class GraphityUser extends StatusUpdateUser {

	/**
	 * user node represented
	 */
	private final Node userNode;

	/**
	 * create a new user for the Graphity reading process
	 * 
	 * @param user
	 *            user node represented
	 */
	public GraphityUser(final Node user) {
		super(user);
		this.userNode = user;
	}

	/**
	 * access the user being represented
	 * 
	 * @return user the node is representing
	 */
	public Node getUser() {
		return this.userNode;
	}

}