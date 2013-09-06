package de.metalcon.autocompleteServer.Create;

import javax.servlet.ServletContext;

import org.json.simple.JSONObject;

import de.metalcon.autocompleteServer.Response;
import de.metalcon.autocompleteServer.Helper.ProtocolConstants;

public class NewIndexResponse extends Response {

	private final JSONObject jsonResponse;

	public NewIndexResponse(ServletContext context) {
		this.jsonResponse = new JSONObject();
	}

	public JSONObject getResponse() {
		return this.jsonResponse;
	}

	public void addNoIndexError(String indexnameNotGiven) {
		this.parameterMissing(ProtocolConstants.INDEX_PARAMETER,
				"The index name is not given. Please repeat the request with an index name.");
	}
}
