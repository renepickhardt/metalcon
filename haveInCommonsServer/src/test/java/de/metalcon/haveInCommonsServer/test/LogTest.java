package de.metalcon.haveInCommonsServer.test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import org.junit.Test;

public class LogTest {
	
	final private ServletConfig servletConfig = mock(ServletConfig.class);
	final private ServletContext servletContext = mock(ServletContext.class);
	
	private HttpServletRequest request;
	
	public void init(){
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
	public void checkParameterExists(){
		
		// Query if all parameters are present
		assertTrue(request.getParameterMap().containsKey("uuid1") && 
				request.getParameterMap().containsKey("uuid2"));
	}
}
