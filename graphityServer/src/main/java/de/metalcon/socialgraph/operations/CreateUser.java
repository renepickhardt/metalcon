package de.metalcon.socialgraph.operations;

import de.metalcon.server.tomcat.GraphityHttpServlet;
import de.metalcon.server.tomcat.NSSP.create.user.CreateUserRequest;
import de.metalcon.server.tomcat.NSSP.create.user.CreateUserResponse;
import de.metalcon.socialgraph.SocialGraph;

/**
 * create command: user
 * 
 * @author sebschlicht
 * 
 */
public class CreateUser extends SocialGraphOperation {

	/**
	 * create user response object
	 */
	private final CreateUserResponse response;

	/**
	 * create user request object
	 */
	private final CreateUserRequest request;

	/**
	 * create a new create user command
	 * 
	 * @param servlet
	 *            request servlet
	 * @param createUserResponse
	 *            create user response
	 * @param createUserRequest
	 *            create user request
	 */
	public CreateUser(final GraphityHttpServlet servlet,
			final CreateUserResponse createUserResponse,
			final CreateUserRequest createUserRequest) {
		super(servlet, null);
		this.response = createUserResponse;
		this.request = createUserRequest;
	}

	@Override
	protected boolean execute(final SocialGraph graph) {
		graph.createUser(this.request.getUserId(),
				this.request.getDisplayName(),
				this.request.getProfilePicturePath());
		this.response.createUserSucceeded();
		return true;
	}

}
