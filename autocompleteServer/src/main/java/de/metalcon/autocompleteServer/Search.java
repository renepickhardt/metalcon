package de.metalcon.autocompleteServer;

import java.io.*;
import de.metalcon.autocompleteServer.SuggestTree;
import de.metalcon.autocompleteServer.SuggestTree.*;

public class Search {
	public static SuggestTree suggestTree = null;

	public static String treeSearch(String input) {
		if (suggestTree == null) {	//When the ContextListener works as intended, the initalization should already be done before calling this method
			initilizeSuggestTree();
		}
		Node result = suggestTree.getBestSuggestions(input);
		StringBuffer resultList = new StringBuffer("");
		for (int i = 0; i < result.listLength(); ++i) {
			resultList = resultList.append(result.getSuggestion(i) + ",");
		}
		return resultList.toString();
	}

	public static void initilizeSuggestTree() {	//can't be private now, because ContextListener needs access
		suggestTree = new SuggestTree(7);
		int priority = 100000;
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
					suggestTree.put(parts[0], priority, parts[1]);
					--priority;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}		
}
