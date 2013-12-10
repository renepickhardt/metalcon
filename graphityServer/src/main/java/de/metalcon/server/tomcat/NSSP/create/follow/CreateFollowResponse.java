package de.metalcon.server.tomcat.NSSP.create.follow;

import de.metalcon.server.tomcat.NSSP.ProtocolConstants;
import de.metalcon.server.tomcat.NSSP.create.CreateResponse;

/**
 * response to create follow edge requests according to NSSP
 * 
 * @author sebschlicht
 * 
 */
public class CreateFollowResponse extends CreateResponse {

	/**
	 * add status message: user identifier missing
	 */
	public void userIdentifierMissing() {
		this.parameterMissing(
				ProtocolConstants.Parameters.Create.Follow.USER_IDENTIFIER,
				"Please provide a user identifier matching to an existing user.");
	}

	/**
	 * add status message: followed identifier missing
	 */
	public void followedIdentifierMissing() {
		this.parameterMissing(
				ProtocolConstants.Parameters.Create.Follow.FOLLOWED_IDENTIFIER,
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
				ProtocolConstants.StatusCodes.Create.Follow.USER_NOT_EXISTING,
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
				ProtocolConstants.StatusCodes.Create.Follow.FOLLOWED_NOT_EXISTING,
				"there is no user with the identifier \"" + followedId
						+ "\". Please provide a valid user identifier.");
	}

	/**
	 * add status message: create follow edge succeeded
	 */
	public void createFollowSucceeded() {
		this.addStatusMessage(ProtocolConstants.StatusCodes.Create.Follow.SUCCEEDED,
				"");
	}

}