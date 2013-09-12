package de.metalcon.imageStorageServer.protocol.read;

import org.json.simple.parser.JSONParser;

import de.metalcon.imageStorageServer.protocol.ProtocolConstants;
import de.metalcon.utils.FormItemList;

public class ReadRequest {

	private static final JSONParser PARSER = new JSONParser();

	private final String imageIdentifier;

	private final Boolean originalImageFlag;

	private Integer imageWidth;

	private Integer imageHeight;

	public ReadRequest(final String imageIdentifier,
			final Boolean originalImageFlag) {
		this.imageIdentifier = imageIdentifier;
		this.originalImageFlag = originalImageFlag;
	}

	public ReadRequest(final String imageIdentifier,
			final Boolean originalImageFlag, final Integer imageWidth,
			final Integer imageHeight) {
		this.imageIdentifier = imageIdentifier;
		this.originalImageFlag = originalImageFlag;
		this.imageWidth = imageWidth;
		this.imageHeight = imageHeight;
	}

	public String getImageIdentifier() {
		return this.imageIdentifier;
	}

	public Boolean getOriginalImageFlag() {
		return this.originalImageFlag;
	}

	public static ReadRequest checkRequest(final FormItemList formItemList,
			final ReadResponse response) {
		final String imageIdentifier = checkImageIdentifier(formItemList,
				response);
		if (imageIdentifier != null) {
			final Boolean originalImageFlag = checkOriginalImageFlag(
					formItemList, response);
			if (originalImageFlag != null) {
				return new ReadRequest(imageIdentifier, originalImageFlag);
			}
		}
		return null;
	}

	public static ReadRequest checkRequestWithScaling(
			FormItemList formItemList, ReadResponse readResponse) {
		final String imageIdentifier = checkImageIdentifier(formItemList,
				readResponse);
		if (imageIdentifier != null) {
			final Boolean originalImageFlag = checkOriginalImageFlag(
					formItemList, readResponse);
			if (originalImageFlag != null) {
				final Integer imageWidth = checkImageWidth(formItemList,
						readResponse);
				if (imageWidth != null) {
					final Integer imageHeight = checkImageHeight(formItemList,
							readResponse);
					if (imageWidth != null) {
						return new ReadRequest(imageIdentifier,
								originalImageFlag, imageWidth, imageHeight);
					}
				}
			}
		}
		return null;
	}

	private static Integer checkImageHeight(FormItemList formItemList,
			ReadResponse response) {
		String imageHeightString = null;
		try {
			imageHeightString = formItemList
					.getField(ProtocolConstants.Parameters.Read.IMAGE_HEIGHT);
		} catch (IllegalArgumentException e) {
			response.addNoImageHeightError();
			return null;
		}
		try {
			Integer imageHeightInteger = Integer.parseInt(imageHeightString);
			return imageHeightInteger;
		} catch (NumberFormatException e) {
			response.addImageHeightMalformedError();
			return null;
		}
	}

	private static Integer checkImageWidth(FormItemList formItemList,
			ReadResponse response) {
		String imageWidthString = null;
		try {
			imageWidthString = formItemList
					.getField(ProtocolConstants.Parameters.Read.IMAGE_WIDTH);
		} catch (IllegalArgumentException e) {
			response.addNoImageWidthError();
			return null;
		}
		try {
			Integer imageWidthInteger = Integer.parseInt(imageWidthString);
			return imageWidthInteger;
		} catch (NumberFormatException e) {
			response.addImageWidthMalformedError();
			return null;
		}
	}

	private static Boolean checkOriginalImageFlag(FormItemList formItemList,
			ReadResponse response) {
		String originalImageFlagString = null;
		try {
			originalImageFlagString = formItemList
					.getField(ProtocolConstants.Parameters.Read.ORIGINAL_FLAG);
		} catch (IllegalArgumentException e) {
			response.addNoOriginalFlagError();
			return null;
		}
		try {
			Integer originalImageFlagInteger = Integer
					.parseInt(originalImageFlagString);
			if (originalImageFlagInteger == 0) {
				return false;
			}
			return true;
		} catch (NumberFormatException e) {
			response.addOriginalImageFlagMalformedError();
			return null;
		}
	}

	private static String checkImageIdentifier(FormItemList formItemList,
			ReadResponse response) {
		String imageIdentifier = null;
		try {
			imageIdentifier = formItemList
					.getField(ProtocolConstants.Parameters.Read.IMAGE_IDENTIFIER);
		} catch (IllegalArgumentException e) {
			response.addNoImageIdentifierError();
			return null;
		}
		return imageIdentifier;

	}

}