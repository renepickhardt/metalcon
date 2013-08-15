package de.metalcon.server.tomcat.NSSP.delete;

import de.metalcon.server.tomcat.NSSProtocol;

/**
 * response to delete follow edge requests according to NSSP
 * 
 * @author sebschlicht
 * 
 */
public class DeleteFollowResponse extends DeleteResponse {

	/**
	 * add status message: user identifier missing
	 */
	public void userIdentifierMissing() {
		this.parameterMissing(
				NSSProtocol.Parameters.Delete.Follow.USER_IDENTIFIER,
				"Please provide a user identifier matching to an existing user.");
	}

	/**
	 * add status message: followed identifier missing
	 */
	public void followedIdentifierMissing() {
		this.parameterMissing(
				NSSProtocol.Parameters.Delete.Follow.FOLLOWED_IDENTIFIER,
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
				NSSProtocol.StatusCodes.Delete.Follow.USER_NOT_EXISTING,
				"there is no user with the identifier \"" + userId
						+ "\". Please provide a valid user identifier.");
	}

	/**
	 * add status message: followed identifier invalid
	 * 
	 * @param followedId
	 *            followed identifier passed
	 */
	public void followedIdentifierInvalid(final String followedId) {
		this.addStatusMessage(
				NSSProtocol.StatusCodes.Delete.Follow.FOLLOWED_NOT_EXISTING,
				"there is no user with the identifier \"" + followedId
						+ "\". Please provide a valid user identifier.");
	}

}