package de.metalcon.imageServer.protocol;

import static org.junit.Assert.assertNull;

import org.junit.Test;

import de.metalcon.imageServer.ProtocolTestConstants;
import de.metalcon.imageStorageServer.protocol.ProtocolConstants;
import de.metalcon.imageStorageServer.protocol.read.ReadResponse;
import de.metalcon.imageStorageServer.protocol.read.ReadScaledRequest;
import de.metalcon.utils.FormItemList;

public class ReadScaledRequestTest extends RequestTest {

	private ReadScaledRequest readScaledRequest;

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
		this.readScaledRequest = ReadScaledRequest.checkRequest(formItemList,
				readResponse);
		this.extractJson(readResponse);

	}

	@Test
	public void testImageIdentifierMissing() {
		this.fillRequest(null, ProtocolTestConstants.VALID_SCALING_HEIGHT,
				ProtocolTestConstants.VALID_SCALING_WIDTH);
		this.checkForMissingParameterMessage(ProtocolConstants.Parameters.Read.IMAGE_IDENTIFIER);
		assertNull(this.readScaledRequest);
	}

	@Test
	public void testHeightMissing() {
		this.fillRequest(ProtocolTestConstants.VALID_IMAGE_IDENTIFIER, null,
				ProtocolTestConstants.VALID_SCALING_WIDTH);
		this.checkForMissingParameterMessage(ProtocolConstants.Parameters.Read.IMAGE_HEIGHT);
		assertNull(this.readScaledRequest);
	}

	@Test
	public void testWidthMissing() {
		this.fillRequest(ProtocolTestConstants.VALID_IMAGE_IDENTIFIER,
				ProtocolTestConstants.VALID_SCALING_HEIGHT, null);
		this.checkForMissingParameterMessage(ProtocolConstants.Parameters.Read.IMAGE_WIDTH);
		assertNull(this.readScaledRequest);
	}

	@Test
	public void testHeightMalformed() {
		this.fillRequest(ProtocolTestConstants.VALID_IMAGE_IDENTIFIER,
				ProtocolTestConstants.MALFORMED_SCALING_HEIGHT,
				ProtocolTestConstants.VALID_SCALING_WIDTH);
		this.checkForStatusMessage(ProtocolConstants.StatusMessage.Read.SCALING_HEIGHT_MALFORMED);
		assertNull(this.readScaledRequest);
	}

	@Test
	public void testWidthMalformed() {
		this.fillRequest(ProtocolTestConstants.VALID_IMAGE_IDENTIFIER,
				ProtocolTestConstants.VALID_SCALING_HEIGHT,
				ProtocolTestConstants.MALFORMED_SCALING_WIDTH);
		this.checkForStatusMessage(ProtocolConstants.StatusMessage.Read.SCALING_WIDTH_MALFORMED);
		assertNull(this.readScaledRequest);
	}

	@Test
	public void testHeightTooSmall() {
		this.fillRequest(ProtocolTestConstants.VALID_IMAGE_IDENTIFIER,
				ProtocolTestConstants.INVALID_SCALING_HEIGHT,
				ProtocolTestConstants.VALID_SCALING_WIDTH);
		this.checkForStatusMessage(ProtocolConstants.StatusMessage.Read.GEOMETRY_REQUESTED_HEIGHT_LESS_OR_EQUAL_ZERO);
		assertNull(this.readScaledRequest);
	}

	@Test
	public void testWidthTooSmall() {
		this.fillRequest(ProtocolTestConstants.VALID_IMAGE_IDENTIFIER,
				ProtocolTestConstants.VALID_SCALING_HEIGHT,
				ProtocolTestConstants.INVALID_SCALING_WIDTH);
		this.checkForStatusMessage(ProtocolConstants.StatusMessage.Read.GEOMETRY_REQUESTED_WIDTH_LESS_OR_EQUAL_ZERO);
		assertNull(this.readScaledRequest);
	}

}