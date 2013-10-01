package de.metalcon.autocompleteServer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;

import javax.servlet.ServletContext;

import de.metalcon.autocompleteServer.Create.SuggestionComponents;
import de.metalcon.autocompleteServer.Helper.ContextListener;
import de.metalcon.autocompleteServer.Helper.ProtocolConstants;
import de.metalcon.autocompleteServer.Helper.SuggestTree;

public class Search {

	public static void initilizeSuggestTree(ServletContext context) {
		SuggestTree suggestTree = new SuggestTree(
				ProtocolConstants.MAX_NUMBER_OF_SUGGESTIONS);

		HashMap<String, String> imageIndex = new HashMap<String, String>();

		try {
			// FIXME change after debugging
			File saveFile = new File("/var/lib/tomcat/"
					+ ProtocolConstants.DEFAULT_INDEX_NAME + ".save");

			if (saveFile.exists()) {

				FileInputStream fileInputStream = new FileInputStream(saveFile);

				ObjectInputStream restore = new ObjectInputStream(
						fileInputStream);
				SuggestionComponents suggestTreeEntry = null;
				while ((suggestTreeEntry = (SuggestionComponents) restore
						.readObject()) != null) {
					suggestTree.put(suggestTreeEntry.getSuggestString(),
							suggestTreeEntry.getWeight(),
							suggestTreeEntry.getKey());
					if (suggestTreeEntry.getImageBase64() != null) {
						imageIndex.put(suggestTreeEntry.getKey(),
								suggestTreeEntry.getImageBase64());
					}
				}

				// while (true) {
				// try {
				// SuggestionComponents suggestTreeEntry =
				// (SuggestionComponents) restore
				// .readObject();
				// suggestTree.put(suggestTreeEntry.getSuggestString(),
				// suggestTreeEntry.getWeight(),
				// suggestTreeEntry.getKey());
				// // imageIndex.put(suggestTreeEntry.getKey(),
				// // suggestTreeEntry.getImageBase64());
				// } catch (EOFException e) {
				// restore.close();
				// break;
				// }
				//
				// }

			}

		} catch (IOException | ClassNotFoundException e1) {

			e1.printStackTrace();

		}
		ContextListener.setIndex(ProtocolConstants.DEFAULT_INDEX_NAME,
				suggestTree, context);
		ContextListener.setImageIndex(imageIndex, context);
	}
}