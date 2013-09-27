package de.metalcon.imageStorageServer.protocol.create;

import de.metalcon.imageStorageServer.protocol.ProtocolConstants;
import de.metalcon.imageStorageServer.protocol.Response;

/**
 * Response class for any type of ISS create request
 * 
 * @author Christian Schowalter
 * 
 */
public class CreateResponse extends Response {

	// TODO: write helpful solution messages for missing parameters!
	// TODO: JavaDoc

	/**
	 * Adds an error message for missing image identifier parameter to the
	 * response.
	 */
	public void imageIdentifierMissing() {
		this.parameterMissing(
				ProtocolConstants.Parameters.Create.IMAGE_IDENTIFIER,
				"The image identifier is missing. Please deliver one");
	}

	/**
	 * Adds an error message about the requested image identifier already being
	 * in use to the response.
	 */
	public void imageIdentifierInUse(final String imageIdentifier) {
		this.addStatusMessage(
				ProtocolConstants.StatusMessage.Create.IMAGE_IDENTIFIER_IN_USE,
				"There is already an image using the identifier \""
						+ imageIdentifier
						+ "\". Please provide another one that is not used yet.");
	}

	/**
	 * Adds an error message for missing image stream to the response.
	 */
	public void imageStreamMissing() {
		this.parameterMissing(ProtocolConstants.Parameters.Create.IMAGE_STREAM,
				"The image stream is missing. Please deliver one");
	}

	/**
	 * Adds an error message for an invalid image stream to the response.
	 */
	public void imageStreamInvalid() {
		this.addStatusMessage(
				ProtocolConstants.StatusMessage.Create.IMAGE_STREAM_INVALID,
				"The image stream passed is no valid image stream. Please provide a stream of an image encoded in one of the formats supported.");
	}

	/**
	 * Adds an error message for missing auto rotate flag to the response.
	 */
	public void autoRotateFlagMissing() {
		this.parameterMissing(
				ProtocolConstants.Parameters.Create.AUTOROTATE_FLAG,
				"The autoRotate flag is missing. Please deliver one");
	}

	/**
	 * Adds an error message for a malformed auto rotate flag String to the
	 * response.
	 */
	public void autoRotateFlagMalformed(final String autoRotateFlag) {
		this.addStatusMessage(
				ProtocolConstants.StatusMessage.Create.AUTOROTATE_FLAG_MALFORMED,
				"\""
						+ autoRotateFlag
						+ "\" is not a number. Please provide a number such as \"1\" to enable or \"0\" to disable automatic rotation.");
	}

	/**
	 * Adds an error message for missing meta data to the response.
	 */
	public void metaDataMissing() {
		this.parameterMissing(ProtocolConstants.Parameters.Create.META_DATA,
				"The image meta data is missing. Please deliver some");
	}

	/**
	 * Adds an error message for a malformed meta data String to the response.
	 */
	public void metaDataMalformed() {
		this.addStatusMessage(
				ProtocolConstants.StatusMessage.Create.META_DATA_MALFORMED,
				"Please pass a JSON object that contains the meta data you want to store or leave the field blank if you do not want to store meta data at all.");
	}

	public void imageUrlMissing() {
		this.parameterMissing(ProtocolConstants.Parameters.Create.URL,
				"The image URL is missing. Please deliver one");

	}

	/**
	 * Adds an error message for a malformed URL to the response. This means the
	 * URL can't be parsed correctly.
	 */
	public void imageUrlMalformed(final String imageUrl) {
		this.addStatusMessage(
				ProtocolConstants.StatusMessage.Create.IMAGE_URL_MALFORMED,
				"\""
						+ imageUrl
						+ "\" is not a valid URL. Please provide a valid URL referring to an image.");
	}

	/**
	 * Adds an error message for an invalid URL to the response. This refers to
	 * the URL not leading to an image file.
	 */
	public void imageUrlInvalid(final String imageUrl) {
		this.addStatusMessage(
				ProtocolConstants.StatusMessage.Create.IMAGE_URL_INVALID,
				"The URL \""
						+ imageUrl
						+ "\" does not lead to an existing image. Please provide an URL that referrs to an image encoded in one of the formats supported.");
	}

	/**
	 * Adds an error message for missing left cropping coordinate parameter to
	 * the response.
	 */
	public void cropLeftCoordinateMissing() {
		this.parameterMissing(ProtocolConstants.Parameters.Create.CROP_LEFT,
				"The cropping coordinate left is missing. Please deliver one");
	}

	/**
	 * add status message: left cropping coordinate malformed
	 * 
	 * @param left
	 *            left cropping coordinate passed
	 */
	public void cropLeftCoordinateMalformed(final String left) {
		this.addStatusMessage(
				ProtocolConstants.StatusMessage.Create.CROP_LEFT_MALFORMED,
				"\""
						+ left
						+ "\" is not a number."
						+ " Please provide a number to specify the left cropping coordinate.");
	}

	/**
	 * Adds an error message for invalid left cropping coordinate parameter to
	 * the response.
	 */
	public void cropLeftCoordinateInvalid(int left) {
		this.addStatusMessage(
				ProtocolConstants.StatusMessage.Create.CROP_LEFT_INVALID,
				"The left-hand cropping coordinate is" + left
						+ ". This value should be greater or equals 0");
	}

	/**
	 * Adds an error message for missing top cropping coordinate parameter to
	 * the response.
	 */
	public void cropTopCoordinateMissing() {
		this.parameterMissing(ProtocolConstants.Parameters.Create.CROP_TOP,
				"The cropping coordinate top is missing. Please deliver one");
	}

	/**
	 * add status message: top cropping coordinate malformed
	 * 
	 * @param top
	 *            top cropping coordinate passed
	 */
	public void cropTopCoordinateMalformed(final String top) {
		this.addStatusMessage(
				ProtocolConstants.StatusMessage.Create.CROP_TOP_MALFORMED,
				"\""
						+ top
						+ "\" is not a number."
						+ " Please provide a number to specify the upper cropping coordinate.");
	}

	/**
	 * Adds an error message for invalid top cropping coordinate parameter to
	 * the response.
	 */
	public void cropTopCoordinateInvalid(int top) {
		this.addStatusMessage(
				ProtocolConstants.StatusMessage.Create.CROP_TOP_INVALID,
				"The top side cropping coordinate is" + top
						+ ". This value should be greater or equals 0");
	}

	/**
	 * Adds an error message for missing cropping width parameter to the
	 * response.
	 */
	public void cropWidthMissing() {
		this.parameterMissing(ProtocolConstants.Parameters.Create.CROP_WIDTH,
				"The cropping width is missing. Please deliver one");
	}

	/**
	 * add status message: cropping width malformed
	 * 
	 * @param width
	 *            cropping width passed
	 */
	public void cropWidthMalformed(final String width) {
		this.addStatusMessage(
				ProtocolConstants.StatusMessage.Create.CROP_WIDTH_MALFORMED,
				"\""
						+ width
						+ "\" is not a number."
						+ " Please provide a number to specify the cropping width.");
	}

	/**
	 * Adds an error message for invalid cropping width parameter to the
	 * response.
	 */
	public void cropWidthInvalid(int width) {
		this.addStatusMessage(
				ProtocolConstants.StatusMessage.Create.CROP_WIDTH_INVALID,
				"The top side cropping coordinate is" + width
						+ ". This value should be greater or equals 0");

	}

	/**
	 * Adds an error message for missing cropping height parameter to the
	 * response.
	 */
	public void cropHeightMissing() {
		this.parameterMissing(ProtocolConstants.Parameters.Create.CROP_HEIGHT,
				"The cropping height is missing. Please deliver one");
	}

	/**
	 * add status message: cropping height malformed
	 * 
	 * @param width
	 *            cropping height passed
	 */
	public void cropHeightMalformed(final String height) {
		this.addStatusMessage(
				ProtocolConstants.StatusMessage.Create.CROP_HEIGHT_MALFORMED,
				"\""
						+ height
						+ "\" is not a number."
						+ " Please provide a number to specify the cropping height.");
	}

	/**
	 * Adds an error message for invalid cropping height parameter to the
	 * response.
	 */
	public void cropHeightInvalid(int height) {
		this.addStatusMessage(
				ProtocolConstants.StatusMessage.Create.CROP_HEIGHT_INVALID,
				"The top side cropping coordinate is" + height
						+ ". This value should be greater or equals 0");

	}

}