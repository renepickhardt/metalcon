package de.metalcon.autocompleteServer;

import java.io.EOFException;
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

	public static void initializeSuggestTree(ServletContext context) {

		HashMap<String, String> imageIndex = new HashMap<String, String>();

		boolean defaultFound = false;

		try {
			// FIXME change directory to config-variable
			File saveFile = new File("/var/lib/tomcat/");
			String[] fileList = saveFile.list();

			for (String element : fileList) {
				if (element.endsWith(".save")) {

				}
			}
			for (String element : fileList) {
				if (element.endsWith(".save")) {

					// FIXME use constants
					saveFile = new File("/var/lib/tomcat/" + element);

					SuggestTree suggestTree = new SuggestTree(
							ProtocolConstants.MAX_NUMBER_OF_SUGGESTIONS);

					// otherwise the index would be called *.save
					String[] indexName = element.split(".save");

					if (indexName[0]
							.equals(ProtocolConstants.DEFAULT_INDEX_NAME)) {
						defaultFound = true;
					}

					ContextListener
							.setIndex(indexName[0], suggestTree, context);

					FileInputStream fileInputStream = new FileInputStream(
							saveFile);

					// this constructor hangs, if he received no data! This
					// also seems to happen in the read file has a corrupt
					// header.
					ObjectInputStream restore = new ObjectInputStream(
							fileInputStream);
					SuggestionComponents suggestTreeEntry = null;

					try {
						while ((suggestTreeEntry = (SuggestionComponents) restore
								.readObject()) != null) {

							suggestTree.put(
									suggestTreeEntry.getSuggestString(),
									suggestTreeEntry.getWeight(),
									suggestTreeEntry.getKey());

							if (suggestTreeEntry.getImageBase64() != null) {
								imageIndex.put(suggestTreeEntry.getKey(),
										suggestTreeEntry.getImageBase64());
							}
						}
					} catch (EOFException e) {
						restore.close();
					}
				}
			}

		} catch (IOException | ClassNotFoundException e1) {

			e1.printStackTrace();

		}

		// generate a default index, if it was not loaded from disc
		if (!defaultFound) {
			SuggestTree defaultTree = new SuggestTree(
					ProtocolConstants.MAX_NUMBER_OF_SUGGESTIONS);
			ContextListener.setIndex(ProtocolConstants.DEFAULT_INDEX_NAME,
					defaultTree, context);
		}

		ContextListener.setImageIndex(imageIndex, context);
	}
}