package de.metalcon.imageServer.protocol.read;

import de.metalcon.imageServer.protocol.ProtocolConstants;
import de.metalcon.imageServer.protocol.Response;

public class ReadResponse extends Response {

	public void addNoImageIdentifierError() {
		this.parameterMissing(
				ProtocolConstants.Parameters.Read.IMAGE_IDENTIFIER,
				"The image identifier is missing. Please deliver one");

	}

	public void addOriginalImageFlagMalformedError() {
		this.addStatusMessage(
				"request corrupt: parameter \"originalFlag\" is malformed",
				"The originalImage flag String is malformed");

	}

	public void addNoOriginalFlagError() {
		this.parameterMissing(ProtocolConstants.Parameters.Read.ORIGINAL_FLAG,
				"The originalImage flag is missing. Please deliver one");

	}

}