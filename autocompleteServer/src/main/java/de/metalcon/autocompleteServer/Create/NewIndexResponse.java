package de.metalcon.autocompleteServer.Create;

import javax.servlet.ServletContext;

import org.json.simple.JSONObject;

import de.metalcon.autocompleteServer.Response;
import de.metalcon.autocompleteServer.Helper.ProtocolConstants;

public class NewIndexResponse extends Response {

	private final JSONObject jsonResponse;

	private NewIndexContainer container;

	public NewIndexResponse(ServletContext context) {
		this.jsonResponse = new JSONObject();
	}

	public JSONObject getResponse() {
		return this.jsonResponse;
	}

	public void addNoIndexError() {
		this.parameterMissing(ProtocolConstants.INDEX_PARAMETER,
				"The index name is not given. Please repeat the request with an index name.");
	}

	public void addIndexAlreadyExistsError(String duplicateIndex) {
		this.addStatusMessage(CreateStatusCodes.STATUS_INDEX_DUPLICATE,
				"the index name " + duplicateIndex
						+ " already exists. Please choose another one.");
	}

	/**
	 * Adds a requestMustBeMultipart-Error to the response. Expects the String
	 * to be not NULL and correctly formatted.
	 * 
	 * @param noImage
	 */
	@SuppressWarnings("unchecked")
	public void addHttpRequestError(String requestMustBeMultipart) {
		this.jsonResponse.put(CreateStatusCodes.STATUS_REQUEST_NOT_MULTIPART,
				requestMustBeMultipart);
	}

	public void addStatusOk(String statusOk) {
		this.jsonResponse.put(CreateStatusCodes.STATUS_OK, statusOk);

	}

	public NewIndexContainer getContainer() {

		return this.container;
	}

	public void addContainer(NewIndexContainer newIndexContainer) {
		this.container = newIndexContainer;

	}

}
