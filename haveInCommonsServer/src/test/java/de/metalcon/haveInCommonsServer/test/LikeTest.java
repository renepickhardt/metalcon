package de.metalcon.haveInCommonsServer.test;

import static org.junit.Assert.*;
import static org.junit.Assert.fail;

import de.metalcon.HaveInCommonsServer.Vote;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

public class LikeTest {

	final private ServletConfig servletConfig = mock(ServletConfig.class);
	final private ServletContext servletContext = mock(ServletContext.class);
	
	private HttpServletRequest request;
	
	@Before
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
				request.getParameterMap().containsKey("uuid2") &&
				request.getParameterMap().containsKey("vote"));
	}
	
	@Test
	public void isVoteValid(){
		
		// Only one vote parameter is assumed to be present -> [0]
		boolean valid = false;
		if(request.getParameterMap().size() > 0){
			valid = request.getParameterMap().get("vote")[0] == Vote.UP.getString();
			if(!valid)
				valid &= request.getParameterMap().get("vote")[0] == Vote.DOWN.getString();
			if(!valid)
				valid &= request.getParameterMap().get("vote")[0] == Vote.NONE.getString();
		}
		assertTrue(valid);
	}
	
	@Test
	public void isUUIDvalid(){
		//("Not yet ")
	}
	
	@Test
	public void isJSONValid(){
		
	}
	
	@Test
	public void test() {
		fail("Not yet implemented");
	}

}
