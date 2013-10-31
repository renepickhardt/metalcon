package de.metalcon.imageServer.protocol;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.metalcon.imageStorageServer.ISSConfig;
import de.metalcon.imageStorageServer.protocol.ProtocolConstants;
import de.metalcon.imageStorageServer.protocol.create.CreateResponse;
import de.metalcon.imageStorageServer.protocol.create.CreateWithCroppingRequest;
import de.metalcon.utils.FormItemList;

public class CreateWithCroppingRequestTest extends RequestTest {

	private static final String VALID_CROPPING_LEFT_COORDINATE = "10";
	private static final String VALID_CROPPING_TOP_COORDINATE = "15";
	private static final String VALID_CROPPING_WIDTH = "50";
	private static final String VALID_CROPPING_HEIGHT = "55";

	private static final String INVALID_CROPPING_LEFT_COORDINATE = "-10";
	private static final String INVALID_CROPPING_TOP_COORDINATE = "-10";
	private static final String INVALID_CROPPING_WIDTH = "-50";
	private static final String INVALID_CROPPING_HEIGHT = "-50";

	private static final String MALFORMED_CROPPING_VALUE = "NotANumber";

	private static final String CONFIG_PATH = "test.iss.config";

	private static File TEST_FILE_DIRECTORY, DISK_FILE_REPOSITORY;

	private static FileItem VALID_IMAGE_ITEM_JPEG;

	private static String VALID_CREATE_META_DATA;

	private CreateWithCroppingRequest createWithCroppingRequest;

	@BeforeClass
	public static void beforeClass() {
		final ISSConfig config = new ISSConfig(CONFIG_PATH);
		TEST_FILE_DIRECTORY = new File(config.getImageDirectory())
				.getParentFile();
		DISK_FILE_REPOSITORY = new File(config.getTemporaryDirectory());
	}

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws IOException {
		DISK_FILE_REPOSITORY.delete();
		DISK_FILE_REPOSITORY.mkdirs();

		// JPEG image item
		final File imageItemJpeg = new File(TEST_FILE_DIRECTORY, "jpeg.jpeg");
		VALID_IMAGE_ITEM_JPEG = createImageItem("image/jpeg", imageItemJpeg);
		assertEquals(imageItemJpeg.length(), VALID_IMAGE_ITEM_JPEG.getSize());

		// meta data
		final JSONObject metaDataCreate = new JSONObject();
		metaDataCreate.put("title", "My Great Picture");
		metaDataCreate.put("camera", "Testy BFC9000");
		metaDataCreate.put("resolution", "yes");
		metaDataCreate.put("license", "General More AllYouCanSee License");
		metaDataCreate.put("date", "1991-11-11");
		metaDataCreate.put("description", "Pixels in a frame!");
		VALID_CREATE_META_DATA = metaDataCreate.toJSONString();
	}

	@After
	public void tearDown() {
		DISK_FILE_REPOSITORY.delete();
	}

	@Test
	public void testCreateRequest() throws IOException {
		this.fillRequest(VALID_IDENTIFIER, VALID_IMAGE_ITEM_JPEG,
				VALID_CREATE_META_DATA, VALID_CROPPING_WIDTH,
				VALID_CROPPING_HEIGHT, VALID_CROPPING_LEFT_COORDINATE,
				VALID_CROPPING_TOP_COORDINATE);
		assertNotNull(this.createWithCroppingRequest);
		assertEquals(VALID_IDENTIFIER,
				this.createWithCroppingRequest.getImageIdentifier());
		assertTrue(compareInputStreams(VALID_IMAGE_ITEM_JPEG.getInputStream(),
				this.createWithCroppingRequest.getImageStream()));
		assertEquals(VALID_CREATE_META_DATA,
				this.createWithCroppingRequest.getMetaData());
		assertEquals(VALID_CROPPING_LEFT_COORDINATE,
				Integer.toString(this.createWithCroppingRequest
						.getCoordinateLeft()));
		assertEquals(VALID_CROPPING_TOP_COORDINATE,
				Integer.toString(this.createWithCroppingRequest
						.getCoordinateTop()));
		assertEquals(VALID_CROPPING_WIDTH,
				Integer.toString(this.createWithCroppingRequest.getWidth()));
		assertEquals(VALID_CROPPING_HEIGHT,
				Integer.toString(this.createWithCroppingRequest.getHeight()));
	}

	@Test
	public void testImageIdentifierMissing() {
		this.fillRequest(null, VALID_IMAGE_ITEM_JPEG,
				ProtocolTestConstants.VALID_IMAGE_METADATA,
				VALID_CROPPING_WIDTH, VALID_CROPPING_HEIGHT,
				VALID_CROPPING_LEFT_COORDINATE, VALID_CROPPING_TOP_COORDINATE);
		this.checkForMissingParameterMessage(ProtocolConstants.Parameters.Create.IMAGE_IDENTIFIER);
		assertNull(this.createWithCroppingRequest);
	}

	@Test
	public void testImageStreamMissing() {
		this.fillRequest(VALID_IDENTIFIER, null,
				ProtocolTestConstants.VALID_IMAGE_METADATA,
				VALID_CROPPING_WIDTH, VALID_CROPPING_HEIGHT,
				VALID_CROPPING_LEFT_COORDINATE, VALID_CROPPING_TOP_COORDINATE);
		this.checkForMissingParameterMessage(ProtocolConstants.Parameters.Create.IMAGE_STREAM);
		assertNull(this.createWithCroppingRequest);
	}

	@Test
	public void testImageMetadataMissing() {
		this.fillRequest(VALID_IDENTIFIER, VALID_IMAGE_ITEM_JPEG, null,
				VALID_CROPPING_WIDTH, VALID_CROPPING_HEIGHT,
				VALID_CROPPING_LEFT_COORDINATE, VALID_CROPPING_TOP_COORDINATE);
		assertNotNull(this.createWithCroppingRequest);
		assertNull(this.createWithCroppingRequest.getMetaData());
	}

	@Test
	public void testImageMetadataMalformed() {
		this.fillRequest(VALID_IDENTIFIER, VALID_IMAGE_ITEM_JPEG,
				ProtocolTestConstants.MALFORMED_IMAGE_METADATA,
				VALID_CROPPING_WIDTH, VALID_CROPPING_HEIGHT,
				VALID_CROPPING_LEFT_COORDINATE, VALID_CROPPING_TOP_COORDINATE);
		this.checkForStatusMessage(ProtocolConstants.StatusMessage.Create.META_DATA_MALFORMED);
		assertNull(this.createWithCroppingRequest);
	}

	@Test
	public void testCroppingLeftMissing() {
		this.fillRequest(VALID_IDENTIFIER, VALID_IMAGE_ITEM_JPEG,
				ProtocolTestConstants.VALID_IMAGE_METADATA,
				VALID_CROPPING_WIDTH, VALID_CROPPING_HEIGHT, null,
				VALID_CROPPING_TOP_COORDINATE);
		this.checkForMissingParameterMessage(ProtocolConstants.Parameters.Create.CROP_LEFT);
		assertNull(this.createWithCroppingRequest);
	}

	@Test
	public void testCroppingLeftMalformed() {
		this.fillRequest(VALID_IDENTIFIER, VALID_IMAGE_ITEM_JPEG,
				ProtocolTestConstants.VALID_IMAGE_METADATA,
				VALID_CROPPING_WIDTH, VALID_CROPPING_HEIGHT,
				MALFORMED_CROPPING_VALUE, VALID_CROPPING_TOP_COORDINATE);
		this.checkForStatusMessage(ProtocolConstants.StatusMessage.Create.CROP_LEFT_MALFORMED);
		assertNull(this.createWithCroppingRequest);
	}

	@Test
	public void testCroppingLeftInvalid() {
		this.fillRequest(VALID_IDENTIFIER, VALID_IMAGE_ITEM_JPEG,
				VALID_CREATE_META_DATA, VALID_CROPPING_WIDTH,
				VALID_CROPPING_HEIGHT, INVALID_CROPPING_LEFT_COORDINATE,
				VALID_CROPPING_TOP_COORDINATE);
		this.checkForStatusMessage(ProtocolConstants.StatusMessage.Create.CROP_LEFT_INVALID);
		assertNull(this.createWithCroppingRequest);
	}

	@Test
	public void testCroppingTopMissing() {
		this.fillRequest(VALID_IDENTIFIER, VALID_IMAGE_ITEM_JPEG,
				ProtocolTestConstants.VALID_IMAGE_METADATA,
				VALID_CROPPING_WIDTH, VALID_CROPPING_HEIGHT,
				VALID_CROPPING_LEFT_COORDINATE, null);
		this.checkForMissingParameterMessage(ProtocolConstants.Parameters.Create.CROP_TOP);
		assertNull(this.createWithCroppingRequest);
	}

	@Test
	public void testCroppingTopMalformed() {
		this.fillRequest(VALID_IDENTIFIER, VALID_IMAGE_ITEM_JPEG,
				ProtocolTestConstants.VALID_IMAGE_METADATA,
				VALID_CROPPING_WIDTH, VALID_CROPPING_HEIGHT,
				VALID_CROPPING_LEFT_COORDINATE, MALFORMED_CROPPING_VALUE);
		this.checkForStatusMessage(ProtocolConstants.StatusMessage.Create.CROP_TOP_MALFORMED);
		assertNull(this.createWithCroppingRequest);
	}

	@Test
	public void testCroppingTopInvalid() {
		this.fillRequest(VALID_IDENTIFIER, VALID_IMAGE_ITEM_JPEG,
				VALID_CREATE_META_DATA, VALID_CROPPING_WIDTH,
				VALID_CROPPING_HEIGHT, VALID_CROPPING_LEFT_COORDINATE,
				INVALID_CROPPING_TOP_COORDINATE);
		this.checkForStatusMessage(ProtocolConstants.StatusMessage.Create.CROP_TOP_INVALID);
		assertNull(this.createWithCroppingRequest);
	}

	@Test
	public void testCroppingWidthMissing() {
		this.fillRequest(VALID_IDENTIFIER, VALID_IMAGE_ITEM_JPEG,
				ProtocolTestConstants.VALID_IMAGE_METADATA, null,
				VALID_CROPPING_HEIGHT, VALID_CROPPING_LEFT_COORDINATE,
				VALID_CROPPING_TOP_COORDINATE);
		this.checkForMissingParameterMessage(ProtocolConstants.Parameters.Create.CROP_WIDTH);
		assertNull(this.createWithCroppingRequest);
	}

	@Test
	public void testCroppingWidthMalformed() {
		this.fillRequest(VALID_IDENTIFIER, VALID_IMAGE_ITEM_JPEG,
				ProtocolTestConstants.VALID_IMAGE_METADATA,
				MALFORMED_CROPPING_VALUE, VALID_CROPPING_HEIGHT,
				VALID_CROPPING_LEFT_COORDINATE, VALID_CROPPING_TOP_COORDINATE);
		this.checkForStatusMessage(ProtocolConstants.StatusMessage.Create.CROP_WIDTH_MALFORMED);
		assertNull(this.createWithCroppingRequest);
	}

	@Test
	public void testCroppingWidthInvalid() {
		this.fillRequest(VALID_IDENTIFIER, VALID_IMAGE_ITEM_JPEG,
				VALID_CREATE_META_DATA, INVALID_CROPPING_WIDTH,
				VALID_CROPPING_HEIGHT, VALID_CROPPING_LEFT_COORDINATE,
				VALID_CROPPING_TOP_COORDINATE);
		this.checkForStatusMessage(ProtocolConstants.StatusMessage.Create.CROP_WIDTH_INVALID);
		assertNull(this.createWithCroppingRequest);
	}

	@Test
	public void testCroppingHeightMissing() {
		this.fillRequest(VALID_IDENTIFIER, VALID_IMAGE_ITEM_JPEG,
				ProtocolTestConstants.VALID_IMAGE_METADATA,
				VALID_CROPPING_WIDTH, null, VALID_CROPPING_LEFT_COORDINATE,
				VALID_CROPPING_TOP_COORDINATE);
		this.checkForMissingParameterMessage(ProtocolConstants.Parameters.Create.CROP_HEIGHT);
		assertNull(this.createWithCroppingRequest);
	}

	@Test
	public void testCroppingHeightMalformed() {
		this.fillRequest(VALID_IDENTIFIER, VALID_IMAGE_ITEM_JPEG,
				ProtocolTestConstants.VALID_IMAGE_METADATA,
				VALID_CROPPING_WIDTH, MALFORMED_CROPPING_VALUE,
				VALID_CROPPING_LEFT_COORDINATE, VALID_CROPPING_TOP_COORDINATE);
		this.checkForStatusMessage(ProtocolConstants.StatusMessage.Create.CROP_HEIGHT_MALFORMED);
		assertNull(this.createWithCroppingRequest);
	}

	@Test
	public void testCroppingHeightInvalid() {
		this.fillRequest(VALID_IDENTIFIER, VALID_IMAGE_ITEM_JPEG,
				VALID_CREATE_META_DATA, VALID_CROPPING_WIDTH,
				INVALID_CROPPING_HEIGHT, VALID_CROPPING_LEFT_COORDINATE,
				VALID_CROPPING_TOP_COORDINATE);
		this.checkForStatusMessage(ProtocolConstants.StatusMessage.Create.CROP_HEIGHT_INVALID);
		assertNull(this.createWithCroppingRequest);
	}

	private void fillRequest(final String imageIdentifier,
			final FileItem imageItem, final String metaData,
			final String width, final String height, final String left,
			final String top) {
		// create and fill form item list
		final FormItemList formItemList = new FormItemList();

		if (imageIdentifier != null) {
			formItemList.addField(
					ProtocolConstants.Parameters.Create.IMAGE_IDENTIFIER,
					imageIdentifier);
		}

		if (imageItem != null) {
			formItemList.addFile(
					ProtocolConstants.Parameters.Create.IMAGE_ITEM, imageItem);
		}

		if (metaData != null) {
			formItemList.addField(
					ProtocolConstants.Parameters.Create.META_DATA, metaData);
		}

		if (left != null) {
			formItemList.addField(
					ProtocolConstants.Parameters.Create.CROP_LEFT, left);
		}

		if (top != null) {
			formItemList.addField(ProtocolConstants.Parameters.Create.CROP_TOP,
					top);
		}

		if (width != null) {
			formItemList.addField(
					ProtocolConstants.Parameters.Create.CROP_WIDTH, width);
		}

		if (height != null) {
			formItemList.addField(
					ProtocolConstants.Parameters.Create.CROP_HEIGHT, height);
		}

		// check request and extract the response
		final CreateResponse createResponse = new CreateResponse();
		this.createWithCroppingRequest = CreateWithCroppingRequest
				.checkRequest(formItemList, createResponse);
		this.extractJson(createResponse);
	}

	/**
	 * create an image item
	 * 
	 * @param contentType
	 *            content type of the music file
	 * @param musicFile
	 *            file handle to the music file
	 * @return music item representing the music file passed
	 */
	private static FileItem createImageItem(final String contentType,
			final File imageFile) {
		final FileItem imageItem = new DiskFileItem(
				ProtocolConstants.Parameters.Create.IMAGE_ITEM, contentType,
				false, imageFile.getName(), (int) imageFile.length(),
				DISK_FILE_REPOSITORY);

		// reason for call of getOutputStream: bug in commons.IO
		// called anyway to create file
		try {
			final OutputStream outputStream = imageItem.getOutputStream();
			final InputStream inputStream = new FileInputStream(imageFile);
			IOUtils.copy(inputStream, outputStream);
		} catch (final IOException e) {
			e.printStackTrace();
			fail("image item creation failed!");
		}

		return imageItem;
	}

	/**
	 * compare two input streams
	 * 
	 * @param stream1
	 *            first input stream
	 * @param stream2
	 *            second input stream
	 * @return true - if the two streams does contain the same content<br>
	 *         false - otherwise
	 * @throws IOException
	 *             if IO errors occurred
	 */
	private static boolean compareInputStreams(final InputStream stream1,
			final InputStream stream2) throws IOException {
		final ReadableByteChannel channel1 = Channels.newChannel(stream1);
		final ReadableByteChannel channel2 = Channels.newChannel(stream2);
		final ByteBuffer buffer1 = ByteBuffer.allocateDirect(4096);
		final ByteBuffer buffer2 = ByteBuffer.allocateDirect(4096);

		try {
			while (true) {

				int n1 = channel1.read(buffer1);
				int n2 = channel2.read(buffer2);

				if ((n1 == -1) || (n2 == -1)) {
					return n1 == n2;
				}

				buffer1.flip();
				buffer2.flip();

				for (int i = 0; i < Math.min(n1, n2); i++) {
					if (buffer1.get() != buffer2.get()) {
						return false;
					}
				}

				buffer1.compact();
				buffer2.compact();
			}

		} finally {
			if (stream1 != null) {
				stream1.close();
			}
			if (stream2 != null) {
				stream2.close();
			}
		}
	}
}