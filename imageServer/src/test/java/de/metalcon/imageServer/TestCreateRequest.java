package de.metalcon.imageServer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.metalcon.imageServer.protocol.ProtocolConstants;
import de.metalcon.imageServer.protocol.create.CreateRequest;
import de.metalcon.imageServer.protocol.create.CreateResponse;

public class TestCreateRequest {

	final private ServletConfig servletConfig = mock(ServletConfig.class);
	final private ServletContext servletContext = mock(ServletContext.class);

	private CreateResponse createResponse;
	private JSONObject response;

	private HttpServletRequest request;

	@BeforeClass
	public static void LoadImage() throws FileNotFoundException {
		ProtocolTestConstants.VALID_IMAGESTREAM = new FileInputStream(
				"Metallica.jpg");
	}

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
		this.processCreateRequest("validIdentifier",
				ProtocolTestConstants.VALID_IMAGESTREAM,
				ProtocolTestConstants.VALID_IMAGE_METADATA,
				ProtocolTestConstants.VALID_BOOLEAN_AUTOROTATE_TRUE);
		fail("Not yet implemented");
	}

	@Test
	public void testImageIdentifierMissing() {
		this.processCreateRequest(null,
				ProtocolTestConstants.VALID_IMAGESTREAM,
				ProtocolTestConstants.VALID_IMAGE_METADATA,
				ProtocolTestConstants.VALID_BOOLEAN_AUTOROTATE_TRUE);
		assertEquals(ProtocolConstants.StatusMessage.Create.IMAGE_IDENTIFIER,
				this.response.get(ProtocolConstants.STATUS_MESSAGE));
	}

	private void processCreateRequest(final String imageIdentifier,
			final InputStream imageStream, final String metaData,
			final String autoRotate) {

		this.createResponse = new CreateResponse();

		CreateRequest.checkRequest(imageIdentifier, imageStream, metaData,
				autoRotate, this.createResponse);
		// this.response = extractJson(this.createResponse);
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
