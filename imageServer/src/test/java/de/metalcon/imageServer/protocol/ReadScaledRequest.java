package de.metalcon.imageServer.protocol;

import static org.junit.Assert.assertNull;

import org.junit.Test;

import de.metalcon.imageServer.ProtocolTestConstants;
import de.metalcon.imageStorageServer.protocol.ProtocolConstants;
import de.metalcon.imageStorageServer.protocol.read.ReadRequest;
import de.metalcon.imageStorageServer.protocol.read.ReadResponse;
import de.metalcon.utils.FormItemList;

public class ReadScaledRequest extends RequestTest {

	private ReadRequest readRequest;

	private void fillRequest(final String imageIdentifier, final String Height,
			final String Width) {
		FormItemList formItemList = new FormItemList();

		if (imageIdentifier != null) {
			formItemList.addField(
					ProtocolConstants.Parameters.Read.IMAGE_IDENTIFIER,
					imageIdentifier);
		}

		if (Height != null) {
			formItemList.addField(
					ProtocolConstants.Parameters.Read.IMAGE_HEIGHT, Height);
		}

		if (Width != null) {
			formItemList.addField(
					ProtocolConstants.Parameters.Read.IMAGE_WIDTH, Width);

		}

		final ReadResponse readResponse = new ReadResponse();
		this.readRequest = ReadRequest.checkRequest(formItemList, readResponse);
		this.extractJson(readResponse);

	}

	@Test
	public void testImageIdentifierMissing() {
		this.fillRequest(null, ProtocolTestConstants.VALID_SCALING_HEIGHT,
				ProtocolTestConstants.VALID_SCALING_WIDTH);
		this.checkForMissingParameterMessage(ProtocolConstants.Parameters.Read.IMAGE_IDENTIFIER);
		assertNull(this.readRequest);
	}
}