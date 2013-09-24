package de.metalcon.imageStorageServer.protocol.read;

import de.metalcon.imageStorageServer.protocol.ProtocolConstants;
import de.metalcon.utils.FormItemList;

public class ReadScaledRequest {

	@SuppressWarnings("unused")
	private final String imageIdentifier;

	@SuppressWarnings("unused")
	private final Integer imageWidth;

	@SuppressWarnings("unused")
	private final Integer imageHeight;

	public ReadScaledRequest(final String imageIdentifier,
			final Integer imageWidth, final Integer imageHeight) {
		this.imageIdentifier = imageIdentifier;
		this.imageWidth = imageWidth;
		this.imageHeight = imageHeight;
	}

	public static ReadScaledRequest checkRequest(FormItemList formItemList,
			ReadResponse readResponse) {
		final String imageIdentifier = checkImageIdentifier(formItemList,
				readResponse);
		if (imageIdentifier != null) {
			final Integer imageWidth = checkImageWidth(formItemList,
					readResponse);
			if (imageWidth != null) {
				final Integer imageHeight = checkImageHeight(formItemList,
						readResponse);
				if (imageHeight != null) {
					return new ReadScaledRequest(imageIdentifier, imageWidth,
							imageHeight);
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
			if (imageHeightInteger > 0) {
				return imageHeightInteger;
			} else {
				response.addImageHeightToSmallError(imageHeightInteger);
			}
		} catch (NumberFormatException e) {
			response.addImageHeightMalformedError();

		}
		return null;
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
			if (imageWidthInteger > 0) {
				return imageWidthInteger;
			} else {
				response.addImageWidthToSmallError(imageWidthInteger);
			}
		} catch (NumberFormatException e) {
			response.addImageWidthMalformedError();

		}
		return null;
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
