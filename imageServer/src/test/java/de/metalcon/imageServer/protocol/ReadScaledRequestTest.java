package de.metalcon.imageServer.protocol;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import de.metalcon.imageStorageServer.protocol.ProtocolConstants;
import de.metalcon.imageStorageServer.protocol.read.ReadResponse;
import de.metalcon.imageStorageServer.protocol.read.ReadScaledRequest;
import de.metalcon.utils.FormItemList;

public class ReadScaledRequestTest extends RequestTest {

	private ReadScaledRequest readScaledRequest;

	private void fillRequest(final String imageIdentifier, final String width,
			final String height) {
		FormItemList formItemList = new FormItemList();

		if (imageIdentifier != null) {
			formItemList.addField(
					ProtocolConstants.Parameters.Read.IMAGE_IDENTIFIER,
					imageIdentifier);
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
		this.readScaledRequest = ReadScaledRequest.checkRequest(formItemList,
				readResponse);
		this.extractJson(readResponse);
	}

	@Test
	public void testReadScaledRequest() {
		this.fillRequest(VALID_IDENTIFIER,
				ProtocolTestConstants.VALID_SCALING_WIDTH,
				ProtocolTestConstants.VALID_SCALING_HEIGHT);
		assertNotNull(this.readScaledRequest);
		assertEquals(VALID_IDENTIFIER,
				this.readScaledRequest.getImageIdentifier());
		assertEquals(ProtocolTestConstants.VALID_SCALING_WIDTH,
				String.valueOf(this.readScaledRequest.getImageWidth()));
		assertEquals(ProtocolTestConstants.VALID_SCALING_HEIGHT,
				String.valueOf(this.readScaledRequest.getImageHeight()));
	}

	@Test
	public void testImageIdentifierMissing() {
		this.fillRequest(null, ProtocolTestConstants.VALID_SCALING_WIDTH,
				ProtocolTestConstants.VALID_SCALING_HEIGHT);
		this.checkForMissingParameterMessage(ProtocolConstants.Parameters.Read.IMAGE_IDENTIFIER);
		assertNull(this.readScaledRequest);
	}

	@Test
	public void testWidthMissing() {
		this.fillRequest(VALID_IDENTIFIER, null,
				ProtocolTestConstants.VALID_SCALING_HEIGHT);
		this.checkForMissingParameterMessage(ProtocolConstants.Parameters.Read.IMAGE_WIDTH);
		assertNull(this.readScaledRequest);
	}

	@Test
	public void testWidthMalformed() {
		this.fillRequest(VALID_IDENTIFIER,
				ProtocolTestConstants.MALFORMED_SCALING_VALUE,
				ProtocolTestConstants.VALID_SCALING_HEIGHT);
		this.checkForStatusMessage(ProtocolConstants.StatusMessage.Read.SCALING_WIDTH_MALFORMED);
		assertNull(this.readScaledRequest);
	}

	@Test
	public void testWidthInvalid() {
		this.fillRequest(VALID_IDENTIFIER,
				ProtocolTestConstants.INVALID_SCALING_WIDTH,
				ProtocolTestConstants.VALID_SCALING_HEIGHT);
		this.checkForStatusMessage(ProtocolConstants.StatusMessage.Read.SCALING_WIDTH_INVALID);
		assertNull(this.readScaledRequest);
	}

	@Test
	public void testHeightMissing() {
		this.fillRequest(VALID_IDENTIFIER,
				ProtocolTestConstants.VALID_SCALING_WIDTH, null);
		this.checkForMissingParameterMessage(ProtocolConstants.Parameters.Read.IMAGE_HEIGHT);
		assertNull(this.readScaledRequest);
	}

	@Test
	public void testHeightMalformed() {
		this.fillRequest(VALID_IDENTIFIER,
				ProtocolTestConstants.VALID_SCALING_WIDTH,
				ProtocolTestConstants.MALFORMED_SCALING_VALUE);
		this.checkForStatusMessage(ProtocolConstants.StatusMessage.Read.SCALING_HEIGHT_MALFORMED);
		assertNull(this.readScaledRequest);
	}

	@Test
	public void testHeightInvalid() {
		this.fillRequest(VALID_IDENTIFIER,
				ProtocolTestConstants.VALID_SCALING_WIDTH,
				ProtocolTestConstants.INVALID_SCALING_HEIGHT);
		this.checkForStatusMessage(ProtocolConstants.StatusMessage.Read.SCALING_HEIGHT_INVALID);
		assertNull(this.readScaledRequest);
	}

}