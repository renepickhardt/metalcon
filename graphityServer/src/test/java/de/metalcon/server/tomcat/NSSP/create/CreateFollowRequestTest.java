package de.metalcon.server.tomcat.NSSP.create;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import de.metalcon.server.tomcat.NSSP.ProtocolConstants;
import de.metalcon.server.tomcat.NSSP.create.follow.CreateFollowRequest;
import de.metalcon.server.tomcat.NSSP.create.follow.CreateFollowResponse;
import de.metalcon.utils.FormItemList;

public class CreateFollowRequestTest extends CreateRequestTest {

	/**
	 * valid create request type: follow edge
	 */
	private static final String VALID_TYPE = CreateRequestType.FOLLOW
			.getIdentifier();

	/**
	 * create follow request object
	 */
	private CreateFollowRequest createFollowRequest;

	private void fillRequest(final String type, final String userId,
			final String followedId) {
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
					ProtocolConstants.Parameters.Create.Follow.USER_IDENTIFIER,
					userId);
			formItemList.addField(
					ProtocolConstants.Parameters.Create.Follow.FOLLOWED_IDENTIFIER,
					followedId);

			final CreateFollowResponse createFollowResponse = new CreateFollowResponse();
			this.createFollowRequest = CreateFollowRequest.checkRequest(
					formItemList, createRequest, createFollowResponse);
			this.jsonResponse = extractJson(createFollowResponse);
		}
	}

	@Test
	public void testParameterMissing() {
		// missing: create request type
		this.fillRequest(null, VALID_USER_IDENTIFIER, VALID_USER_IDENTIFIER);
		assertEquals(MISSING_PARAM_BEFORE + ProtocolConstants.Parameters.Create.TYPE
				+ MISSING_PARAM_AFTER,
				this.jsonResponse.get(ProtocolConstants.STATUS_MESSAGE));

		// missing: user identifier
		this.fillRequest(VALID_TYPE, null, VALID_USER_IDENTIFIER);
		assertEquals(MISSING_PARAM_BEFORE
				+ ProtocolConstants.Parameters.Create.Follow.USER_IDENTIFIER
				+ MISSING_PARAM_AFTER,
				this.jsonResponse.get(ProtocolConstants.STATUS_MESSAGE));

		// missing: followed identifier
		this.fillRequest(VALID_TYPE, VALID_USER_IDENTIFIER, null);
		assertEquals(MISSING_PARAM_BEFORE
				+ ProtocolConstants.Parameters.Create.Follow.FOLLOWED_IDENTIFIER
				+ MISSING_PARAM_AFTER,
				this.jsonResponse.get(ProtocolConstants.STATUS_MESSAGE));
	}

	@Test
	public void testTypeValid() {
		this.fillRequest(VALID_TYPE, VALID_USER_IDENTIFIER,
				VALID_USER_IDENTIFIER);
		assertFalse(ProtocolConstants.StatusCodes.Create.TYPE_INVALID
				.equals(this.jsonResponse.get(ProtocolConstants.STATUS_MESSAGE)));
	}

	@Test
	public void testTypeInvalid() {
		this.fillRequest(INVALID_TYPE, VALID_USER_IDENTIFIER,
				VALID_USER_IDENTIFIER);
		assertEquals(ProtocolConstants.StatusCodes.Create.TYPE_INVALID,
				this.jsonResponse.get(ProtocolConstants.STATUS_MESSAGE));
		assertNull(this.createFollowRequest);
	}

	@Test
	public void testUserIdentifierValid() {
		this.fillRequest(VALID_TYPE, VALID_USER_IDENTIFIER,
				VALID_USER_IDENTIFIER);
		assertFalse(ProtocolConstants.StatusCodes.Create.Follow.USER_NOT_EXISTING
				.equals(this.jsonResponse.get(ProtocolConstants.STATUS_MESSAGE)));
	}

	@Test
	public void testUserIdentifierInvalid() {
		this.fillRequest(VALID_TYPE, INVALID_USER_IDENTIFIER,
				VALID_USER_IDENTIFIER);
		assertEquals(ProtocolConstants.StatusCodes.Create.Follow.USER_NOT_EXISTING,
				this.jsonResponse.get(ProtocolConstants.STATUS_MESSAGE));
		assertNull(this.createFollowRequest);
	}

	@Test
	public void testFollowedIdentifierValid() {
		this.fillRequest(VALID_TYPE, VALID_USER_IDENTIFIER,
				VALID_USER_IDENTIFIER);
		assertFalse(ProtocolConstants.StatusCodes.Create.Follow.FOLLOWED_NOT_EXISTING
				.equals(this.jsonResponse.get(ProtocolConstants.STATUS_MESSAGE)));
	}

	@Test
	public void testFollowedIdentifierInvalid() {
		this.fillRequest(VALID_TYPE, VALID_USER_IDENTIFIER,
				INVALID_USER_IDENTIFIER);
		assertEquals(
				ProtocolConstants.StatusCodes.Create.Follow.FOLLOWED_NOT_EXISTING,
				this.jsonResponse.get(ProtocolConstants.STATUS_MESSAGE));
		assertNull(this.createFollowRequest);
	}
}