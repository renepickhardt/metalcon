package de.metalcon.imageServer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;

import org.json.simple.JSONObject;
import org.junit.Test;

import de.metalcon.imageStorageServer.protocol.ProtocolConstants;
import de.metalcon.imageStorageServer.protocol.Response;
import de.metalcon.imageStorageServer.protocol.read.ReadBulkRequest;
import de.metalcon.imageStorageServer.protocol.read.ReadResponse;
import de.metalcon.utils.FormItemList;

public class TestReadBulkRequest {

	private ReadResponse readResponse;

	private static final String PARAM_MISSING_BEGIN = "request incomplete: parameter \"";
	private static final String PARAM_MISSING_END = "\" is missing";
	private JSONObject jsonResponse;

	@Test
	public void testNoIdentifierGiven() {
		this.processReadRequest(null, "100", "100");
		System.out.println(this.readResponse);
		assertEquals(PARAM_MISSING_BEGIN
				+ ProtocolConstants.Parameters.Read.IMAGE_IDENTIFIER
				+ PARAM_MISSING_END,
				this.jsonResponse.get(ProtocolConstants.STATUS_MESSAGE));
	}

	// test if there are empty strings in the sting array after splitting
	@Test
	public void testIdentifierListContainsEmptyParts() {
		this.processReadRequest(
				ProtocolTestConstants.MALFORMED_IDENTIFIER_LIST, "100", "100");
		assertEquals(ProtocolConstants.Parameters.Read.IMAGE_IDENTIFIER_LIST,
				this.jsonResponse.get(ProtocolConstants.STATUS_MESSAGE));

	}

	@Test
	public void testNoHeightGiven() {
		this.processReadRequest("testIdentifier", null, "100");
		assertEquals(PARAM_MISSING_BEGIN
				+ ProtocolConstants.Parameters.Read.IMAGE_HEIGHT
				+ PARAM_MISSING_END,
				this.jsonResponse.get(ProtocolConstants.STATUS_MESSAGE));
	}

	@Test
	public void testNoWidthGiven() {
		this.processReadRequest("testIdentifier", "100", null);
		assertEquals(PARAM_MISSING_BEGIN
				+ ProtocolConstants.Parameters.Read.IMAGE_WIDTH
				+ PARAM_MISSING_END,
				this.jsonResponse.get(ProtocolConstants.STATUS_MESSAGE));
	}

	@Test
	public void testWidthMalformed() {
		this.processReadRequest("testIdentifier", "100", "wrong");
		// TODO: use status message!
		assertEquals(ProtocolConstants.Parameters.Read.IMAGE_WIDTH,
				this.jsonResponse.get(ProtocolConstants.STATUS_MESSAGE));
	}

	@Test
	public void testHeightMalformed() {
		this.processReadRequest("testIdentifier", "wrong", "100");
		// TODO: use status message!
		assertEquals(ProtocolConstants.Parameters.Read.IMAGE_HEIGHT,
				this.jsonResponse.get(ProtocolConstants.STATUS_MESSAGE));
	}

	@Test
	public void testWidthTooSmall() {
		this.processReadRequest("testIdentifier", "100", "-1");
		assertEquals(
				ProtocolConstants.StatusMessage.Read.GEOMETRY_REQUESTED_WIDTH_LESS_OR_EQUAL_ZERO,
				this.jsonResponse.get(ProtocolConstants.STATUS_MESSAGE));
	}

	@Test
	public void testHeightTooSmall() {
		this.processReadRequest("testIdentifier", "-1", "100");
		assertEquals(
				ProtocolConstants.StatusMessage.Read.GEOMETRY_REQUESTED_HEIGHT_LESS_OR_EQUAL_ZERO,
				this.jsonResponse.get(ProtocolConstants.STATUS_MESSAGE));
	}

	private void processReadRequest(String imageIdentifier, String height,
			String width) {

		FormItemList formItemList = new FormItemList();
		this.readResponse = new ReadResponse();

		if (imageIdentifier != null) {
			formItemList.addField(
					ProtocolConstants.Parameters.Read.IMAGE_IDENTIFIER,
					imageIdentifier);
		}

		if (height != null) {
			formItemList.addField(
					ProtocolConstants.Parameters.Read.IMAGE_HEIGHT, height);
		}

		if (width != null) {
			formItemList.addField(
					ProtocolConstants.Parameters.Read.IMAGE_WIDTH, width);
		}

		ReadBulkRequest.checkRequest(formItemList, this.readResponse);
		this.jsonResponse = extractJson(this.readResponse);
	}

	/**
	 * extract the JSON object from the response, failing the test if this is
	 * not possible
	 * 
	 * @param response
	 *            NSSP response
	 * @return JSON object in the response passed
	 */
	protected static JSONObject extractJson(final Response response) {
		try {
			final Field field = Response.class.getDeclaredField("json");
			field.setAccessible(true);
			return (JSONObject) field.get(response);
		} catch (final Exception e) {
			fail("failed to extract the JSON object from class Response");
			return null;
		}
	}
}
