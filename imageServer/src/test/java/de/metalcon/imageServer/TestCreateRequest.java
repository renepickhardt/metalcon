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
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.metalcon.imageServer.protocol.ProtocolConstants;
import de.metalcon.imageServer.protocol.Response;
import de.metalcon.imageServer.protocol.create.CreateResponse;
import de.metalcon.utils.FormItemList;

public class TestCreateRequest {

	final private ServletConfig servletConfig = mock(ServletConfig.class);
	final private ServletContext servletContext = mock(ServletContext.class);

	private static FileItem imageFileItem;
	private HttpServletRequest request;

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

	public void testCreateRequest() {
		this.processCreateRequest("validIdentifier", imageFileItem,
				ProtocolTestConstants.VALID_IMAGE_METADATA,
				ProtocolTestConstants.VALID_BOOLEAN_AUTOROTATE_TRUE);
		fail("Not yet implemented");
	}

	@Test
	public void testImageIdentifierMissing() {
		CreateResponse createResponse = this.processCreateRequest(null,
				imageFileItem, ProtocolTestConstants.VALID_IMAGE_METADATA,
				ProtocolTestConstants.VALID_BOOLEAN_AUTOROTATE_TRUE);
		if (createResponse.getResponse().containsKey(
				ProtocolConstants.STATUS_MESSAGE)) {
			assertEquals(
					ProtocolConstants.StatusMessage.Create.IMAGE_IDENTIFIER_MISSING,

					createResponse.getResponse().get(
							ProtocolConstants.STATUS_MESSAGE));
		} else {
			fail("Status missing");
		}
	}

	@Test
	public void testImageStreamMissing() {
		CreateResponse createResponse = this.processCreateRequest(
				"validIdentifier", null,
				ProtocolTestConstants.VALID_IMAGE_METADATA,
				ProtocolTestConstants.VALID_BOOLEAN_AUTOROTATE_TRUE);
		if (createResponse.getResponse().containsKey(
				ProtocolConstants.STATUS_MESSAGE)) {
			assertEquals(
					ProtocolConstants.StatusMessage.Create.IMAGESTREAM_MISSING,

					createResponse.getResponse().get(
							ProtocolConstants.STATUS_MESSAGE));
		} else {
			fail("Status missing");
		}
	}

	@Test
	public void testImageMetadataMissing() {
		CreateResponse createResponse = this.processCreateRequest(
				"validIdentifier", imageFileItem, null,
				ProtocolTestConstants.VALID_BOOLEAN_AUTOROTATE_TRUE);
		if (createResponse.getResponse().containsKey(
				ProtocolConstants.STATUS_MESSAGE)) {
			assertEquals(
					ProtocolConstants.StatusMessage.Create.IMAGE_METADATA_MISSING,

					createResponse.getResponse().get(
							ProtocolConstants.STATUS_MESSAGE));
		} else {
			fail("Status missing");
		}
	}

	@Test
	public void testImageAutorotateFlagMissing() {
		CreateResponse createResponse = this.processCreateRequest(
				"validIdentifier", imageFileItem,
				ProtocolTestConstants.VALID_IMAGE_METADATA, null);
		if (createResponse.getResponse().containsKey(
				ProtocolConstants.STATUS_MESSAGE)) {
			assertEquals(
					ProtocolConstants.StatusMessage.Create.IMAGE_METADATA_MISSING,

					createResponse.getResponse().get(
							ProtocolConstants.STATUS_MESSAGE));
		} else {
			fail("Status missing");
		}
	}

	@Test
	public void testImageAutorotateFlagMalformed() {
		CreateResponse createResponse = this.processCreateRequest(
				"validIdentifier", imageFileItem,
				ProtocolTestConstants.VALID_IMAGE_METADATA,
				ProtocolTestConstants.INVALID_BOOLEAN_AUTOROTATE);
		if (createResponse.getResponse().containsKey(
				ProtocolConstants.STATUS_MESSAGE)) {
			assertEquals(
					ProtocolConstants.StatusMessage.Create.AUTOROTATE_FLAG_MALFORMED,

					createResponse.getResponse().get(
							ProtocolConstants.STATUS_MESSAGE));
		} else {
			fail("Status missing");
		}
	}

	private CreateResponse processCreateRequest(final String imageIdentifier,
			final FileItem imageStream, final String metaData,
			final String autoRotate) {
		FormItemList formItemList = new FormItemList();
		CreateResponse createResponse = new CreateResponse(
				this.servletConfig.getServletContext());

		System.out.println(imageIdentifier);

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

		return createResponse;
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
