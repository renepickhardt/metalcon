package de.metalcon.musicStorageServer.protocol;

import org.json.simple.JSONObject;

/**
 * basic response according to NSSP
 * 
 * @author sebschlicht
 * 
 */
public abstract class Response {

	/**
	 * HTTP status code
	 */
	protected int statusCode;

	// response JSON object
	protected final JSONObject json;

	/**
	 * create a new basic response
	 */
	public Response() {
		this.json = new JSONObject();
		this.statusCode = 200;
	}

	/**
	 * add a status message to the response
	 * 
	 * @param statusMessage
	 *            status message
	 * @param solution
	 *            detailed description and solution
	 */
	@SuppressWarnings("unchecked")
	protected void addStatusMessage(final String statusMessage,
			final String solution) {
		this.json.put(ProtocolConstants.SOLUTION, solution);
		this.json.put(ProtocolConstants.STATUS_MESSAGE, statusMessage);
	}

	/**
	 * add a status message for a missing parameter
	 * 
	 * @param paramName
	 *            name of the missing parameter
	 * @param solution
	 *            detailed description and solution
	 */
	protected void parameterMissing(final String paramName,
			final String solution) {
		this.addStatusMessage("request incomplete: parameter \"" + paramName
				+ "\" is missing", solution);
	}

	/**
	 * @return HTTP status code
	 */
	public int getStatusCode() {
		return this.statusCode;
	}

	/**
	 * set the status code
	 * 
	 * @param statusCode
	 *            HTTP status code
	 */
	public void setStatusCode(final int statusCode) {
		this.statusCode = statusCode;
	}

	/**
	 * an internal server error occurred<br>
	 * sets the HTTP status code to 500
	 */
	public void internalServerError() {
		this.setStatusCode(500);

	}

	@Override
	public String toString() {
		return this.json.toJSONString();
	}

}