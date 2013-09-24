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

	@SuppressWarnings("unused")
	private final int top;
	@SuppressWarnings("unused")
	private final int left;

	@SuppressWarnings("unused")
	private final int width;
	@SuppressWarnings("unused")
	private final int height;

	public CreateWithCroppingRequest(final String imageIdentifier,
			final InputStream imageStream, final String metaData,
			final int top, final int left, final int width, final int height) {
		this.imageIdentifier = imageIdentifier;
		this.imageStream = imageStream;
		this.metaData = metaData;
		this.left = left;
		this.top = top;
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
				final Integer top = checkTop(formItemList, response);
				if (top != null) {
					final Integer left = checkLeft(formItemList, response);
					if (left != null) {
						final Integer width = checkWidth(formItemList, response);
						if (width != null) {
							final Integer height = checkHeight(formItemList,
									response);
							if (height != null) {
								return new CreateWithCroppingRequest(
										imageIdentifier, imageStream, metaData,
										top, left, width, height);
							}
						}
					}
				}
			}
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
			int top = Integer.parseInt(formItemList
					.getField(ProtocolConstants.Parameters.Create.CROP_TOP));
			if (top >= 0) {
				return top;
			} else {
				response.cropTopCoordinateInvalid(top);
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
			int left = Integer.parseInt(formItemList
					.getField(ProtocolConstants.Parameters.Create.CROP_LEFT));
			if (left >= 0) {
				return left;
			} else {
				response.cropLeftCoordinateInvalid(left);
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
			int width = Integer.parseInt(formItemList
					.getField(ProtocolConstants.Parameters.Create.CROP_WIDTH));
			if (width >= 0) {
				return width;
			} else {
				response.cropWidthInvalid(width);
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
			int height = Integer.parseInt(formItemList
					.getField(ProtocolConstants.Parameters.Create.CROP_HEIGHT));
			if (height >= 0) {
				return height;
			} else {
				response.cropHeightInvalid(height);
			}
		} catch (final IllegalArgumentException e) {
			response.cropHeightMissing();
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
	 * @return String metaData if valid, else null
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
			response.metaDataMissing();
		}

		return null;
	}
}
