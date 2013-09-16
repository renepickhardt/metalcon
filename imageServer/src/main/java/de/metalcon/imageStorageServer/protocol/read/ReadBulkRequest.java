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

	public static ReadScaledRequest checkRequest(FormItemList formItemList,
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
					return new ReadScaledRequest(imageIdentifierList,
							imageWidth, imageHeight);
				}
			}
		}
		return null;
	}

	private static String checkImageIdentifierList(FormItemList formItemList,
			ReadResponse readResponse) {
		// TODO Auto-generated method stub
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
}
