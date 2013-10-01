package de.metalcon.autocompleteServer.Create;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import de.metalcon.autocompleteServer.Helper.ProtocolConstants;
import de.metalcon.autocompleteServer.Helper.SuggestTree;

public class NewIndexRequest {

	public static NewIndexResponse checkRequestParameter(
			HttpServletRequest request, ServletContext context) {
		NewIndexResponse response = new NewIndexResponse(context);
		String indexName = checkIndexName(request, response, context);
		return response;
	}

	private static String checkIndexName(HttpServletRequest request,
			NewIndexResponse response, ServletContext context) {
		String indexName = request
				.getParameter(ProtocolConstants.INDEX_PARAMETER);
		SuggestTree index = null;
		// if no indexName Parameter was provided use the default index.
		if (indexName == null) {
			response.addNoIndexError();
			return null;
		}

		return indexName;
	}

}
