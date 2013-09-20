package de.metalcon.musicStorageServer.protocol;

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

import de.metalcon.musicStorageServer.MSSConfig;
import de.metalcon.musicStorageServer.protocol.create.CreateRequest;
import de.metalcon.musicStorageServer.protocol.create.CreateResponse;
import de.metalcon.utils.FormItemList;

public class CreateRequestTest extends RequestTest {

	private static final String CONFIG_PATH = "test.mss.config";

	private static File TEST_FILE_DIRECTORY, DISK_FILE_REPOSITORY;

	private static FileItem VALID_MUSIC_ITEM_MP3;

	private static String VALID_CREATE_META_DATA;

	private static final String INVALID_META_DATA = "{ title: Another Great Song }";

	private CreateRequest createRequest;

	@BeforeClass
	public static void beforeClass() {
		final MSSConfig config = new MSSConfig(CONFIG_PATH);
		TEST_FILE_DIRECTORY = new File(config.getMusicDirectory())
				.getParentFile();
		DISK_FILE_REPOSITORY = new File(config.getTemporaryDirectory());
	}

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws IOException {
		DISK_FILE_REPOSITORY.delete();
		DISK_FILE_REPOSITORY.mkdirs();

		// MP3 music item
		final File musicItemMp3 = new File(TEST_FILE_DIRECTORY, "mp3.mp3");
		VALID_MUSIC_ITEM_MP3 = createMusicItem("audio/mpeg", musicItemMp3);
		assertEquals(musicItemMp3.length(), VALID_MUSIC_ITEM_MP3.getSize());

		// meta data
		final JSONObject metaDataCreate = new JSONObject();
		metaDataCreate.put("title", "My Great Song");
		metaDataCreate.put("album", "Testy Forever");
		metaDataCreate.put("artist", "Testy");
		metaDataCreate.put("license", "General Less AllYouCanEat License");
		metaDataCreate.put("date", "1991-11-11");
		metaDataCreate.put("description", "All your cookies belong to me!");
		VALID_CREATE_META_DATA = metaDataCreate.toJSONString();
	}

	@After
	public void tearDown() {
		DISK_FILE_REPOSITORY.delete();
	}

	private void fillRequest(final String musicItemIdentifier,
			final FileItem musicItem, final String metaData) {
		// create and fill form item list
		final FormItemList formItemList = new FormItemList();

		if (musicItemIdentifier != null) {
			formItemList.addField(
					ProtocolConstants.Parameter.Create.MUSIC_ITEM_IDENTIFIER,
					musicItemIdentifier);
		}
		if (musicItem != null) {
			formItemList.addFile(ProtocolConstants.Parameter.Create.MUSIC_ITEM,
					musicItem);
		}
		if (metaData != null) {
			formItemList.addField(ProtocolConstants.Parameter.Create.META_DATA,
					metaData);
		}

		// check request and extract the response
		final CreateResponse createResponse = new CreateResponse();
		this.createRequest = CreateRequest.checkRequest(formItemList,
				createResponse);
		this.extractJson(createResponse);
	}

	@Test
	public void testCreateRequest() throws IOException {
		this.fillRequest(VALID_IDENTIFIER, VALID_MUSIC_ITEM_MP3,
				VALID_CREATE_META_DATA);
		assertNotNull(this.createRequest);
		assertEquals(VALID_IDENTIFIER,
				this.createRequest.getMusicItemIdentifier());
		assertTrue(compareInputStreams(VALID_MUSIC_ITEM_MP3.getInputStream(),
				this.createRequest.getImageStream()));
		assertEquals(VALID_CREATE_META_DATA, this.createRequest.getMetaData());
	}

	@Test
	public void testMusicItemIdentifierMissing() {
		this.fillRequest(null, VALID_MUSIC_ITEM_MP3, VALID_CREATE_META_DATA);
		this.checkForMissingParameterMessage(ProtocolConstants.Parameter.Create.MUSIC_ITEM_IDENTIFIER);
		assertNull(this.createRequest);
	}

	@Test
	public void testMusicItemMissing() {
		this.fillRequest(VALID_IDENTIFIER, null, VALID_CREATE_META_DATA);
		this.checkForMissingParameterMessage(ProtocolConstants.Parameter.Create.MUSIC_ITEM);
		assertNull(this.createRequest);
	}

	@Test
	public void testMetaDataMissing() {
		this.fillRequest(VALID_IDENTIFIER, VALID_MUSIC_ITEM_MP3, null);
		this.checkForMissingParameterMessage(ProtocolConstants.Parameter.Create.META_DATA);
		assertNull(this.createRequest);
	}

	@Test
	public void testMetaDataMalformed() {
		this.fillRequest(VALID_IDENTIFIER, VALID_MUSIC_ITEM_MP3,
				INVALID_META_DATA);
		this.checkForStatusMessage(ProtocolConstants.StatusMessage.Create.META_DATA_MALFORMED);
		assertNull(this.createRequest);
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
	private static FileItem createMusicItem(final String contentType,
			final File musicFile) {
		final FileItem musicItem = new DiskFileItem(
				ProtocolConstants.Parameter.Create.MUSIC_ITEM, contentType,
				false, musicFile.getName(), (int) musicFile.length(),
				DISK_FILE_REPOSITORY);

		// reason for call of getOutputStream: bug in commons.IO
		// called anyway to create file
		try {
			final OutputStream outputStream = musicItem.getOutputStream();
			final InputStream inputStream = new FileInputStream(musicFile);
			IOUtils.copy(inputStream, outputStream);
		} catch (final IOException e) {
			e.printStackTrace();
			fail("music item creation failed!");
		}

		return musicItem;
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