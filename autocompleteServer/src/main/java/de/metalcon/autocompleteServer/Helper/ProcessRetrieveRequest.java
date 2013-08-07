package de.metalcon.autocompleteServer.Helper;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import de.metalcon.autocompleteServer.ContextListener;
import de.metalcon.autocompleteServer.SuggestTree;
import de.metalcon.autocompleteServer.SuggestTree.Node;

/**
 * This class is a helper class to process the Read Requests
 * @author Rene Pickhardt
 *
 */
public class ProcessRetrieveRequest {
	
	/**
	 * checks if the request Parameters follow the Protocol or corrects them.
	 * @param request
	 */
	public static void checkRequestParameter(HttpServletRequest request, ServletContext context){
		Integer numItems = checkNumItems(request);
		SuggestTree index = checkIndexName(request, context);
		if (index == null){
			ProcessRetrieveResponse.addError(StatusCodes.NO_INDEX_AVAILABLE);
		}
		Node result = checkTerm(request, index);
		for (int i = 0; i < Math.min(result.listLength(), numItems); ++i) {
			result.getSuggestion(i);
			result.getWeight(i);
		}
	}	
	
	/**
	 * checks the ASTP request for the number of items that should be retrieved
	 * If the parameter is not set we use the default value
	 * If the parameter is not set correctly (no integer or bigger than the maximum
	 * allowed Integer or negative) we use the default value
	 * @param request
	 */
	private static Integer checkNumItems(HttpServletRequest request) {
		String tmp = request.getParameter(ProtocolConstants.NUM_ITEMS);
		Integer numItems = null;
		if (tmp != null){
			numItems = Integer.parseInt(tmp);
			if (numItems != null){
				if (numItems < 1 || numItems > ProtocolConstants.MAX_NUMBER_OF_SUGGESTIONS){
					numItems = ProtocolConstants.MAX_NUMBER_OF_SUGGESTIONS;
					ProcessRetrieveResponse.addNumItemsWarning(StatusCodes.NUMITEMS_OUT_OF_RANGE);
				}
			}else {
				numItems = ProtocolConstants.MAX_NUMBER_OF_SUGGESTIONS;
				ProcessRetrieveResponse.addNumItemsWarning(StatusCodes.NUMITEMS_NOT_AN_INTEGER);

			}
		}else{
			numItems = ProtocolConstants.MAX_NUMBER_OF_SUGGESTIONS;
			ProcessRetrieveResponse.addNumItemsWarning(StatusCodes.NUMITEMS_NOT_GIVEN);
		}
		return numItems;
	}
	/**
	 * Returns the search Index according to the indexName Parameter of the ASTP
	 * if no Parameter was in the request the default index will be used.
	 * if the parameter did not match any known index the default index will be used.
	 * returns null if the default index is requested but does not exist  
	 * @param request
	 */
	private static SuggestTree checkIndexName(HttpServletRequest request, ServletContext context) {
		String indexName = request.getParameter(ProtocolConstants.INDEX_PARAMETER);
		SuggestTree index = null;
		// if no indexName Parameter was provided use the default index.
		if (indexName==null){
			indexName = ProtocolConstants.DEFAULT_INDEX_NAME;
			ProcessRetrieveResponse.addIndexWarning(StatusCodes.NO_INDEX_GIVEN);
			index = ContextListener.getIndex(indexName, context);
		}
		// if an indexName Parameter was provided use this one
		else {
			index = ContextListener.getIndex(indexName, context);
			// if the indexName given is unknown to the server use the default.
			if (index==null){
				indexName = ProtocolConstants.DEFAULT_INDEX_NAME;
				ProcessRetrieveResponse.addIndexWarning(StatusCodes.INDEX_UNKNOWN);
				index = ContextListener.getIndex(indexName, context);
			}
		}
		return index;
	}
	
	/**
	 * @param request
	 */
	private static Node checkTerm(HttpServletRequest request, SuggestTree index) {
		String term = request.getParameter(ProtocolConstants.QUERY_PARAMETER);
		Node bestSuggestions = null;
		if (term == null){
			ProcessRetrieveResponse.addError(StatusCodes.NO_TERM_GIVEN);
		}
		else {
			bestSuggestions = index.getBestSuggestions(term);
		}
		return bestSuggestions;
	}
}