package de.metalcon.socialgraph.algorithms;

import org.neo4j.graphdb.Node;

/**
 * user used within Gravity reading process
 * 
 * @author Sebastian Schlicht
 * 
 */
public class GraphityUser extends StatusUpdateUser {

	/**
	 * replica node of the user represented
	 */
	private final Node userReplicaNode;

	/**
	 * create a new user for the Graphity reading process
	 * 
	 * @param user
	 *            node of the user represented
	 * @param userReplica
	 *            replica node of the user represented
	 */
	public GraphityUser(final Node user, final Node userReplica) {
		super(user);
		this.userReplicaNode = userReplica;
	}

	/**
	 * access the replica node of the user represented
	 * 
	 * @return replica node of the user represented
	 */
	public Node getUserReplica() {
		return this.userReplicaNode;
	}

}