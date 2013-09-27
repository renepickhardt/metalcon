package de.metalcon.imageServer.protocol;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import de.metalcon.imageStorageServer.protocol.ProtocolConstants;
import de.metalcon.imageStorageServer.protocol.delete.DeleteRequest;
import de.metalcon.imageStorageServer.protocol.delete.DeleteResponse;
import de.metalcon.utils.FormItemList;

public class DeleteRequestTest extends RequestTest {

	private DeleteRequest deleteRequest;

	private void fillRequest(final String imageIdentifier) {
		FormItemList formItemList = new FormItemList();

		if (imageIdentifier != null) {
			formItemList.addField(
					ProtocolConstants.Parameters.Delete.IMAGE_IDENTIFIER,
					imageIdentifier);
		}

		final DeleteResponse deleteResponse = new DeleteResponse();
		this.deleteRequest = DeleteRequest.checkRequest(formItemList,
				deleteResponse);
		this.extractJson(deleteResponse);
	}

	@Test
	public void testDeleteRequest() {
		this.fillRequest(VALID_IDENTIFIER);
		assertNotNull(this.deleteRequest);
		assertEquals(VALID_IDENTIFIER, this.deleteRequest.getImageIdentifier());
	}

	@Test
	public void testImageIdentifierMissing() {
		this.fillRequest(null);
		this.checkForMissingParameterMessage(ProtocolConstants.Parameters.Delete.IMAGE_IDENTIFIER);
		assertNull(this.deleteRequest);
	}
}