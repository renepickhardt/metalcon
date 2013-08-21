package de.metalcon.server.tomcat.NSSP.delete.statusupdate;

import javax.servlet.http.HttpServletRequest;

import org.neo4j.graphdb.Node;

import de.metalcon.server.tomcat.NSSP.ProtocolConstants;
import de.metalcon.server.tomcat.NSSP.delete.DeleteRequest;
import de.metalcon.server.tomcat.NSSP.delete.DeleteRequestType;
import de.metalcon.socialgraph.NeoUtils;

/**
 * delete status update requests according to NSSP
 * 
 * @author sebschlicht
 * 
 */
public class DeleteStatusUpdateRequest extends DeleteRequest {

	/**
	 * user requesting
	 */
	private final Node user;

	/**
	 * status update that shall be deleted
	 */
	private final Node statusUpdate;

	/**
	 * create a new delete status update request
	 * 
	 * @param type
	 *            delete request type
	 * @param user
	 *            user requesting
	 * @param statusUpdate
	 *            status update that shall be deleted
	 */
	public DeleteStatusUpdateRequest(final DeleteRequestType type,
			final Node user, final Node statusUpdate) {
		super(type);
		this.user = user;
		this.statusUpdate = statusUpdate;
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
	 * @return status update that shall be deleted
	 */
	public Node getStatusUpdate() {
		return this.statusUpdate;
	}

	/**
	 * check a delete status update request for validity concerning NSSP
	 * 
	 * @param request
	 *            Tomcat servlet request
	 * @param deleteRequest
	 *            basic delete request object
	 * @param deleteStatusUpdateResponse
	 *            delete status update response object
	 * @return delete status update request object<br>
	 *         <b>null</b> if the delete status update request is invalid
	 */
	public static DeleteStatusUpdateRequest checkRequest(
			final HttpServletRequest request,
			final DeleteRequest deleteRequest,
			final DeleteStatusUpdateResponse deleteStatusUpdateResponse) {
		final Node user = checkUserIdentifier(request,
				deleteStatusUpdateResponse);
		if (user != null) {
			final Node statusUpdate = checkStatusUpdateIdentifier(request,
					deleteStatusUpdateResponse);
			if (statusUpdate != null) {
				return new DeleteStatusUpdateRequest(deleteRequest.getType(),
						user, statusUpdate);
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
			final DeleteStatusUpdateResponse response) {
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
	 * check if the request contains a valid status update identifier
	 * 
	 * @param request
	 *            Tomcat servlet request
	 * @param response
	 *            response object
	 * @return status update node with the identifier passed<br>
	 *         <b>null</b> if the identifier is invalid
	 */
	private static Node checkStatusUpdateIdentifier(
			final HttpServletRequest request,
			final DeleteStatusUpdateResponse response) {
		final String statusUpdateId = request
				.getParameter(ProtocolConstants.Parameters.Delete.StatusUpdate.STATUS_UPDATE_IDENTIFIER);
		if (statusUpdateId != null) {
			final Node statusUpdate = NeoUtils
					.getStatusUpdateByIdentifier(statusUpdateId);
			if (statusUpdate != null) {
				return statusUpdate;
			}
			response.statusUpdateIdentifierInvalid(statusUpdateId);
		} else {
			response.statusUpdateIdentifierMissing();
		}

		return null;
	}

}