package de.metalcon.haveInCommonsServer.test;

import static org.junit.Assert.*;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import de.metalcon.HaveInCommonsServer.Vote;
import de.metalcon.HaveInCommonsServer.Like;
import de.metalcon.HaveInCommonsServer.helper.ProtocolConstants;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

public class LikeTest {

	final private ServletConfig servletConfig = mock(ServletConfig.class);
	final private ServletContext servletContext = mock(ServletContext.class);
	
	private HttpServletRequest request;
	private HttpServletResponse response;
	private Like likeServlet;
	
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
	public void validLikeRequest() throws IOException, ServletException{
		
		StringWriter sw = new StringWriter();
		PrintWriter pw  =new PrintWriter(sw);
				
		likeServlet = new Like();
		when(request.getParameter("muid1")).thenReturn("1234143123");
		when(request.getParameter("muid2")).thenReturn("1234143124");
		when(request.getParameter("vote")).thenReturn(Vote.UP.getString());
		when(response.getWriter()).thenReturn(pw);
		
		likeServlet.doPost(request, response);
		String result = sw.getBuffer().toString().trim();
				
		TestCase.assertEquals(result, ProtocolConstants.REQUEST_VALID);
	}
	
	@Test
	public void invalidMUID() throws ServletException, IOException{
		StringWriter sw = new StringWriter();
		PrintWriter pw  =new PrintWriter(sw);

		when(request.getParameter("muid1")).thenReturn("-1");
		when(request.getParameter("muid2")).thenReturn("1234143124");
		when(request.getParameter("vote")).thenReturn(Vote.UP.getString());
		when(response.getWriter()).thenReturn(pw);
		
		likeServlet.doPost(request, response);
		String result = sw.getBuffer().toString().trim();
				
		TestCase.assertEquals(result, ProtocolConstants.MUID_MALFORMED);
		
		result = null;
		sw.getBuffer().setLength(0);
				
		when(request.getParameter("muid1")).thenReturn("1234143124");
		when(request.getParameter("muid2")).thenReturn("-1");
				
		likeServlet.doPost(request, response);
		result = sw.getBuffer().toString().trim();
		
		TestCase.assertEquals(result, ProtocolConstants.MUID_MALFORMED);
	}
	
	@Test
	public void invalidVote() throws IOException, ServletException{
		StringWriter sw = new StringWriter();
		PrintWriter pw  =new PrintWriter(sw);

		when(request.getParameter("muid1")).thenReturn("1235234223");
		when(request.getParameter("muid2")).thenReturn("1234143124");
		when(request.getParameter("vote")).thenReturn("foobar");
		when(response.getWriter()).thenReturn(pw);
		
		likeServlet.doPost(request, response);
		String result = sw.getBuffer().toString().trim();
				
		TestCase.assertEquals(result, ProtocolConstants.VOTE_MALFORMED);
		result = null;
		sw.getBuffer().setLength(0);
		
		//*********missing vote*********//
		
		when(request.getParameter("muid1")).thenReturn("1235234223");
		when(request.getParameter("muid2")).thenReturn("1234143124");
		when(request.getParameter("vote")).thenReturn("");
		when(response.getWriter()).thenReturn(pw);
		
		likeServlet.doPost(request, response);
		result = sw.getBuffer().toString().trim();				
		TestCase.assertEquals(result, ProtocolConstants.VOTE_NOT_GIVEN);
	}
	
	@Test
	public void test() {
		
	}

}
