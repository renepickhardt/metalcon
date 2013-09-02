package de.metalcon.imageServer;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.InputStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;

import de.metalcon.imageServer.protocol.create.CreateRequest;
import de.metalcon.imageServer.protocol.create.CreateResponse;

public class TestCreateRequest {

	final private ServletConfig servletConfig = mock(ServletConfig.class);
	final private ServletContext servletContext = mock(ServletContext.class);

	private HttpServletRequest request;

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
	public void testCreateRequest() {
		CreateResponse testResponse = this.processCreateRequest(null, null,
				null, null);
		fail("Not yet implemented");
	}

	private CreateResponse processCreateRequest(final String imageIdentifier,
			final InputStream imageStream, final String metaData,
			final String autoRotate) {

		CreateResponse response = new CreateResponse();

		return CreateRequest.checkRequest(imageIdentifier, imageStream,
				metaData, autoRotate);
	}

	@Test
	public void testCreateRequestWithCropping() {
		fail("Not yet implemented");

	}

	@Test
	public void testCreateRequestPerURL() {
		fail("Not yet implemented");

	}

}
