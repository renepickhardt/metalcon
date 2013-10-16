package de.metalcon.autocompleteServer.Helper;

import java.util.HashMap;

import javax.servlet.ServletContext;

public class ContextListener {

	public static SuggestTree getIndex(String indexName, ServletContext context) {
		return (SuggestTree) context
				.getAttribute(ProtocolConstants.INDEX_PARAMETER + indexName);
	}

	public static void setIndex(String indexName, SuggestTree index,
			ServletContext context) {
		context.setAttribute(ProtocolConstants.INDEX_PARAMETER + indexName,
				index);
	}

	public static HashMap<String, String> getImageIndex(ServletContext context) {
		return (HashMap<String, String>) context
				.getAttribute(ProtocolConstants.IMAGE_SERVER_CONTEXT_KEY);
	}

	public static void setImageIndex(HashMap<String, String> imageIndex,
			ServletContext context) {
		context.setAttribute(ProtocolConstants.IMAGE_SERVER_CONTEXT_KEY,
				imageIndex);
	}

}
