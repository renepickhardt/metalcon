package de.metalcon.imageServer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.zip.ZipInputStream;

import javax.imageio.ImageIO;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.Test;

import de.metalcon.imageStorageServer.ImageData;
import de.metalcon.imageStorageServer.ImageStorageServer;
import de.metalcon.imageStorageServer.protocol.ProtocolConstants;
import de.metalcon.imageStorageServer.protocol.Response;
import de.metalcon.imageStorageServer.protocol.create.CreateResponse;
import de.metalcon.imageStorageServer.protocol.delete.DeleteResponse;
import de.metalcon.imageStorageServer.protocol.read.ReadResponse;
import de.metalcon.imageStorageServer.protocol.update.UpdateResponse;

public class ImageStorageServerTest {

	private static final JSONParser PARSER = new JSONParser();

	private static final String VALID_READ_IDENTIFIER = "img1";

	private static final String VALID_READ_IDENTIFIER2 = "img2";

	private static final String INVALID_READ_IDENTIFIER = "img0";

	private static final String[] VALID_READ_IDENTIFIERS = {
			VALID_READ_IDENTIFIER, VALID_READ_IDENTIFIER2 };

	private static final String VALID_CREATE_IDENTIFIER = "img3";

	private static final String INVALID_META_DATA = "{ author: Testy }";

	private static final String VALID_META_DATA = "{ \"author\": \"Testy\" }";

	private static final String VALID_IMAGE_PATH_JPEG = "/home/sebschlicht/mr_t_ba.jpg";

	private static InputStream VALID_IMAGE_STREAM_JPEG;

	private static final String VALID_IMAGE_PATH_PNG = "/home/sebschlicht/mr_t_ba.png";

	private static InputStream VALID_IMAGE_STREAM_PNG;

	private static final String VALID_IMAGE_URL = "http://mos.totalfilm.com/images/6/6-original-casting-ideas-for-the-a-team.jpg";

	private static final int VALID_CROPPING_LEFT = 100;

	private static final int VALID_CROPPING_TOP = 100;

	private static final int VALID_CROPPING_WIDTH = 200;

	private static final int VALID_CROPPING_HEIGHT = 200;

	private static final int VALID_READ_WIDTH = 200;

	private static final int VALID_READ_HEIGHT = 200;

	private static final int INVALID_READ_WIDTH_TOO_LARGE = 1000;

	private static final int INVALID_READ_HEIGHT_TOO_LARGE = 1000;

	private static final String VALID_UPDATE_META_DATA = "{ \"author\": \"Komissar Kugelblitz\" }";

	private ImageStorageServer server;

	private JSONObject jsonResponse;

	private CreateResponse createResponse;

	private ReadResponse readResponse;

	private UpdateResponse updateResponse;

	private DeleteResponse deleteResponse;

	@Before
	public void setUp() throws Exception {
		this.server = new ImageStorageServer("test.iss.config");
		this.server.clear();

		VALID_IMAGE_STREAM_JPEG = new FileInputStream(VALID_IMAGE_PATH_JPEG);
		VALID_IMAGE_STREAM_PNG = new FileInputStream(VALID_IMAGE_PATH_PNG);

		this.createResponse = new CreateResponse();
		this.readResponse = new ReadResponse();
		this.updateResponse = new UpdateResponse();
		this.deleteResponse = new DeleteResponse();

		assertTrue(this.server.createImage(VALID_READ_IDENTIFIER,
				new FileInputStream(VALID_IMAGE_PATH_JPEG), VALID_META_DATA,
				false, this.createResponse));
		assertTrue(this.server.createImage(VALID_READ_IDENTIFIER2,
				new FileInputStream(VALID_IMAGE_PATH_PNG), VALID_META_DATA,
				false, this.createResponse));
	}

	@Test
	public void testCreateImageFromJPEG() {
		assertTrue(this.server.createImage(VALID_CREATE_IDENTIFIER,
				VALID_IMAGE_STREAM_JPEG, VALID_META_DATA, false,
				this.createResponse));

		assertFalse(this.server.createImage(VALID_CREATE_IDENTIFIER,
				VALID_IMAGE_STREAM_JPEG, VALID_META_DATA, false,
				this.createResponse));
		this.jsonResponse = extractJson(this.createResponse);
		assertEquals(
				ProtocolConstants.StatusMessage.Create.IMAGE_IDENTIFIER_IN_USE,
				this.jsonResponse.get(ProtocolConstants.STATUS_MESSAGE));
	}

	@Test
	public void testCreateImageFromPNG() {
		assertTrue(this.server.createImage(VALID_CREATE_IDENTIFIER,
				VALID_IMAGE_STREAM_PNG, VALID_META_DATA, false,
				this.createResponse));

		assertFalse(this.server.createImage(VALID_CREATE_IDENTIFIER,
				VALID_IMAGE_STREAM_PNG, VALID_META_DATA, false,
				this.createResponse));
		this.jsonResponse = extractJson(this.createResponse);
		assertEquals(
				ProtocolConstants.StatusMessage.Create.IMAGE_IDENTIFIER_IN_USE,
				this.jsonResponse.get(ProtocolConstants.STATUS_MESSAGE));
	}

	@Test
	public void testCreateImageWithoutMetaData() {
		assertTrue(this.server.createImage(VALID_CREATE_IDENTIFIER,
				VALID_IMAGE_STREAM_PNG, null, false, this.createResponse));
	}

	@Test
	public void testCreateImageInvalidMetaData() {
		assertFalse(this.server.createImage(VALID_CREATE_IDENTIFIER,
				VALID_IMAGE_STREAM_JPEG, INVALID_META_DATA, false,
				this.createResponse));
		this.jsonResponse = extractJson(this.createResponse);
		assertEquals(
				ProtocolConstants.StatusMessage.Create.META_DATA_MALFORMED,
				this.jsonResponse.get(ProtocolConstants.STATUS_MESSAGE));
	}

	@Test
	public void testCreateImageAutoRotation() {
		assertTrue(this.server.createImage(VALID_CREATE_IDENTIFIER,
				VALID_IMAGE_STREAM_JPEG, VALID_META_DATA, true,
				this.createResponse));
	}

	@Test
	public void testCreateImageCropping() {
		assertTrue(this.server.createImage(VALID_CREATE_IDENTIFIER,
				VALID_IMAGE_STREAM_JPEG, VALID_META_DATA, VALID_CROPPING_LEFT,
				VALID_CROPPING_TOP, VALID_CROPPING_WIDTH,
				VALID_CROPPING_HEIGHT, this.createResponse));
		assertFalse(this.server.createImage(VALID_CREATE_IDENTIFIER,
				VALID_IMAGE_STREAM_JPEG, VALID_META_DATA, VALID_CROPPING_LEFT,
				VALID_CROPPING_TOP, VALID_CROPPING_WIDTH,
				VALID_CROPPING_HEIGHT, this.createResponse));
	}

	/**
	 * assert the creation of an image via URL to success
	 */
	@Test
	public void testCreateImageFromUrl() {
		assertTrue(this.server.createImage(VALID_CREATE_IDENTIFIER,
				VALID_IMAGE_URL, this.createResponse));
	}

	/**
	 * assert the reading of the original image to return exactly the image/meta
	 * data used in the create request
	 * 
	 * @throws FileNotFoundException
	 */
	@Test
	public void testReadOriginalImage() throws FileNotFoundException {
		final ImageData imageData = this.server.readImageWithMetaData(
				VALID_READ_IDENTIFIER, this.readResponse);
		assertNotNull(imageData);
		assertNotNull(imageData.getImageStream());

		compareJson(VALID_META_DATA, imageData.getMetaData());
		this.compareImages(
				new FileInputStream(new File(VALID_IMAGE_PATH_JPEG)),
				imageData.getImageStream());
	}

	/**
	 * assert the reading of the original image created via URL to return
	 * exactly the image and (empty) meta data used in the create request
	 * 
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	@Test
	public void testReadOriginalImageCreatedFromUrl()
			throws MalformedURLException, IOException {
		this.testCreateImageFromUrl();

		final ImageData imageData = this.server.readImageWithMetaData(
				VALID_CREATE_IDENTIFIER, this.readResponse);
		assertNotNull(imageData);
		assertNotNull(imageData.getImageStream());

		final String metaData = imageData.getMetaData();
		final JSONObject metaDataJson = parseToJson(metaData);
		assertTrue(metaDataJson.isEmpty());

		this.compareImages(new URL(VALID_IMAGE_URL).openStream(),
				imageData.getImageStream());
	}

	/**
	 * assert the reading to fail if the image identifier passed is invalid
	 */
	@Test
	public void testReadOriginalImageInvalid() {
		assertNull(this.server.readImageWithMetaData(INVALID_READ_IDENTIFIER,
				this.readResponse));

		// check for status message
		this.jsonResponse = extractJson(this.readResponse);
		assertEquals(ProtocolConstants.StatusMessage.Read.NO_IMAGE_FOUND,
				this.jsonResponse.get(ProtocolConstants.STATUS_MESSAGE));
	}

	/**
	 * assert the multiple reading of a scaled image to success and to return an
	 * image having the dimension passed
	 */
	@Test
	public void testReadImageScaling() {
		// let the server create a scaled version
		InputStream imageStream = this.server.readImage(VALID_READ_IDENTIFIER,
				VALID_READ_WIDTH, VALID_READ_HEIGHT, this.readResponse);

		assertNotNull(imageStream);
		this.checkImageDimension(imageStream, VALID_READ_WIDTH,
				VALID_READ_HEIGHT);

		// read the scaled version created before again
		imageStream = this.server.readImage(VALID_READ_IDENTIFIER,
				VALID_READ_WIDTH, VALID_READ_HEIGHT, this.readResponse);

		assertNotNull(imageStream);
		this.checkImageDimension(imageStream, VALID_READ_WIDTH,
				VALID_READ_HEIGHT);
	}

	/**
	 * assert the reading to fail if the reading dimension passed is larger than
	 * the one of the basis image
	 */
	@Test
	public void testReadImageScalingTooLarge() {
		final InputStream imageStream = this.server.readImage(
				VALID_READ_IDENTIFIER, INVALID_READ_WIDTH_TOO_LARGE,
				INVALID_READ_HEIGHT_TOO_LARGE, this.readResponse);
		assertNull(imageStream);

		// check for status message
		this.jsonResponse = extractJson(this.readResponse);
		assertEquals(
				ProtocolConstants.StatusMessage.Read.GEOMETRY_BIGGER_THAN_ORIGINAL,
				this.jsonResponse.get(ProtocolConstants.STATUS_MESSAGE));
	}

	/**
	 * assert the reading of a scaled image to return an image having the
	 * dimension passed and exactly the meta data used in the create request
	 */
	@Test
	public void testReadImageScalingWithMetadata() {
		final ImageData imageData = this.server.readImageWithMetaData(
				VALID_READ_IDENTIFIER, VALID_READ_WIDTH, VALID_READ_HEIGHT,
				this.readResponse);
		assertNotNull(imageData);
		assertNotNull(imageData.getImageStream());
		compareJson(VALID_META_DATA, imageData.getMetaData());

		this.checkImageDimension(imageData.getImageStream(), VALID_READ_WIDTH,
				VALID_READ_HEIGHT);
	}

	@Test
	public void testReadImages() throws IOException {
		// let the server create the scaled versions
		InputStream inputStream = this.server.readImages(
				VALID_READ_IDENTIFIERS, VALID_READ_WIDTH, VALID_READ_WIDTH,
				this.readResponse);
		assertNotNull(inputStream);

		ZipInputStream archiveStream = new ZipInputStream(inputStream);

		int numEntries = 0;
		while (archiveStream.getNextEntry() != null) {
			this.checkImageDimension(archiveStream, VALID_READ_WIDTH,
					VALID_READ_HEIGHT);

			numEntries += 1;
		}
		assertEquals(VALID_READ_IDENTIFIERS.length, numEntries);

		// read the scaled versions created before
		inputStream = this.server.readImages(VALID_READ_IDENTIFIERS,
				VALID_READ_WIDTH, VALID_READ_WIDTH, this.readResponse);
		assertNotNull(inputStream);

		archiveStream = new ZipInputStream(inputStream);

		numEntries = 0;
		while (archiveStream.getNextEntry() != null) {
			this.checkImageDimension(archiveStream, VALID_READ_WIDTH,
					VALID_READ_HEIGHT);

			numEntries += 1;
		}
		assertEquals(VALID_READ_IDENTIFIERS.length, numEntries);
	}

	@Test
	public void testAppendMetaData() {
		assertTrue(this.server.appendImageMetaData(VALID_READ_IDENTIFIER,
				VALID_UPDATE_META_DATA, this.updateResponse));
	}

	@Test
	public void testReadAppendedMetaData() {
		this.testAppendMetaData();

		final ImageData imageData = this.server.readImageWithMetaData(
				VALID_READ_IDENTIFIER, this.readResponse);
		assertNotNull(imageData);
		assertNotNull(imageData.getImageStream());

		compareJson(VALID_UPDATE_META_DATA, imageData.getMetaData());
	}

	@Test
	public void testAppendMetaDataInvalidIdentifier() {
		assertFalse(this.server.appendImageMetaData(INVALID_READ_IDENTIFIER,
				VALID_UPDATE_META_DATA, this.updateResponse));
		this.jsonResponse = extractJson(this.updateResponse);
		assertEquals(ProtocolConstants.StatusMessage.Update.IMAGE_NOT_EXISTING,
				this.jsonResponse.get(ProtocolConstants.STATUS_MESSAGE));
	}

	@Test
	public void testAppendMetaDataInvalidMetaData() {
		assertFalse(this.server.appendImageMetaData(VALID_READ_IDENTIFIER,
				INVALID_META_DATA, this.updateResponse));
	}

	@Test
	public void testDeleteImage() {
		assertTrue(this.server.deleteImage(VALID_READ_IDENTIFIER,
				this.deleteResponse));

		assertFalse(this.server.deleteImage(VALID_READ_IDENTIFIER,
				this.deleteResponse));
		this.jsonResponse = extractJson(this.deleteResponse);
		assertEquals(ProtocolConstants.StatusMessage.Delete.IMAGE_NOT_EXISTING,
				this.jsonResponse.get(ProtocolConstants.STATUS_MESSAGE));
	}

	@Test
	public void testReadOriginalImageDeleted() {
		this.testDeleteImage();

		assertNull(this.server.readImageWithMetaData(VALID_READ_IDENTIFIER,
				this.readResponse));
	}

	/**
	 * check the dimension of an image
	 * 
	 * @param imageStream
	 *            stream of the image being checked
	 * @param width
	 *            expected width
	 * @param height
	 *            expected height
	 */
	private void checkImageDimension(final InputStream imageStream,
			final int width, final int height) {
		try {
			final BufferedImage image = ImageIO.read(imageStream);

			assertEquals(width, image.getWidth());
			assertEquals(height, image.getHeight());
		} catch (final IOException e) {
			fail("IO exception occurred!");
		}
	}

	/**
	 * compare two images
	 * 
	 * @param imageStream1
	 *            stream of the reference image
	 * @param imageStream2
	 *            stream of the image to be compared
	 * @throws IOException
	 *             if IO errors occurred
	 */
	private void compareImages(final InputStream imageStream1,
			final InputStream imageStream2) {
		try {
			final BufferedImage image1 = ImageIO.read(imageStream1);
			final BufferedImage image2 = ImageIO.read(imageStream2);

			assertEquals(image1.getWidth(), image2.getWidth());
			assertEquals(image1.getHeight(), image2.getHeight());

			final DataBufferByte buffer1 = (DataBufferByte) image1.getRaster()
					.getDataBuffer();
			final DataBufferByte buffer2 = (DataBufferByte) image2.getRaster()
					.getDataBuffer();
			assertEquals(buffer1.getNumBanks(), buffer2.getNumBanks());

			for (int bank = 0; bank < buffer1.getNumBanks(); bank++) {
				assertTrue(Arrays.equals(buffer1.getData(bank),
						buffer2.getData(bank)));
			}
		} catch (final IOException e) {
			fail("IO exception occurred!");
		}
	}

	/**
	 * compare two JSON strings at JSON level (ignoring spaces aso.)
	 * 
	 * @param json1
	 *            first JSON string
	 * @param json2
	 *            second JSON string
	 */
	private static void compareJson(final String json1, final String json2) {
		final JSONObject object1 = parseToJson(json1);
		final JSONObject object2 = parseToJson(json2);

		assertEquals(object1, object2);
	}

	/**
	 * parse a String to a JSON object<br>
	 * <b>fails the test</b> if the parsing failed
	 * 
	 * @param value
	 *            String to be parsed
	 * @return JSON object represented by the String passed<br>
	 *         <b>null</b> if the parsing failed
	 */
	private static JSONObject parseToJson(final String value) {
		try {
			return (JSONObject) PARSER.parse(value);
		} catch (final ParseException e) {
			fail("failed to parse to JSON object!");
		}

		return null;
	}

	/**
	 * extract the JSON object from the response, failing the test if this is
	 * not possible
	 * 
	 * @param response
	 *            NSSP response
	 * @return JSON object in the response passed
	 */
	private static JSONObject extractJson(final Response response) {
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