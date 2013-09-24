package de.metalcon.imageServer.protocol;

import static org.junit.Assert.assertNull;

import org.junit.Test;

import de.metalcon.imageServer.ProtocolTestConstants;
import de.metalcon.imageStorageServer.protocol.ProtocolConstants;
import de.metalcon.imageStorageServer.protocol.create.CreateFromUrlRequest;
import de.metalcon.imageStorageServer.protocol.create.CreateResponse;
import de.metalcon.utils.FormItemList;

public class CreateFromUrlTest extends RequestTest {

	private CreateFromUrlRequest createFromUrlRequest;

	@Test
	public void testMalformedUrl() {
		this.fillRequest(ProtocolTestConstants.MALFORMED_URL);
		this.checkForStatusMessage(ProtocolConstants.StatusMessage.Create.IMAGE_URL_MALFORMED);
		assertNull(this.createFromUrlRequest);
	}

	@Test
	public void testUrlMissing() {
		this.fillRequest(null);
		this.checkForMissingParameterMessage(ProtocolConstants.Parameters.Create.URL);
		assertNull(this.createFromUrlRequest);
	}

	private void fillRequest(final String imageUrl) {
		// create and fill form item list
		final FormItemList formItemList = new FormItemList();

		if (imageUrl != null) {
			formItemList.addField(ProtocolConstants.Parameters.Create.URL,
					imageUrl);
		}

		// check request and extract the response
		final CreateResponse createResponse = new CreateResponse();
		this.createFromUrlRequest = CreateFromUrlRequest.checkRequest(
				formItemList, createResponse);
		this.extractJson(createResponse);
	}
}
