package de.metalcon.imageServer.protocol.read;

import de.metalcon.imageServer.protocol.ProtocolConstants;
import de.metalcon.imageServer.protocol.Response;

public class ReadResponse extends Response {

	public void addNoImageIdentifierError(String imageIdentifierMissing) {
		this.parameterMissing(
				ProtocolConstants.Parameters.Read.IMAGE_IDENTIFIER,
				"The image identifier is missing. Please deliver one");

	}

}