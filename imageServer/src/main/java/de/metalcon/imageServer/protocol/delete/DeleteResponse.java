package de.metalcon.imageServer.protocol.delete;

import de.metalcon.imageServer.protocol.ProtocolConstants;
import de.metalcon.imageServer.protocol.Response;

public class DeleteResponse extends Response {

	public void addNoImageIdentifierError() {
		this.parameterMissing(
				ProtocolConstants.Parameters.Delete.IMAGE_IDENTIFIER,
				"The image identifier is missing. Please deliver one");

	}

	public void addImageNotFoundError() {
		this.addStatusMessage(
				ProtocolConstants.StatusMessage.Delete.NO_IMAGE_FOUND,
				ProtocolConstants.Solution.Delete.NO_IMAGE_FOUND);
	}

}