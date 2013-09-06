package de.metalcon.autocompleteServer.Create;

import javax.servlet.ServletContext;

import de.metalcon.autocompleteServer.Helper.ProtocolConstants;
import de.metalcon.utils.FormItemList;

public class NewIndexRequest {

	public static NewIndexResponse checkRequestParameter(
			FormItemList formItemList, NewIndexResponse response,
			ServletContext servletContext) {

		String indexName = checkIndexName(formItemList, response);
		System.out.println("hello!");
		return null;
	}

	private static String checkIndexName(FormItemList formItemList,
			NewIndexResponse response) {
		String indexName = null;
		try {
			indexName = formItemList
					.getField(ProtocolConstants.INDEX_PARAMETER);
		} catch (IllegalArgumentException e) {
			response.addNoIndexError(CreateStatusCodes.INDEXNAME_NOT_GIVEN);
			return null;
		}
		return indexName;
	}

}
