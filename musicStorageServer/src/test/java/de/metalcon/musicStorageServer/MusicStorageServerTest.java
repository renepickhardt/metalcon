package de.metalcon.musicStorageServer;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.metalcon.musicStorageServer.protocol.create.CreateResponse;

public class MusicStorageServerTest {

	private static final String CONFIG_PATH = "test.mss.config";

	private static final String VALID_CREATE_IDENTIFIER = "mi2";

	private static File TEST_FILE_DIRECTORY;

	private static InputStream VALID_READ_STREAM_MP3;

	private static InputStream INVALID_READ_STREAM_JPEG;

	private static InputStream INVALID_READ_STREAM_TXT;

	private static InputStream INVALID_READ_STREAM_EMPTY;

	private static String VALID_META_DATA;

	private MusicStorageServer server;

	private CreateResponse createResponse;

	@SuppressWarnings("unchecked")
	@BeforeClass
	public static void beforeClass() {
		final MSSConfig config = new MSSConfig(CONFIG_PATH);
		TEST_FILE_DIRECTORY = new File(config.getMusicDirectory())
				.getParentFile();

		final JSONObject metaData = new JSONObject();
		metaData.put("title", "My Great Song");
		metaData.put("album", "Testy Forever");
		metaData.put("artist", "Testy");
		metaData.put("license", "General Less AllYouCanEat License");
		metaData.put("date", "1991-11-11");

		metaData.put("comment", "All your cookies belong to me!");

		VALID_META_DATA = metaData.toJSONString();
	}

	@Before
	public void setUp() throws FileNotFoundException {
		this.server = new MusicStorageServer(CONFIG_PATH);
		this.server.clear();
		assertTrue(this.server.isRunning());

		VALID_READ_STREAM_MP3 = new FileInputStream(new File(
				TEST_FILE_DIRECTORY, "mp3.mp3"));

		INVALID_READ_STREAM_JPEG = new FileInputStream(new File(
				TEST_FILE_DIRECTORY, "jpeg.jpeg"));
		INVALID_READ_STREAM_TXT = new FileInputStream(new File(
				TEST_FILE_DIRECTORY, "txt.txt"));
		INVALID_READ_STREAM_EMPTY = new FileInputStream(new File(
				TEST_FILE_DIRECTORY, "empty"));

		this.createResponse = new CreateResponse();
	}

	@Test
	public void testCreateMusicItemMp3Valid() {
		assertTrue(this.server.createMusicItem(VALID_CREATE_IDENTIFIER,
				VALID_READ_STREAM_MP3, VALID_META_DATA, this.createResponse));
	}

	// @Test
	public void testCreateMusicItemJpeg() {
		assertFalse(this.server.createMusicItem(VALID_CREATE_IDENTIFIER,
				INVALID_READ_STREAM_JPEG, VALID_META_DATA, this.createResponse));
	}

	// @Test
	public void testCreateMusicItemTxt() {
		assertFalse(this.server.createMusicItem(VALID_CREATE_IDENTIFIER,
				INVALID_READ_STREAM_TXT, VALID_META_DATA, this.createResponse));
	}

	// @Test
	public void testCreateMusicItemEmpty() {
		assertFalse(this.server
				.createMusicItem(VALID_CREATE_IDENTIFIER,
						INVALID_READ_STREAM_EMPTY, VALID_META_DATA,
						this.createResponse));
	}

}