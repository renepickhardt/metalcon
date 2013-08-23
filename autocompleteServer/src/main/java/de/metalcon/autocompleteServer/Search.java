package de.metalcon.autocompleteServer;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;

import javax.servlet.ServletContext;

import de.metalcon.autocompleteServer.Create.SuggestionComponents;
import de.metalcon.autocompleteServer.Helper.ProtocolConstants;
import de.metalcon.autocompleteServer.Helper.SuggestTree;

public class Search {
	// public static SuggestTree suggestTree = null;

	// public static String treeSearch(String input) {
	// if (suggestTree == null) { //When the ContextListener works as intended,
	// the initalization should already be done before calling this method
	// initilizeSuggestTree();
	// }
	// Node result = suggestTree.getBestSuggestions(input);
	// StringBuffer resultList = new StringBuffer("");
	// for (int i = 0; i < result.listLength(); ++i) {
	// resultList = resultList.append(result.getSuggestion(i) + ",");
	// }
	// return resultList.toString();
	// }

	public static void initilizeSuggestTree(ServletContext context) {
		SuggestTree suggestTree = new SuggestTree(
				ProtocolConstants.MAX_NUMBER_OF_SUGGESTIONS);

		HashMap<String, String> imageIndex = new HashMap<String, String>();

		try {
			File saveFile = new File("Database.save");

			if (saveFile.exists()) {

				FileInputStream fileInputStream = new FileInputStream(saveFile);

				ObjectInputStream restore = new ObjectInputStream(
						fileInputStream);

				while (true) {
					try {
						SuggestionComponents suggestTreeEntry = (SuggestionComponents) restore
								.readObject();
						suggestTree.put(suggestTreeEntry.getSuggestString(),
								suggestTreeEntry.getWeight(),
								suggestTreeEntry.getKey());
						imageIndex.put(suggestTreeEntry.getKey(),
								suggestTreeEntry.getImageBase64());
					} catch (EOFException e) {
						restore.close();
						break;
					}

				}

			}

		} catch (IOException | ClassNotFoundException e1) {

			e1.printStackTrace();

		}
		context.setAttribute(ProtocolConstants.INDEX_PARAMETER
				+ ProtocolConstants.DEFAULT_INDEX_NAME, suggestTree);
	}
}
