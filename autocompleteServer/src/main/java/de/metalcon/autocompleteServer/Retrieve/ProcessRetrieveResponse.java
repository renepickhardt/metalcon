/**
 * 
 */
package de.metalcon.autocompleteServer.Retrieve;

/**
 * @author Rene Pickhardt
 *
 */
public class ProcessRetrieveResponse {
	//private static HashMap<String, String> warning
	
	/**
	 * not implemented yet
	 * @param numitemsNotGiven
	 */
	public void addNumItemsWarning(String message) {
		// TODO NEED to implement method. Put stuff to a HashMap
		
	}

	/**
	 * not implemented yet
	 * @param noIndexGiven
	 */
	public void addIndexWarning(String noIndexGiven) {
		// TODO NEED to implement method. Put stuff to a HashMap		
	}

	/**
	 * @param noIndexAvailable
	 */
	public void addError(String noIndexAvailable) {
		// TODO NEED to implement. Errors should really stop the request and send the answer away
		
	}

	/**
	 * @param suggestString
	 * @param key
	 */
	public void addSuggestion(String suggestString, String key) {
		// also retrieve image from the image index
		if (key != null){
			
		}
	}

}
