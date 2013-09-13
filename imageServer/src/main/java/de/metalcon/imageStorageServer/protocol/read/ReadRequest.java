package de.metalcon.imageStorageServer.protocol.read;

import de.metalcon.imageStorageServer.protocol.ProtocolConstants;
import de.metalcon.utils.FormItemList;

public class ReadRequest {

	private final String imageIdentifier;

	public ReadRequest(final String imageIdentifier,
			final Boolean originalImageFlag) {
		this.imageIdentifier = imageIdentifier;
	}

	public String getImageIdentifier() {
		return this.imageIdentifier;
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

	private static String checkImageIdentifier(final FormItemList formItemList,
			final ReadResponse response) {
		try {
			return formItemList
					.getField(ProtocolConstants.Parameters.Read.IMAGE_IDENTIFIER);
		} catch (final IllegalArgumentException e) {
			response.addNoImageIdentifierError();
		}

		return null;

	}

}