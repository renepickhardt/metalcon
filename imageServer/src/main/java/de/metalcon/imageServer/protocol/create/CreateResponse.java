package de.metalcon.imageServer.protocol.create;

import de.metalcon.imageServer.protocol.ProtocolConstants;
import de.metalcon.imageServer.protocol.Response;

public class CreateResponse extends Response {

	// TODO: write helpful solution messages!

	public void addNoIdentifierError() {
		this.parameterMissing(
				ProtocolConstants.Parameters.Create.IMAGE_IDENTIFIER,
				"The image identifier is missing. Please deliver one");
	}

	public void addNoImageStreamError() {
		this.parameterMissing(ProtocolConstants.Parameters.Create.IMAGESTREAM,
				"The image stream is missing. Please deliver one");
	}

	public void addAutoRotateFlagMissingError(String autorotateFlagMissing) {
		this.parameterMissing(
				ProtocolConstants.Parameters.Create.AUTOROTATE_FLAG,
				"The autoRotate flag is missing. Please deliver one");
	}

	public void addAutoRotateFlagMalformedError() {
		this.addStatusMessage(
				"request corrupt: parameter \"autoRotateFlag\" is malformed",
				"The autoRotate flag String is malformed");

	}

	public void addMetadataMalformedError() {
		this.addStatusMessage(

				ProtocolConstants.StatusMessage.Create.REQUEST_BROKEN_RESPONSE_BEGIN
						+ ProtocolConstants.Parameters.Create.META_DATA
						+ ProtocolConstants.StatusMessage.Create.REQUEST_BROKEN_RESPONSE_END,

				ProtocolConstants.Solution.Create.IMAGE_METADATA_MALFORMED);

	}

	public void addNoMetadataError() {
		this.parameterMissing(ProtocolConstants.Parameters.Create.META_DATA,
				"The image meta data is missing. Please deliver some");
	}

}