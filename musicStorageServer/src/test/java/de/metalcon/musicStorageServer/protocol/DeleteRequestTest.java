package de.metalcon.musicStorageServer.protocol;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import de.metalcon.musicStorageServer.protocol.delete.DeleteRequest;
import de.metalcon.musicStorageServer.protocol.delete.DeleteResponse;
import de.metalcon.utils.FormItemList;

public class DeleteRequestTest extends RequestTest {

	private DeleteRequest deleteRequest;

	private void fillRequest(final String musicItemIdentifier) {
		// create and fill form item list
		final FormItemList formItemList = new FormItemList();

		if (musicItemIdentifier != null) {
			formItemList.addField(
					ProtocolConstants.Parameter.Delete.MUSIC_ITEM_IDENTIFIER,
					musicItemIdentifier);
		}

		// check request and extract the response
		final DeleteResponse deleteResponse = new DeleteResponse();
		this.deleteRequest = DeleteRequest.checkRequest(formItemList,
				deleteResponse);
		this.extractJson(deleteResponse);
	}

	@Test
	public void testDeleteRequest() {
		this.fillRequest(VALID_IDENTIFIER);
		assertNotNull(this.deleteRequest);
		assertEquals(VALID_IDENTIFIER,
				this.deleteRequest.getMusicItemIdentifier());
	}

	@Test
	public void testMusicItemIdentifierMissing() {
		this.fillRequest(null);
		this.checkForMissingParameterMessage(ProtocolConstants.Parameter.Delete.MUSIC_ITEM_IDENTIFIER);
		assertNull(this.deleteRequest);
	}

}