package de.metalcon.imageServer.protocol.update;

import de.metalcon.imageServer.protocol.ProtocolConstants;
import de.metalcon.imageServer.protocol.Response;

public class UpdateResponse extends Response {

	public void addNoImageIdentifierError() {
		this.parameterMissing(
				ProtocolConstants.Parameters.Update.IMAGE_IDENTIFIER,
				"The image identifier is missing. Please deliver one");
	}

	public void addNoMetadataError() {
		this.parameterMissing(ProtocolConstants.Parameters.Update.META_DATA,
				"The image meta data is missing. Please deliver some");
	}

	public void addMetadataMalformedError() {
		this.addStatusMessage(
				"request corrupt: parameter \"metaData\" is malformed",
				"The metaData String is malformed");
	}

}