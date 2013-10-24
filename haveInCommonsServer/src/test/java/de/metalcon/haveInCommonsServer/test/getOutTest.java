package de.metalcon.haveInCommonsServer.test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;

public class getOutTest {
	
	final private ServletConfig servletConfig = mock(ServletConfig.class);
	final private ServletContext servletContext = mock(ServletContext.class);
	
	private HttpServletRequest request;
	private HttpServletResponse response;
	
	@Before
	public void init(){
		this.request = mock(HttpServletRequest.class);
		this.response = mock(HttpServletResponse.class);
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
	public void checkParameterExists(){
		// Query if all parameters are present
		assertTrue(request.getParameterMap().containsKey("uuid"));
	}
	
	@Test
	public void ifUUIDisValid(){
		fail("Not yet implemented!");
	}

}
