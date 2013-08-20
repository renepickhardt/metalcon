package de.metalcon.autocompleteServer.Create;

import javax.servlet.ServletContext;

import org.json.simple.JSONObject;

public class ProcessCreateResponse {

	private final ServletContext context;
	private final JSONObject jsonResponse;
	private CreateRequestContainer container;

	public ProcessCreateResponse(ServletContext context) {
		this.context = context;
		this.jsonResponse = new JSONObject();
		this.container = new CreateRequestContainer();

	}

	// TODO: double-check JSON keys!

	/**
	 * Adds a queryName-Error to the container object.Expects the String to be
	 * not NULL and correctly formatted.
	 * 
	 * @param querynameNotGiven
	 */
	public void addQueryNameError(String querynameNotGiven) {
		this.jsonResponse.put("Error:queryName", querynameNotGiven);
	}

	/**
	 * Adds the suggestion String to the container object. Expects the String to
	 * be not NULL and correctly formatted.
	 * 
	 * @param suggestionString
	 */
	public void addSuggestStringToContainer(String suggestionString) {
		this.jsonResponse.put("term", suggestionString);
	}

	/**
	 * Adds a DefaultIndex-Warning to the container object.Expects the String to
	 * be not NULL and correctly formatted.
	 * 
	 * @param indexnameNotGiven
	 */
	public void addDefaultIndexWarning(String indexnameNotGiven) {
		this.jsonResponse.put("Warning:defaultIndex", indexnameNotGiven);
	}

	/**
	 * Adds a NoImage-Warning to the container object.Expects the String to be
	 * not NULL and correctly formatted.
	 * 
	 * @param noImage
	 */
	public void addNoImageWarning(String noImage) {
		this.jsonResponse.put("Warning:noImage", noImage);
	}

	/**
	 * Adds a requestMustBeMultipart-Error to the container object.Expects the
	 * String to be not NULL and correctly formatted.
	 * 
	 * @param noImage
	 */
	public void addHttpRequestError(String requestMustBeMultipart) {
		this.jsonResponse.put("Error:RequestNotMultipart",
				requestMustBeMultipart);
	}

	/**
	 * Getter for the response as JSONObject.
	 * 
	 * @return JSONObject
	 */
	public JSONObject getResponse() {
		return this.jsonResponse;
	}

	public CreateRequestContainer getContainer() {
		return this.container;
	}

	public void addContainer(
			CreateRequestContainer suggestTreeCreateRequestContainer) {
		this.container = suggestTreeCreateRequestContainer;

	}

}
