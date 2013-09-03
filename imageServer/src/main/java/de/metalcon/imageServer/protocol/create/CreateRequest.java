package de.metalcon.imageServer.protocol.create;

import de.metalcon.imageServer.protocol.ProtocolConstants;
import de.metalcon.utils.FormItemList;

public class CreateRequest {

	public static CreateResponse checkRequest(FormItemList formItemList,
			CreateResponse response) {

		String imageIdentifier = checkImageIdentifier(formItemList, response);
		// File imageStream = checkImageStream(formItemList, response);
		String metaData = checkMetaData(formItemList, response);
		String autoRotateFlag = checkAutoRotateFlag(formItemList, response);
		return response;
	}

	private static String checkAutoRotateFlag(FormItemList formItemList,
			CreateResponse response) {
		String autoRotateFlag = null;
		try {
			autoRotateFlag = formItemList
					.getField(ProtocolConstants.Parameters.Create.AUTOROTATE_FLAG);
		} catch (IllegalArgumentException e) {
			response.addAutoRotateFlagMissingError(ProtocolConstants.StatusMessage.Create.AUTOROTATE_FLAG_MISSING);
			return null;
		}
		return autoRotateFlag;
	}

	private static String checkMetaData(FormItemList formItemList,
			CreateResponse response) {
		String autoRotateFlag = null;
		try {
			autoRotateFlag = formItemList
					.getField(ProtocolConstants.Parameters.Create.META_DATA);
		} catch (IllegalArgumentException e) {
			response.addNoMetadataError(ProtocolConstants.StatusMessage.Create.IMAGE_METADATA_MISSING);
			return null;
		}
		return autoRotateFlag;
	}

	private static String checkImageIdentifier(FormItemList formItemList,
			CreateResponse response) {
		String imageIdentifier = null;
		try {
			imageIdentifier = formItemList
					.getField(ProtocolConstants.Parameters.Create.IMAGE_IDENTIFIER);
		} catch (IllegalArgumentException e) {
			response.addNoIdentifierError(ProtocolConstants.StatusMessage.Create.IMAGE_IDENTIFIER_MISSING);
			return null;
		}
		return imageIdentifier;
	}
	// if (formItemList
	// .getField(ProtocolConstants.Parameters.Create.IMAGE_IDENTIFIER) == null)
	// {
	//
	// response.addNoIdentifierError(ProtocolConstants.StatusMessage.Create.IMAGE_IDENTIFIER_MISSING);
	// return response;
	// }
	//
	// if (formItemList
	// .getFile(ProtocolConstants.Parameters.Create.IMAGESTREAM) == null) {
	//
	// response.addNoImageStreamError(ProtocolConstants.StatusMessage.Create.IMAGESTREAM_MISSING);
	// return response;
	// }
	// if (formItemList
	// .getField(ProtocolConstants.Parameters.Create.META_DATA) == null) {
	//
	// response.addNoMetadataError(ProtocolConstants.StatusMessage.Create.IMAGE_METADATA_MISSING);
	// return response;
	// }
	// if (formItemList
	// .getField(ProtocolConstants.Parameters.Create.AUTOROTATE_FLAG) == null) {
	//
	// response.addAutoRotateFlagMissingError(ProtocolConstants.StatusMessage.Create.AUTOROTATE_FLAG_MISSING);
	// return response;
	// }

	// TODO Auto-generated method stub

}