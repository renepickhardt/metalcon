package de.metalcon.server.tomcat.NSSP.delete;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

import org.junit.Test;

import de.metalcon.server.tomcat.NSSP.ProtocolConstants;
import de.metalcon.server.tomcat.NSSP.delete.follow.DeleteFollowRequest;
import de.metalcon.server.tomcat.NSSP.delete.follow.DeleteFollowResponse;

public class DeleteFollowRequestTest extends DeleteRequestTest {

	/**
	 * valid delete request type: follow
	 */
	private static final String VALID_TYPE = DeleteRequestType.FOLLOW
			.getIdentifier();

	/**
	 * delete follow edge request object
	 */
	private DeleteFollowRequest deleteFollowRequest;

	private void fillRequest(final String type, final String userId,
			final String followedId) {
		when(this.request.getParameter(ProtocolConstants.Parameters.Delete.TYPE))
				.thenReturn(type);
		final DeleteResponse deleteResponse = new DeleteResponse();
		final DeleteRequest deleteRequest = DeleteRequest.checkRequest(
				this.request, deleteResponse);

		if (deleteRequest == null) {
			this.jsonResponse = extractJson(deleteResponse);
		} else {
			when(
					this.request
							.getParameter(ProtocolConstants.Parameters.Delete.Follow.USER_IDENTIFIER))
					.thenReturn(userId);
			when(
					this.request
							.getParameter(ProtocolConstants.Parameters.Delete.Follow.FOLLOWED_IDENTIFIER))
					.thenReturn(followedId);
			final DeleteFollowResponse deleteFollowResponse = new DeleteFollowResponse();
			this.deleteFollowRequest = DeleteFollowRequest.checkRequest(
					this.request, deleteRequest, deleteFollowResponse);
			this.jsonResponse = extractJson(deleteFollowResponse);
		}
	}

	@Test
	public void testParameterMissing() {
		// missing: delete request type
		this.fillRequest(null, VALID_USER_IDENTIFIER, VALID_USER_IDENTIFIER);
		assertEquals(MISSING_PARAM_BEFORE + ProtocolConstants.Parameters.Delete.TYPE
				+ MISSING_PARAM_AFTER,
				this.jsonResponse.get(ProtocolConstants.STATUS_MESSAGE));

		// missing: user identifier
		this.fillRequest(VALID_TYPE, null, VALID_USER_IDENTIFIER);
		assertEquals(MISSING_PARAM_BEFORE
				+ ProtocolConstants.Parameters.Delete.Follow.USER_IDENTIFIER
				+ MISSING_PARAM_AFTER,
				this.jsonResponse.get(ProtocolConstants.STATUS_MESSAGE));

		// missing: followed identifier
		this.fillRequest(VALID_TYPE, VALID_USER_IDENTIFIER, null);
		assertEquals(MISSING_PARAM_BEFORE
				+ ProtocolConstants.Parameters.Delete.Follow.FOLLOWED_IDENTIFIER
				+ MISSING_PARAM_AFTER,
				this.jsonResponse.get(ProtocolConstants.STATUS_MESSAGE));
	}

	@Test
	public void testDeleteTypeValid() {
		this.fillRequest(VALID_TYPE, VALID_USER_IDENTIFIER,
				VALID_USER_IDENTIFIER);
		assertFalse(ProtocolConstants.StatusCodes.Delete.TYPE_INVALID
				.equals(this.jsonResponse.get(ProtocolConstants.STATUS_MESSAGE)));
	}

	@Test
	public void testDeleteTypeInvalid() {
		this.fillRequest(INVALID_TYPE, VALID_USER_IDENTIFIER,
				VALID_USER_IDENTIFIER);
		assertEquals(ProtocolConstants.StatusCodes.Delete.TYPE_INVALID,
				this.jsonResponse.get(ProtocolConstants.STATUS_MESSAGE));
		assertNull(this.deleteFollowRequest);
	}

	@Test
	public void testUserIdentifierValid() {
		this.fillRequest(VALID_TYPE, VALID_USER_IDENTIFIER,
				VALID_USER_IDENTIFIER);
		assertFalse(ProtocolConstants.StatusCodes.Delete.Follow.USER_NOT_EXISTING
				.equals(this.jsonResponse.get(ProtocolConstants.STATUS_MESSAGE)));
	}

	@Test
	public void testUserIdentifierInvalid() {
		this.fillRequest(VALID_TYPE, INVALID_USER_IDENTIFIER,
				VALID_USER_IDENTIFIER);
		assertEquals(ProtocolConstants.StatusCodes.Delete.Follow.USER_NOT_EXISTING,
				this.jsonResponse.get(ProtocolConstants.STATUS_MESSAGE));
		assertNull(this.deleteFollowRequest);
	}

	@Test
	public void testFollowedIdentifierValid() {
		this.fillRequest(VALID_TYPE, VALID_USER_IDENTIFIER,
				VALID_USER_IDENTIFIER);
		assertFalse(ProtocolConstants.StatusCodes.Delete.Follow.FOLLOWED_NOT_EXISTING
				.equals(this.jsonResponse.get(ProtocolConstants.STATUS_MESSAGE)));
	}

	@Test
	public void testFollowedIdentifierInvalid() {
		this.fillRequest(VALID_TYPE, VALID_USER_IDENTIFIER,
				INVALID_USER_IDENTIFIER);
		assertEquals(
				ProtocolConstants.StatusCodes.Delete.Follow.FOLLOWED_NOT_EXISTING,
				this.jsonResponse.get(ProtocolConstants.STATUS_MESSAGE));
		assertNull(this.deleteFollowRequest);
	}

}