package de.metalcon.imageStorageServer.protocol.read;

import de.metalcon.imageStorageServer.protocol.ProtocolConstants;
import de.metalcon.utils.FormItemList;

public class ReadScaledRequest {

	private final String imageIdentifier;

	private final int imageWidth;
	private final int imageHeight;

	public ReadScaledRequest(final String imageIdentifier,
			final int imageWidth, final int imageHeight) {
		this.imageIdentifier = imageIdentifier;
		this.imageWidth = imageWidth;
		this.imageHeight = imageHeight;
	}

	public String getImageIdentifier() {
		return this.imageIdentifier;
	}

	public int getImageWidth() {
		return this.imageWidth;
	}

	public int getImageHeight() {
		return this.imageHeight;
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

	private static Integer checkImageWidth(FormItemList formItemList,
			ReadResponse response) {
		try {
			final String sWidth = formItemList
					.getField(ProtocolConstants.Parameters.Read.IMAGE_WIDTH);

			try {
				final int width = Integer.parseInt(sWidth);
				if (width > 0) {
					return width;
				}

				response.addImageWidthInvalidError(width);

			} catch (final NumberFormatException e) {
				response.addImageWidthMalformedError(sWidth);
			}
		} catch (final IllegalArgumentException e) {
			response.addNoImageWidthError();
		}

		return null;
	}

	private static Integer checkImageHeight(FormItemList formItemList,
			ReadResponse response) {
		try {
			final String sHeight = formItemList
					.getField(ProtocolConstants.Parameters.Read.IMAGE_HEIGHT);

			try {
				final int height = Integer.parseInt(sHeight);
				if (height > 0) {
					return height;
				}

				response.addImageHeightInvalidError(height);

			} catch (final NumberFormatException e) {
				response.addImageHeightMalformedError(sHeight);
			}
		} catch (final IllegalArgumentException e) {
			response.addNoImageHeightError();
		}

		return null;
	}

}