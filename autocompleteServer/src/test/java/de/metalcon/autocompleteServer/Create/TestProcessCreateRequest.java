package de.metalcon.autocompleteServer.Create;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import org.junit.Test;

import de.metalcon.autocompleteServer.Helper.SuggestTree;

public class TestProcessCreateRequest {

	final private ServletConfig servletConfig = mock(ServletConfig.class);
	final private ServletContext servletContext = mock(ServletContext.class);

	SuggestTree generalIndex = new SuggestTree(7);
	
	private HttpServletRequest initializeTest() {

		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServlet servlet = mock(HttpServlet.class);
		when(this.servletConfig.getServletContext()).thenReturn(
				this.servletContext);
		when(this.servletConfig.getServletContext()).thenReturn(
				this.servletContext);

	//	SuggestTree generalIndex = new SuggestTree(7);

		try {
			servlet.init(this.servletConfig);
		} catch (ServletException e) {
			fail("could not initialize servlet");
			e.printStackTrace();
		}
		return request;
	}

	@Test
	public void testContainerRun() {
		
		//TODO: include an image to this test or make another one with an image
		HttpServletRequest request = this.initializeTest();
		ProcessCreateResponse response = ProcessCreateRequest
				.checkRequestParameter(request,
						this.servletConfig.getServletContext());
		
		CreateRequestContainer suggestTreeCreateRequestContainer = new CreateRequestContainer();
		
		suggestTreeCreateRequestContainer.setSuggestString("testband");
		suggestTreeCreateRequestContainer.setIndexName("testIndex");
		suggestTreeCreateRequestContainer.setKey("testkey");
		suggestTreeCreateRequestContainer.setWeight(1);
		
		suggestTreeCreateRequestContainer.run(this.servletConfig.getServletContext());
		
		String retrieveResponse = generalIndex.getBestSuggestions("test").toString();
		
		//FIXME: insert correct response-string.
		//assertTrue(output.equals("testband, testIndex, testkey"));
		
	}
	
	
	@Test
	public void testFullFormWithoutImage() {
		HttpServletRequest request = this.initializeTest();

		when(request.getParameter("term")).thenReturn("testband");
		when(request.getParameter("key")).thenReturn("testkey");
		when(request.getParameter("weight")).thenReturn("1");
		when(request.getParameter("index")).thenReturn("testIndex");

		ProcessCreateResponse response = ProcessCreateRequest
				.checkRequestParameter(request,
						this.servletConfig.getServletContext());

		// if (response != null) {
		// if (response.getContainer() != null) {
		// CreateRequestContainer resp = response.getContainer();
		// if (resp.getKey() != null) {
		// assertTrue(response.getContainer().getKey()
		// .equals("testkey"));
		// } else {
		// System.out.println(response.getResponse().toString());
		// System.out.println("key null");
		// }
		// } else {
		// System.out.println("resp null");
		// }
		// } else {
		// System.out.println("response null");
		// }

		// FIXME: The Form-check recognizes this not being actual form-data and
		// thus throws its exception
		assertTrue(response.getContainer().getKey().equals("testKey"));
		assertTrue(response.getContainer().getSuggestString()
				.equals("testband"));
		assertTrue(response.getContainer().getWeight().equals("1"));
		assertTrue(response.getContainer().getIndexName().equals("testIndex"));

	}
}
