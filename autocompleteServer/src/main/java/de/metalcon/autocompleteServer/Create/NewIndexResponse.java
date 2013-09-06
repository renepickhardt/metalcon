package de.metalcon.autocompleteServer.Create;

import javax.servlet.ServletContext;

import org.json.simple.JSONObject;

public class NewIndexResponse {

	private final JSONObject jsonResponse;

	public NewIndexResponse(ServletContext context) {
		super();
		this.jsonResponse = new JSONObject();
	}

	public JSONObject getResponse() {
		return this.jsonResponse;
	}
}
