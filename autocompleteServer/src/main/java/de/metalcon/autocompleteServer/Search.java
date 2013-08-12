package de.metalcon.autocompleteServer;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;

import javax.servlet.ServletContext;

import de.metalcon.autocompleteServer.Helper.ContextListener;
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

	public static void initilizeSuggestTree(ServletContext context) { // can't
																		// be
																		// private
																		// now,
																		// because
																		// ContextListener
																		// needs
																		// access
		SuggestTree suggestTree = new SuggestTree(
				ProtocolConstants.MAX_NUMBER_OF_SUGGESTIONS);
		HashMap<String, String> imageIndex = new HashMap<String, String>();
		int priority = 100000;

		try {
			FileInputStream saveFile = new FileInputStream("saveFile.sav");
			ObjectInputStream restore = new ObjectInputStream(saveFile);
			Object obj = restore.readObject();
			suggestTree = (SuggestTree) restore.readObject();
			restore.close();
		} catch (IOException | ClassNotFoundException e1) {

			String filename = "/var/lib/datasets/metalcon/Band.csv";
			// there must be a way to use relative paths!

			try {
				BufferedReader in = new BufferedReader(new FileReader(filename));
				String zeile = null;
				String newZeile = null;
				String newZeileMyspace = null;
				// String Bandname = null;

				while ((zeile = in.readLine()) != null) {
					String[] parts = zeile.split(";");
					if ((parts.length == 3) && !(parts[1].equals("NULL"))
							&& !(parts[2].equals("NULL"))
							&& !(parts[0].equals(""))) {
						parts[0] = parts[0].replaceAll("[\"]", "");
						newZeile = "<a href=" + parts[1] + ">" + parts[0]
								+ "</a>" + "<br>";
						newZeileMyspace = "<a href=" + parts[2] + ">"
								+ parts[0] + "</a>" + "<br>";
						// suggestTree.put(parts[0], priority, parts[1]);
						suggestTree.put(parts[0], priority, parts[0]);
						String p = parts[0];
						// TODO: quick test for images since no image data is
						// available right now
						p = p + p + p + p + p + p + p + p + p + p + p + p + p
								+ p + p + p + p + p + p + p + p + p + p + p + p
								+ p + p + p;
						p = p + p;
						imageIndex.put(parts[0],
								p.substring(0, Math.min(2048, p.length())));
						--priority;
					}
				}
			} catch (IOException e2) {
				e2.printStackTrace();
			}
		}
		context.setAttribute("indexName:"
				+ ProtocolConstants.DEFAULT_INDEX_NAME, suggestTree);
		ContextListener.setImageIndex(imageIndex, context);
	}
}
