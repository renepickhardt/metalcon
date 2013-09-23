package de.metalcon.imageServer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;

import de.metalcon.imageStorageServer.protocol.ProtocolConstants;
import de.metalcon.imageStorageServer.protocol.Response;
import de.metalcon.imageStorageServer.protocol.update.UpdateRequest;
import de.metalcon.imageStorageServer.protocol.update.UpdateResponse;
import de.metalcon.utils.FormItemList;

public class TestUpdateRequest {

	final private ServletConfig servletConfig = mock(ServletConfig.class);
	final private ServletContext servletContext = mock(ServletContext.class);

	private UpdateResponse updateResponse;
	private JSONObject jsonResponse;
	// private static FileItem imageFileItem;
	private final String responseBeginMissing = "request incomplete: parameter \"";
	private final String responseEndMissing = "\" is missing";

	@Before
	public void initializeTest() {
		HttpServlet servlet = mock(HttpServlet.class);
		when(this.servletConfig.getServletContext()).thenReturn(
				this.servletContext);

		try {
			servlet.init(this.servletConfig);
		} catch (ServletException e) {
			fail("could not initialize servlet");
			e.printStackTrace();
		}
	}

	@Test
	public void testNoIdentifierGiven() {
		this.processUpdateRequest(null,
				ProtocolTestConstants.VALID_IMAGE_METADATA);

		assertEquals(this.responseBeginMissing
				+ ProtocolConstants.Parameters.Update.IMAGE_IDENTIFIER
				+ this.responseEndMissing,
				this.jsonResponse.get(ProtocolConstants.STATUS_MESSAGE));
	}

	@Test
	public void testNoMetadataGiven() {
		this.processUpdateRequest("testIdentifier", null);

		assertEquals(this.responseBeginMissing
				+ ProtocolConstants.Parameters.Update.META_DATA
				+ this.responseEndMissing,
				this.jsonResponse.get(ProtocolConstants.STATUS_MESSAGE));
	}

	@Test
	public void testMetadataNotJson() {
		this.processUpdateRequest("testIdentifier",
				ProtocolTestConstants.MALFORMED_IMAGE_METADATA);

		assertEquals(
				ProtocolConstants.StatusMessage.Update.META_DATA_MALFORMED,
				this.jsonResponse.get(ProtocolConstants.STATUS_MESSAGE));
	}

	// test if json

	private void processUpdateRequest(String imageIdentifier, String metaData) {
		FormItemList formItemList = new FormItemList();
		this.updateResponse = new UpdateResponse();

		if (imageIdentifier != null) {
			formItemList.addField(
					ProtocolConstants.Parameters.Update.IMAGE_IDENTIFIER,
					imageIdentifier);
		}

		if (metaData != null) {
			formItemList.addField(
					ProtocolConstants.Parameters.Update.META_DATA, metaData);
		}

		UpdateRequest.checkRequest(formItemList, this.updateResponse);
		this.jsonResponse = extractJson(this.updateResponse);
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
