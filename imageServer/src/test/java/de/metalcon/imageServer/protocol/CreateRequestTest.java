package de.metalcon.imageServer.protocol;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

import de.metalcon.imageStorageServer.ISSConfig;
import de.metalcon.imageStorageServer.protocol.ProtocolConstants;
import de.metalcon.imageStorageServer.protocol.create.CreateRequest;

public class CreateRequestTest extends RequestTest {

	private CreateRequest createRequest;
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

	/**
	 * create a music item
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
}
