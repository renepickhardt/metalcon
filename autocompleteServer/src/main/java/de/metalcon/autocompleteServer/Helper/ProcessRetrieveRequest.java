package de.metalcon.autocompleteServer.Helper;

import javax.servlet.http.HttpServletRequest;

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
	public static void checkRequestParameter(HttpServletRequest request){
		checkTerm(request);
		checkIndexName(request);
		checkNumItems(request);
	}		
	/**
	 * @param request
	 */
	private static void checkNumItems(HttpServletRequest request) {
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
	}
	/**
	 * @param request
	 */
	private static void checkIndexName(HttpServletRequest request) {
		String indexName = request.getParameter(ProtocolConstants.INDEX_PARAMETER);		
	}
	/**
	 * @param request
	 */
	private static void checkTerm(HttpServletRequest request) {
		String term = request.getParameter(ProtocolConstants.QUERY_PARAMETER);		
	}
}