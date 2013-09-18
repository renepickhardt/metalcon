package de.metalcon.imageServer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.metalcon.imageStorageServer.protocol.ProtocolConstants;
import de.metalcon.imageStorageServer.protocol.Response;
import de.metalcon.imageStorageServer.protocol.create.CreateRequest;
import de.metalcon.imageStorageServer.protocol.create.CreateResponse;
import de.metalcon.utils.FormItemList;

public class TestCreateRequest {

	final private ServletConfig servletConfig = mock(ServletConfig.class);
	final private ServletContext servletContext = mock(ServletContext.class);

	private CreateResponse createResponse;
	private JSONObject jsonResponse;
	private static FileItem imageFileItem;

	// maybe make global constants out of this variables?
	private final static String responseBeginMissing = "request incomplete: parameter \"";
	private final static String responseEndMissing = "\" is missing";

	@BeforeClass
	public static void LoadImage() throws FileNotFoundException {
		File image = new File("Metallica.jpg");

		if (image.length() > 0) {

			imageFileItem = new DiskFileItem("image", "image/JPEG", true,
					"file", 50000, null);
		} else {
			fail("no image to test with provided!");
		}
	}

	@Before
	public void initializeTest() {
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

	public void testCreateRequest() {
		this.processCreateRequest("validIdentifier", imageFileItem,
				ProtocolTestConstants.VALID_IMAGE_METADATA,
				ProtocolTestConstants.VALID_BOOLEAN_AUTOROTATE_TRUE);
		fail("Not yet implemented");
	}

	@Test
	public void testImageIdentifierMissing() {
		this.processCreateRequest(null, imageFileItem,
				ProtocolTestConstants.VALID_IMAGE_METADATA,
				ProtocolTestConstants.VALID_BOOLEAN_AUTOROTATE_TRUE);
		System.out.println(this.createResponse);
		assertEquals(this.responseBeginMissing
				+ ProtocolConstants.Parameters.Create.IMAGE_IDENTIFIER
				+ this.responseEndMissing,
				this.jsonResponse.get(ProtocolConstants.STATUS_MESSAGE)

		);
	}

	@Test
	public void testImageStreamMissing() {
		this.processCreateRequest("validIdentifier", null,
				ProtocolTestConstants.VALID_IMAGE_METADATA,
				ProtocolTestConstants.VALID_BOOLEAN_AUTOROTATE_TRUE);
		System.out.println(this.createResponse);
		assertEquals(this.responseBeginMissing
				+ ProtocolConstants.Parameters.Create.IMAGESTREAM
				+ this.responseEndMissing,

		this.jsonResponse.get(ProtocolConstants.STATUS_MESSAGE));
	}

	@Test
	public void testImageMetadataMissing() {
		this.processCreateRequest("validIdentifier", imageFileItem, null,
				ProtocolTestConstants.VALID_BOOLEAN_AUTOROTATE_TRUE);
		System.out.println(this.createResponse);
		assertEquals(this.responseBeginMissing
				+ ProtocolConstants.Parameters.Create.META_DATA
				+ this.responseEndMissing,

		this.jsonResponse.get(ProtocolConstants.STATUS_MESSAGE));
	}

	@Test
	public void testImageMetadataNotJson() {
		this.processCreateRequest("validIdentifier", imageFileItem,
				ProtocolTestConstants.INVALID_IMAGE_METADATA,
				ProtocolTestConstants.VALID_BOOLEAN_AUTOROTATE_TRUE);

		assertEquals(
				ProtocolConstants.StatusMessage.Create.META_DATA_MALFORMED,
				this.jsonResponse.get(ProtocolConstants.STATUS_MESSAGE));
	}

	@Test
	public void testImageAutorotateFlagMissing() {
		this.processCreateRequest("validIdentifier", imageFileItem,
				ProtocolTestConstants.VALID_IMAGE_METADATA, null);
		System.out.println(this.createResponse);
		assertEquals(this.responseBeginMissing
				+ ProtocolConstants.Parameters.Create.AUTOROTATE_FLAG
				+ this.responseEndMissing,

		this.jsonResponse.get(ProtocolConstants.STATUS_MESSAGE));
	}

	@Test
	public void testImageAutorotateFlagMalformed() {
		this.processCreateRequest("validIdentifier", imageFileItem,
				ProtocolTestConstants.VALID_IMAGE_METADATA,
				ProtocolTestConstants.MALFORMED_BOOLEAN_AUTOROTATE);
		System.out.println(this.createResponse);
		assertEquals(ProtocolConstants.Parameters.Create.AUTOROTATE_FLAG,
				this.jsonResponse.get(ProtocolConstants.STATUS_MESSAGE));

	}

	private void processCreateRequest(final String imageIdentifier,
			final FileItem imageStream, final String metaData,
			final String autoRotate) {
		FormItemList formItemList = new FormItemList();
		this.createResponse = new CreateResponse();

		if (imageIdentifier != null) {
			formItemList.addField(
					ProtocolConstants.Parameters.Create.IMAGE_IDENTIFIER,
					imageIdentifier);
		}

		if (imageStream != null) {
			formItemList.addFile(
					ProtocolConstants.Parameters.Create.IMAGESTREAM,
					imageStream);
		}

		if (metaData != null) {
			formItemList.addField(
					ProtocolConstants.Parameters.Create.META_DATA, metaData);
		}

		if (autoRotate != null) {
			formItemList.addField(
					ProtocolConstants.Parameters.Create.AUTOROTATE_FLAG,
					autoRotate);
		}
		CreateRequest.checkRequest(formItemList, this.createResponse);
		this.jsonResponse = extractJson(this.createResponse);
	}

	/**
	 * extract the JSON object from the response, failing the test if this is
	 * not possible
	 * 
	 * @param response
	 *            NSSP response
	 * @return JSON object in the response passed
	 */
	protected static JSONObject extractJson(final Response response) {
		try {
			final Field field = Response.class.getDeclaredField("json");
			field.setAccessible(true);
			return (JSONObject) field.get(response);
		} catch (final Exception e) {
			fail("failed to extract the JSON object from class Response");
			return null;
		}
	}

}