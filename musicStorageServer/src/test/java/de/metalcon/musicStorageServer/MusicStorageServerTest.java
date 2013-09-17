package de.metalcon.musicStorageServer;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;

import de.metalcon.musicStorageServer.protocol.create.CreateResponse;

public class MusicStorageServerTest {

	private static final String VALID_CREATE_IDENTIFIER = "mi2";

	private static InputStream VALID_READ_STREAM_MP3;

	private static final String VALID_META_DATA = "{}";

	private MusicStorageServer server;

	private CreateResponse createResponse;

	@Before
	public void setUp() throws FileNotFoundException {
		this.server = new MusicStorageServer("test.mss.config");
		this.server.clear();
		assertTrue(this.server.isRunning());

		VALID_READ_STREAM_MP3 = new FileInputStream(new File(
				"/home/sebschlicht/mr_t_ba.jpg"));

		this.createResponse = new CreateResponse();
	}

	@Test
	public void testCreateMusicItem() {
		assertTrue(this.server.createMusicItem(VALID_CREATE_IDENTIFIER,
				VALID_READ_STREAM_MP3, VALID_META_DATA, this.createResponse));
	}

}