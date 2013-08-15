package de.metalcon.server.tomcat.NSSP.delete;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.when;

import org.junit.Test;

import de.metalcon.server.tomcat.NSSProtocol;

public class DeleteStatusUpdateRequestTest extends DeleteRequestTest {

	/**
	 * valid delete request type: status update
	 */
	private static final String VALID_TYPE = DeleteRequestType.STATUS_UPDATE
			.getIdentifier();

	/**
	 * valid status update identifier
	 */
	private static final String VALID_STATUS_UPDATE_IDENTIFIER = "";

	/**
	 * invalid status update identifier
	 */
	private static final String INVALID_STATUS_UPDATE_IDENTIFIER = "this_does_not_exist";

	/**
	 * delete status update request object
	 */
	private DeleteStatusUpdateRequest deleteStatusUpdateRequest;

	private void fillRequest(final String type, final String userId,
			final String statusUpdateId) {
		when(this.request.getParameter(NSSProtocol.Parameters.Delete.TYPE))
				.thenReturn(type);
		final DeleteResponse deleteResponse = new DeleteResponse();
		final DeleteRequest deleteRequest = DeleteRequest.checkRequest(
				this.request, deleteResponse);

		if (deleteRequest == null) {
			this.jsonResponse = extractJson(deleteResponse);
		} else {
			when(
					this.request
							.getParameter(NSSProtocol.Parameters.Delete.StatusUpdate.USER_IDENTIFIER))
					.thenReturn(userId);
			when(
					this.request
							.getParameter(NSSProtocol.Parameters.Delete.StatusUpdate.STATUS_UPDATE_IDENTIFIER))
					.thenReturn(statusUpdateId);
			final DeleteStatusUpdateResponse deleteStatusUpdateResponse = new DeleteStatusUpdateResponse();
			this.deleteStatusUpdateRequest = DeleteStatusUpdateRequest
					.checkRequest(this.request, deleteRequest,
							deleteStatusUpdateResponse);
			this.jsonResponse = extractJson(deleteStatusUpdateResponse);
		}
	}

	@Test
	public void testDeleteTypeValid() {
		this.fillRequest(VALID_TYPE, VALID_USER_IDENTIFIER,
				VALID_STATUS_UPDATE_IDENTIFIER);
		assertFalse(NSSProtocol.StatusCodes.Delete.TYPE_INVALID
				.equals(this.jsonResponse.get(NSSProtocol.STATUS_MESSAGE)));
	}

	@Test
	public void testDeleteTypeInvalid() {
		this.fillRequest(INVALID_TYPE, VALID_USER_IDENTIFIER,
				VALID_STATUS_UPDATE_IDENTIFIER);
		assertEquals(NSSProtocol.StatusCodes.Delete.TYPE_INVALID,
				this.jsonResponse.get(NSSProtocol.STATUS_MESSAGE));
		assertEquals(null, this.deleteStatusUpdateRequest);
	}

	@Test
	public void testUserIdentifierValid() {
		this.fillRequest(VALID_TYPE, VALID_USER_IDENTIFIER,
				VALID_STATUS_UPDATE_IDENTIFIER);
		assertFalse(NSSProtocol.StatusCodes.Delete.StatusUpdate.USER_NOT_EXISTING
				.equals(this.jsonResponse.get(NSSProtocol.STATUS_MESSAGE)));
	}

	@Test
	public void testUserIdentifierInvalid() {
		this.fillRequest(VALID_TYPE, INVALID_USER_IDENTIFIER,
				VALID_STATUS_UPDATE_IDENTIFIER);
		assertEquals(
				NSSProtocol.StatusCodes.Delete.StatusUpdate.USER_NOT_EXISTING,
				this.jsonResponse.get(NSSProtocol.STATUS_MESSAGE));
		assertEquals(null, this.deleteStatusUpdateRequest);
	}

	@Test
	public void testStatusUpdateIdentifierValid() {
		this.fillRequest(VALID_TYPE, VALID_USER_IDENTIFIER,
				VALID_STATUS_UPDATE_IDENTIFIER);
		// TODO: create this status update
		assertFalse(NSSProtocol.StatusCodes.Delete.StatusUpdate.STATUS_UPDATE_NOT_EXISTING
				.equals(this.jsonResponse.get(NSSProtocol.STATUS_MESSAGE)));
	}

	@Test
	public void testStatusUpdateIdentifierInvalid() {
		this.fillRequest(VALID_TYPE, VALID_USER_IDENTIFIER,
				INVALID_STATUS_UPDATE_IDENTIFIER);
		assertEquals(
				NSSProtocol.StatusCodes.Delete.StatusUpdate.STATUS_UPDATE_NOT_EXISTING,
				this.jsonResponse.get(NSSProtocol.STATUS_MESSAGE));
		assertEquals(null, this.deleteStatusUpdateRequest);
	}

}