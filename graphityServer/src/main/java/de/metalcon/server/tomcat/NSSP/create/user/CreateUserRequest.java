package de.metalcon.server.tomcat.NSSP.create.user;

import de.metalcon.server.tomcat.NSSP.create.CreateRequest;
import de.metalcon.server.tomcat.NSSP.create.CreateRequestType;

/**
 * create user requests according to NSSP
 * 
 * @author sebschlicht
 * 
 */
public class CreateUserRequest extends CreateRequest {

	/**
	 * identifier of the user that shall be created
	 */
	private final String userId;

	/**
	 * display name of the new user
	 */
	private final String displayName;

	/**
	 * path to the profile picture of the new user
	 */
	private final String profilePicturePath;

	/**
	 * create a new create user request
	 * 
	 * @param type
	 *            create request type
	 * @param userId
	 *            identifier of the user that shall be created
	 * @param displayName
	 *            display name of the new user
	 * @param profilePicturePath
	 *            path to the profile picture of the new user
	 */
	public CreateUserRequest(final CreateRequestType type, final String userId,
			final String displayName, final String profilePicturePath) {
		super(type);
		this.userId = userId;
		this.displayName = displayName;
		this.profilePicturePath = profilePicturePath;
	}

	/**
	 * 
	 * @return identifier of the user that shall be created
	 */
	public String getUserId() {
		return this.userId;
	}

	/**
	 * 
	 * @return display name of the new user
	 */
	public String getDisplayName() {
		return this.displayName;
	}

	/**
	 * 
	 * @return path to the profile picture of the new user
	 */
	public String getProfilePicturePath() {
		return this.profilePicturePath;
	}

}