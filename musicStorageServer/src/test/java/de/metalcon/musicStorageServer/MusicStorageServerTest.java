package de.metalcon.musicStorageServer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.commons.io.FileUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.metalcon.musicStorageServer.protocol.create.CreateResponse;
import de.metalcon.musicStorageServer.protocol.delete.DeleteResponse;
import de.metalcon.musicStorageServer.protocol.read.ReadResponse;
import de.metalcon.musicStorageServer.protocol.update.UpdateResponse;

public class MusicStorageServerTest {

	private static final Format FORMATTER = new SimpleDateFormat("yyyy-MM-dd");

	private static final JSONParser PARSER = new JSONParser();

	private static final String CONFIG_PATH = "test.mss.config";

	private static final String VALID_READ_IDENTIFIER = "mi1";

	private static final String VALID_CREATE_IDENTIFIER = "mi2";

	private static File TEST_FILE_DIRECTORY;

	private static File BACKUP_FILE_ORIGINAL, BACKUP_FILE_BASIS,
			BACKUP_FILE_STREAMING;
	private static File DESTINATION_FILE_ORIGINAL, DESTINATION_FILE_BASIS,
			DESTINATION_FILE_STREAMING;

	private static InputStream VALID_READ_STREAM_MP3;

	private static InputStream INVALID_READ_STREAM_JPEG,
			INVALID_READ_STREAM_TXT, INVALID_READ_STREAM_EMPTY;

	private static JSONObject VALID_READ_META_DATA_JSON;
	private static String VALID_READ_META_DATA;

	private static JSONObject VALID_CREATE_META_DATA_JSON;
	private static String VALID_CREATE_META_DATA;
	private static String INVALID_CREATE_META_DATA = "{ artist: Testy }";

	private MusicStorageServer server;

	private CreateResponse createResponse;
	private ReadResponse readResponse;
	private UpdateResponse updateResponse;
	private DeleteResponse deleteResponse;

	@SuppressWarnings("unchecked")
	@BeforeClass
	public static void beforeClass() throws IOException {
		final MSSConfig config = new MSSConfig(CONFIG_PATH);
		TEST_FILE_DIRECTORY = new File(config.getMusicDirectory())
				.getParentFile();

		VALID_READ_META_DATA_JSON = new JSONObject();
		VALID_READ_META_DATA_JSON.put("title", "My Great Song");
		VALID_READ_META_DATA_JSON.put("album", "Testy Forever");
		VALID_READ_META_DATA_JSON.put("artist", "Testy");

		VALID_READ_META_DATA = VALID_READ_META_DATA_JSON.toJSONString();

		VALID_CREATE_META_DATA_JSON = new JSONObject();
		VALID_CREATE_META_DATA_JSON.putAll(VALID_READ_META_DATA_JSON);
		VALID_CREATE_META_DATA_JSON.put("license",
				"General Less AllYouCanEat License");
		VALID_CREATE_META_DATA_JSON.put("date", "1991-11-11");

		VALID_CREATE_META_DATA_JSON.put("comment",
				"All your cookies belong to me!");

		VALID_CREATE_META_DATA = VALID_CREATE_META_DATA_JSON.toJSONString();

		// convert files for reading tests once
		final MusicStorageServer server = new MusicStorageServer(CONFIG_PATH);
		server.clear();

		BACKUP_FILE_ORIGINAL = new File(TEST_FILE_DIRECTORY, "original.mp3");
		BACKUP_FILE_BASIS = new File(TEST_FILE_DIRECTORY, "basis.ogg");
		BACKUP_FILE_STREAMING = new File(TEST_FILE_DIRECTORY, "streaming.ogg");

		final String hash = "108053";
		final Calendar calendar = Calendar.getInstance();
		final String day = FORMATTER.format(calendar.getTime());
		final String year = String.valueOf(calendar.get(Calendar.YEAR));

		DESTINATION_FILE_ORIGINAL = new File(config.getMusicDirectory()
				+ "originals/" + year + "/" + day + "/1/10/", hash);
		DESTINATION_FILE_BASIS = new File(config.getMusicDirectory()
				+ "1/10/108/basis/", hash + ".ogg");
		DESTINATION_FILE_STREAMING = new File(config.getMusicDirectory()
				+ "1/10/108/streaming/", hash + ".ogg");

		// convert and copy files if not existing
		if (!(BACKUP_FILE_ORIGINAL.exists() && BACKUP_FILE_BASIS.exists() && BACKUP_FILE_STREAMING
				.exists())) {
			try {
				assertTrue(server.createMusicItem(VALID_READ_IDENTIFIER,
						new FileInputStream(new File(TEST_FILE_DIRECTORY,
								"mp3.mp3")), VALID_READ_META_DATA,
						new CreateResponse()));

				FileUtils.copyFile(DESTINATION_FILE_ORIGINAL,
						BACKUP_FILE_ORIGINAL);
				FileUtils.copyFile(DESTINATION_FILE_BASIS, BACKUP_FILE_BASIS);
				FileUtils.copyFile(DESTINATION_FILE_STREAMING,
						BACKUP_FILE_STREAMING);
			} catch (final FileNotFoundException e) {
				fail("audio file for test is not avialable!");
			}
		}
	}

	@Before
	public void setUp() throws IOException, NoSuchFieldException,
			SecurityException, IllegalArgumentException, IllegalAccessException {
		this.server = new MusicStorageServer(CONFIG_PATH);
		this.server.clear();
		assertTrue(this.server.isRunning());

		// restore converted files to save time
		FileUtils.copyFile(BACKUP_FILE_ORIGINAL, DESTINATION_FILE_ORIGINAL);
		FileUtils.copyFile(BACKUP_FILE_BASIS, DESTINATION_FILE_BASIS);
		FileUtils.copyFile(BACKUP_FILE_STREAMING, DESTINATION_FILE_STREAMING);

		// TODO: find a better solution
		final Field metaDatabase = MusicStorageServer.class
				.getDeclaredField("musicMetaDatabase");
		metaDatabase.setAccessible(true);
		((MusicMetaDatabase) metaDatabase.get(this.server)).addDatabaseEntry(
				VALID_READ_IDENTIFIER, VALID_READ_META_DATA_JSON);

		assertFalse(this.server.createMusicItem(VALID_READ_IDENTIFIER,
				new FileInputStream(new File(TEST_FILE_DIRECTORY, "mp3.mp3")),
				VALID_CREATE_META_DATA, new CreateResponse()));

		VALID_READ_STREAM_MP3 = new FileInputStream(new File(
				TEST_FILE_DIRECTORY, "mp3.mp3"));

		INVALID_READ_STREAM_JPEG = new FileInputStream(new File(
				TEST_FILE_DIRECTORY, "jpeg.jpeg"));
		INVALID_READ_STREAM_TXT = new FileInputStream(new File(
				TEST_FILE_DIRECTORY, "txt.txt"));
		INVALID_READ_STREAM_EMPTY = new FileInputStream(new File(
				TEST_FILE_DIRECTORY, "empty"));

		this.createResponse = new CreateResponse();
		this.readResponse = new ReadResponse();
		this.updateResponse = new UpdateResponse();
		this.deleteResponse = new DeleteResponse();
	}

	// @Test
	public void testCreateMusicItemMp3Valid() {
		assertTrue(this.server.createMusicItem(VALID_CREATE_IDENTIFIER,
				VALID_READ_STREAM_MP3, VALID_CREATE_META_DATA,
				this.createResponse));
	}

	@Test
	public void testCreateInvalidIdentifier() {
		assertFalse(this.server.createMusicItem(VALID_READ_IDENTIFIER,
				VALID_READ_STREAM_MP3, VALID_CREATE_META_DATA,
				this.createResponse));
	}

	@Test
	public void testCreateInvalidMetaData() {
		assertFalse(this.server.createMusicItem(VALID_CREATE_IDENTIFIER,
				VALID_READ_STREAM_MP3, INVALID_CREATE_META_DATA,
				this.createResponse));
	}

	@Test
	public void testCreateMusicItemJpeg() {
		assertFalse(this.server.createMusicItem(VALID_CREATE_IDENTIFIER,
				INVALID_READ_STREAM_JPEG, VALID_CREATE_META_DATA,
				this.createResponse));

	}

	@Test
	public void testCreateMusicItemTxt() {
		assertFalse(this.server.createMusicItem(VALID_CREATE_IDENTIFIER,
				INVALID_READ_STREAM_TXT, VALID_CREATE_META_DATA,
				this.createResponse));
	}

	@Test
	public void testCreateMusicItemEmpty() {
		assertFalse(this.server.createMusicItem(VALID_CREATE_IDENTIFIER,
				INVALID_READ_STREAM_EMPTY, VALID_CREATE_META_DATA,
				this.createResponse));
	}

	@Test
	public void testReadMusicItemOriginal() throws FileNotFoundException,
			IOException {
		final MusicData musicData = this.server.readMusicItem(
				VALID_READ_IDENTIFIER, MusicItemVersion.ORIGINAL,
				this.readResponse);
		assertNotNull(musicData);

		compareJson(VALID_READ_META_DATA, musicData.getMetaData());
		assertTrue(compareInputStreams(
				new FileInputStream(BACKUP_FILE_ORIGINAL),
				musicData.getMusicItemStream()));
	}

	@Test
	public void testReadMusicItemBasis() throws FileNotFoundException,
			IOException {
		final MusicData musicData = this.server.readMusicItem(
				VALID_READ_IDENTIFIER, MusicItemVersion.BASIS,
				this.readResponse);
		assertNotNull(musicData);

		compareJson(VALID_READ_META_DATA, musicData.getMetaData());
		assertTrue(compareInputStreams(new FileInputStream(BACKUP_FILE_BASIS),
				musicData.getMusicItemStream()));
	}

	@Test
	public void testReadMusicItemStreaming() throws FileNotFoundException,
			IOException {
		final MusicData musicData = this.server.readMusicItem(
				VALID_READ_IDENTIFIER, MusicItemVersion.STREAM,
				this.readResponse);
		assertNotNull(musicData);

		compareJson(VALID_READ_META_DATA, musicData.getMetaData());
		assertTrue(compareInputStreams(new FileInputStream(
				BACKUP_FILE_STREAMING), musicData.getMusicItemStream()));
	}

	@Test
	public void testReadInvalidIdentifier() {
		assertNull(this.server.readMusicItem(VALID_CREATE_IDENTIFIER,
				MusicItemVersion.ORIGINAL, this.readResponse));
	}

	@Test
	public void testReadMusicItemVersionNull() throws FileNotFoundException,
			IOException {
		final MusicData musicData = this.server.readMusicItem(
				VALID_READ_IDENTIFIER, null, this.readResponse);
		assertNotNull(musicData);

		compareJson(VALID_READ_META_DATA, musicData.getMetaData());
		assertTrue(compareInputStreams(new FileInputStream(
				BACKUP_FILE_STREAMING), musicData.getMusicItemStream()));
	}

	@Test
	public void testReadMetaDataSingle() {
		final String[] identifiers = new String[] { VALID_READ_IDENTIFIER };
		final String[] metaData = this.server.readMusicItemMetaData(
				identifiers, this.readResponse);
		assertNotNull(metaData);
		assertEquals(identifiers.length, metaData.length);
		compareJson(VALID_READ_META_DATA, metaData[0]);
	}

	@Test
	public void testReadMetaDataInvalid() {
		final String[] identifiers = new String[] { VALID_READ_IDENTIFIER,
				VALID_CREATE_IDENTIFIER };
		assertNull(this.server.readMusicItemMetaData(identifiers,
				this.readResponse));
	}

	@Test
	public void testUpdateMetaData() {
		assertTrue(this.server.updateMetaData(VALID_READ_IDENTIFIER,
				VALID_CREATE_META_DATA, this.updateResponse));
	}

	@Test
	public void testUpdateMetaDataInvalidIdentifier() {
		assertFalse(this.server.updateMetaData(VALID_CREATE_IDENTIFIER,
				VALID_CREATE_META_DATA, this.updateResponse));
	}

	@Test
	public void testUpdateMetaDataInvalidMetaData() {
		assertFalse(this.server.updateMetaData(VALID_READ_IDENTIFIER,
				INVALID_CREATE_META_DATA, this.updateResponse));
	}

	@Test
	public void testDelete() {
		assertTrue(this.server.deleteMusicItem(VALID_READ_IDENTIFIER,
				this.deleteResponse));
	}

	@Test
	public void testDeleteInvalidIdentifier() {
		assertFalse(this.server.deleteMusicItem(VALID_CREATE_IDENTIFIER,
				this.deleteResponse));
	}

	/**
	 * compare to input streams
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