package de.metalcon.imageStorageServer.protocol.create;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import de.metalcon.imageStorageServer.protocol.ProtocolConstants;
import de.metalcon.utils.FormFile;
import de.metalcon.utils.FormItemList;

public class CreateWithCroppingRequest {
	// TODO: JavaDoc

	private static final JSONParser PARSER = new JSONParser();

	private final String imageIdentifier;

	private final FormFile imageStream;

	private final String metaData;

	private final int top;
	private final int left;

	private final int width;
	private final int height;

	public CreateWithCroppingRequest(final String imageIdentifier,
			final FormFile imageStream, final String metaData, final int top,
			final int left, final int width, final int height) {
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

	public FormFile getImageStream() {
		return this.imageStream;
	}

	public String getMetaData() {
		return this.metaData;
	}

	public static CreateWithCroppingRequest checkRequest(
			final FormItemList formItemList, final CreateResponse response) {

		final String imageIdentifier = checkImageIdentifier(formItemList,
				response);
		if (imageIdentifier != null) {
			final FormFile imageStream = checkImageStream(formItemList,
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

	// TODO should this test also check if the value is >= 0?
	private static Integer checkTop(final FormItemList formItemList,
			final CreateResponse response) {
		try {
			return Integer.parseInt(formItemList
					.getField(ProtocolConstants.Parameters.Create.CROP_TOP));
		} catch (final IllegalArgumentException e) {
			response.cropTopCoordinateMissing();
		}
		return null;
	}

	private static Integer checkLeft(final FormItemList formItemList,
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

	private static Integer checkWidth(final FormItemList formItemList,
			final CreateResponse response) {
		try {
			return Integer.parseInt(formItemList
					.getField(ProtocolConstants.Parameters.Create.CROP_WIDTH));
		} catch (final IllegalArgumentException e) {
			response.cropWidthMissing();
		}
		return null;
	}

	private static Integer checkHeight(final FormItemList formItemList,
			final CreateResponse response) {
		try {
			return Integer.parseInt(formItemList
					.getField(ProtocolConstants.Parameters.Create.CROP_HEIGHT));
		} catch (final IllegalArgumentException e) {
			response.cropHeightMissing();
		}
		return null;
	}

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

	protected static FormFile checkImageStream(final FormItemList formItemList,
			final CreateResponse response) {
		try {
			return formItemList
					.getFile(ProtocolConstants.Parameters.Create.IMAGESTREAM);
		} catch (final IllegalArgumentException e) {
			response.imageStreamMissing();
		}

		return null;
	}

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
