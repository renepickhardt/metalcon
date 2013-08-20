package de.metalcon.server.tomcat.NSSP.delete;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

import org.junit.BeforeClass;
import org.junit.Test;

import de.metalcon.server.tomcat.NSSP.ProtocolConstants;
import de.metalcon.server.tomcat.NSSP.RequestTest;
import de.metalcon.server.tomcat.NSSP.create.CreateStatusUpdateRequestTest;
import de.metalcon.server.tomcat.NSSP.delete.statusupdate.DeleteStatusUpdateRequest;
import de.metalcon.server.tomcat.NSSP.delete.statusupdate.DeleteStatusUpdateResponse;
import de.metalcon.socialgraph.NeoUtils;
import de.metalcon.socialgraph.algorithms.AlgorithmTests;

public class DeleteStatusUpdateRequestTest extends DeleteRequestTest {

	/**
	 * valid delete request type: status update
	 */
	private static final String VALID_TYPE = DeleteRequestType.STATUS_UPDATE
			.getIdentifier();

	/**
	 * valid status update identifier
	 */
	public static final String VALID_STATUS_UPDATE_IDENTIFIER = "stup1";

	/**
	 * invalid status update identifier
	 */
	private static final String INVALID_STATUS_UPDATE_IDENTIFIER = "this_does_not_exist";

	/**
	 * delete status update request object
	 */
	private DeleteStatusUpdateRequest deleteStatusUpdateRequest;

	// TODO: move to CreateStatusUpdateRequestTest

	@BeforeClass
	public static void beforeClass() {
		RequestTest.beforeClass();

		// create a status update that can be deleted
		CreateStatusUpdateRequestTest.createStatusUpdate(
				AlgorithmTests.DATABASE, VALID_STATUS_UPDATE_IDENTIFIER);
		assertNotNull(NeoUtils
				.getStatusUpdateByIdentifier(VALID_STATUS_UPDATE_IDENTIFIER));
	}

	private void fillRequest(final String type, final String userId,
			final String statusUpdateId) {
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
							.getParameter(ProtocolConstants.Parameters.Delete.StatusUpdate.USER_IDENTIFIER))
					.thenReturn(userId);
			when(
					this.request
							.getParameter(ProtocolConstants.Parameters.Delete.StatusUpdate.STATUS_UPDATE_IDENTIFIER))
					.thenReturn(statusUpdateId);
			final DeleteStatusUpdateResponse deleteStatusUpdateResponse = new DeleteStatusUpdateResponse();
			this.deleteStatusUpdateRequest = DeleteStatusUpdateRequest
					.checkRequest(this.request, deleteRequest,
							deleteStatusUpdateResponse);
			this.jsonResponse = extractJson(deleteStatusUpdateResponse);
		}
	}

	@Test
	public void testParameterMissing() {
		// missing: delete request type
		this.fillRequest(null, VALID_USER_IDENTIFIER,
				VALID_STATUS_UPDATE_IDENTIFIER);
		assertEquals(MISSING_PARAM_BEFORE + ProtocolConstants.Parameters.Delete.TYPE
				+ MISSING_PARAM_AFTER,
				this.jsonResponse.get(ProtocolConstants.STATUS_MESSAGE));

		// missing: user identifier
		this.fillRequest(VALID_TYPE, null, VALID_STATUS_UPDATE_IDENTIFIER);
		assertEquals(MISSING_PARAM_BEFORE
				+ ProtocolConstants.Parameters.Delete.StatusUpdate.USER_IDENTIFIER
				+ MISSING_PARAM_AFTER,
				this.jsonResponse.get(ProtocolConstants.STATUS_MESSAGE));

		// missing: status update identifier
		this.fillRequest(VALID_TYPE, VALID_USER_IDENTIFIER, null);
		assertEquals(
				MISSING_PARAM_BEFORE
						+ ProtocolConstants.Parameters.Delete.StatusUpdate.STATUS_UPDATE_IDENTIFIER
						+ MISSING_PARAM_AFTER,
				this.jsonResponse.get(ProtocolConstants.STATUS_MESSAGE));
	}

	@Test
	public void testDeleteTypeValid() {
		this.fillRequest(VALID_TYPE, VALID_USER_IDENTIFIER,
				VALID_STATUS_UPDATE_IDENTIFIER);
		assertFalse(ProtocolConstants.StatusCodes.Delete.TYPE_INVALID
				.equals(this.jsonResponse.get(ProtocolConstants.STATUS_MESSAGE)));
	}

	@Test
	public void testDeleteTypeInvalid() {
		this.fillRequest(INVALID_TYPE, VALID_USER_IDENTIFIER,
				VALID_STATUS_UPDATE_IDENTIFIER);
		assertEquals(ProtocolConstants.StatusCodes.Delete.TYPE_INVALID,
				this.jsonResponse.get(ProtocolConstants.STATUS_MESSAGE));
		assertNull(this.deleteStatusUpdateRequest);
	}

	@Test
	public void testUserIdentifierValid() {
		this.fillRequest(VALID_TYPE, VALID_USER_IDENTIFIER,
				VALID_STATUS_UPDATE_IDENTIFIER);
		assertFalse(ProtocolConstants.StatusCodes.Delete.StatusUpdate.USER_NOT_EXISTING
				.equals(this.jsonResponse.get(ProtocolConstants.STATUS_MESSAGE)));
	}

	@Test
	public void testUserIdentifierInvalid() {
		this.fillRequest(VALID_TYPE, INVALID_USER_IDENTIFIER,
				VALID_STATUS_UPDATE_IDENTIFIER);
		assertEquals(
				ProtocolConstants.StatusCodes.Delete.StatusUpdate.USER_NOT_EXISTING,
				this.jsonResponse.get(ProtocolConstants.STATUS_MESSAGE));
		assertNull(this.deleteStatusUpdateRequest);
	}

	@Test
	public void testStatusUpdateIdentifierValid() {
		this.fillRequest(VALID_TYPE, VALID_USER_IDENTIFIER,
				VALID_STATUS_UPDATE_IDENTIFIER);
		assertFalse(ProtocolConstants.StatusCodes.Delete.StatusUpdate.STATUS_UPDATE_NOT_EXISTING
				.equals(this.jsonResponse.get(ProtocolConstants.STATUS_MESSAGE)));
	}

	@Test
	public void testStatusUpdateIdentifierInvalid() {
		this.fillRequest(VALID_TYPE, VALID_USER_IDENTIFIER,
				INVALID_STATUS_UPDATE_IDENTIFIER);
		assertEquals(
				ProtocolConstants.StatusCodes.Delete.StatusUpdate.STATUS_UPDATE_NOT_EXISTING,
				this.jsonResponse.get(ProtocolConstants.STATUS_MESSAGE));
		assertNull(this.deleteStatusUpdateRequest);
	}

}