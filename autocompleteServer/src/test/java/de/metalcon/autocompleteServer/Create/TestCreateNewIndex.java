package de.metalcon.autocompleteServer.Create;

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

import de.metalcon.autocompleteServer.Response;
import de.metalcon.autocompleteServer.Helper.ProtocolConstants;
import de.metalcon.utils.FormItemList;

public class TestCreateNewIndex {

	private JSONObject jsonResponse;
	@SuppressWarnings("unused")
	private HttpServletRequest request;

	final private ServletConfig servletConfig = mock(ServletConfig.class);
	final private ServletContext servletContext = mock(ServletContext.class);
	private NewIndexResponse newIndexResponse;

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
	public void testMissingIndexName() {

		this.processNewIndexRequest(null);

		assertEquals("request incomplete: parameter \""
				+ ProtocolConstants.INDEX_PARAMETER + "\" is missing",
				this.jsonResponse.get(ProtocolConstants.STATUS_MESSAGE));
	}

	@Test
	public void testDuplicateIndexName() {

		this.processNewIndexRequest(ProtocolTestConstants.VALID_SUGGESTION_INDEX);
		this.processNewIndexRequest(ProtocolTestConstants.VALID_SUGGESTION_INDEX);

		// TODO adapt error message
		assertEquals("request invalid: index \""
				+ ProtocolTestConstants.VALID_SUGGESTION_INDEX
				+ "\" already exists.",
				this.jsonResponse.get(ProtocolConstants.STATUS_MESSAGE));

	}

	private void processNewIndexRequest(String indexName) {

		this.newIndexResponse = new NewIndexResponse(
				this.servletConfig.getServletContext());
		FormItemList formItemList = new FormItemList();

		if (indexName != null) {
			formItemList.addField(ProtocolConstants.INDEX_PARAMETER, indexName);
		}

		NewIndexRequest.checkRequestParameter(formItemList,
				this.newIndexResponse, this.servletConfig.getServletContext());
		this.jsonResponse = extractJson(this.newIndexResponse);

	}

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
