package de.metalcon.socialgraph.operations;

import org.neo4j.graphdb.Node;

import de.metalcon.server.tomcat.GraphityHttpServlet;
import de.metalcon.server.tomcat.NSSP.delete.user.DeleteUserRequest;
import de.metalcon.server.tomcat.NSSP.delete.user.DeleteUserResponse;
import de.metalcon.socialgraph.SocialGraph;

/**
 * delete command: user
 * 
 * @author sebschlicht
 * 
 */
public class DeleteUser extends SocialGraphOperation {

	/**
	 * delete user response object
	 */
	private final DeleteUserResponse response;

	/**
	 * user that shall be deleted
	 */
	private final Node user;

	/**
	 * create a new delete user command
	 * 
	 * @param servlet
	 *            response servlet
	 * @param deleteUserResponse
	 *            delete user response
	 * @param deleteUserRequest
	 *            delete user request
	 */
	public DeleteUser(final GraphityHttpServlet servlet,
			final DeleteUserResponse deleteUserResponse,
			final DeleteUserRequest deleteUserRequest) {
		super(servlet, null);
		this.response = deleteUserResponse;
		this.user = deleteUserRequest.getUser();
	}

	@Override
	protected boolean execute(final SocialGraph graph) {
		graph.deleteUser(this.user);
		this.response.deleteUserSucceeded();
		return true;
	}

}
