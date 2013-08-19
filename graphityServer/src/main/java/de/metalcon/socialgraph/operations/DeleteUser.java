package de.metalcon.socialgraph.operations;

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
	 * delete user request object
	 */
	private final DeleteUserRequest request;

	/**
	 * create a new delete user command
	 * 
	 * @param servlet
	 *            request servlet
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
		this.request = deleteUserRequest;
	}

	@Override
	protected boolean execute(final SocialGraph graph) {
		graph.deleteUser(this.request.getUser());
		this.response.deleteUserSucceeded();
		return true;
	}

}
