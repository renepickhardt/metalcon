package de.metalcon.autocompleteServer.Create;

import javax.servlet.http.HttpServletRequest;

/**
 * 
 * @author Christian Schowalter
 * 
 */
public class ProcessCreateRequest {

	public void checkRequestParameter(HttpServletRequest request) {
		// checkContentType(); Accept must be text/x-json.

	}

	private static void checkImage(HttpServletRequest request) {
		// This method needs a library to analyze JPEGs concerning their size.

	}

	private static void checkIndex(HttpServletRequest request) {
		// Check if index is provided. If not, use default and respond
	}

	private static void checkKey(HttpServletRequest request) {
		// Check if key is provided. Wait, is this even necessary?
	}

	private static void checkTerm(HttpServletRequest request) {
		// Check if term is provided. If not, abort and respond

	}

}
