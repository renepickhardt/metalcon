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

import de.metalcon.imageServer.ProtocolTestConstants;
import de.metalcon.imageStorageServer.ISSConfig;
import de.metalcon.imageStorageServer.protocol.ProtocolConstants;
import de.metalcon.imageStorageServer.protocol.create.CreateResponse;
import de.metalcon.imageStorageServer.protocol.create.CreateWithCroppingRequest;
import de.metalcon.utils.FormItemList;

public class CreateWithCroppingRequestTest extends RequestTest {

	private CreateWithCroppingRequest createWithCroppingRequest;
	private static final String CONFIG_PATH = "test.iss.config";

	private static File TEST_FILE_DIRECTORY, DISK_FILE_REPOSITORY;

	private static FileItem VALID_IMAGE_ITEM_JPEG;

	private static String VALID_CREATE_META_DATA;

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
		this.fillRequest(ProtocolTestConstants.VALID_IMAGE_IDENTIFIER,
				VALID_IMAGE_ITEM_JPEG, VALID_CREATE_META_DATA,
				ProtocolTestConstants.VALID_CROPPING_WIDTH_COORDINATE,
				ProtocolTestConstants.VALID_CROPPING_HEIGHT_COORDINATE,
				ProtocolTestConstants.VALID_CROPPING_LEFT_COORDINATE,
				ProtocolTestConstants.VALID_CROPPING_TOP_COORDINATE);
		assertNotNull(this.createWithCroppingRequest);
		assertEquals(VALID_IDENTIFIER,
				this.createWithCroppingRequest.getImageIdentifier());
		assertTrue(compareInputStreams(VALID_IMAGE_ITEM_JPEG.getInputStream(),
				this.createWithCroppingRequest.getImageStream()));
		assertEquals(VALID_CREATE_META_DATA,
				this.createWithCroppingRequest.getMetaData());
	}

	@Test
	public void testImageIdentifierMissing() {
		this.fillRequest(null, VALID_IMAGE_ITEM_JPEG,
				ProtocolTestConstants.VALID_IMAGE_METADATA,
				ProtocolTestConstants.VALID_CROPPING_WIDTH_COORDINATE,
				ProtocolTestConstants.VALID_CROPPING_HEIGHT_COORDINATE,
				ProtocolTestConstants.VALID_CROPPING_LEFT_COORDINATE,
				ProtocolTestConstants.VALID_CROPPING_TOP_COORDINATE);
		this.checkForMissingParameterMessage(ProtocolConstants.Parameters.Create.IMAGE_IDENTIFIER);
		assertNull(this.createWithCroppingRequest);
	}

	@Test
	public void testImageMetadataMissing() {
		this.fillRequest(ProtocolTestConstants.VALID_IMAGE_IDENTIFIER,
				VALID_IMAGE_ITEM_JPEG, null,
				ProtocolTestConstants.VALID_CROPPING_WIDTH_COORDINATE,
				ProtocolTestConstants.VALID_CROPPING_HEIGHT_COORDINATE,
				ProtocolTestConstants.VALID_CROPPING_LEFT_COORDINATE,
				ProtocolTestConstants.VALID_CROPPING_TOP_COORDINATE);
		this.checkForMissingParameterMessage(ProtocolConstants.Parameters.Create.META_DATA);
		assertNull(this.createWithCroppingRequest);
	}

	@Test
	public void testImageMetadataMalformed() {
		this.fillRequest(ProtocolTestConstants.VALID_IMAGE_IDENTIFIER,
				VALID_IMAGE_ITEM_JPEG,
				ProtocolTestConstants.MALFORMED_IMAGE_METADATA,
				ProtocolTestConstants.VALID_CROPPING_WIDTH_COORDINATE,
				ProtocolTestConstants.VALID_CROPPING_HEIGHT_COORDINATE,
				ProtocolTestConstants.VALID_CROPPING_LEFT_COORDINATE,
				ProtocolTestConstants.VALID_CROPPING_TOP_COORDINATE);
		this.checkForMissingParameterMessage(ProtocolConstants.Parameters.Create.META_DATA);
		assertNull(this.createWithCroppingRequest);
	}

	// @Test
	// testCroppingHeightInvalid(){
	//
	// }
	//
	// @Test
	// testCroppingHeightMissing() {
	//
	// }
	//
	// @Test
	// testCroppingLeftInvalid() {
	//
	// }
	//
	// @Test
	// testCroppingLeftMissing() {
	//
	// }
	//
	// @Test
	// testTopInvalid() {
	//
	// }
	//
	// @Test
	// testTopMissing() {
	//
	// }
	//
	// @Test
	// testWidthInvalid() {
	//
	// }
	//
	// @Test
	// testWidthMissing() {
	//
	// }
	//
	// @Test
	// testImageIdentifierMissing() {
	//
	// }
	//
	// @Test
	// testImageMetadataMissing() {
	//
	// }
	//
	// @Test
	// testImageMetadataMalformed() {
	//
	// }
	private void fillRequest(final String imageIdentifier,
			final FileItem imageItem, final String metaData,
			final String height, final String width, final String top,
			final String left) {
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
		if (width != null) {
			formItemList.addField(
					ProtocolConstants.Parameters.Create.CROP_WIDTH, width);
		}

		if (height != null) {
			formItemList.addField(
					ProtocolConstants.Parameters.Create.CROP_HEIGHT, height);
		}

		if (top != null) {
			formItemList.addField(ProtocolConstants.Parameters.Create.CROP_TOP,
					top);
		}

		if (left != null) {
			formItemList.addField(
					ProtocolConstants.Parameters.Create.CROP_LEFT, left);
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
