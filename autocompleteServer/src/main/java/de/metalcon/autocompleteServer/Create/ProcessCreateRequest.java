package de.metalcon.autocompleteServer.Create;

import java.io.BufferedReader;
import java.io.IOException;

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
	 * @throws IOException 
	 */
	public static void checkRequestBody(HttpServletRequest request) throws IOException{
		String line = null;
		BufferedReader reader = request.getReader();
		while ((line = reader.readLine()) != null) {
			String[] parts = line.split("--ASTP-Boundary");
			
		}

	}
	private static void checkImage(HttpServletRequest request) {
		// This method needs a library to analyze JPEGs concerning their size.
		
	}
	private static void checkIndex(HttpServletRequest request) {

	}
	private static void checkKey(HttpServletRequest request) {

	}
	private static void checkTerm(HttpServletRequest request) {
		// TODO Auto-generated method stub

	}
	/**
	 * checks the HTTP-body of a create request for its correctness.
	 * @param request
	 */

}
