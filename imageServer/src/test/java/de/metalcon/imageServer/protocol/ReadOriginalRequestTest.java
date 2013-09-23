package de.metalcon.imageServer.protocol;

import static org.junit.Assert.assertNull;

import org.junit.Test;

import de.metalcon.imageStorageServer.protocol.ProtocolConstants;
import de.metalcon.imageStorageServer.protocol.read.ReadRequest;
import de.metalcon.imageStorageServer.protocol.read.ReadResponse;
import de.metalcon.utils.FormItemList;

public class ReadOriginalRequestTest extends RequestTest {

	private ReadRequest readRequest;

	private void fillRequest(final String imageIdentifier) {
		FormItemList formItemList = new FormItemList();

		if (imageIdentifier != null) {
			formItemList.addField(
					ProtocolConstants.Parameters.Update.IMAGE_IDENTIFIER,
					imageIdentifier);
		}

		final ReadResponse readResponse = new ReadResponse();
		this.readRequest = ReadRequest.checkRequest(formItemList, readResponse);
		this.extractJson(readResponse);

	}

	@Test
	public void testImageIdentifierMissing() {
		this.fillRequest(null);
		this.checkForMissingParameterMessage(ProtocolConstants.Parameters.Read.IMAGE_IDENTIFIER);
		assertNull(this.readRequest);
	}
}
