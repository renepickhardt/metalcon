package de.metalcon.imageServer.protocol;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import de.metalcon.imageStorageServer.protocol.ProtocolConstants;
import de.metalcon.imageStorageServer.protocol.create.CreateFromUrlRequest;
import de.metalcon.imageStorageServer.protocol.create.CreateResponse;
import de.metalcon.utils.FormItemList;

public class CreateFromUrlTest extends RequestTest {

	private static final String MALFORMED_URL = "/:::malformedurl:::/";

	private static final String VALID_URL = "http://www.cyclonefanatic.com/forum/attachments/big-xii-conference/20805d1374005349-whats-awkward-awkward-club-photos-5.jpg";

	private CreateFromUrlRequest createFromUrlRequest;

	@Test
	public void testCreateFromUrl() {
		this.fillRequest(VALID_URL);
		assertNotNull(this.createFromUrlRequest);
	}

	@Test
	public void testUrlMissing() {
		this.fillRequest(null);
		this.checkForMissingParameterMessage(ProtocolConstants.Parameters.Create.URL);
		assertNull(this.createFromUrlRequest);
	}

	@Test
	public void testUrlMalformed() {
		this.fillRequest(MALFORMED_URL);
		this.checkForStatusMessage(ProtocolConstants.StatusMessage.Create.IMAGE_URL_MALFORMED);
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