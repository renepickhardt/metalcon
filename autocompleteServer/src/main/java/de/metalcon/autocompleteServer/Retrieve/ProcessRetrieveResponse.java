package de.metalcon.autocompleteServer.Retrieve;

import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletContext;

import org.json.simple.JSONObject;

import de.metalcon.autocompleteServer.Helper.ContextListener;
import de.metalcon.autocompleteServer.Helper.ProtocolConstants;

/**
 * The ProcessRetrieveResponse class handles the data that are send to the
 * client after a retrieve request has been sent. This can be the actual content
 * as well as meta information (like error messages and warnings)
 * 
 * 
 * @author Rene Pickhardt
 * 
 */
public class ProcessRetrieveResponse {
	private final ServletContext context;
	private JSONObject jsonResponse;
	private final ArrayList<HashMap<String, String>> suggestionsResponseList;

	/**
	 * @param context
	 */

	public ProcessRetrieveResponse(ServletContext context) {
		// TODO:what happens if the provided context is null?
		this.context = context;
		this.jsonResponse = new JSONObject();
		this.suggestionsResponseList = new ArrayList<HashMap<String, String>>();
	}

	/**
	 * @param numitemsNotGiven
	 */
	@SuppressWarnings("unchecked")
	public void addNumItemsWarning(String message) {
		if (this.jsonResponse == null) {
			this.jsonResponse = new JSONObject();
		}
		this.jsonResponse.put("warning:numItems", message);
	}

	/**
	 * not implemented yet
	 * 
	 * @param noIndexGiven
	 */
	@SuppressWarnings("unchecked")
	public void addIndexWarning(String noIndexGiven) {
		if (this.jsonResponse == null) {
			this.jsonResponse = new JSONObject();
		}
		this.jsonResponse.put("warning:noIndexGiven", noIndexGiven);
	}

	/**
	 * @param noIndexAvailable
	 */
	@SuppressWarnings("unchecked")
	public void addError(String message) {
		// TODO Errors should really stop the request and send the answer away
		if (this.jsonResponse == null) {
			this.jsonResponse = new JSONObject();
		}
		this.jsonResponse.put("error", message);
	}

	/**
	 * @param suggestString
	 * @param key
	 */
	public void addSuggestion(String suggestString, String key) {
		// also retrieve image from the image index
		HashMap<String, String> suggestionJsonEntry = new HashMap<String, String>();
		suggestionJsonEntry.put(ProtocolConstants.RESP_JSON_FIELD_SUGGESTION,
				suggestString);
		if (key != null) {
			suggestionJsonEntry.put(
					ProtocolConstants.RESP_JSON_FIELD_SUGGESTION_KEY, key);
			HashMap<String, String> imageIndex = ContextListener
					.getImageIndex(this.context);
			String serializedImage = imageIndex.get(key);
			if (serializedImage != null) {
				suggestionJsonEntry.put(
						ProtocolConstants.RESP_JSON_FIELD_SUGGESTION_IMAGE,
						serializedImage);
			}
		}
		this.suggestionsResponseList.add(suggestionJsonEntry);
	}

	/**
	 * creates the response JSON String that will be send to the client
	 * containing all the data.
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String buildJsonResonse() {
		this.jsonResponse.put(
				ProtocolConstants.RESP_JSON_FIELD_SUGGESTION_LIST,
				this.suggestionsResponseList);
		return this.jsonResponse.toJSONString();
	}

}
