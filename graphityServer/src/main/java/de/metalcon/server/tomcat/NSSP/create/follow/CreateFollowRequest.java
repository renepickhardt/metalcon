package de.metalcon.server.tomcat.NSSP.create.follow;

import org.neo4j.graphdb.Node;

import de.metalcon.server.tomcat.NSSP.create.CreateRequest;
import de.metalcon.server.tomcat.NSSP.create.CreateRequestType;
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
		return null;
	}

}