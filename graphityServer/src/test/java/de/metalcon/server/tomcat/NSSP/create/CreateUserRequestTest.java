package de.metalcon.server.tomcat.NSSP.create;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import de.metalcon.server.tomcat.NSSP.ProtocolConstants;
import de.metalcon.server.tomcat.NSSP.RequestTest;
import de.metalcon.server.tomcat.NSSP.create.user.CreateUserRequest;
import de.metalcon.server.tomcat.NSSP.create.user.CreateUserResponse;
import de.metalcon.utils.FormItemList;

public class CreateUserRequestTest extends CreateRequestTest {

	/**
	 * valid create request type: user
	 */
	private static final String VALID_TYPE = CreateRequestType.USER
			.getIdentifier();

	/**
	 * valid user identifier (unused identifier)
	 */
	private static final String VALID_USER_IDENTIFIER = RequestTest.INVALID_USER_IDENTIFIER;

	/**
	 * invalid user identifier (used identifier)
	 */
	private static final String INVALID_USER_IDENTIFIER = RequestTest.VALID_USER_IDENTIFIER;

	/**
	 * valid user display name
	 */
	private static final String VALID_DISPLAY_NAME = "Testy";

	/**
	 * valid user profile picture path
	 */
	private static final String VALID_PROFILE_PICTURE_PATH = "http://www.totaberlustig.com/comics/2011-04-22-Clap%20your%20hands.jpg";

	/**
	 * create user request object
	 */
	private CreateUserRequest createUserRequest;

	private void fillRequest(final String type, final String userId,
			final String displayName, final String profilePicturePath) {
		// create form item list
		final FormItemList formItemList = new FormItemList();
		formItemList.addField(ProtocolConstants.Parameters.Create.TYPE, type);

		final CreateResponse createResponse = new CreateResponse();
		CreateRequest createRequest;
		createRequest = CreateRequest
				.checkRequest(formItemList, createResponse);

		if (createRequest == null) {
			this.jsonResponse = extractJson(createResponse);
		} else {
			formItemList.addField(
					ProtocolConstants.Parameters.Create.User.USER_IDENTIFIER, userId);
			formItemList.addField(
					ProtocolConstants.Parameters.Create.User.DISPLAY_NAME,
					displayName);
			formItemList.addField(
					ProtocolConstants.Parameters.Create.User.PROFILE_PICTURE_PATH,
					profilePicturePath);

			final CreateUserResponse createUserResponse = new CreateUserResponse();
			this.createUserRequest = CreateUserRequest.checkRequest(
					formItemList, createRequest, createUserResponse);
			this.jsonResponse = extractJson(createUserResponse);
		}
	}

	@Test
	public void testParameterMissing() {
		// missing: create request type
		this.fillRequest(null, VALID_USER_IDENTIFIER, VALID_DISPLAY_NAME,
				VALID_PROFILE_PICTURE_PATH);
		assertEquals(MISSING_PARAM_BEFORE + ProtocolConstants.Parameters.Create.TYPE
				+ MISSING_PARAM_AFTER,
				this.jsonResponse.get(ProtocolConstants.STATUS_MESSAGE));

		// missing: user identifier
		this.fillRequest(VALID_TYPE, null, VALID_DISPLAY_NAME,
				VALID_PROFILE_PICTURE_PATH);
		assertEquals(MISSING_PARAM_BEFORE
				+ ProtocolConstants.Parameters.Create.User.USER_IDENTIFIER
				+ MISSING_PARAM_AFTER,
				this.jsonResponse.get(ProtocolConstants.STATUS_MESSAGE));

		// missing: display name
		this.fillRequest(VALID_TYPE, VALID_USER_IDENTIFIER, null,
				VALID_PROFILE_PICTURE_PATH);
		assertEquals(MISSING_PARAM_BEFORE
				+ ProtocolConstants.Parameters.Create.User.DISPLAY_NAME
				+ MISSING_PARAM_AFTER,
				this.jsonResponse.get(ProtocolConstants.STATUS_MESSAGE));

		// missing: profile picture path
		this.fillRequest(VALID_TYPE, VALID_USER_IDENTIFIER, VALID_DISPLAY_NAME,
				null);
		assertEquals(MISSING_PARAM_BEFORE
				+ ProtocolConstants.Parameters.Create.User.PROFILE_PICTURE_PATH
				+ MISSING_PARAM_AFTER,
				this.jsonResponse.get(ProtocolConstants.STATUS_MESSAGE));
	}

	@Test
	public void testTypeValid() {
		this.fillRequest(VALID_TYPE, VALID_USER_IDENTIFIER, VALID_DISPLAY_NAME,
				VALID_PROFILE_PICTURE_PATH);
		assertFalse(ProtocolConstants.StatusCodes.Create.TYPE_INVALID
				.equals(this.jsonResponse.get(ProtocolConstants.STATUS_MESSAGE)));
	}

	@Test
	public void testTypeInvalid() {
		this.fillRequest(INVALID_TYPE, VALID_USER_IDENTIFIER,
				VALID_DISPLAY_NAME, VALID_PROFILE_PICTURE_PATH);
		assertEquals(ProtocolConstants.StatusCodes.Create.TYPE_INVALID,
				this.jsonResponse.get(ProtocolConstants.STATUS_MESSAGE));
		assertNull(this.createUserRequest);
	}

	@Test
	public void testUserIdentifierValid() {
		this.fillRequest(VALID_TYPE, VALID_USER_IDENTIFIER, VALID_DISPLAY_NAME,
				VALID_PROFILE_PICTURE_PATH);
		assertFalse(ProtocolConstants.StatusCodes.Create.User.USER_EXISTING
				.equals(this.jsonResponse.get(ProtocolConstants.STATUS_MESSAGE)));
	}

	@Test
	public void testUserIdentifierInvalid() {
		this.fillRequest(VALID_TYPE, INVALID_USER_IDENTIFIER,
				VALID_DISPLAY_NAME, VALID_PROFILE_PICTURE_PATH);
		assertEquals(ProtocolConstants.StatusCodes.Create.User.USER_EXISTING,
				this.jsonResponse.get(ProtocolConstants.STATUS_MESSAGE));
		assertNull(this.createUserRequest);
	}

}