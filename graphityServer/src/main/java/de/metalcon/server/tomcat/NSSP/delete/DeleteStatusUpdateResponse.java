package de.metalcon.server.tomcat.NSSP.delete;

import de.metalcon.server.tomcat.NSSProtocol;

/**
 * response to delete status update requests according to NSSP
 * 
 * @author sebschlicht
 * 
 */
public class DeleteStatusUpdateResponse extends DeleteResponse {

	/**
	 * add status message: user identifier missing
	 */
	public void userIdentifierMissing() {
		this.parameterMissing(
				NSSProtocol.Parameters.Delete.StatusUpdate.USER_IDENTIFIER,
				"Please provide a user identifier matching to an existing user.");
	}

	/**
	 * add status message: status update identifier missing
	 */
	public void statusUpdateIdentifierMissing() {
		this.parameterMissing(
				NSSProtocol.Parameters.Delete.StatusUpdate.STATUS_UPDATE_IDENTIFIER,
				"Please provide a status update identifier matching to an existing status update.");
	}

	/**
	 * add status message: user identifier invalid
	 * 
	 * @param userId
	 *            user identifier passed
	 */
	public void userIdentifierInvalid(final String userId) {
		this.addStatusMessage(
				NSSProtocol.StatusCodes.Delete.StatusUpdate.USER_NOT_EXISTING,
				"there is no user with the identifier \"" + userId
						+ "\". Please provide a valid user identifier.");
	}

	/**
	 * add status message: status update identifier invalid
	 * 
	 * @param statusUpdateId
	 *            status update identifier passed
	 */
	public void statusUpdateIdentifierInvalid(final String statusUpdateId) {
		this.addStatusMessage(
				NSSProtocol.StatusCodes.Delete.StatusUpdate.STATUS_UPDATE_NOT_EXISTING,
				"there is no status update with the identifier \""
						+ statusUpdateId
						+ "\". Please provide a valid status update identifier.");
	}

}