package de.metalcon.imageServer;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;

import de.metalcon.imageServer.protocol.ProtocolConstants;
import de.metalcon.imageServer.protocol.read.ReadResponse;
import de.metalcon.utils.FormItemList;

public class TestReadRequest {

	final private ServletConfig servletConfig = mock(ServletConfig.class);
	final private ServletContext servletContext = mock(ServletContext.class);

	private ReadResponse readResponse;
	private JSONObject jsonResponse;
	private static FileItem imageFileItem;
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
	public void testNoIdentifierGive() {
		// this.processReadRequest(imageIdentifier (null), OriginalFlag
		// (true||false))
		// ?(assert(no Image returned))
		// assert(status message given)
		this.processReadRequest(null,
				ProtocolConstants.Parameters.Read.IMAGE_IDENTIFIER);
		fail("Not yet implemented");
	}

	private void processReadRequest(String imageIdentifier, String originalFlag) {
		FormItemList formItemList = new FormItemList();
		this.readResponse = new ReadResponse();

		if (imageIdentifier != null) {
			formItemList.addField(
					ProtocolConstants.Parameters.Read.IMAGE_IDENTIFIER,
					imageIdentifier);
		}

	}

}
