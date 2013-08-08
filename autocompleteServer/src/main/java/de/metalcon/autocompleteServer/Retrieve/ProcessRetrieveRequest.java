package de.metalcon.autocompleteServer.Retrieve;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import de.metalcon.autocompleteServer.Helper.ContextListener;
import de.metalcon.autocompleteServer.Helper.ProtocolConstants;
import de.metalcon.autocompleteServer.Helper.SuggestTree;
import de.metalcon.autocompleteServer.Helper.SuggestTree.Node;

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
	public static ProcessRetrieveResponse checkRequestParameter(HttpServletRequest request, ServletContext context){
		ProcessRetrieveResponse response = new ProcessRetrieveResponse();
		Integer numItems = checkNumItems(request, response);
		SuggestTree index = checkIndexName(request, response, context);
		if (index == null){
			response.addError(RetrieveStatusCodes.NO_INDEX_AVAILABLE);
			return response;
		}
		String term = checkTerm(request, response);
		if (term == null){
			return response;
		}
		retrieveSuggestions(request, response, index, term, numItems);
		return response;
	}	
	
	/**
	 * checks the ASTP request for the number of items that should be retrieved
	 * If the parameter is not set we use the default value
	 * If the parameter is not set correctly (no integer or bigger than the maximum
	 * allowed Integer or negative) we use the default value
	 * @param request
	 * @param response 
	 */
	private static Integer checkNumItems(HttpServletRequest request, ProcessRetrieveResponse response) {
		String tmp = request.getParameter(ProtocolConstants.NUM_ITEMS);
		Integer numItems = null;
		if (tmp != null){
			numItems = Integer.parseInt(tmp);
			if (numItems != null){
				if (numItems < 1 || numItems > ProtocolConstants.MAX_NUMBER_OF_SUGGESTIONS){
					numItems = ProtocolConstants.MAX_NUMBER_OF_SUGGESTIONS;
					response.addNumItemsWarning(RetrieveStatusCodes.NUMITEMS_OUT_OF_RANGE);
				}
			}else {
				numItems = ProtocolConstants.MAX_NUMBER_OF_SUGGESTIONS;
				response.addNumItemsWarning(RetrieveStatusCodes.NUMITEMS_NOT_AN_INTEGER);

			}
		}else{
			numItems = ProtocolConstants.MAX_NUMBER_OF_SUGGESTIONS;
			response.addNumItemsWarning(RetrieveStatusCodes.NUMITEMS_NOT_GIVEN);
		}
		return numItems;
	}
	/**
	 * Returns the search Index according to the indexName Parameter of the ASTP
	 * if no Parameter was in the request the default index will be used.
	 * if the parameter did not match any known index the default index will be used.
	 * returns null if the default index is requested but does not exist  
	 * @param request
	 * @param response 
	 */
	private static SuggestTree checkIndexName(HttpServletRequest request, ProcessRetrieveResponse response, ServletContext context) {
		String indexName = request.getParameter(ProtocolConstants.INDEX_PARAMETER);
		SuggestTree index = null;
		// if no indexName Parameter was provided use the default index.
		if (indexName==null){
			indexName = ProtocolConstants.DEFAULT_INDEX_NAME;
			response.addIndexWarning(RetrieveStatusCodes.NO_INDEX_GIVEN);
			index = ContextListener.getIndex(indexName, context);
		}
		// if an indexName Parameter was provided use this one
		else {
			index = ContextListener.getIndex(indexName, context);
			// if the indexName given is unknown to the server use the default.
			if (index==null){
				indexName = ProtocolConstants.DEFAULT_INDEX_NAME;
				response.addIndexWarning(RetrieveStatusCodes.INDEX_UNKNOWN);
				index = ContextListener.getIndex(indexName, context);
			}
		}
		return index;
	}
	
	/**
	 * @param request
	 * @param response 
	 */
	private static String checkTerm(HttpServletRequest request, ProcessRetrieveResponse response) {
		String term = request.getParameter(ProtocolConstants.QUERY_PARAMETER);
		if (term == null){
			response.addError(RetrieveStatusCodes.NO_TERM_GIVEN);
			return null;
		}
		else {
			return term;
		}
	}
	
	/**
	 * @param request
	 * @param response
	 * @param index
	 * @param term 
	 * @param numItems 
	 */
	private static void retrieveSuggestions(HttpServletRequest request,
			ProcessRetrieveResponse response, SuggestTree index, String term, Integer numItems) {
		Node suggestions = index.getBestSuggestions(term);
		if (suggestions == null){
			response.addError(RetrieveStatusCodes.NO_SUGGESTIONS_MATCHING_TERM);
			return;
		}
		for (int i = 0; i < Math.min(suggestions.listLength(), numItems); ++i) {
			String suggestString = suggestions.getSuggestion(i);
			String key = suggestions.getKey(i);
			response.addSuggestion(suggestString, key);

		}		
	}

}