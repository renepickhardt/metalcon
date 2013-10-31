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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import javax.imageio.ImageIO;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.metalcon.imageServer.protocol.RequestTest;
import de.metalcon.imageStorageServer.ImageData;
import de.metalcon.imageStorageServer.ImageFrame;
import de.metalcon.imageStorageServer.ImageStorageServer;
import de.metalcon.imageStorageServer.ScalingType;
import de.metalcon.imageStorageServer.protocol.ProtocolConstants;
import de.metalcon.imageStorageServer.protocol.create.CreateResponse;
import de.metalcon.imageStorageServer.protocol.delete.DeleteResponse;
import de.metalcon.imageStorageServer.protocol.read.ReadResponse;
import de.metalcon.imageStorageServer.protocol.update.UpdateResponse;

public class ImageStorageServerTest extends RequestTest {

	private static final JSONParser PARSER = new JSONParser();

	private static final String VALID_READ_IDENTIFIER = "img1";

	private static final String VALID_READ_IDENTIFIER2 = "img2";

	private static final String INVALID_READ_IDENTIFIER = "img0";

	private static final String VALID_CREATE_IDENTIFIER = "img3";

	private static final String INVALID_META_DATA = "{ author: Testy }";

	private static final String VALID_META_DATA = "{ \"author\": \"Testy\" }";

	private static final String VALID_IMAGE_PATH_JPEG = "/home/sebschlicht/mr_t_ba.jpg";

	private static InputStream VALID_IMAGE_STREAM_JPEG;

	private static final String VALID_IMAGE_PATH_PNG = "/home/sebschlicht/mr_t_ba.png";

	private static InputStream VALID_IMAGE_STREAM_PNG;

	private static final String VALID_IMAGE_URL = "http://mos.totalfilm.com/images/6/6-original-casting-ideas-for-the-a-team.jpg";

	private static final String INVALID_IMAGE_URL = "http://en.wikipedia.org/wiki/Sign_%28mathematics%29";

	private static final int VALID_CROPPING_LEFT = 100;

	private static final int VALID_CROPPING_TOP = 100;

	private static final int VALID_CROPPING_WIDTH = 200;

	private static final int VALID_CROPPING_HEIGHT = 200;

	private static final int INVALID_CROPPING_LEFT_TOO_LOW = -1;
	private static int INVALID_CROPPING_LEFT_TOO_HIGH;

	private static final int INVALID_CROPPING_TOP_TOO_LOW = -1;
	private static int INVALID_CROPPING_TOP_TOO_HIGH;

	private static final int INVALID_CROPPING_WIDTH_TOO_LOW = 0;
	private static int INVALID_CROPPING_WIDTH_TOO_HIGH;

	private static final int INVALID_CROPPING_HEIGHT_TOO_LOW = 0;
	private static int INVALID_CROPPING_HEIGHT_TOO_HIGH;

	private static final int VALID_READ_WIDTH = 200;

	private static final int VALID_READ_HEIGHT = 200;

	private static final int INVALID_READ_WIDTH_TOO_LARGE = 1000;

	private static final int INVALID_READ_HEIGHT_TOO_LARGE = 1000;

	private static final String VALID_UPDATE_META_DATA = "{ \"author\": \"Komissar Kugelblitz\" }";

	private static double IMAGE_WIDTH, IMAGE_HEIGHT;

	private ImageStorageServer server;

	private CreateResponse createResponse;

	private ReadResponse readResponse;

	private UpdateResponse updateResponse;

	private DeleteResponse deleteResponse;

	@BeforeClass
	public static void beforeClass() throws FileNotFoundException, IOException {
		BufferedImage image = ImageIO.read(new FileInputStream(
				VALID_IMAGE_PATH_JPEG));
		IMAGE_WIDTH = image.getWidth();
		IMAGE_HEIGHT = image.getHeight();

		INVALID_CROPPING_LEFT_TOO_HIGH = image.getWidth();
		INVALID_CROPPING_TOP_TOO_HIGH = image.getHeight();
		INVALID_CROPPING_WIDTH_TOO_HIGH = (image.getWidth() - VALID_CROPPING_LEFT) + 1;
		INVALID_CROPPING_HEIGHT_TOO_HIGH = (image.getHeight() - VALID_CROPPING_TOP) + 1;
	}

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

		this.extractJson(this.createResponse);
		this.checkForStatusMessage(ProtocolConstants.StatusMessage.Create.IMAGE_IDENTIFIER_IN_USE);
	}

	@Test
	public void testCreateImageFromPNG() {
		assertTrue(this.server.createImage(VALID_CREATE_IDENTIFIER,
				VALID_IMAGE_STREAM_PNG, VALID_META_DATA, false,
				this.createResponse));

		assertFalse(this.server.createImage(VALID_CREATE_IDENTIFIER,
				VALID_IMAGE_STREAM_PNG, VALID_META_DATA, false,
				this.createResponse));

		this.extractJson(this.createResponse);
		this.checkForStatusMessage(ProtocolConstants.StatusMessage.Create.IMAGE_IDENTIFIER_IN_USE);
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

		this.extractJson(this.createResponse);
		this.checkForStatusMessage(ProtocolConstants.StatusMessage.Create.META_DATA_MALFORMED);
	}

	@Test
	public void testCreateImageAutoRotation() {
		assertTrue(this.server.createImage(VALID_CREATE_IDENTIFIER,
				VALID_IMAGE_STREAM_JPEG, VALID_META_DATA, true,
				this.createResponse));
	}

	@Test
	public void testCreateCroppedImage() {
		assertTrue(this.server.createCroppedImage(VALID_CREATE_IDENTIFIER,
				VALID_IMAGE_STREAM_JPEG, VALID_META_DATA, new ImageFrame(
						VALID_CROPPING_LEFT, VALID_CROPPING_TOP,
						VALID_CROPPING_WIDTH, VALID_CROPPING_HEIGHT),
				this.createResponse));

		assertFalse(this.server.createCroppedImage(VALID_CREATE_IDENTIFIER,
				VALID_IMAGE_STREAM_JPEG, VALID_META_DATA, new ImageFrame(
						VALID_CROPPING_LEFT, VALID_CROPPING_TOP,
						VALID_CROPPING_WIDTH, VALID_CROPPING_HEIGHT),
				this.createResponse));

		this.extractJson(this.createResponse);
		this.checkForStatusMessage(ProtocolConstants.StatusMessage.Create.IMAGE_IDENTIFIER_IN_USE);
	}

	@Test
	public void testCreateCroppedImageLeftTooLow() {
		final ImageFrame frame = new ImageFrame(INVALID_CROPPING_LEFT_TOO_LOW,
				VALID_CROPPING_TOP, VALID_CROPPING_WIDTH, VALID_CROPPING_HEIGHT);

		assertFalse(this.server.createCroppedImage(VALID_CREATE_IDENTIFIER,
				VALID_IMAGE_STREAM_JPEG, VALID_META_DATA, frame,
				this.createResponse));

		this.extractJson(this.createResponse);
		this.checkForStatusMessage(ProtocolConstants.StatusMessage.Create.CROP_LEFT_INVALID);
	}

	@Test
	public void testCreateCroppedImageLeftTooHigh() {
		final ImageFrame frame = new ImageFrame(INVALID_CROPPING_LEFT_TOO_HIGH,
				VALID_CROPPING_TOP, VALID_CROPPING_WIDTH, VALID_CROPPING_HEIGHT);

		assertFalse(this.server.createCroppedImage(VALID_CREATE_IDENTIFIER,
				VALID_IMAGE_STREAM_JPEG, VALID_META_DATA, frame,
				this.createResponse));

		this.extractJson(this.createResponse);
		this.checkForStatusMessage(ProtocolConstants.StatusMessage.Create.CROP_LEFT_INVALID);
	}

	@Test
	public void testCreateCroppedImageTopTooLow() {
		final ImageFrame frame = new ImageFrame(VALID_CROPPING_LEFT,
				INVALID_CROPPING_TOP_TOO_LOW, VALID_CROPPING_WIDTH,
				VALID_CROPPING_HEIGHT);

		assertFalse(this.server.createCroppedImage(VALID_CREATE_IDENTIFIER,
				VALID_IMAGE_STREAM_JPEG, VALID_META_DATA, frame,
				this.createResponse));

		this.extractJson(this.createResponse);
		this.checkForStatusMessage(ProtocolConstants.StatusMessage.Create.CROP_TOP_INVALID);
	}

	@Test
	public void testCreateCroppedImageTopTooHigh() {
		final ImageFrame frame = new ImageFrame(VALID_CROPPING_LEFT,
				INVALID_CROPPING_TOP_TOO_HIGH, VALID_CROPPING_WIDTH,
				VALID_CROPPING_HEIGHT);

		assertFalse(this.server.createCroppedImage(VALID_CREATE_IDENTIFIER,
				VALID_IMAGE_STREAM_JPEG, VALID_META_DATA, frame,
				this.createResponse));

		this.extractJson(this.createResponse);
		this.checkForStatusMessage(ProtocolConstants.StatusMessage.Create.CROP_TOP_INVALID);
	}

	@Test
	public void testCreateCroppedImageWidthTooLow() {
		final ImageFrame frame = new ImageFrame(VALID_CROPPING_LEFT,
				VALID_CROPPING_TOP, INVALID_CROPPING_WIDTH_TOO_LOW,
				VALID_CROPPING_HEIGHT);

		assertFalse(this.server.createCroppedImage(VALID_CREATE_IDENTIFIER,
				VALID_IMAGE_STREAM_JPEG, VALID_META_DATA, frame,
				this.createResponse));

		this.extractJson(this.createResponse);
		this.checkForStatusMessage(ProtocolConstants.StatusMessage.Create.CROP_WIDTH_INVALID);
	}

	@Test
	public void testCreateCroppedImageWidthTooHigh() {
		final ImageFrame frame = new ImageFrame(VALID_CROPPING_LEFT,
				VALID_CROPPING_TOP, INVALID_CROPPING_WIDTH_TOO_HIGH,
				VALID_CROPPING_HEIGHT);

		assertFalse(this.server.createCroppedImage(VALID_CREATE_IDENTIFIER,
				VALID_IMAGE_STREAM_JPEG, VALID_META_DATA, frame,
				this.createResponse));

		this.extractJson(this.createResponse);
		this.checkForStatusMessage(ProtocolConstants.StatusMessage.Create.CROP_WIDTH_INVALID);
	}

	@Test
	public void testCreateCroppedImageHeightTooLow() {
		final ImageFrame frame = new ImageFrame(VALID_CROPPING_LEFT,
				VALID_CROPPING_TOP, VALID_CROPPING_WIDTH,
				INVALID_CROPPING_HEIGHT_TOO_LOW);

		assertFalse(this.server.createCroppedImage(VALID_CREATE_IDENTIFIER,
				VALID_IMAGE_STREAM_JPEG, VALID_META_DATA, frame,
				this.createResponse));

		this.extractJson(this.createResponse);
		this.checkForStatusMessage(ProtocolConstants.StatusMessage.Create.CROP_HEIGHT_INVALID);
	}

	@Test
	public void testCreateCroppedImageHeightTooHigh() {
		final ImageFrame frame = new ImageFrame(VALID_CROPPING_LEFT,
				VALID_CROPPING_TOP, VALID_CROPPING_WIDTH,
				INVALID_CROPPING_HEIGHT_TOO_HIGH);

		assertFalse(this.server.createCroppedImage(VALID_CREATE_IDENTIFIER,
				VALID_IMAGE_STREAM_JPEG, VALID_META_DATA, frame,
				this.createResponse));

		this.extractJson(this.createResponse);
		this.checkForStatusMessage(ProtocolConstants.StatusMessage.Create.CROP_HEIGHT_INVALID);
	}

	/**
	 * assert the creation of an image via URL to success
	 */
	@Test
	public void testCreateImageFromUrl() {
		assertTrue(this.server.createImageFromUrl(VALID_CREATE_IDENTIFIER,
				VALID_IMAGE_URL, VALID_META_DATA, this.createResponse));
	}

	@Test
	public void testCreateImageFromUrlInvalid() {
		assertFalse(this.server.createImageFromUrl(VALID_CREATE_IDENTIFIER,
				INVALID_IMAGE_URL, VALID_META_DATA, this.createResponse));

		this.extractJson(this.createResponse);
		this.checkForStatusMessage(ProtocolConstants.StatusMessage.Create.IMAGE_URL_INVALID);
	}

	/**
	 * assert the reading of the original image to return exactly the image/meta
	 * data used in the create request
	 * 
	 * @throws FileNotFoundException
	 */
	@Test
	public void testReadOriginalImage() throws FileNotFoundException {
		final ImageData imageData = this.server.readOriginalImage(
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

		final ImageData imageData = this.server.readOriginalImage(
				VALID_CREATE_IDENTIFIER, this.readResponse);
		assertNotNull(imageData);
		assertNotNull(imageData.getImageStream());

		compareJson(VALID_META_DATA, imageData.getMetaData());
		this.compareImages(new URL(VALID_IMAGE_URL).openStream(),
				imageData.getImageStream());
	}

	/**
	 * assert the reading to fail if the image identifier passed is invalid
	 */
	@Test
	public void testReadOriginalImageInvalid() {
		assertNull(this.server.readOriginalImage(INVALID_READ_IDENTIFIER,
				this.readResponse));

		this.extractJson(this.readResponse);
		this.checkForStatusMessage(ProtocolConstants.StatusMessage.Read.NO_IMAGE_FOUND);
	}

	/**
	 * assert the reading of a scaled image to return an image having the
	 * dimension passed and exactly the meta data used in the create request
	 */
	@Test
	public void testReadImageScalingFit() {
		// let the server create a scaled version
		ImageData imageData = this.server.readScaledImage(
				VALID_READ_IDENTIFIER, VALID_READ_WIDTH, VALID_READ_HEIGHT,
				ScalingType.FIT, this.readResponse);

		assertNotNull(imageData);
		assertNotNull(imageData.getImageStream());
		compareJson(VALID_META_DATA, imageData.getMetaData());

		this.checkImageDimension(imageData.getImageStream(), VALID_READ_WIDTH,
				VALID_READ_HEIGHT, ScalingType.FIT);

		// read the scaled version created before again
		imageData = this.server.readScaledImage(VALID_READ_IDENTIFIER,
				VALID_READ_WIDTH, VALID_READ_HEIGHT, null, this.readResponse);

		assertNotNull(imageData);
		assertNotNull(imageData.getImageStream());
		compareJson(VALID_META_DATA, imageData.getMetaData());

		this.checkImageDimension(imageData.getImageStream(), VALID_READ_WIDTH,
				VALID_READ_HEIGHT, ScalingType.FIT);
	}

	@Test
	public void testReadImageScalingWidth() {
		// let the server create a scaled version
		final ImageData imageData = this.server.readScaledImage(
				VALID_READ_IDENTIFIER, VALID_READ_WIDTH, VALID_READ_HEIGHT,
				ScalingType.WIDTH, this.readResponse);

		assertNotNull(imageData);
		assertNotNull(imageData.getImageStream());
		compareJson(VALID_META_DATA, imageData.getMetaData());

		this.checkImageDimension(imageData.getImageStream(), VALID_READ_WIDTH,
				0, ScalingType.WIDTH);
	}

	@Test
	public void testReadImageScalingHeight() {
		// let the server create a scaled version
		final ImageData imageData = this.server.readScaledImage(
				VALID_READ_IDENTIFIER, VALID_READ_WIDTH, VALID_READ_HEIGHT,
				ScalingType.HEIGHT, this.readResponse);

		assertNotNull(imageData);
		assertNotNull(imageData.getImageStream());
		compareJson(VALID_META_DATA, imageData.getMetaData());

		this.checkImageDimension(imageData.getImageStream(), 0,
				VALID_READ_HEIGHT, ScalingType.HEIGHT);
	}

	/**
	 * assert the reading to fail if the reading dimension passed is larger than
	 * the one of the basis image
	 */
	@Test
	public void testReadImageScalingTooLarge() {
		final ImageData imageData = this.server.readScaledImage(
				VALID_READ_IDENTIFIER, INVALID_READ_WIDTH_TOO_LARGE,
				INVALID_READ_HEIGHT_TOO_LARGE, null, this.readResponse);
		assertNotNull(imageData);

		this.extractJson(this.readResponse);
		this.checkForStatusMessage(ProtocolConstants.StatusMessage.Read.GEOMETRY_BIGGER_THAN_ORIGINAL);
	}

	@Test
	public void testAppendMetaData() {
		assertTrue(this.server.updateImageMetaData(VALID_READ_IDENTIFIER,
				VALID_UPDATE_META_DATA, this.updateResponse));
	}

	@Test
	public void testReadAppendedMetaData() {
		this.testAppendMetaData();

		final ImageData imageData = this.server.readImage(
				VALID_READ_IDENTIFIER, this.readResponse);
		assertNotNull(imageData);
		assertNotNull(imageData.getImageStream());

		compareJson(VALID_UPDATE_META_DATA, imageData.getMetaData());
	}

	@Test
	public void testAppendMetaDataInvalidIdentifier() {
		assertFalse(this.server.updateImageMetaData(INVALID_READ_IDENTIFIER,
				VALID_UPDATE_META_DATA, this.updateResponse));

		this.extractJson(this.updateResponse);
		this.checkForStatusMessage(ProtocolConstants.StatusMessage.Update.IMAGE_NOT_EXISTING);
	}

	@Test
	public void testAppendMetaDataInvalidMetaData() {
		assertFalse(this.server.updateImageMetaData(VALID_READ_IDENTIFIER,
				INVALID_META_DATA, this.updateResponse));

		this.extractJson(this.updateResponse);
		this.checkForStatusMessage(ProtocolConstants.StatusMessage.Update.META_DATA_MALFORMED);
	}

	@Test
	public void testDeleteImage() {
		assertTrue(this.server.deleteImage(VALID_READ_IDENTIFIER,
				this.deleteResponse));

		assertFalse(this.server.deleteImage(VALID_READ_IDENTIFIER,
				this.deleteResponse));

		this.extractJson(this.deleteResponse);
		this.checkForStatusMessage(ProtocolConstants.StatusMessage.Delete.IMAGE_NOT_EXISTING);
	}

	@Test
	public void testReadOriginalImageDeleted() {
		this.testDeleteImage();

		assertNull(this.server.readOriginalImage(VALID_READ_IDENTIFIER,
				this.readResponse));

		this.extractJson(this.readResponse);
		this.checkForStatusMessage(ProtocolConstants.StatusMessage.Read.NO_IMAGE_FOUND);
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
	 * @param scalingType
	 *            scaling type being checked
	 */
	private void checkImageDimension(final InputStream imageStream, int width,
			int height, final ScalingType scalingType) {
		try {
			final BufferedImage image = ImageIO.read(imageStream);

			if (scalingType == ScalingType.WIDTH) {
				height = (int) ((width * IMAGE_HEIGHT) / IMAGE_WIDTH);
			} else if (scalingType == ScalingType.HEIGHT) {
				width = (int) ((height * IMAGE_WIDTH) / IMAGE_HEIGHT);
			} else if (scalingType == ScalingType.FIT) {
				double ratioWidth = width / IMAGE_WIDTH;
				double ratioHeight = height / IMAGE_HEIGHT;

				if (ratioWidth <= ratioHeight) {
					height *= ratioWidth;
				} else {
					width *= ratioHeight;
				}
			}

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

}