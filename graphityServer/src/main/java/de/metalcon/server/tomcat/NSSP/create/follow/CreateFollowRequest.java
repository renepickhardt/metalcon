package de.metalcon.server.tomcat.NSSP.create.follow;

import org.neo4j.graphdb.Node;

import de.metalcon.server.tomcat.NSSP.ProtocolConstants;
import de.metalcon.server.tomcat.NSSP.create.CreateRequest;
import de.metalcon.server.tomcat.NSSP.create.CreateRequestType;
import de.metalcon.socialgraph.NeoUtils;
import de.metalcon.utils.FormItemList;

/**
 * create follow edge requests according to NSSP
 * 
 * @author sebschlicht
 * 
 */
public class CreateFollowRequest extends CreateRequest {

	/**
	 * user requesting
	 */
	private final Node user;

	/**
	 * user followed
	 */
	private final Node followed;

	/**
	 * create a new create follow edge request
	 * 
	 * @param type
	 *            create request type
	 * @param user
	 *            user requesting
	 * @param followed
	 *            user followed
	 */
	public CreateFollowRequest(final CreateRequestType type, final Node user,
			final Node followed) {
		super(type);
		this.user = user;
		this.followed = followed;
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
	 * @return user followed
	 */
	public Node getFollowed() {
		return this.followed;
	}

	/**
	 * check a create follow edge request for validity concerning NSSP
	 * 
	 * @param formItemList
	 *            form item list extracted
	 * @param createRequest
	 *            basic create request object
	 * @param createFollowResponse
	 *            create follow edge response object
	 * @return create follow edge request object<br>
	 *         <b>null</b> if the create follow edge request is invalid
	 */
	public static CreateFollowRequest checkRequest(
			final FormItemList formItemList, final CreateRequest createRequest,
			final CreateFollowResponse createFollowResponse) {
		final Node user = checkUserIdentifier(formItemList,
				createFollowResponse);
		if (user != null) {
			final Node followed = checkFollowedIdentifier(formItemList,
					createFollowResponse);
			if (followed != null) {
				return new CreateFollowRequest(createRequest.getType(), user,
						followed);
			}
		}

		return null;
	}

	/**
	 * check if the request contains a valid user identifier
	 * 
	 * @param formItemList
	 *            form item list extracted
	 * @param createFollowResponse
	 *            response object
	 * @return user node with the identifier passed<br>
	 *         <b>null</b> if the identifier is invalid
	 */
	private static Node checkUserIdentifier(final FormItemList formItemList,
			final CreateFollowResponse createFollowResponse) {
		final String userId = formItemList
				.getField(ProtocolConstants.Parameters.Create.Follow.USER_IDENTIFIER);
		if (userId != null) {
			final Node user = NeoUtils.getUserByIdentifier(userId);
			if (user != null) {
				return user;
			}

			createFollowResponse.userIdentifierInvalid(userId);
		} else {
			createFollowResponse.userIdentifierMissing();
		}

		return null;
	}

	/**
	 * check if the request contains a valid followed identifier
	 * 
	 * @param formItemList
	 *            form item list extracted
	 * @param createFollowResponse
	 *            response object
	 * @return user node with the identifier passed<br>
	 *         <b>null</b> if the identifier is invalid
	 */
	private static Node checkFollowedIdentifier(
			final FormItemList formItemList,
			final CreateFollowResponse createFollowResponse) {
		final String followedId = formItemList
				.getField(ProtocolConstants.Parameters.Create.Follow.FOLLOWED_IDENTIFIER);
		if (followedId != null) {
			final Node followed = NeoUtils.getUserByIdentifier(followedId);
			if (followed != null) {
				return followed;
			}

			createFollowResponse.followedIdentifierInvalid(followedId);
		} else {
			createFollowResponse.followedIdentifierMissing();
		}

		return null;
	}

}