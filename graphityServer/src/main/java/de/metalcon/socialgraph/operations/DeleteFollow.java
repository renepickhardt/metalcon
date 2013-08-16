package de.metalcon.socialgraph.operations;

import org.neo4j.graphdb.Node;

import de.metalcon.server.tomcat.GraphityHttpServlet;
import de.metalcon.server.tomcat.NSSP.delete.follow.DeleteFollowRequest;
import de.metalcon.server.tomcat.NSSP.delete.follow.DeleteFollowResponse;
import de.metalcon.socialgraph.SocialGraph;

/**
 * delete command: follow edge
 * 
 * @author Sebastian Schlicht
 * 
 */
public class DeleteFollow extends SocialGraphOperation {

	/**
	 * delete follow edge response object
	 */
	private final DeleteFollowResponse response;

	/**
	 * followed user
	 */
	private final Node followed;

	/**
	 * create a new delete follow edge command
	 * 
	 * @param servlet
	 *            response servlet
	 * @param deleteFollowResponse
	 *            delete follow edge response
	 * @param deleteFollowRequest
	 *            delete follow edge request
	 */
	public DeleteFollow(final GraphityHttpServlet servlet,
			final DeleteFollowResponse deleteFollowResponse,
			final DeleteFollowRequest deleteFollowRequest) {
		super(servlet, deleteFollowRequest.getUser());
		this.response = deleteFollowResponse;
		this.followed = deleteFollowRequest.getFollowed();
	}

	@Override
	protected boolean execute(final SocialGraph graph) {
		graph.removeFriendship(this.user, this.followed);
		this.response.deleteFollowSucceeded();
		return true;
	}

}