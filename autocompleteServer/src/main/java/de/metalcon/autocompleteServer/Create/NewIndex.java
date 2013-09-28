package de.metalcon.autocompleteServer.Create;

import java.util.HashMap;

import javax.servlet.ServletContext;

import de.metalcon.autocompleteServer.Helper.ContextListener;
import de.metalcon.autocompleteServer.Helper.ProtocolConstants;
import de.metalcon.autocompleteServer.Helper.SuggestTree;

/**
 * Contains a method which creates a new index.
 * 
 * @author Christian Schowalter
 * 
 */
public class NewIndex {

	/**
	 * Creates a new index. Does not allow to overwrite an existing index with a
	 * new one.
	 * 
	 * @param context
	 * @param indexName
	 * @author Christian Schowalter
	 */
	public static void newSuggestTree(ServletContext context, String indexName,
			NewIndexResponse response) {

		// TODO: add warning if already existing indexName was given.
		SuggestTree checkIndex = ContextListener.getIndex(indexName, context);

		if (checkIndex == null) {
			SuggestTree suggestTree = new SuggestTree(
					ProtocolConstants.MAX_NUMBER_OF_SUGGESTIONS);

			HashMap<String, String> imageIndex = new HashMap<String, String>();

			ContextListener.setIndex(indexName, suggestTree, context);
			ContextListener.setImageIndex(imageIndex, context);
		} else {
			checkIndex = null;
			response.addIndexAlreadyExistsError(indexName);
		}
	}
}