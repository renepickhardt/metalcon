package de.metalcon.server.tomcat.NSSP.read;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

import org.junit.Test;

import de.metalcon.server.tomcat.NSSP.ProtocolConstants;
import de.metalcon.server.tomcat.NSSP.RequestTest;

public class ReadRequestTest extends RequestTest {

	/**
	 * valid number of items to retrieve
	 */
	private static String VALID_NUM_ITEMS = "15";

	/**
	 * valid retrieval flag
	 */
	private static String VALID_OWN_UPDATES = "0";

	/**
	 * invalid retrieval flag
	 */
	private static String INVALID_OWN_UPDATES = "abc";

	/**
	 * read request created from POST request
	 */
	private ReadRequest readRequest;

	private void fillRequest(final String userId, final String posterId,
			final String numItems, final String ownUpdates) {
		when(
				this.request
						.getParameter(ProtocolConstants.Parameters.Read.USER_IDENTIFIER))
				.thenReturn(userId);
		when(
				this.request
						.getParameter(ProtocolConstants.Parameters.Read.POSTER_IDENTIFIER))
				.thenReturn(posterId);
		when(this.request.getParameter(ProtocolConstants.Parameters.Read.NUM_ITEMS))
				.thenReturn(numItems);
		when(this.request.getParameter(ProtocolConstants.Parameters.Read.OWN_UPDATES))
				.thenReturn(ownUpdates);
		final ReadResponse response = new ReadResponse();
		this.readRequest = ReadRequest.checkRequest(this.request, response);
		this.jsonResponse = extractJson(response);
	}

	@Test
	public void testParameterMissing() {
		// missing: user identifier
		this.fillRequest(null, VALID_USER_IDENTIFIER, VALID_NUM_ITEMS,
				VALID_OWN_UPDATES);
		assertEquals(MISSING_PARAM_BEFORE
				+ ProtocolConstants.Parameters.Read.USER_IDENTIFIER
				+ MISSING_PARAM_AFTER,
				this.jsonResponse.get(ProtocolConstants.STATUS_MESSAGE));

		// missing: poster identifier
		this.fillRequest(VALID_USER_IDENTIFIER, null, VALID_NUM_ITEMS,
				VALID_OWN_UPDATES);
		assertEquals(MISSING_PARAM_BEFORE
				+ ProtocolConstants.Parameters.Read.POSTER_IDENTIFIER
				+ MISSING_PARAM_AFTER,
				this.jsonResponse.get(ProtocolConstants.STATUS_MESSAGE));

		// missing: number of items
		this.fillRequest(VALID_USER_IDENTIFIER, VALID_USER_IDENTIFIER, null,
				VALID_OWN_UPDATES);
		assertEquals(MISSING_PARAM_BEFORE
				+ ProtocolConstants.Parameters.Read.NUM_ITEMS + MISSING_PARAM_AFTER,
				this.jsonResponse.get(ProtocolConstants.STATUS_MESSAGE));

		// missing: retrieval flag
		this.fillRequest(VALID_USER_IDENTIFIER, VALID_USER_IDENTIFIER,
				VALID_NUM_ITEMS, null);
		assertEquals(
				MISSING_PARAM_BEFORE + ProtocolConstants.Parameters.Read.OWN_UPDATES
						+ MISSING_PARAM_AFTER,
				this.jsonResponse.get(ProtocolConstants.STATUS_MESSAGE));
	}

	@Test
	public void testUserIdentifierValid() {
		this.fillRequest(VALID_USER_IDENTIFIER, VALID_USER_IDENTIFIER,
				VALID_NUM_ITEMS, VALID_OWN_UPDATES);
		assertFalse(ProtocolConstants.StatusCodes.Read.USER_NOT_EXISTING
				.equals(this.jsonResponse.get(ProtocolConstants.STATUS_MESSAGE)));
	}

	@Test
	public void testUserIdentifierInvalid() {
		this.fillRequest(INVALID_USER_IDENTIFIER, VALID_USER_IDENTIFIER,
				VALID_NUM_ITEMS, VALID_OWN_UPDATES);
		assertEquals(ProtocolConstants.StatusCodes.Read.USER_NOT_EXISTING,
				this.jsonResponse.get(ProtocolConstants.STATUS_MESSAGE));
		assertNull(this.readRequest);
	}

	@Test
	public void testPosterIdentifierValid() {
		this.fillRequest(VALID_USER_IDENTIFIER, VALID_USER_IDENTIFIER,
				VALID_NUM_ITEMS, VALID_OWN_UPDATES);
		assertFalse(ProtocolConstants.StatusCodes.Read.POSTER_NOT_EXISTING
				.equals(this.jsonResponse.get(ProtocolConstants.STATUS_MESSAGE)));
	}

	@Test
	public void testPosterIdentifierInvalid() {
		this.fillRequest(VALID_USER_IDENTIFIER, INVALID_USER_IDENTIFIER,
				VALID_NUM_ITEMS, VALID_OWN_UPDATES);
		assertEquals(ProtocolConstants.StatusCodes.Read.POSTER_NOT_EXISTING,
				this.jsonResponse.get(ProtocolConstants.STATUS_MESSAGE));
		assertNull(this.readRequest);
	}

	@Test
	public void testNumItemsValid() {
		this.fillRequest(VALID_USER_IDENTIFIER, VALID_USER_IDENTIFIER,
				VALID_NUM_ITEMS, VALID_OWN_UPDATES);
		assertFalse(ProtocolConstants.StatusCodes.Read.NUM_ITEMS_INVALID
				.equals(this.jsonResponse.get(ProtocolConstants.STATUS_MESSAGE)));
	}

	@Test
	public void testNumItemsInvalid() {
		// number of items is no number
		this.fillRequest(VALID_USER_IDENTIFIER, VALID_USER_IDENTIFIER, "abc",
				VALID_OWN_UPDATES);
		assertEquals(ProtocolConstants.StatusCodes.Read.NUM_ITEMS_INVALID,
				this.jsonResponse.get(ProtocolConstants.STATUS_MESSAGE));
		assertNull(this.readRequest);

		// number of items is an invalid number
		this.fillRequest(VALID_USER_IDENTIFIER, VALID_USER_IDENTIFIER, "0",
				VALID_OWN_UPDATES);
		assertEquals(ProtocolConstants.StatusCodes.Read.NUM_ITEMS_INVALID,
				this.jsonResponse.get(ProtocolConstants.STATUS_MESSAGE));
		assertNull(this.readRequest);
	}

	@Test
	public void testOwnUpdatesValid() {
		this.fillRequest(VALID_USER_IDENTIFIER, VALID_USER_IDENTIFIER,
				VALID_NUM_ITEMS, "0");
		assertFalse(ProtocolConstants.StatusCodes.Read.OWN_UPDATES_INVALID
				.equals(this.jsonResponse.get(ProtocolConstants.STATUS_MESSAGE)));

		this.fillRequest(VALID_USER_IDENTIFIER, VALID_USER_IDENTIFIER,
				VALID_NUM_ITEMS, "1");
		assertFalse(ProtocolConstants.StatusCodes.Read.OWN_UPDATES_INVALID
				.equals(this.jsonResponse.get(ProtocolConstants.STATUS_MESSAGE)));
	}

	@Test
	public void testOwnUpdatesInvalid() {
		this.fillRequest(VALID_USER_IDENTIFIER, VALID_USER_IDENTIFIER,
				VALID_NUM_ITEMS, INVALID_OWN_UPDATES);
		assertEquals(ProtocolConstants.StatusCodes.Read.OWN_UPDATES_INVALID,
				this.jsonResponse.get(ProtocolConstants.STATUS_MESSAGE));
		assertNull(this.readRequest);
	}
}