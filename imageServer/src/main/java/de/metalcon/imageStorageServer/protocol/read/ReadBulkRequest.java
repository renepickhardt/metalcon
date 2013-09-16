package de.metalcon.imageStorageServer.protocol.read;

import de.metalcon.imageStorageServer.protocol.ProtocolConstants;
import de.metalcon.utils.FormItemList;

public class ReadBulkRequest {
	private final String imageIdentifierList;

	private final Integer imageWidth;

	private final Integer imageHeight;

	public ReadBulkRequest(final String imageIdentifierList,
			final Integer imageWidth, final Integer imageHeight) {
		this.imageIdentifierList = imageIdentifierList;
		this.imageWidth = imageWidth;
		this.imageHeight = imageHeight;
	}

	public static ReadBulkRequest checkRequest(FormItemList formItemList,
			ReadResponse readResponse) {
		final String imageIdentifierList = checkImageIdentifierList(
				formItemList, readResponse);
		if (imageIdentifierList != null) {
			final Integer imageWidth = checkImageWidth(formItemList,
					readResponse);
			if (imageWidth != null) {
				final Integer imageHeight = checkImageHeight(formItemList,
						readResponse);
				if (imageWidth != null) {
					return new ReadBulkRequest(imageIdentifierList, imageWidth,
							imageHeight);
				}
			}
		}
		return null;
	}

	private static String checkImageIdentifierList(
			final FormItemList formItemList, final ReadResponse response) {
		try {
			return formItemList
					.getField(ProtocolConstants.Parameters.Read.IMAGE_IDENTIFIER);
		} catch (final IllegalArgumentException e) {
			response.addNoImageIdentifierError();
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
}
