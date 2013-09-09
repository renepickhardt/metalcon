package de.metalcon.autocompleteServer.Create;

import java.util.HashMap;

import javax.servlet.ServletContext;

import de.metalcon.autocompleteServer.Helper.ContextListener;
import de.metalcon.autocompleteServer.Helper.ProtocolConstants;
import de.metalcon.autocompleteServer.Helper.SuggestTree;

public class NewIndex {

	public static void newSuggestTree(ServletContext context, String indexName) {
		SuggestTree suggestTree = new SuggestTree(
				ProtocolConstants.MAX_NUMBER_OF_SUGGESTIONS);

		HashMap<String, String> imageIndex = new HashMap<String, String>();

		ContextListener.setIndex(indexName, suggestTree, context);
		ContextListener.setImageIndex(imageIndex, context);
	}
}