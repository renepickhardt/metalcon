package de.metalcon.imageServer.protocol.create;

import de.metalcon.imageServer.protocol.ProtocolConstants;
import de.metalcon.utils.FormItemList;

public class CreateRequest {

	public static CreateResponse checkRequest(FormItemList formItemList,
			CreateResponse response) {

		if (formItemList
				.getField(ProtocolConstants.Parameters.Create.IMAGE_IDENTIFIER) == null) {

			response.addNoIdentifierError(ProtocolConstants.StatusMessage.Create.IMAGE_IDENTIFIER_MISSING);
			return response;
		}

		if (formItemList
				.getField(ProtocolConstants.Parameters.Create.IMAGESTREAM) == null) {

			response.addNoImageStreamError(ProtocolConstants.StatusMessage.Create.IMAGESTREAM_MISSING);
			return response;
		}
		if (formItemList
				.getField(ProtocolConstants.Parameters.Create.META_DATA) == null) {

			response.addNoMetadataError(ProtocolConstants.StatusMessage.Create.IMAGE_METADATA_MISSING);
			return response;
		}
		if (formItemList
				.getField(ProtocolConstants.Parameters.Create.AUTOROTATE_FLAG) == null) {

			response.addAutoRotateFlagMissingError(ProtocolConstants.StatusMessage.Create.AUTOROTATE_FLAG_MISSING);
			return response;
		}

		// TODO Auto-generated method stub
		return response;
	}
}