package de.metalcon.server.tomcat.NSSP.delete.user;

import de.metalcon.server.tomcat.NSSP.ProtocolConstants;
import de.metalcon.server.tomcat.NSSP.delete.DeleteResponse;

/**
 * response to delete user requests according to NSSP
 * 
 * @author sebschlicht
 * 
 */
public class DeleteUserResponse extends DeleteResponse {

	/**
	 * add status message: user identifier missing
	 */
	public void userIdentifierMissing() {
		this.parameterMissing(
				ProtocolConstants.Parameters.Delete.User.USER_IDENTIFIER,
				"Please provide a user identifier matching to an existing user.");
	}

	/**
	 * add status message: user identifier invalid
	 * 
	 * @param userId
	 *            user identifier passed
	 */
	public void userIdentifierInvalid(final String userId) {
		this.addStatusMessage(
				ProtocolConstants.StatusCodes.Delete.User.USER_NOT_EXISTING,
				"there is no user with the identifier \"" + userId
						+ "\". Please provide a valid user identifier.");
	}

	/**
	 * add status message: delete user succeeded
	 */
	public void deleteUserSucceeded() {
		this.addStatusMessage(ProtocolConstants.StatusCodes.Delete.User.SUCCEEDED, "");
	}

}