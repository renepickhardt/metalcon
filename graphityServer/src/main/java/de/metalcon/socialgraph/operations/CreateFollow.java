package de.metalcon.socialgraph.operations;

import de.metalcon.server.tomcat.GraphityHttpServlet;
import de.metalcon.server.tomcat.NSSP.create.follow.CreateFollowRequest;
import de.metalcon.server.tomcat.NSSP.create.follow.CreateFollowResponse;
import de.metalcon.socialgraph.SocialGraph;

/**
 * create command: follow edge
 * 
 * @author Sebastian Schlicht
 * 
 */
public class CreateFollow extends SocialGraphOperation {

	/**
	 * create follow edge response object
	 */
	private final CreateFollowResponse response;

	/**
	 * create follow edge request object
	 */
	private final CreateFollowRequest request;

	/**
	 * create a new create follow edge command
	 * 
	 * @param servlet
	 *            request servlet
	 * @param createFollowResponse
	 *            create follow edge response object
	 * @param createFollowRequest
	 *            create follow edge request object
	 */
	public CreateFollow(final GraphityHttpServlet servlet,
			final CreateFollowResponse createFollowResponse,
			final CreateFollowRequest createFollowRequest) {
		super(servlet, createFollowRequest.getUser());
		this.response = createFollowResponse;
		this.request = createFollowRequest;
	}

	@Override
	protected boolean execute(final SocialGraph graph) {
		graph.createFriendship(this.user, this.request.getFollowed());
		this.response.createFollowSucceeded();
		return true;
	}

}