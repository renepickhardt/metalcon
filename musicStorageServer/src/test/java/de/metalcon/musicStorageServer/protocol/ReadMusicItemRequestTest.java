package de.metalcon.musicStorageServer.protocol;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import de.metalcon.musicStorageServer.MusicItemVersion;
import de.metalcon.musicStorageServer.protocol.read.ReadMusicItemRequest;
import de.metalcon.musicStorageServer.protocol.read.ReadResponse;
import de.metalcon.utils.FormItemList;

public class ReadMusicItemRequestTest extends RequestTest {

	private static final MusicItemVersion VALID_MUSIC_ITEM_VERSION_ORIGINAL = MusicItemVersion.ORIGINAL;
	private static final MusicItemVersion VALID_MUSIC_ITEM_VERSION_BASIS = MusicItemVersion.BASIS;
	private static final MusicItemVersion VALID_MUSIC_ITEM_VERSION_STREAM = MusicItemVersion.STREAM;

	private static final String INVALID_MUSIC_ITEM_VERSION = "nuff";

	private ReadMusicItemRequest readRequest;

	private void fillRequest(final String musicItemIdentifier,
			final String musicItemVersion) {
		// create and fill form item list
		final FormItemList formItemList = new FormItemList();

		if (musicItemIdentifier != null) {
			formItemList.addField(
					ProtocolConstants.Parameter.Read.MUSIC_ITEM_IDENTIFIER,
					musicItemIdentifier);
		}
		if (musicItemVersion != null) {
			formItemList.addField(
					ProtocolConstants.Parameter.Read.MUSIC_ITEM_VERSION,
					musicItemVersion);
		}

		// check request and extract the response
		final ReadResponse readResponse = new ReadResponse();
		this.readRequest = ReadMusicItemRequest.checkRequest(formItemList,
				readResponse);
		this.extractJson(readResponse);
	}

	@Test
	public void testReadMusicItemRequest() {
		// original music file
		this.fillRequest(VALID_IDENTIFIER,
				VALID_MUSIC_ITEM_VERSION_ORIGINAL.toString());
		assertNotNull(this.readRequest);
		assertEquals(VALID_IDENTIFIER,
				this.readRequest.getMusicItemIdentifier());
		assertEquals(VALID_MUSIC_ITEM_VERSION_ORIGINAL,
				this.readRequest.getMusicItemVersion());

		// converted (basis version)
		this.fillRequest(VALID_IDENTIFIER,
				VALID_MUSIC_ITEM_VERSION_BASIS.toString());
		assertNotNull(this.readRequest);
		assertEquals(VALID_IDENTIFIER,
				this.readRequest.getMusicItemIdentifier());
		assertEquals(VALID_MUSIC_ITEM_VERSION_BASIS,
				this.readRequest.getMusicItemVersion());

		// converted (streaming version)
		this.fillRequest(VALID_IDENTIFIER,
				VALID_MUSIC_ITEM_VERSION_STREAM.toString());
		assertNotNull(this.readRequest);
		assertEquals(VALID_IDENTIFIER,
				this.readRequest.getMusicItemIdentifier());
		assertEquals(VALID_MUSIC_ITEM_VERSION_STREAM,
				this.readRequest.getMusicItemVersion());
	}

	@Test
	public void testMusicItemIdentifierMissing() {
		this.fillRequest(null, VALID_MUSIC_ITEM_VERSION_ORIGINAL.toString());
		this.checkForMissingParameterMessage(ProtocolConstants.Parameter.Read.MUSIC_ITEM_IDENTIFIER);
		assertNull(this.readRequest);
	}

	@Test
	public void testMusicItemVersionMissing() {
		this.fillRequest(VALID_IDENTIFIER, null);
		this.checkForMissingParameterMessage(ProtocolConstants.Parameter.Read.MUSIC_ITEM_VERSION);
		assertNull(this.readRequest);
	}

	@Test
	public void testMusicItemVersionInvalid() {
		this.fillRequest(VALID_IDENTIFIER, INVALID_MUSIC_ITEM_VERSION);
		this.checkForStatusMessage(ProtocolConstants.StatusMessage.Read.MUSIC_ITEM_VERSION_INVALID);
		assertNull(this.readRequest);
	}

}