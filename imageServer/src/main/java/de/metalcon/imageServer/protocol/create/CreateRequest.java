package de.metalcon.imageServer.protocol.create;

import de.metalcon.imageServer.protocol.ProtocolConstants;
import de.metalcon.utils.FormFile;
import de.metalcon.utils.FormItemList;

public class CreateRequest {

	public static CreateResponse checkRequest(FormItemList formItemList,
			CreateResponse response) {

		String imageIdentifier = checkImageIdentifier(formItemList, response);
		FormFile imageStream = checkImageStream(formItemList, response);
		String metaData = checkMetaData(formItemList, response);
		Boolean autoRotateFlag = checkAutoRotateFlag(formItemList, response);

		return response;
	}

	private static FormFile checkImageStream(FormItemList formItemList,
			CreateResponse response) {

		FormFile imageStream = null;
		try {
			imageStream = formItemList
					.getFile(ProtocolConstants.Parameters.Create.IMAGESTREAM);
		} catch (IllegalArgumentException e) {

			response.addNoImageStreamError();
			return imageStream;
		}
		return imageStream;
	}

	private static Boolean checkAutoRotateFlag(FormItemList formItemList,
			CreateResponse response) {

		String autoRotateFlag = null;
		try {
			autoRotateFlag = formItemList
					.getField(ProtocolConstants.Parameters.Create.AUTOROTATE_FLAG);
		} catch (IllegalArgumentException e) {

			response.addAutoRotateFlagMissingError(ProtocolConstants.StatusMessage.Create.AUTOROTATE_FLAG_MISSING);
			return null;
		}
		try {
			Integer autoRotateInteger = Integer.parseInt(autoRotateFlag);
			if (autoRotateInteger == 0) {
				return false;
			}
			return true;
		} catch (NumberFormatException e) {
			response.addAutoRotateFlagMalformedError();
			return null;
		}
	}

	private static String checkMetaData(FormItemList formItemList,
			CreateResponse response) {
		String metaData = null;
		try {
			metaData = formItemList
					.getField(ProtocolConstants.Parameters.Create.META_DATA);
		} catch (IllegalArgumentException e) {
			response.addNoMetadataError(ProtocolConstants.StatusMessage.Create.IMAGE_METADATA_MISSING);
			return null;
		}
		return metaData;
	}

	private static String checkImageIdentifier(FormItemList formItemList,
			CreateResponse response) {
		String imageIdentifier = null;
		try {
			imageIdentifier = formItemList
					.getField(ProtocolConstants.Parameters.Create.IMAGE_IDENTIFIER);
		} catch (IllegalArgumentException e) {
			response.addNoIdentifierError();
			return null;
		}
		return imageIdentifier;
	}

}