package de.metalcon.musicStorageServer.protocol;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;

import de.metalcon.musicStorageServer.protocol.update.UpdateRequest;
import de.metalcon.musicStorageServer.protocol.update.UpdateResponse;
import de.metalcon.utils.FormItemList;

public class UpdateRequestTest extends RequestTest {

	private static String VALID_UPDATE_META_DATA;

	private static final String INVALID_META_DATA = "{ title: Another Great Song }";

	private UpdateRequest updateRequest;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() {
		// meta data
		final JSONObject metaDataUpdate = new JSONObject();
		metaDataUpdate.put("title", "My Great Song");
		metaDataUpdate.put("album", "Testy Forever");
		metaDataUpdate.put("artist", "Testy");
		metaDataUpdate.put("license", "General Less AllYouCanEat License");
		metaDataUpdate.put("date", "1991-11-11");
		metaDataUpdate.put("description", "All your cookies belong to me!");
		VALID_UPDATE_META_DATA = metaDataUpdate.toJSONString();
	}

	private void fillRequest(final String musicItemIdentifier,
			final String metaData) {
		// create and fill form item list
		final FormItemList formItemList = new FormItemList();

		if (musicItemIdentifier != null) {
			formItemList.addField(
					ProtocolConstants.Parameter.Update.MUSIC_ITEM_IDENTIFIER,
					musicItemIdentifier);
		}
		if (metaData != null) {
			formItemList.addField(ProtocolConstants.Parameter.Update.META_DATA,
					metaData);
		}

		// check request and extract the response
		final UpdateResponse updateResponse = new UpdateResponse();
		this.updateRequest = UpdateRequest.checkRequest(formItemList,
				updateResponse);
		this.extractJson(updateResponse);
	}

	@Test
	public void testUpdateRequest() {
		this.fillRequest(VALID_IDENTIFIER, VALID_UPDATE_META_DATA);
		assertNotNull(this.updateRequest);
		assertEquals(VALID_IDENTIFIER,
				this.updateRequest.getMusicItemIdentifier());
		assertEquals(VALID_UPDATE_META_DATA, this.updateRequest.getMetaData());
	}

	@Test
	public void testMusicItemIdentifierMissing() {
		this.fillRequest(null, VALID_UPDATE_META_DATA);
		this.checkForMissingParameterMessage(ProtocolConstants.Parameter.Update.MUSIC_ITEM_IDENTIFIER);
		assertNull(this.updateRequest);
	}

	@Test
	public void testMetaDataMissing() {
		this.fillRequest(VALID_IDENTIFIER, null);
		this.checkForMissingParameterMessage(ProtocolConstants.Parameter.Update.META_DATA);
		assertNull(this.updateRequest);
	}

	@Test
	public void testMetaDataMalformed() {
		this.fillRequest(VALID_IDENTIFIER, INVALID_META_DATA);
		this.checkForStatusMessage(ProtocolConstants.StatusMessage.Update.META_DATA_MALFORMED);
		assertNull(this.updateRequest);
	}

}