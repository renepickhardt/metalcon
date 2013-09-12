package de.metalcon.imageStorageServer.protocol.create;

import de.metalcon.imageStorageServer.protocol.ProtocolConstants;
import de.metalcon.imageStorageServer.protocol.Response;

public class CreateResponse extends Response {

	// TODO: write helpful solution messages for missing parameters!
	// TODO: JavaDoc

	public void imageIdentifierMissing() {
		this.parameterMissing(
				ProtocolConstants.Parameters.Create.IMAGE_IDENTIFIER,
				"The image identifier is missing. Please deliver one");
	}

	public void imageStreamMissing() {
		this.parameterMissing(ProtocolConstants.Parameters.Create.IMAGESTREAM,
				"The image stream is missing. Please deliver one");
	}

	public void autoRotateFlagMissing() {
		this.parameterMissing(
				ProtocolConstants.Parameters.Create.AUTOROTATE_FLAG,
				"The autoRotate flag is missing. Please deliver one");
	}

	public void metaDataMissing() {
		this.parameterMissing(ProtocolConstants.Parameters.Create.META_DATA,
				"The image meta data is missing. Please deliver some");
	}

	public void imageIdentifierInUse(final String imageIdentifier) {
		this.addStatusMessage(
				ProtocolConstants.StatusMessage.Create.IMAGE_IDENTIFIER_IN_USE,
				"There is already an image using the identifier \""
						+ imageIdentifier
						+ "\". Please provide another one that is not used yet.");
	}

	public void imageStreamInvalid() {
		this.addStatusMessage(
				ProtocolConstants.StatusMessage.Create.IMAGE_STREAM_INVALID,
				"The image stream passed is no valid image stream. Please provide a stream of an image encoded in one of the formats supported.");
	}

	public void imageUrlMalformed(final String imageUrl) {
		this.addStatusMessage(
				ProtocolConstants.StatusMessage.Create.IMAGE_URL_MALFORMED,
				"\""
						+ imageUrl
						+ "\" is not a valid URL. Please provide a valid URL referring to an image.");
	}

	public void imageUrlInvalid(final String imageUrl) {
		this.addStatusMessage(
				ProtocolConstants.StatusMessage.Create.IMAGE_URL_INVALID,
				"The URL \""
						+ imageUrl
						+ "\" does not lead to an existing image. Please provide an URL that referrs to an image encoded in one of the formats supported.");
	}

	public void autoRotateFlagMalformed(final String autoRotateFlag) {
		this.addStatusMessage(
				ProtocolConstants.StatusMessage.Create.AUTOROTATE_FLAG_MALFORMED,
				"\""
						+ autoRotateFlag
						+ "\" is not a number. Please provide a number such as \"1\" to enable or \"0\" to disable automatic rotation.");
	}

	public void metaDataMalformed() {
		this.addStatusMessage(
				ProtocolConstants.StatusMessage.Create.META_DATA_MALFORMED,
				"Please pass a JSON object that contains the meta data you want to store or leave the field blank if you do not want to store meta data at all.");
	}

}