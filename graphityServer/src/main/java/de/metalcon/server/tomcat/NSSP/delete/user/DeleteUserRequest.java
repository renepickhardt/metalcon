package de.metalcon.server.tomcat.NSSP.delete.user;

import javax.servlet.http.HttpServletRequest;

import org.neo4j.graphdb.Node;

import de.metalcon.server.tomcat.NSSP.ProtocolConstants;
import de.metalcon.server.tomcat.NSSP.delete.DeleteRequest;
import de.metalcon.server.tomcat.NSSP.delete.DeleteRequestType;
import de.metalcon.socialgraph.NeoUtils;

/**
 * delete user requests according to NSSP
 * 
 * @author sebschlicht
 * 
 */
public class DeleteUserRequest extends DeleteRequest {

	/**
	 * user that shall be deleted
	 */
	private final Node user;

	/**
	 * create a new delete user request
	 * 
	 * @param type
	 *            delete request type
	 * @param user
	 *            user that shall be deleted
	 */
	public DeleteUserRequest(final DeleteRequestType type, final Node user) {
		super(type);
		this.user = user;
	}

	/**
	 * 
	 * @return user that shall be deleted
	 */
	public Node getUser() {
		return this.user;
	}

	/**
	 * check a delete user request for validity concerning NSSP
	 * 
	 * @param request
	 *            Tomcat servlet request
	 * @param deleteRequest
	 *            basic delete request object
	 * @param deleteUserResponse
	 *            delete user response object
	 * @return delete user request object<br>
	 *         <b>null</b> if the delete user request is invalid
	 */
	public static DeleteUserRequest checkRequest(
			final HttpServletRequest request,
			final DeleteRequest deleteRequest,
			final DeleteUserResponse deleteUserResponse) {
		final Node user = checkUserIdentifier(request, deleteUserResponse);
		if (user != null) {
			return new DeleteUserRequest(deleteRequest.getType(), user);
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
			final DeleteUserResponse response) {
		final String userId = request
				.getParameter(ProtocolConstants.Parameters.Delete.User.USER_IDENTIFIER);
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

}