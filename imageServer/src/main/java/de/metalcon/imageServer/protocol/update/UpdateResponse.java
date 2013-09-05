package de.metalcon.imageServer.protocol.update;

import de.metalcon.imageServer.protocol.ProtocolConstants;
import de.metalcon.imageServer.protocol.Response;

public class UpdateResponse extends Response {

	public void addNoImageIdentifierError() {
		this.parameterMissing(
				ProtocolConstants.Parameters.Update.IMAGE_IDENTIFIER,
				"The image identifier is missing. Please deliver one");
	}

}