package de.metalcon.server.tomcat.NSSP.delete.follow;

import javax.servlet.http.HttpServletRequest;

import org.neo4j.graphdb.Node;

import de.metalcon.server.tomcat.NSSP.ProtocolConstants;
import de.metalcon.server.tomcat.NSSP.delete.DeleteRequest;
import de.metalcon.server.tomcat.NSSP.delete.DeleteRequestType;
import de.metalcon.socialgraph.NeoUtils;

/**
 * delete follow edge requests according to NSSP
 * 
 * @author sebschlicht
 * 
 */
public class DeleteFollowRequest extends DeleteRequest {

	/**
	 * user requesting
	 */
	private final Node user;

	/**
	 * user followed
	 */
	private final Node followed;

	/**
	 * create a new delete follow edge request
	 * 
	 * @param type
	 *            delete request type
	 * @param user
	 *            user requesting
	 * @param followed
	 *            user followed
	 */
	public DeleteFollowRequest(final DeleteRequestType type, final Node user,
			final Node followed) {
		super(type);
		this.user = user;
		this.followed = followed;
	}

	/**
	 * 
	 * @return user requesting
	 */
	public Node getUser() {
		return this.user;
	}

	/**
	 * 
	 * @return user followed
	 */
	public Node getFollowed() {
		return this.followed;
	}

	/**
	 * check a delete follow edge request for validity concerning NSSP
	 * 
	 * @param request
	 *            Tomcat servlet request
	 * @param deleteRequest
	 *            basic delete request object
	 * @param deleteFollowResponse
	 *            delete follow edge response object
	 * @return delete follow edge request object<br>
	 *         <b>null</b> if the delete follow edge request is invalid
	 */
	public static DeleteFollowRequest checkRequest(
			final HttpServletRequest request,
			final DeleteRequest deleteRequest,
			final DeleteFollowResponse deleteFollowResponse) {
		final Node user = checkUserIdentifier(request, deleteFollowResponse);
		if (user != null) {
			final Node followed = checkFollowedIdentifier(request,
					deleteFollowResponse);
			if (followed != null) {
				return new DeleteFollowRequest(deleteRequest.getType(), user,
						followed);
			}
		}

		return null;
	}

	/**
	 * check if the request contains a valid user identifier
	 * 
	 * @param request
	 *            Tomcat servlet request
	 * @param response
	 *            response object
	 * @return user node with the identifier passed<br>
	 *         <b>null</b> if the identifier is invalid
	 */
	private static Node checkUserIdentifier(final HttpServletRequest request,
			final DeleteFollowResponse response) {
		final String userId = request
				.getParameter(ProtocolConstants.Parameters.Delete.Follow.USER_IDENTIFIER);
		if (userId != null) {
			final Node user = NeoUtils.getUserByIdentifier(userId);
			if (user != null) {
				return user;
			}
			response.userIdentifierInvalid(userId);
		} else {
			response.userIdentifierMissing();
		}

		return null;
	}

	/**
	 * check if the request contains a valid followed identifier
	 * 
	 * @param request
	 *            Tomcat servlet request
	 * @param response
	 *            response object
	 * @return user node with the identifier passed<br>
	 *         <b>null</b> if the identifier is invalid
	 */
	private static Node checkFollowedIdentifier(
			final HttpServletRequest request,
			final DeleteFollowResponse response) {
		final String followedId = request
				.getParameter(ProtocolConstants.Parameters.Delete.Follow.FOLLOWED_IDENTIFIER);
		if (followedId != null) {
			final Node followed = NeoUtils.getUserByIdentifier(followedId);
			if (followed != null) {
				return followed;
			}
			response.followedIdentifierInvalid(followedId);
		} else {
			response.followedIdentifierMissing();
		}

		return null;
	}

}