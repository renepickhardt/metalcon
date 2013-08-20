package de.metalcon.server.tomcat.NSSP.delete;

import de.metalcon.server.tomcat.NSSP.ProtocolConstants;
import de.metalcon.server.tomcat.NSSP.Response;

/**
 * basic response to delete requests according to NSSP
 * 
 * @author sebschlicht
 * 
 */
public class DeleteResponse extends Response {

	/**
	 * add status message: delete request type missing
	 */
	public void typeMissing() {
		this.parameterMissing(
				ProtocolConstants.Parameters.Delete.TYPE,
				"Please provide the type of the delete request. Use \""
						+ DeleteRequestType.USER.getIdentifier()
						+ "\" to delete a user, \""
						+ DeleteRequestType.FOLLOW.getIdentifier()
						+ "\" to delete a follow edge or \""
						+ DeleteRequestType.STATUS_UPDATE.getIdentifier()
						+ "\" to delete a status update.");
	}

	/**
	 * add status message: delete request type invalid
	 * 
	 * @param type
	 *            delete request type passed
	 */
	public void typeInvalid(final String type) {
		this.addStatusMessage(
				ProtocolConstants.StatusCodes.Delete.TYPE_INVALID,
				"\"" + type + "\" is not a valid delete request type. Use \""
						+ DeleteRequestType.USER.getIdentifier()
						+ "\" to delete a user, \""
						+ DeleteRequestType.FOLLOW.getIdentifier()
						+ "\" to delete a follow edge or \""
						+ DeleteRequestType.STATUS_UPDATE.getIdentifier()
						+ "\" to delete a status update.");
	}

}