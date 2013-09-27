package de.metalcon.imageServer.protocol;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import de.metalcon.imageStorageServer.protocol.ProtocolConstants;
import de.metalcon.imageStorageServer.protocol.read.ReadBulkRequest;
import de.metalcon.imageStorageServer.protocol.read.ReadResponse;
import de.metalcon.utils.FormItemList;

public class ReadBulkRequestTest extends RequestTest {

	private static final String MALFORMED_IDENTIFIER_LIST = "test,,test";

	private static final String VALID_IMAGE_IDENTIFIER_LIST = "ii1, ii2, ii3";

	private ReadBulkRequest readBulkRequest;

	private void fillRequest(final String imageIdentifierList,
			final String width, final String height) {
		FormItemList formItemList = new FormItemList();

		if (imageIdentifierList != null) {
			formItemList.addField(
					ProtocolConstants.Parameters.Read.IMAGE_IDENTIFIER_LIST,
					imageIdentifierList);
		}

		if (width != null) {
			formItemList.addField(
					ProtocolConstants.Parameters.Read.IMAGE_WIDTH, width);

		}

		if (height != null) {
			formItemList.addField(
					ProtocolConstants.Parameters.Read.IMAGE_HEIGHT, height);
		}

		final ReadResponse readResponse = new ReadResponse();
		this.readBulkRequest = ReadBulkRequest.checkRequest(formItemList,
				readResponse);
		this.extractJson(readResponse);

	}

	@Test
	public void testReadBulkRequest() {
		this.fillRequest(VALID_IMAGE_IDENTIFIER_LIST,
				ProtocolTestConstants.VALID_SCALING_WIDTH,
				ProtocolTestConstants.VALID_SCALING_HEIGHT);
		assertNotNull(this.readBulkRequest);
		assertEquals(VALID_IMAGE_IDENTIFIER_LIST.split(","),
				this.readBulkRequest.getImageIdentifierList());
		assertEquals(ProtocolTestConstants.VALID_SCALING_WIDTH,
				String.valueOf(this.readBulkRequest.getImageWidth()));
		assertEquals(ProtocolTestConstants.VALID_SCALING_HEIGHT,
				String.valueOf(this.readBulkRequest.getImageHeight()));
	}

	@Test
	public void testImageIdentifierListMissing() {
		this.fillRequest(null, ProtocolTestConstants.VALID_SCALING_WIDTH,
				ProtocolTestConstants.VALID_SCALING_HEIGHT);
		this.checkForMissingParameterMessage(ProtocolConstants.Parameters.Read.IMAGE_IDENTIFIER);
		assertNull(this.readBulkRequest);
	}

	@Test
	public void testImageIdentifierListMalformed() {
		this.fillRequest(MALFORMED_IDENTIFIER_LIST,
				ProtocolTestConstants.VALID_SCALING_WIDTH,
				ProtocolTestConstants.VALID_SCALING_HEIGHT);
		this.checkForStatusMessage(ProtocolConstants.StatusMessage.Read.IDENTIFIER_LIST_CONTAINS_EMPTY_PARTS);
		assertNull(this.readBulkRequest);
	}

	@Test
	public void testWidthMissing() {
		this.fillRequest(VALID_IMAGE_IDENTIFIER_LIST, null,
				ProtocolTestConstants.VALID_SCALING_HEIGHT);
		this.checkForMissingParameterMessage(ProtocolConstants.Parameters.Read.IMAGE_WIDTH);
		assertNull(this.readBulkRequest);
	}

	@Test
	public void testWidthMalformed() {
		this.fillRequest(VALID_IMAGE_IDENTIFIER_LIST,
				ProtocolTestConstants.MALFORMED_SCALING_VALUE,
				ProtocolTestConstants.VALID_SCALING_HEIGHT);
		this.checkForStatusMessage(ProtocolConstants.StatusMessage.Read.SCALING_WIDTH_MALFORMED);
		assertNull(this.readBulkRequest);
	}

	@Test
	public void testWidthInvalid() {
		this.fillRequest(VALID_IMAGE_IDENTIFIER_LIST,
				ProtocolTestConstants.INVALID_SCALING_WIDTH,
				ProtocolTestConstants.VALID_SCALING_HEIGHT);
		this.checkForStatusMessage(ProtocolConstants.StatusMessage.Read.SCALING_WIDTH_INVALID);
		assertNull(this.readBulkRequest);
	}

	@Test
	public void testHeightMissing() {
		this.fillRequest(VALID_IMAGE_IDENTIFIER_LIST,
				ProtocolTestConstants.VALID_SCALING_WIDTH, null);
		this.checkForMissingParameterMessage(ProtocolConstants.Parameters.Read.IMAGE_HEIGHT);
		assertNull(this.readBulkRequest);
	}

	@Test
	public void testHeightMalformed() {
		this.fillRequest(VALID_IMAGE_IDENTIFIER_LIST,
				ProtocolTestConstants.VALID_SCALING_WIDTH,
				ProtocolTestConstants.MALFORMED_SCALING_VALUE);
		this.checkForStatusMessage(ProtocolConstants.StatusMessage.Read.SCALING_HEIGHT_MALFORMED);
		assertNull(this.readBulkRequest);
	}

	@Test
	public void testHeightInvalid() {
		this.fillRequest(VALID_IMAGE_IDENTIFIER_LIST,
				ProtocolTestConstants.VALID_SCALING_WIDTH,
				ProtocolTestConstants.INVALID_SCALING_HEIGHT);
		this.checkForStatusMessage(ProtocolConstants.StatusMessage.Read.SCALING_HEIGHT_INVALID);
		assertNull(this.readBulkRequest);
	}

}