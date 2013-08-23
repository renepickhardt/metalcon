package de.metalcon.server.tomcat.NSSP.delete;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

import org.junit.Test;

import de.metalcon.server.tomcat.NSSP.ProtocolConstants;
import de.metalcon.server.tomcat.NSSP.delete.user.DeleteUserRequest;
import de.metalcon.server.tomcat.NSSP.delete.user.DeleteUserResponse;

public class DeleteUserRequestTest extends DeleteRequestTest {

	/**
	 * valid delete request type: user
	 */
	private static final String VALID_TYPE = DeleteRequestType.USER
			.getIdentifier();

	/**
	 * delete user request object
	 */
	private DeleteUserRequest deleteUserRequest;

	private void fillRequest(final String type, final String userId) {
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
							.getParameter(ProtocolConstants.Parameters.Delete.User.USER_IDENTIFIER))
					.thenReturn(userId);
			final DeleteUserResponse deleteUserResponse = new DeleteUserResponse();
			this.deleteUserRequest = DeleteUserRequest.checkRequest(
					this.request, deleteRequest, deleteUserResponse);
			this.jsonResponse = extractJson(deleteUserResponse);
		}
	}

	@Test
	public void testParameterMissing() {
		// missing: delete request type
		this.fillRequest(null, VALID_USER_IDENTIFIER);
		assertEquals(MISSING_PARAM_BEFORE + ProtocolConstants.Parameters.Delete.TYPE
				+ MISSING_PARAM_AFTER,
				this.jsonResponse.get(ProtocolConstants.STATUS_MESSAGE));

		// missing: user identifier
		this.fillRequest(VALID_TYPE, null);
		assertEquals(MISSING_PARAM_BEFORE
				+ ProtocolConstants.Parameters.Delete.User.USER_IDENTIFIER
				+ MISSING_PARAM_AFTER,
				this.jsonResponse.get(ProtocolConstants.STATUS_MESSAGE));
	}

	@Test
	public void testDeleteTypeValid() {
		this.fillRequest(VALID_TYPE, VALID_USER_IDENTIFIER);
		assertFalse(ProtocolConstants.StatusCodes.Delete.TYPE_INVALID
				.equals(this.jsonResponse.get(ProtocolConstants.STATUS_MESSAGE)));
	}

	@Test
	public void testDeleteTypeInvalid() {
		this.fillRequest(INVALID_TYPE, VALID_USER_IDENTIFIER);
		assertEquals(ProtocolConstants.StatusCodes.Delete.TYPE_INVALID,
				this.jsonResponse.get(ProtocolConstants.STATUS_MESSAGE));
		assertNull(this.deleteUserRequest);
	}

	@Test
	public void testUserIdentifierValid() {
		this.fillRequest(VALID_TYPE, VALID_USER_IDENTIFIER);
		assertFalse(ProtocolConstants.StatusCodes.Delete.User.USER_NOT_EXISTING
				.equals(this.jsonResponse.get(ProtocolConstants.STATUS_MESSAGE)));
	}

	@Test
	public void testUserIdentifierInvalid() {
		this.fillRequest(VALID_TYPE, INVALID_USER_IDENTIFIER);
		assertEquals(ProtocolConstants.StatusCodes.Delete.User.USER_NOT_EXISTING,
				this.jsonResponse.get(ProtocolConstants.STATUS_MESSAGE));
		assertNull(this.deleteUserRequest);
	}

}