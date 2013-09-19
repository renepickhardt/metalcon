package de.metalcon.musicStorageServer.protocol;

import org.apache.commons.fileupload.disk.DiskFileItem;

import de.metalcon.musicStorageServer.protocol.create.CreateRequest;
import de.metalcon.musicStorageServer.protocol.create.CreateResponse;
import de.metalcon.utils.FormItemList;

public class CreateRequestTest extends RequestTest {

	private CreateRequest createRequest;

	private void fillRequest(final String musicItemIdentifier,
			final DiskFileItem musicItem, final String metaData) {
		// create and fill form item list
		final FormItemList formItemList = new FormItemList();

		if (musicItemIdentifier != null) {
			formItemList.addField(
					ProtocolConstants.Parameter.Create.MUSIC_ITEM_IDENTIFIER,
					musicItemIdentifier);
		}
		if (musicItem != null) {
			formItemList.addFile(
					ProtocolConstants.Parameter.Create.MUSIC_ITEM_STREAM,
					musicItem);
		}
		if (metaData != null) {
			formItemList.addField(ProtocolConstants.Parameter.Create.META_DATA,
					metaData);
		}

		// check request and extract the response
		final CreateResponse createResponse = new CreateResponse();
		this.createRequest = CreateRequest.checkRequest(formItemList,
				createResponse);
		this.extractJson(createResponse);
	}
}