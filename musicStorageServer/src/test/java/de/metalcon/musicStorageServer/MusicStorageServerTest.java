package de.metalcon.musicStorageServer;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class MusicStorageServerTest {

	private MusicStorageServer server;

	@Before
	public void setUp() {
		this.server = new MusicStorageServer("test.mss.config");
		assertTrue(this.server.isRunning());
	}

	@Test
	public void testCreateMusicItem() {

	}

}