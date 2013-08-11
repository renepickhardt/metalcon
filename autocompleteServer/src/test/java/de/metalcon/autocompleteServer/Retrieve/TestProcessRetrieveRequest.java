/**
 * 
 */
package de.metalcon.autocompleteServer.Retrieve;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.junit.Test;
import org.mockito.stubbing.OngoingStubbing;

import de.metalcon.autocompleteServer.Helper.ContextListener;
import de.metalcon.autocompleteServer.Helper.ProtocolConstants;
import de.metalcon.autocompleteServer.Helper.SuggestTree;

import static org.mockito.Mockito.*;

/**
 * @author Rene Pickhardt
 * 
 */
public class TestProcessRetrieveRequest {

	/**
	 * Test method for
	 * {@link de.metalcon.autocompleteServer.Retrieve.ProcessRetrieveRequest#checkRequestParameter(javax.servlet.http.HttpServletRequest, javax.servlet.ServletContext)}
	 * . foolowing:
	 * http://stackoverflow.com/questions/5434419/how-to-test-my-servlet
	 * -using-junit
	 * @throws ServletException 
	 */
	@Test
	public void testCheckRequestParameter() throws ServletException {

		final ServletConfig servletConfig = mock(ServletConfig.class);
		final ServletContext servletContext = mock(ServletContext.class);

		// setup the test.
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServlet servlet = mock(HttpServlet.class);
		when(servletConfig.getServletContext()).thenReturn(servletContext);

		SuggestTree generalIndex = new SuggestTree(7);
		generalIndex.put("Metallica", 100, "Metallica");
		generalIndex.put("Megadeth", 99, "Metallica");
		generalIndex.put("Megaherz", 98, "Metallica");
		generalIndex.put("Meshuggah", 97, "Metallica");
		generalIndex.put("Menhir", 96, "Metallica");
		generalIndex.put("Meat Loaf", 95, "Metallica");
		generalIndex.put("Melechesh", 94, "Metallica");

		when(servletContext.getAttribute("indexName:"+ProtocolConstants.DEFAULT_INDEX_NAME)).thenReturn(
				generalIndex);
		
		SuggestTree venueIndex = new SuggestTree(7);
		venueIndex.put("Das Kult", 55, "http://www.daskult.de");
		venueIndex.put("Die Halle", 44, "http://www.diehalle-frankfurt.de");
		venueIndex.put("Die Mühle", 30, "http://www.die-muehle.net");
		venueIndex.put("Dreams", 30, "http://www.discothek-dreams.de");
		venueIndex.put("Dudelsack", 27, "http://www.dudelsack-bk.de");
		venueIndex.put("Das Haus", 25, "http://www.dashaus-lu.de/home.html");
		venueIndex.put("Druckluftkammer", 25, "http://www.druckluftkammer.de/");

		when(servletContext.getAttribute("indexName:"+"venueIndex")).thenReturn(
				venueIndex);

		HashMap<String, String> imageIndex = new HashMap<String, String>();
		when(servletContext.getAttribute(ProtocolConstants.IMAGE_SERVER_CONTEXT_KEY)).thenReturn(imageIndex);
		
		servlet.init(servletConfig);		

		
		//		ContextListener.setIndex("venues", venueIndex, context);

		/***********************************************************************************************
		 * DO THE TESTING FROM HERE
		 ***********************************************************************************************/
		when(request.getParameter("term")).thenReturn("Me");
		when(request.getParameter("numItems")).thenReturn("7");
		when(request.getParameter("indexName")).thenReturn("generalindex");
		ProcessRetrieveResponse response = ProcessRetrieveRequest
				.checkRequestParameter(request, servletContext);
		response.buildJsonResonse();
		JSONObject jsonResponse = getJson(response);
		ArrayList<HashMap<String, String>> suggestionList = (ArrayList<HashMap<String, String>>) jsonResponse.get("suggestionList");
		assertTrue(suggestionList.size()==7);
		
		
		when(request.getParameter("term")).thenReturn(null);
		response = ProcessRetrieveRequest
				.checkRequestParameter(request, servletContext);
		response.buildJsonResonse();
		jsonResponse = getJson(response);
		suggestionList = (ArrayList<HashMap<String, String>>) jsonResponse.get("suggestionList");
		assertTrue(suggestionList.size()==0);

		when(request.getParameter("term")).thenReturn("Me");
		when(request.getParameter("numItems")).thenReturn("8");
		response = ProcessRetrieveRequest
				.checkRequestParameter(request, servletContext);
		response.buildJsonResonse();
		jsonResponse = getJson(response);
		jsonResponse.get("warning:numItems").equals(RetrieveStatusCodes.NUMITEMS_OUT_OF_RANGE);
		System.out.println(jsonResponse.get("warning:numItems"));
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
