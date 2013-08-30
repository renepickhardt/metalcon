/**
 * 
 */
package de.metalcon.autocompleteServer.Retrieve;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONObject;
import org.junit.Test;

import de.metalcon.autocompleteServer.Helper.ProtocolConstants;
import de.metalcon.autocompleteServer.Helper.SuggestTree;

/**
 * @author Rene Pickhardt
 * 
 *         This TestClass makes sure that the ASTP Retrieve part of the protocol
 *         is properly working
 * 
 *         We check several things like passing wrong parameters, passing wrong
 *         parameter types but also passing the correct values
 * 
 */
public class TestProcessRetrieveRequest {
	final private ServletConfig servletConfig = mock(ServletConfig.class);
	final private ServletContext servletContext = mock(ServletContext.class);

	private HttpServletRequest initializeTest() {
		// setup the test.
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServlet servlet = mock(HttpServlet.class);
		when(this.servletConfig.getServletContext()).thenReturn(
				this.servletContext);

		SuggestTree generalIndex = new SuggestTree(7);
		generalIndex.put("Metallica", 100, "Metallica");
		generalIndex.put("Megadeth", 99, "Metallica");
		generalIndex.put("Megaherz", 98, "Metallica");
		generalIndex.put("Meshuggah", 97, "Metallica");
		generalIndex.put("Menhir", 96, "Metallica");
		generalIndex.put("Meat Loaf", 95, "Metallica");
		generalIndex.put("Melechesh", 94, "Metallica");

		when(
				this.servletContext
						.getAttribute(ProtocolConstants.INDEX_PARAMETER
								+ ProtocolConstants.DEFAULT_INDEX_NAME))
				.thenReturn(generalIndex);

		SuggestTree venueIndex = new SuggestTree(7);
		venueIndex.put("Das Kult", 55, "http://www.daskult.de");
		venueIndex.put("Die Halle", 44, "http://www.diehalle-frankfurt.de");
		venueIndex.put("Die Mühle", 30, "http://www.die-muehle.net");
		venueIndex.put("Dreams", 30, "http://www.discothek-dreams.de");
		venueIndex.put("Dudelsack", 27, "http://www.dudelsack-bk.de");
		venueIndex.put("Das Haus", 25, "http://www.dashaus-lu.de/home.html");
		venueIndex.put("Druckluftkammer", 25, "http://www.druckluftkammer.de/");

		when(
				this.servletContext
						.getAttribute(ProtocolConstants.INDEX_PARAMETER
								+ "venueIndex")).thenReturn(venueIndex);

		HashMap<String, String> imageIndex = new HashMap<String, String>();
		when(
				this.servletContext
						.getAttribute(ProtocolConstants.IMAGE_SERVER_CONTEXT_KEY))
				.thenReturn(imageIndex);

		try {
			servlet.init(this.servletConfig);
		} catch (ServletException e) {
			fail("could not initialize servlet");
			e.printStackTrace();
		}
		return request;
	}

	/**
	 * Test method for
	 * {@link de.metalcon.autocompleteServer.Retrieve.ProcessRetrieveRequest#checkRequestParameter(javax.servlet.http.HttpServletRequest, javax.servlet.ServletContext)}
	 * . foolowing:
	 * http://stackoverflow.com/questions/5434419/how-to-test-my-servlet
	 * -using-junit
	 * 
	 * @throws ServletException
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCheckRequestParameter() throws ServletException {
		HttpServletRequest request = this.initializeTest();
		JSONObject jsonResponse = this.testRequest(request, "Me", "7",
				ProtocolConstants.DEFAULT_INDEX_NAME);
		ArrayList<HashMap<String, String>> suggestionList = (ArrayList<HashMap<String, String>>) jsonResponse
				.get("suggestionList");
		assertTrue(suggestionList.size() == 7);
	}

	/**
	 * Tests all possible ways the suggest string ca be set
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testTermField() {
		HttpServletRequest request = this.initializeTest();
		JSONObject jsonResponse = this.testRequest(request, "Ne", "7",
				ProtocolConstants.DEFAULT_INDEX_NAME);
		ArrayList<HashMap<String, String>> suggestionList = (ArrayList<HashMap<String, String>>) jsonResponse
				.get(ProtocolConstants.RESP_JSON_FIELD_SUGGESTION_LIST);
		assertTrue(suggestionList.size() == 0);

		jsonResponse = this.testRequest(request, null, "7",
				ProtocolConstants.DEFAULT_INDEX_NAME);
		suggestionList = (ArrayList<HashMap<String, String>>) jsonResponse
				.get(ProtocolConstants.RESP_JSON_FIELD_SUGGESTION_LIST);
		assertTrue(suggestionList.size() == 0);
	}

	/**
	 * tests all possible ways the numItems parameter can be set
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testNumItemField() {
		HttpServletRequest request = this.initializeTest();
		JSONObject jsonResponse = this.testRequest(request, "Me", "8",
				ProtocolConstants.DEFAULT_INDEX_NAME);
		assertTrue(jsonResponse.get("warning:numItems").equals(
				RetrieveStatusCodes.NUMITEMS_OUT_OF_RANGE));
		ArrayList<HashMap<String, String>> suggestionList = (ArrayList<HashMap<String, String>>) jsonResponse
				.get("suggestionList");
		assertTrue(suggestionList.size() == 7);

		jsonResponse = this.testRequest(request, "Me", "someRandomString",
				ProtocolConstants.DEFAULT_INDEX_NAME);
		suggestionList = (ArrayList<HashMap<String, String>>) jsonResponse
				.get("suggestionList");
		assertTrue(jsonResponse.get("warning:numItems").equals(
				RetrieveStatusCodes.NUMITEMS_NOT_AN_INTEGER));
		assertTrue(suggestionList.size() == 7);

		jsonResponse = this.testRequest(request, "Me", null,
				ProtocolConstants.DEFAULT_INDEX_NAME);
		suggestionList = (ArrayList<HashMap<String, String>>) jsonResponse
				.get("suggestionList");
		assertTrue(jsonResponse.get("warning:numItems").equals(
				RetrieveStatusCodes.NUMITEMS_NOT_GIVEN));
		assertTrue(suggestionList.size() == 7);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testIndexNames() {
		HttpServletRequest request = this.initializeTest();
		JSONObject jsonResponse = this.testRequest(request, "D", "7",
				"venueIndex");
		ArrayList<HashMap<String, String>> suggestionList = (ArrayList<HashMap<String, String>>) jsonResponse
				.get(ProtocolConstants.RESP_JSON_FIELD_SUGGESTION_LIST);
		assertTrue(suggestionList.size() == 7);

		jsonResponse = this.testRequest(request, "M", "7", "venueIndex");
		suggestionList = (ArrayList<HashMap<String, String>>) jsonResponse
				.get(ProtocolConstants.RESP_JSON_FIELD_SUGGESTION_LIST);
		assertTrue(suggestionList.size() == 0);

		jsonResponse = this.testRequest(request, "D", "7", "generalIndex");
		suggestionList = (ArrayList<HashMap<String, String>>) jsonResponse
				.get(ProtocolConstants.RESP_JSON_FIELD_SUGGESTION_LIST);
		assertTrue(suggestionList.size() == 0);

		jsonResponse = this.testRequest(request, "M", "7", "generalIndex");
		suggestionList = (ArrayList<HashMap<String, String>>) jsonResponse
				.get(ProtocolConstants.RESP_JSON_FIELD_SUGGESTION_LIST);
		assertTrue(suggestionList.size() == 7);

		jsonResponse = this.testRequest(request, "Me", "7", "someRandomIndex");
		suggestionList = (ArrayList<HashMap<String, String>>) jsonResponse
				.get(ProtocolConstants.RESP_JSON_FIELD_SUGGESTION_LIST);
		assertTrue(jsonResponse.get("warning:noIndexGiven").equals(
				RetrieveStatusCodes.INDEX_UNKNOWN));
		assertTrue(suggestionList.size() == 7);
	}

	/**
	 * tests if the keys are correctly trasfered TODO: need to be more specific
	 * what happens if no keys are in the answer and if mixed keys are availabel
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testKeys() {
		HttpServletRequest request = this.initializeTest();
		JSONObject jsonResponse = this.testRequest(request, "D", "7",
				"venueIndex");

		ArrayList<HashMap<String, String>> suggestionList = (ArrayList<HashMap<String, String>>) jsonResponse
				.get("suggestionList");
		suggestionList.get(0).get("key").equals("http://www.daskult.de");
	}

	@Test
	public void testImages() {
		// TODO: need to implement Images Test
	}

	/**
	 * @param request
	 * @param term
	 * @param numItems
	 * @param indexName
	 * @return
	 */
	private JSONObject testRequest(HttpServletRequest request, String term,
			String numItems, String indexName) {
		when(request.getParameter(ProtocolConstants.QUERY_PARAMETER))
				.thenReturn(term);
		when(request.getParameter(ProtocolConstants.NUM_ITEMS)).thenReturn(
				numItems);
		when(request.getParameter(ProtocolConstants.INDEX_PARAMETER))
				.thenReturn(indexName);
		ProcessRetrieveResponse response = ProcessRetrieveRequest
				.checkRequestParameter(request, this.servletContext);
		response.buildJsonResonse();
		JSONObject jsonResponse = this.getJson(response);
		return jsonResponse;
	}

	/**
	 * @param response
	 * @return
	 */
	private JSONObject getJson(ProcessRetrieveResponse response) {
		Field field = null;
		try {
			field = ProcessRetrieveResponse.class
					.getDeclaredField("jsonResponse");
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
			fail("could not test the Json resonse of the ProcessRetrieveResponse class");
		} catch (SecurityException e) {
			e.printStackTrace();
			fail("could not test the Json resonse of the ProcessRetrieveResponse class");
		}
		field.setAccessible(true);
		try {
			return (JSONObject) field.get(response);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			fail("could not test the Json resonse of the ProcessRetrieveResponse class");

		} catch (IllegalAccessException e) {
			e.printStackTrace();
			fail("could not test the Json resonse of the ProcessRetrieveResponse class");
		}
		return null;
	}

}
