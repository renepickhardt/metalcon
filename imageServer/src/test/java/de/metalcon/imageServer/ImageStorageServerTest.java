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
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import javax.imageio.ImageIO;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.Test;

import de.metalcon.imageServer.protocol.create.CreateResponse;
import de.metalcon.imageServer.protocol.read.ReadResponse;

public class ImageStorageServerTest {

	private static final JSONParser PARSER = new JSONParser();

	private static final String VALID_READ_IDENTIFIER = "img1";

	private static final String INVALID_READ_IDENTIFIER = "img0";

	private static final String VALID_CREATE_IDENTIFIER = "img2";

	private static final String INVALID_META_DATA = "{ author: Testy }";

	private static final String VALID_META_DATA = "{ \"author\": \"Testy\" }";

	private static final String VALID_IMAGE_PATH_JPEG = "/home/sebschlicht/mr_t_ba.jpg";

	private static InputStream VALID_IMAGE_STREAM_JPEG;

	private static final String VALID_IMAGE_PATH_PNG = "/home/sebschlicht/mr_t_ba.png";

	private static InputStream VALID_IMAGE_STREAM_PNG;

	private static final String VALID_IMAGE_URL = "http://mos.totalfilm.com/images/6/6-original-casting-ideas-for-the-a-team.jpg";

	private static final int VALID_LEFT = 100;

	private static final int VALID_TOP = 100;

	private static final int VALID_WIDTH = 200;

	private static final int VALID_HEIGHT = 200;

	private ImageStorageServer server;

	private CreateResponse createResponse;

	private ReadResponse readResponse;

	@Before
	public void setUp() throws Exception {
		this.server = new ImageStorageServer("test.iss.config");
		this.server.clear();

		VALID_IMAGE_STREAM_JPEG = new FileInputStream(VALID_IMAGE_PATH_JPEG);
		VALID_IMAGE_STREAM_PNG = new FileInputStream(VALID_IMAGE_PATH_PNG);

		this.createResponse = new CreateResponse();
		this.readResponse = new ReadResponse();

		assertTrue(this.server.createImage(VALID_READ_IDENTIFIER,
				new FileInputStream(VALID_IMAGE_PATH_JPEG), VALID_META_DATA,
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
	}

	@Test
	public void testCreateImageFromPNG() {
		assertTrue(this.server.createImage(VALID_CREATE_IDENTIFIER,
				VALID_IMAGE_STREAM_PNG, VALID_META_DATA, false,
				this.createResponse));
		assertFalse(this.server.createImage(VALID_CREATE_IDENTIFIER,
				VALID_IMAGE_STREAM_PNG, VALID_META_DATA, false,
				this.createResponse));
	}

	@Test
	public void testCreateImageNoMetaData() {
		assertTrue(this.server.createImage(VALID_CREATE_IDENTIFIER,
				VALID_IMAGE_STREAM_PNG, null, false, this.createResponse));
	}

	@Test
	public void testCreateImageInvalidMetaData() {
		assertFalse(this.server.createImage(VALID_CREATE_IDENTIFIER,
				VALID_IMAGE_STREAM_JPEG, INVALID_META_DATA, false,
				this.createResponse));
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
				VALID_IMAGE_STREAM_JPEG, VALID_META_DATA, VALID_LEFT,
				VALID_TOP, VALID_WIDTH, VALID_HEIGHT, this.createResponse));
		assertFalse(this.server.createImage(VALID_CREATE_IDENTIFIER,
				VALID_IMAGE_STREAM_JPEG, VALID_META_DATA, VALID_LEFT,
				VALID_TOP, VALID_WIDTH, VALID_HEIGHT, this.createResponse));
	}

	@Test
	public void testCreateImageFromUrl() {
		assertTrue(this.server.createImage(VALID_CREATE_IDENTIFIER,
				VALID_IMAGE_URL, this.createResponse));
	}

	@Test
	public void testReadOriginalImage() {
		final ImageData imageData = this.server.readImageWithMetaData(
				VALID_READ_IDENTIFIER, this.readResponse);
		assertNotNull(imageData);
		assertNotNull(imageData.getImageStream());
		compareJson(VALID_META_DATA, imageData.getMetaData());

		this.compareImages(VALID_IMAGE_PATH_JPEG, imageData.getImageStream());
	}

	@Test
	public void testReadOriginalImageInvalid() {
		assertNull(this.server.readImageWithMetaData(INVALID_READ_IDENTIFIER,
				this.readResponse));
	}

	@Test
	public void testReadImageScaling() {
		final InputStream imageStream = this.server.readImage(
				VALID_READ_IDENTIFIER, VALID_WIDTH, VALID_HEIGHT,
				this.readResponse);

		this.checkImageDimension(imageStream, VALID_WIDTH, VALID_HEIGHT);
	}

	@Test
	public void testReadScaledImage() {
		this.testReadImageScaling();
		this.testReadImageScaling();
	}

	@Test
	public void testReadImageScalingWithMetadata() {
		final ImageData imageData = this.server.readImageWithMetaData(
				VALID_READ_IDENTIFIER, VALID_WIDTH, VALID_HEIGHT,
				this.readResponse);
		assertNotNull(imageData);
		assertNotNull(imageData.getImageStream());
		compareJson(VALID_META_DATA, imageData.getMetaData());

		this.checkImageDimension(imageData.getImageStream(), VALID_WIDTH,
				VALID_HEIGHT);
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
	 * @param refImagePath
	 *            path to the reference file
	 * @param imageStream
	 *            stream of the image to be compared
	 * @throws IOException
	 *             if IO errors occurred
	 */
	private void compareImages(final String refImagePath,
			final InputStream imageStream) {
		try {
			final BufferedImage image1 = ImageIO.read(new File(refImagePath));
			final BufferedImage image2 = ImageIO.read(imageStream);

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
		JSONObject object1 = null;
		try {
			object1 = (JSONObject) PARSER.parse(json1);
		} catch (final ParseException e) {
			fail("first argument no valid JSON!");
		}

		JSONObject object2 = null;
		try {
			object2 = (JSONObject) PARSER.parse(json2);
		} catch (final ParseException e) {
			fail("second argument no valid JSON!");
		}

		assertEquals(object1, object2);
	}
}