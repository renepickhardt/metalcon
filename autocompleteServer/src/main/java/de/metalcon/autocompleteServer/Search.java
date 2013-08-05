package de.metalcon.autocompleteServer;

import java.io.*;
import de.metalcon.autocompleteServer.SuggestTree;
import de.metalcon.autocompleteServer.SuggestTree.*;



public class Search {

	public static String treeSearch(String input){
		SuggestTree Suggestions = new SuggestTree(7);
		int priority = 100000;
		String filename = "/var/lib/datasets/metalcon/Band.csv";	//there must be a way to use relative paths!

		try {
			BufferedReader in = new BufferedReader(new FileReader(filename));
			String zeile = null;
			String newZeile = null;
			String newZeileMyspace = null;
//			String Bandname = null;

			while ((zeile = in.readLine()) != null) {
				String[] parts = zeile.split(";");
				if ((parts.length == 3) && !(parts[1].equals("NULL"))
						&& !(parts[2].equals("NULL")) && !(parts[0].equals (""))) {
					parts[0] = parts[0].replaceAll("[\"]", "");
					newZeile = "<a href=" + parts[1] + ">" + parts[0] + "</a>"
							+ "<br>";
					newZeileMyspace = "<a href=" + parts[2] + ">" + parts[0]
							+ "</a>" + "<br>";
					Suggestions.put(parts[0], priority);
					--priority;
				}
			}

			//TODO: find a working way to return multiple results			
			Node result = Suggestions.getBestSuggestions(input);
			StringBuffer resultList = null;
			for ( int i=0;i < result.listLength();++i){
				resultList = resultList.append(result.getSuggestion(i)+",");
			}
			//return resultList.toString();
			return resultList.toString();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}

