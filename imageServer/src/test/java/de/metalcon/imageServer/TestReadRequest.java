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
import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;

import de.metalcon.imageServer.protocol.ProtocolConstants;
import de.metalcon.imageServer.protocol.Response;
import de.metalcon.imageServer.protocol.read.ReadRequest;
import de.metalcon.imageServer.protocol.read.ReadResponse;
import de.metalcon.utils.FormItemList;

public class TestReadRequest {

	final private ServletConfig servletConfig = mock(ServletConfig.class);
	final private ServletContext servletContext = mock(ServletContext.class);

	private ReadResponse readResponse;
	private JSONObject jsonResponse;
	// private static FileItem imageFileItem;
	private HttpServletRequest request;
	private final String responseBeginMissing = "request incomplete: parameter \"";
	private final String responseBeginCorrupt = "request corrupt: parameter \"";
	private final String responseEndMissing = "\" is missing";

	private final String responseEndMalformed = "\" is malformed";

	@Before
	public void initializeTest() {
		this.request = mock(HttpServletRequest.class);
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
		this.processReadRequest(null, "0");
		System.out.println(this.readResponse);
		assertEquals(this.responseBeginMissing
				+ ProtocolConstants.Parameters.Read.IMAGE_IDENTIFIER
				+ this.responseEndMissing,
				this.jsonResponse.get(ProtocolConstants.STATUS_MESSAGE));
	}

	@Test
	public void testNoOriginalFlagGiven() {
		this.processReadRequest("testIdentifier", null);
		System.out.println(this.readResponse);
		assertEquals(this.responseBeginMissing
				+ ProtocolConstants.Parameters.Read.ORIGINAL_FLAG
				+ this.responseEndMissing,
				this.jsonResponse.get(ProtocolConstants.STATUS_MESSAGE));
	}

	@Test
	public void testOriginalFlagMalformed() {
		this.processReadRequest("testIdentifier", "wrong");
		assertEquals(this.responseBeginCorrupt
				+ ProtocolConstants.Parameters.Read.ORIGINAL_FLAG
				+ this.responseEndMalformed,
				this.jsonResponse.get(ProtocolConstants.STATUS_MESSAGE));
	}

	@Test
	public void testWithScalingNoHeightGiven() {
		this.processReadRequest("testIdentifier", "0", null, "100");
		assertEquals(this.responseBeginMissing
				+ ProtocolConstants.Parameters.Read.IMAGE_WIDTH
				+ this.responseEndMissing,
				this.jsonResponse.get(ProtocolConstants.STATUS_MESSAGE));
	}

	private void processReadRequest(String imageIdentifier,
			String originalFlag, String height, String width) {

		FormItemList formItemList = new FormItemList();
		this.readResponse = new ReadResponse();

		if (imageIdentifier != null) {
			formItemList.addField(
					ProtocolConstants.Parameters.Read.IMAGE_IDENTIFIER,
					imageIdentifier);
		}

		if (originalFlag != null) {
			formItemList.addField(
					ProtocolConstants.Parameters.Read.ORIGINAL_FLAG,
					originalFlag);
		}

		if (height != null) {
			formItemList.addField(
					ProtocolConstants.Parameters.Read.IMAGE_HEIGHT, height);
		}

		if (height != null) {
			formItemList.addField(
					ProtocolConstants.Parameters.Read.IMAGE_WIDTH, width);
		}

		ReadRequest.checkRequest(formItemList, this.readResponse);
		this.jsonResponse = extractJson(this.readResponse);
	}

	private void processReadRequest(String imageIdentifier, String originalFlag) {
		FormItemList formItemList = new FormItemList();
		this.readResponse = new ReadResponse();

		if (imageIdentifier != null) {
			formItemList.addField(
					ProtocolConstants.Parameters.Read.IMAGE_IDENTIFIER,
					imageIdentifier);
		}

		if (originalFlag != null) {
			formItemList.addField(
					ProtocolConstants.Parameters.Read.ORIGINAL_FLAG,
					originalFlag);
		}

		ReadRequest.checkRequest(formItemList, this.readResponse);
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
