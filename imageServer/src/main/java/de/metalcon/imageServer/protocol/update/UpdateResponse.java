package de.metalcon.imageServer.protocol.update;

import de.metalcon.imageServer.protocol.ProtocolConstants;
import de.metalcon.imageServer.protocol.Response;

public class UpdateResponse extends Response {

	// TODO: write helpful solution messages for missing parameters!
	// TODO: JavaDoc

	public void imageIdentifierMissing() {
		this.parameterMissing(
				ProtocolConstants.Parameters.Update.IMAGE_IDENTIFIER,
				"The image identifier is missing. Please deliver one");
	}

	public void metaDataMissing() {
		this.parameterMissing(ProtocolConstants.Parameters.Update.META_DATA,
				"The image meta data is missing. Please deliver some");
	}

	public void imageNotExisting(final String imageIdentifier) {
		this.addStatusMessage(
				ProtocolConstants.StatusMessage.Update.IMAGE_NOT_EXISTING,
				"There is no image having the identifier \""
						+ imageIdentifier
						+ "\". Please provide an identifier used by an existing image.");
	}

	public void metaDataMalformed() {
		this.addStatusMessage(
				ProtocolConstants.StatusMessage.Update.META_DATA_MALFORMED,
				"Please pass a JSON object that contains the meta data you want to append/update.");
	}

}