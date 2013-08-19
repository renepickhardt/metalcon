package de.metalcon.server.tomcat.NSSP.create.user;

import de.metalcon.server.tomcat.NSSProtocol;
import de.metalcon.server.tomcat.NSSP.create.CreateResponse;

/**
 * response to create user requests according to NSSP
 * 
 * @author sebschlicht
 * 
 */
public class CreateUserResponse extends CreateResponse {

	/**
	 * add status message: user identifier missing
	 */
	public void userIdentifierMissing() {
		this.parameterMissing(
				NSSProtocol.Parameters.Create.User.USER_IDENTIFIER,
				"Please provide a user identifier that is not used by an existing user.");
	}

	/**
	 * add status message: display name missing
	 */
	public void displayNameMissing() {
		this.parameterMissing(NSSProtocol.Parameters.Create.User.DISPLAY_NAME,
				"Please provide any display name for the new user.");
	}

	/**
	 * add status message: profile picture path missing
	 */
	public void profilePicturePathMissing() {
		this.parameterMissing(
				NSSProtocol.Parameters.Create.User.PROFILE_PICTURE_PATH,
				"Please provide a path to the profile picture of the new user.");
	}

	/**
	 * add status message: user identifier invalid
	 * 
	 * @param userId
	 *            user identifier passed
	 */
	public void userIdentifierInvalid(final String userId) {
		this.addStatusMessage(
				NSSProtocol.StatusCodes.Create.User.USER_EXISTING,
				"there is already a user with the identifier \""
						+ userId
						+ "\" existing. Please provide a valid user identifier.");
	}

}