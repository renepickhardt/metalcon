package de.metalcon.imageServer.protocol.delete;

import de.metalcon.imageServer.protocol.ProtocolConstants;
import de.metalcon.imageServer.protocol.Response;

public class DeleteResponse extends Response {

	// TODO: write helpful solution messages for missing parameters!
	// TODO: JavaDoc

	public void imageIdentifierMissing() {
		this.parameterMissing(
				ProtocolConstants.Parameters.Delete.IMAGE_IDENTIFIER,
				"The image identifier is missing. Please deliver one");

	}

	public void imageNotExisting(final String imageIdentifier) {
		this.addStatusMessage(
				ProtocolConstants.StatusMessage.Delete.IMAGE_NOT_EXISTING,
				"There is no image having the identifier \""
						+ imageIdentifier
						+ "\". Please provide an identifier used by an existing image.");
	}

}