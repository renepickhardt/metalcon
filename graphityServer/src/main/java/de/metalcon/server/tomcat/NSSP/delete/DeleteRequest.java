package de.metalcon.server.tomcat.NSSP.delete;

import javax.servlet.http.HttpServletRequest;

import de.metalcon.server.tomcat.NSSP.ProtocolConstants;

/**
 * basic delete requests according to NSSP
 * 
 * @author sebschlicht
 * 
 */
public class DeleteRequest {

	/**
	 * delete request type
	 */
	private final DeleteRequestType type;

	/**
	 * create a new basic delete request according to NSSP
	 * 
	 * @param type
	 *            delete request type
	 */
	public DeleteRequest(final DeleteRequestType type) {
		this.type = type;
	}

	/**
	 * 
	 * @return delete request type
	 */
	public DeleteRequestType getType() {
		return this.type;
	}

	/**
	 * check a delete request for validity concerning NSSP
	 * 
	 * @param request
	 *            Tomcat servlet request
	 * @param deleteResponse
	 *            delete response object
	 * @return delete request object<br>
	 *         <b>null</b> if the delete request is invalid
	 */
	public static DeleteRequest checkRequest(final HttpServletRequest request,
			final DeleteResponse deleteResponse) {
		final DeleteRequestType type = checkType(request, deleteResponse);
		if (type != null) {
			return new DeleteRequest(type);
		}

		return null;
	}

	/**
	 * check if the request contains a valid delete request type
	 * 
	 * @param request
	 *            Tomcat servlet request
	 * @param deleteResponse
	 *            delete response object
	 * @return delete request type<br>
	 *         <b>null</b> if the delete request type is invalid
	 */
	private static DeleteRequestType checkType(
			final HttpServletRequest request,
			final DeleteResponse deleteResponse) {
		{
			final String sType = request
					.getParameter(ProtocolConstants.Parameters.Delete.TYPE);
			if (sType != null) {
				if (DeleteRequestType.USER.getIdentifier().equals(sType)) {
					return DeleteRequestType.USER;
				} else if (DeleteRequestType.FOLLOW.getIdentifier().equals(
						sType)) {
					return DeleteRequestType.FOLLOW;
				} else if (DeleteRequestType.STATUS_UPDATE.getIdentifier()
						.equals(sType)) {
					return DeleteRequestType.STATUS_UPDATE;
				} else {
					deleteResponse.typeInvalid(sType);
				}
			} else {
				deleteResponse.typeMissing();
			}

			return null;
		}

	}

}