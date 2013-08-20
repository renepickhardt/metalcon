package de.metalcon.autocompleteServer.Create;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;

import de.metalcon.autocompleteServer.Helper.ProtocolConstants;
import de.metalcon.autocompleteServer.Helper.SuggestTree;
import de.metalcon.utils.FormItemList;

public class TestProcessCreateRequest {

	final private ServletConfig servletConfig = mock(ServletConfig.class);
	final private ServletContext servletContext = mock(ServletContext.class);

	private HttpServletRequest request;

	@Before
	public void initializeTest() {

		this.request = mock(HttpServletRequest.class);
		HttpServlet servlet = mock(HttpServlet.class);
		when(this.servletConfig.getServletContext()).thenReturn(
				this.servletContext);
		SuggestTree generalIndex = new SuggestTree(7);
		when(
				this.servletContext
						.getAttribute(ProtocolConstants.INDEX_PARAMETER
								+ "testIndex")).thenReturn(generalIndex);

		try {
			servlet.init(this.servletConfig);
		} catch (ServletException e) {
			fail("could not initialize servlet");
			e.printStackTrace();
		}
	}

	// TODO: add tests according to protocol specifications. Check each possible
	// status.

	@Test
	public void testFullFormWithImage() {

		// TODO insert base64 encoded image
		ProcessCreateResponse testResponse = this.processTestRequest(
				ProtocolTestConstants.VALID_SUGGESTION_KEY,
				ProtocolTestConstants.VALID_SUGGESTION_STRING,
				ProtocolTestConstants.VALID_SUGGESTION_WEIGHT,
				ProtocolTestConstants.VALID_SUGGESTION_INDEX, null);

		assertEquals(ProtocolTestConstants.VALID_SUGGESTION_KEY, testResponse
				.getContainer().getKey());
		assertEquals(ProtocolTestConstants.VALID_SUGGESTION_STRING,
				testResponse.getContainer().getSuggestString());
		assertEquals(ProtocolTestConstants.VALID_SUGGESTION_WEIGHT,
				testResponse.getContainer().getWeight().toString());
		assertEquals(ProtocolTestConstants.VALID_SUGGESTION_INDEX, testResponse
				.getContainer().getIndexName());
		assertEquals("{" + "\"term\"" + ":" + "\"test\"" + ","
				+ "\"Warning:noImage\"" + ":" + "\"No image inserted\"" + "}",
				testResponse.getResponse().toString());
		// assert image is B64 encoded and not null
	}

	@Test
	public void testFullFormWithoutImage() {

		ProcessCreateResponse testResponse = this.processTestRequest(
				ProtocolTestConstants.VALID_SUGGESTION_KEY,
				ProtocolTestConstants.VALID_SUGGESTION_STRING,
				ProtocolTestConstants.VALID_SUGGESTION_WEIGHT,
				ProtocolTestConstants.VALID_SUGGESTION_INDEX, null);

		assertEquals(ProtocolTestConstants.VALID_SUGGESTION_KEY, testResponse
				.getContainer().getKey());
		assertEquals(ProtocolTestConstants.VALID_SUGGESTION_STRING,
				testResponse.getContainer().getSuggestString());
		assertEquals(ProtocolTestConstants.VALID_SUGGESTION_WEIGHT,
				testResponse.getContainer().getWeight().toString());
		assertEquals(ProtocolTestConstants.VALID_SUGGESTION_INDEX, testResponse
				.getContainer().getIndexName());
		assertEquals("{" + "\"term\"" + ":" + "\"test\"" + ","
				+ "\"Warning:noImage\"" + ":" + "\"No image inserted\"" + "}",
				testResponse.getResponse().toString());
	}

	private ProcessCreateResponse processTestRequest(String key, String term,
			String weight, String index, String imageBase64) {

		ProcessCreateResponse response = new ProcessCreateResponse(
				this.servletConfig.getServletContext());
		FormItemList testItems = new FormItemList();
		testItems.addField(ProtocolConstants.SUGGESTION_KEY, key);
		testItems.addField(ProtocolConstants.SUGGESTION_STRING, term);
		testItems.addField(ProtocolConstants.SUGGESTION_WEIGHT, weight);
		testItems.addField(ProtocolConstants.INDEX_PARAMETER, index);
		testItems.addField(ProtocolConstants.IMAGE, imageBase64);

		return ProcessCreateRequest.checkRequestParameter(testItems, response,
				this.servletConfig.getServletContext());
	}
}
