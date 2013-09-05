package de.metalcon.imageServer.protocol.read;

import de.metalcon.imageServer.protocol.ProtocolConstants;
import de.metalcon.utils.FormItemList;

public class ReadRequest {

	public static void checkRequest(FormItemList formItemList,
			ReadResponse readResponse) {
		String imageIdentifier = checkImageIdentifier(formItemList,
				readResponse);
		Boolean originalImageFlag = checkOriginalImageFlag(formItemList,
				readResponse);
	}

	public static void checkRequestWithScaling(FormItemList formItemList,
			ReadResponse readResponse) {
		String imageIdentifier = checkImageIdentifier(formItemList,
				readResponse);
		Boolean originalImageFlag = checkOriginalImageFlag(formItemList,
				readResponse);
		Integer imageWidth = checkImageWidth(formItemList, readResponse);
		Integer imageHeight = checkImageHeight(formItemList, readResponse);
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
			System.out.println(imageWidthString + "here");
		} catch (IllegalArgumentException e) {
			response.addNoImageWidthError();
			System.out.println(imageWidthString + "here");
			return null;
		}
		try {
			Integer imageWidthInteger = Integer.parseInt(imageWidthString);
			return imageWidthInteger;
		} catch (NumberFormatException e) {
			System.out.println(imageWidthString + "here");
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