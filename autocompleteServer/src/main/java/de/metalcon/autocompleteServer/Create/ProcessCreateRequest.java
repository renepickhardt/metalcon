package de.metalcon.autocompleteServer.Create;

import javax.servlet.http.HttpServletRequest;

import de.metalcon.autocompleteServer.Helper.ProtocolConstants;

/**
 * 
 * @author Christian Schowalter
 *
 */
public class ProcessCreateRequest {


	public static void checkRequestParameter(HttpServletRequest request){
		//checkContentType();	Accept muss text/x-json sein.
		


	}
	/**
	 * checks if the request parameters follow the protocol or corrects them.
	 * @param request
	 */
	public static void checkRequestBody(HttpServletRequest request){
		checkTerm(request);
		checkKey(request);
		checkIndex(request);
		checkImage(request);


	}
	private static void checkImage(HttpServletRequest request) {
		// This method needs a library to analyze JPEGs concerning their size.
		
	}
	private static void checkIndex(HttpServletRequest request) {
		String indexName = request.getParameter(ProtocolConstants.INDEX_PARAMETER);
		if (indexName != null){
			
		}else{
			ProcessCreateResponse.addIndexNameWarning(CreateStatusCodes.INDEXNAME_NOT_GIVEN);
			
		}
	}
	private static void checkKey(HttpServletRequest request) {
		String queryName = request.getParameter(ProtocolConstants.QUERY_PARAMETER);
		if (queryName != null){
			
		}else{
			ProcessCreateResponse.addQueryNameWarning(CreateStatusCodes.QUERYNAME_NOT_GIVEN);
		}
	}
	private static void checkTerm(HttpServletRequest request) {
		// TODO Auto-generated method stub

	}
	/**
	 * checks the HTTP-body of a create request for its correctness.
	 * @param request
	 */

}
