package de.metalcon.autocompleteServer.Retrieve;

import java.util.HashMap;

import javax.servlet.ServletContext;

import org.json.simple.JSONObject;

import de.metalcon.autocompleteServer.Helper.ContextListener;

/**
 * The ProcessRetrieveResponse class handles the data that are send to the client
 * after a retrieve request has been sent. 
 * This can be the actual content as well as meta information (like error messages
 * and warnings) 
 * 
 * TODO: This class needs to be implemented. All functions are just empty functions. 
 * @author Rene Pickhardt
 *
 */
public class ProcessRetrieveResponse {
	private ServletContext context;

	//private static HashMap<String, String> warning
	
	/**
	 * @param context
	 */
	
	public ProcessRetrieveResponse(ServletContext context) {
		//TODO:what happens if the provided context is null? 
		this.context = context;
	}

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
			HashMap<String, String> imageIndex = ContextListener.getImageIndex(context);
			String serializedImage = imageIndex.get(key);
			if (serializedImage==null){
				
			}
			else {
				
			}
		}
		JSONObject jsonResponse = new JSONObject();
	}
	
	/**
	 * creates the response JSON String that will be send to the client containing all the data.
	 * @return
	 */
	public String buildJsonResonse(){
		return null;
	}

}
