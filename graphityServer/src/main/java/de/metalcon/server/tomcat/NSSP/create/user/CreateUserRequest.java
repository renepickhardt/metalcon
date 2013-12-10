package de.metalcon.server.tomcat.NSSP.create.user;

import org.neo4j.graphdb.Node;

import de.metalcon.server.tomcat.NSSP.ProtocolConstants;
import de.metalcon.server.tomcat.NSSP.create.CreateRequest;
import de.metalcon.server.tomcat.NSSP.create.CreateRequestType;
import de.metalcon.socialgraph.NeoUtils;
import de.metalcon.utils.FormItemList;

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

	/**
	 * check a create user request for validity concerning NSSP
	 * 
	 * @param formItemList
	 *            form item list extracted
	 * @param createRequest
	 *            basic create request object
	 * @param createUserResponse
	 *            create user response object
	 * @return create user request object<br>
	 *         <b>null</b> if the create user request is invalid
	 */
	public static CreateUserRequest checkRequest(
			final FormItemList formItemList, final CreateRequest createRequest,
			final CreateUserResponse createUserResponse) {
		final String userId = checkUserIdentifier(formItemList,
				createUserResponse);
		if (userId != null) {
			final String displayName = checkDisplayName(formItemList,
					createUserResponse);
			if (displayName != null) {
				final String profilePicturePath = checkProfilePicturePath(
						formItemList, createUserResponse);
				return new CreateUserRequest(createRequest.getType(), userId,
						displayName, profilePicturePath);
			}
		}

		return null;
	}

	/**
	 * check if the request contains a valid user identifier
	 * 
	 * @param formItemList
	 *            form item list extracted
	 * @param createUserResponse
	 *            response object
	 * @return user identifier<br>
	 *         <b>null</b> if the identifier is invalid
	 */
	private static String checkUserIdentifier(final FormItemList formItemList,
			final CreateUserResponse createUserResponse) {
		final String userId = formItemList
				.getField(ProtocolConstants.Parameters.Create.User.USER_IDENTIFIER);
		if (userId != null) {
			final Node user = NeoUtils.getUserByIdentifier(userId);
			if (user == null) {
				return userId;
			}

			createUserResponse.userIdentifierInvalid(userId);
		} else {
			createUserResponse.userIdentifierMissing();
		}

		return null;
	}

	/**
	 * check if the request contains a display name
	 * 
	 * @param formItemList
	 *            form item list extracted
	 * @param createUserResponse
	 *            response object
	 * @return display name<br>
	 *         <b>null</b> if there was no display name passed
	 */
	private static String checkDisplayName(final FormItemList formItemList,
			final CreateUserResponse createUserResponse) {
		final String displayName = formItemList
				.getField(ProtocolConstants.Parameters.Create.User.DISPLAY_NAME);
		if (displayName != null) {
			return displayName;
		} else {
			createUserResponse.displayNameMissing();
		}

		return null;
	}

	/**
	 * check if the request contains a profile picture path
	 * 
	 * @param formItemList
	 *            form item list extracted
	 * @param createUserResponse
	 *            create user response object
	 * @return profile picture path<br>
	 *         <b>null</b> if there was no profile picture path passed
	 */
	private static String checkProfilePicturePath(
			final FormItemList formItemList,
			final CreateUserResponse createUserResponse) {
		final String profilePicturePath = formItemList
				.getField(ProtocolConstants.Parameters.Create.User.PROFILE_PICTURE_PATH);
		if (profilePicturePath != null) {
			return profilePicturePath;
		} else {
			createUserResponse.profilePicturePathMissing();
		}

		return null;
	}

}