package de.metalcon.imageServer.protocol;

import static org.junit.Assert.assertNull;

import org.junit.Test;

import de.metalcon.imageServer.ProtocolTestConstants;
import de.metalcon.imageStorageServer.protocol.ProtocolConstants;
import de.metalcon.imageStorageServer.protocol.read.ReadBulkRequest;
import de.metalcon.imageStorageServer.protocol.read.ReadResponse;
import de.metalcon.utils.FormItemList;

public class ReadBulkRequestTest extends RequestTest {

	private ReadBulkRequest readBulkRequest;

	private void fillRequest(final String imageIdentifierList,
			final String Height, final String Width) {
		FormItemList formItemList = new FormItemList();

		if (imageIdentifierList != null) {
			formItemList.addField(
					ProtocolConstants.Parameters.Read.IMAGE_IDENTIFIER_LIST,
					imageIdentifierList);
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
		this.readBulkRequest = ReadBulkRequest.checkRequest(formItemList,
				readResponse);
		this.extractJson(readResponse);

	}

	@Test
	public void testImageIdentifierListMissing() {
		this.fillRequest(null, ProtocolTestConstants.VALID_SCALING_HEIGHT,
				ProtocolTestConstants.VALID_SCALING_WIDTH);
		this.checkForMissingParameterMessage(ProtocolConstants.Parameters.Read.IMAGE_IDENTIFIER);
		assertNull(this.readBulkRequest);
	}

	@Test
	public void testHeightMissing() {
		this.fillRequest(ProtocolTestConstants.VALID_IMAGE_IDENTIFIER_LIST,
				null, ProtocolTestConstants.VALID_SCALING_WIDTH);
		this.checkForMissingParameterMessage(ProtocolConstants.Parameters.Read.IMAGE_HEIGHT);
		assertNull(this.readBulkRequest);
	}

	@Test
	public void testWidthMissing() {
		this.fillRequest(ProtocolTestConstants.VALID_IMAGE_IDENTIFIER_LIST,
				ProtocolTestConstants.VALID_SCALING_HEIGHT, null);
		this.checkForMissingParameterMessage(ProtocolConstants.Parameters.Read.IMAGE_WIDTH);
		assertNull(this.readBulkRequest);
	}

	// TODO: double check if the String split method actually writes empty
	// Strings to an array. If it doesn't, this test will always fail and is not
	// even necessary or useful.
	@Test
	public void testImageIdentifierListMalformed() {
		this.fillRequest(ProtocolTestConstants.MALFORMED_IDENTIFIER_LIST,
				ProtocolTestConstants.VALID_SCALING_HEIGHT,
				ProtocolTestConstants.VALID_SCALING_WIDTH);
		this.checkForStatusMessage(ProtocolConstants.StatusMessage.Read.IDENTIFIER_LIST_CONTAINS_EMPTY_PARTS);
		assertNull(this.readBulkRequest);
	}

	@Test
	public void testHeightMalformed() {
		this.fillRequest(ProtocolTestConstants.VALID_IMAGE_IDENTIFIER_LIST,
				ProtocolTestConstants.MALFORMED_SCALING_HEIGHT,
				ProtocolTestConstants.VALID_SCALING_WIDTH);
		this.checkForStatusMessage(ProtocolConstants.StatusMessage.Read.SCALING_HEIGHT_MALFORMED);
	}

	@Test
	public void testWidthMalformed() {
		this.fillRequest(ProtocolTestConstants.VALID_IMAGE_IDENTIFIER_LIST,
				ProtocolTestConstants.VALID_SCALING_HEIGHT,
				ProtocolTestConstants.MALFORMED_SCALING_WIDTH);
		this.checkForStatusMessage(ProtocolConstants.StatusMessage.Read.SCALING_WIDTH_MALFORMED);
	}
}
