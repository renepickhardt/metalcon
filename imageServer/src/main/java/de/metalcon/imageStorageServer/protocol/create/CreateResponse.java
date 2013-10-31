package de.metalcon.imageStorageServer.protocol.create;

import de.metalcon.imageStorageServer.protocol.ProtocolConstants;
import de.metalcon.imageStorageServer.protocol.Response;

/**
 * Response class for any type of ISS create request
 * 
 * @author Sebastian Schlicht, Christian Schowalter
 * 
 */
public class CreateResponse extends Response {

	// TODO: JavaDoc

	private static final String SUPPORTED_FORMATS = "JPEG, PNG";

	public void imageIdentifierMissing() {
		this.parameterMissing(
				ProtocolConstants.Parameters.Create.IMAGE_IDENTIFIER,
				"Please provide an image identifier that is not used yet.");
	}

	public void imageIdentifierInUse(final String imageIdentifier) {
		this.addStatusMessage(
				ProtocolConstants.StatusMessage.Create.IMAGE_IDENTIFIER_IN_USE,
				"There is already an image using the identifier \""
						+ imageIdentifier
						+ "\". Please provide an unused image identifier.");
	}

	public void imageStreamMissing() {
		this.parameterMissing(ProtocolConstants.Parameters.Create.IMAGE_STREAM,
				"Please provide a stream of an image having one of the formats supported. ("
						+ SUPPORTED_FORMATS + ")");
	}

	public void imageStreamInvalid() {
		this.addStatusMessage(
				ProtocolConstants.StatusMessage.Create.IMAGE_STREAM_INVALID,
				"The stream passed is not a supported image stream. Please provide a stream of an image having one of the formats supported. ("
						+ SUPPORTED_FORMATS + ")");
	}

	public void autoRotateFlagMalformed(final String autoRotateFlag) {
		this.addStatusMessage(
				ProtocolConstants.StatusMessage.Create.AUTOROTATE_FLAG_MALFORMED,
				"\""
						+ autoRotateFlag
						+ "\" is not a flag value. Please pass \"1\" to enable automatic rotation, pass \"0\" or leave the field blank if you do not want to use automatic rotation.");
	}

	public void metaDataMalformed() {
		this.addStatusMessage(
				ProtocolConstants.StatusMessage.Create.META_DATA_MALFORMED,
				"Please pass a JSON object that contains the meta data you want to store or leave the field blank if you do not want to store meta data at all.");
	}

	public void imageUrlMissing() {
		this.parameterMissing(
				ProtocolConstants.Parameters.Create.URL,
				"An URL is needed to locate the image you want to create. Please pass an URL referring to an image having one of the formats supported. ("
						+ SUPPORTED_FORMATS + ")");

	}

	public void imageUrlMalformed(final String imageUrl) {
		this.addStatusMessage(
				ProtocolConstants.StatusMessage.Create.IMAGE_URL_MALFORMED,
				"\""
						+ imageUrl
						+ "\" is not an URL. Please pass an URL referring to an image having one of the formats supported. ("
						+ SUPPORTED_FORMATS + ")");
	}

	public void imageUrlInvalid(final String imageUrl) {
		this.addStatusMessage(
				ProtocolConstants.StatusMessage.Create.IMAGE_URL_INVALID,
				"The URL \""
						+ imageUrl
						+ "\" does not lead to a valid image. Please pass an URL referring to an image having one of the formats supported. ("
						+ SUPPORTED_FORMATS + ")");
	}

	public void cropLeftCoordinateMissing() {
		this.parameterMissing(
				ProtocolConstants.Parameters.Create.CROP_LEFT,
				"The distance to the left border of the image is needed to place the cropping frame. Please provide a non-negative number being less than the image width.");
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
						+ " Please provide a number to specify the distance to the left border of the image.");
	}

	public void cropLeftCoordinateInvalid(final int left, final String solution) {
		this.addStatusMessage(
				ProtocolConstants.StatusMessage.Create.CROP_LEFT_INVALID,
				solution);
	}

	public void cropTopCoordinateMissing() {
		this.parameterMissing(
				ProtocolConstants.Parameters.Create.CROP_TOP,
				"The distance to the upper border of the image is needed to place the cropping frame. Please provide a non-negative number being less than the image height.");
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
						+ " Please provide a number to specify the distance to the upper border of the image.");
	}

	public void cropTopCoordinateInvalid(final int top, final String solution) {
		this.addStatusMessage(
				ProtocolConstants.StatusMessage.Create.CROP_TOP_INVALID,
				solution);
	}

	public void cropWidthMissing() {
		this.parameterMissing(
				ProtocolConstants.Parameters.Create.CROP_WIDTH,
				"The width of the cropping frame is needed. Please provide a number greater than zero being valid within the image boundary.");
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
						+ " Please provide a number to specify the width of the cropping frame.");
	}

	public void cropWidthInvalid(final int width, final String solution) {
		this.addStatusMessage(
				ProtocolConstants.StatusMessage.Create.CROP_WIDTH_INVALID,
				solution);
	}

	public void cropHeightMissing() {
		this.parameterMissing(
				ProtocolConstants.Parameters.Create.CROP_HEIGHT,
				"The height of the cropping frame is needed. Please provide a number greater than zero being valid within the image boundary.");
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
						+ " Please provide a number to specify the height of the cropping frame.");
	}

	public void cropHeightInvalid(final int height, final String solution) {
		this.addStatusMessage(
				ProtocolConstants.StatusMessage.Create.CROP_HEIGHT_INVALID,
				solution);

	}

}