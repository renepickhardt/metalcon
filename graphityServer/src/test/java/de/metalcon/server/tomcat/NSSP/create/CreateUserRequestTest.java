package de.metalcon.server.tomcat.NSSP.create;

import de.metalcon.server.tomcat.NSSP.create.user.CreateUserRequest;

public class CreateUserRequestTest extends CreateRequestTest {

	/**
	 * valid create request type: user
	 */
	private static final String VALID_TYPE = CreateRequestType.USER
			.getIdentifier();

	/**
	 * create user request object
	 */
	private CreateUserRequest createUserRequest;

	private void fillRequest(final String type, final String userId,
			final String displayName, final String profilePicturePath) {

		final CreateResponse createResponse = new CreateResponse();
		final CreateRequest createRequest = CreateRequest.checkRequest(
				this.request, createResponse);

		if (createRequest == null) {
			this.jsonResponse = extractJson(createResponse);
		} else {
			// TODO
		}
	}

}