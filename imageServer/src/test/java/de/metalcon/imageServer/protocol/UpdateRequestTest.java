package de.metalcon.imageServer.protocol;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import de.metalcon.imageStorageServer.protocol.ProtocolConstants;
import de.metalcon.imageStorageServer.protocol.update.UpdateRequest;
import de.metalcon.imageStorageServer.protocol.update.UpdateResponse;
import de.metalcon.utils.FormItemList;

public class UpdateRequestTest extends RequestTest {

	private UpdateRequest updateRequest;

	private void fillRequest(final String imageIdentifier,
			final String imageMetaData) {
		FormItemList formItemList = new FormItemList();

		if (imageIdentifier != null) {
			formItemList.addField(
					ProtocolConstants.Parameters.Update.IMAGE_IDENTIFIER,
					imageIdentifier);
		}

		if (imageMetaData != null) {
			formItemList.addField(
					ProtocolConstants.Parameters.Update.META_DATA,
					imageMetaData);
		}

		final UpdateResponse updateResponse = new UpdateResponse();
		this.updateRequest = UpdateRequest.checkRequest(formItemList,
				updateResponse);
		this.extractJson(updateResponse);
	}

	@Test
	public void testUpdateRequest() {
		this.fillRequest(VALID_IDENTIFIER,
				ProtocolTestConstants.VALID_IMAGE_METADATA);
		assertNotNull(this.updateRequest);
		assertEquals(VALID_IDENTIFIER, this.updateRequest.getImageIdentifier());
		assertEquals(ProtocolTestConstants.VALID_IMAGE_METADATA,
				this.updateRequest.getMetaData());
	}

	@Test
	public void testImageIdentifierMissing() {
		this.fillRequest(null, ProtocolTestConstants.VALID_IMAGE_METADATA);
		this.checkForMissingParameterMessage(ProtocolConstants.Parameters.Update.IMAGE_IDENTIFIER);
		assertNull(this.updateRequest);
	}

	@Test
	public void testMetadataMissing() {
		this.fillRequest(VALID_IDENTIFIER, null);
		this.checkForMissingParameterMessage(ProtocolConstants.Parameters.Update.META_DATA);
		assertNull(this.updateRequest);
	}

	@Test
	public void testMetadataMalformed() {
		this.fillRequest(VALID_IDENTIFIER,
				ProtocolTestConstants.MALFORMED_IMAGE_METADATA);
		this.checkForStatusMessage(ProtocolConstants.StatusMessage.Update.META_DATA_MALFORMED);
		assertNull(this.updateRequest);
	}
}