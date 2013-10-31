package de.metalcon.imageStorageServer.protocol.create;

import java.io.IOException;
import java.io.InputStream;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import de.metalcon.imageStorageServer.protocol.ProtocolConstants;
import de.metalcon.utils.FormFile;
import de.metalcon.utils.FormItemList;

/**
 * 
 * @author Christian Schowalter
 * 
 */
public class CreateWithCroppingRequest {
	// TODO: JavaDoc

	private static final JSONParser PARSER = new JSONParser();

	private final String imageIdentifier;

	private final InputStream imageStream;

	private final String metaData;

	private final int coordinateTop;
	private final int coordinateLeft;

	private final int width;
	private final int height;

	public CreateWithCroppingRequest(final String imageIdentifier,
			final InputStream imageStream, final String metaData,
			final int coordinateLeft, final int coordinateTop, final int width,
			final int height) {
		this.imageIdentifier = imageIdentifier;
		this.imageStream = imageStream;
		this.metaData = metaData;

		this.coordinateLeft = coordinateLeft;
		this.coordinateTop = coordinateTop;

		this.width = width;
		this.height = height;
	}

	public String getImageIdentifier() {
		return this.imageIdentifier;
	}

	public InputStream getImageStream() {
		return this.imageStream;
	}

	public String getMetaData() {
		return this.metaData;
	}

	public int getCoordinateLeft() {
		return this.coordinateLeft;
	}

	public int getCoordinateTop() {
		return this.coordinateTop;
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}

	/**
	 * Checks if the FormItemList contains all required objects.
	 * 
	 * @param formItemList
	 * @param response
	 * @return CreateWithCroppingRequest, if all needed objects are found, else
	 *         null
	 */
	public static CreateWithCroppingRequest checkRequest(
			final FormItemList formItemList, final CreateResponse response) {

		final String imageIdentifier = checkImageIdentifier(formItemList,
				response);
		if (imageIdentifier != null) {
			final InputStream imageStream = checkImageStream(formItemList,
					response);
			if (imageStream != null) {
				final String metaData = checkMetaData(formItemList, response);
				if ((metaData != null) || !response.getRequestErrorFlag()) {
					final Integer top = checkTop(formItemList, response);
					if (top != null) {
						final Integer left = checkLeft(formItemList, response);
						if (left != null) {
							final Integer width = checkWidth(formItemList,
									response);
							if (width != null) {
								final Integer height = checkHeight(
										formItemList, response);
								if (height != null) {
									return new CreateWithCroppingRequest(
											imageIdentifier, imageStream,
											metaData, left, top, width, height);
								}
							}
						}
					}
				}
			}
		}

		return null;
	}

	/**
	 * Extracts the image identifier field from the FormItemList and checks for
	 * protocol compliance
	 * 
	 * @param formItemList
	 * @param response
	 * @return String imageIdentifier if valid, else null
	 */
	protected static String checkImageIdentifier(
			final FormItemList formItemList, final CreateResponse response) {
		try {
			return formItemList
					.getField(ProtocolConstants.Parameters.Create.IMAGE_IDENTIFIER);
		} catch (final IllegalArgumentException e) {
			response.imageIdentifierMissing();
		}

		return null;
	}

	/**
	 * Extracts the ImageStream field from the FormItemList and checks for
	 * protocol compliance
	 * 
	 * @param formItemList
	 * @param response
	 * @return FormFile imageStream if valid, else null
	 */
	protected static InputStream checkImageStream(
			final FormItemList formItemList, final CreateResponse response) {
		try {
			final FormFile imageItem = formItemList
					.getFile(ProtocolConstants.Parameters.Create.IMAGE_ITEM);
			if (imageItem != null) {
				return imageItem.getFormItem().getInputStream();
			}

		} catch (final IllegalArgumentException e) {
			response.imageStreamMissing();
		} catch (final IOException e) {
			response.internalServerError();
		}

		return null;
	}

	/**
	 * Extracts the meta data field from the FormItemList and checks for
	 * protocol compliance
	 * 
	 * @param formItemList
	 * @param response
	 * @return valid meta data<br>
	 *         <b>null</b> if field missing or malformed
	 */
	protected static String checkMetaData(final FormItemList formItemList,
			final CreateResponse response) {
		try {
			final String metaData = formItemList
					.getField(ProtocolConstants.Parameters.Create.META_DATA);
			try {
				PARSER.parse(metaData);
				return metaData;
			} catch (final ParseException e) {
				response.metaDataMalformed();
			}
		} catch (final IllegalArgumentException e) {
			// optional meta data missing
		}

		return null;
	}

	/**
	 * Extracts the top coordinate field from the FormItemList and checks for
	 * protocol compliance
	 * 
	 * @param formItemList
	 * @param response
	 * @return Integer top if valid, else null
	 */
	protected static Integer checkTop(final FormItemList formItemList,
			final CreateResponse response) {
		try {
			final String sTop = formItemList
					.getField(ProtocolConstants.Parameters.Create.CROP_TOP);

			try {
				final int top = Integer.parseInt(sTop);
				if (top >= 0) {
					return top;
				}

				response.cropTopCoordinateInvalid(top,
						"Value must be equal to or greater than zero.");

			} catch (final NumberFormatException e) {
				response.cropTopCoordinateMalformed(sTop);
			}
		} catch (final IllegalArgumentException e) {
			response.cropTopCoordinateMissing();
		}

		return null;
	}

	/**
	 * Extracts the left coordinate field from the FormItemList and checks for
	 * protocol compliance
	 * 
	 * @param formItemList
	 * @param response
	 * @return Integer left if valid, else null
	 */
	protected static Integer checkLeft(final FormItemList formItemList,
			final CreateResponse response) {
		try {
			final String sLeft = formItemList
					.getField(ProtocolConstants.Parameters.Create.CROP_LEFT);

			try {
				final int left = Integer.parseInt(sLeft);
				if (left >= 0) {
					return left;
				}

				response.cropLeftCoordinateInvalid(left,
						"Value must be equal to or greater than zero.");

			} catch (final NumberFormatException e) {
				response.cropLeftCoordinateMalformed(sLeft);
			}
		} catch (final IllegalArgumentException e) {
			response.cropLeftCoordinateMissing();
		}

		return null;
	}

	/**
	 * Extracts the width field from the FormItemList and checks for protocol
	 * compliance
	 * 
	 * @param formItemList
	 * @param response
	 * @return Integer width if valid, else null
	 */
	protected static Integer checkWidth(final FormItemList formItemList,
			final CreateResponse response) {
		try {
			final String sWidth = formItemList
					.getField(ProtocolConstants.Parameters.Create.CROP_WIDTH);

			try {
				final int width = Integer.parseInt(sWidth);
				if (width > 0) {
					return width;
				}

				response.cropWidthInvalid(width,
						"Value must be greater than zero.");

			} catch (final NumberFormatException e) {
				response.cropWidthMalformed(sWidth);
			}
		} catch (final IllegalArgumentException e) {
			response.cropWidthMissing();
		}

		return null;
	}

	/**
	 * Extracts the height field from the FormItemList and checks for protocol
	 * compliance
	 * 
	 * @param formItemList
	 * @param response
	 * @return Integer height if valid, else null
	 */
	protected static Integer checkHeight(final FormItemList formItemList,
			final CreateResponse response) {
		try {
			final String sHeight = formItemList
					.getField(ProtocolConstants.Parameters.Create.CROP_HEIGHT);

			try {
				final int height = Integer.parseInt(sHeight);
				if (height > 0) {
					return height;
				}

				response.cropHeightInvalid(height,
						"Value must be greater than zero.");

			} catch (final NumberFormatException e) {
				response.cropHeightMalformed(sHeight);
			}
		} catch (final IllegalArgumentException e) {
			response.cropHeightMissing();
		}

		return null;
	}

}