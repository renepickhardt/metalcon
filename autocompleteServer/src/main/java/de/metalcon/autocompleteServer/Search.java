package de.metalcon.autocompleteServer;

import java.util.HashMap;

import javax.servlet.ServletContext;

import de.metalcon.autocompleteServer.Helper.ContextListener;
import de.metalcon.autocompleteServer.Helper.ImportScript;
import de.metalcon.autocompleteServer.Helper.ProtocolConstants;
import de.metalcon.autocompleteServer.Helper.SuggestTree;

public class Search {

	public static void initilizeSuggestTree(ServletContext context) { // can't
//		SuggestTree suggestTree = new SuggestTree(
//				ProtocolConstants.MAX_NUMBER_OF_SUGGESTIONS);
		HashMap<String, String> imageIndex = new HashMap<String, String>();


//FIXME: comment me in
//				ImportScript.loadFilesToIndex(true, suggestTree, imageIndex);
//				ImportScript.loadFilesToIndex(false, suggestTree, imageIndex);
				
				SuggestTree wikiTree = new SuggestTree(ProtocolConstants.MAX_NUMBER_OF_SUGGESTIONS);
				HashMap<String, String> hitMap = new HashMap<String,String>();
				//ImportScript.loadWikipedia(wikiTree,imageIndex);
				ImportScript.loadServerWiki(wikiTree, imageIndex,hitMap );
				
				context.setAttribute("indexName:"
						+ "wikitree", wikiTree);
//FIXME: comment me in				
//				context.setAttribute("indexName:"
//						+ ProtocolConstants.DEFAULT_INDEX_NAME, suggestTree);
				ContextListener.setImageIndex(imageIndex, context);
				ContextListener.setHitMap(hitMap, context,"wikitree");

	}
}