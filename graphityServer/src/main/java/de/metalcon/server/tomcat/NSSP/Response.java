package de.metalcon.server.tomcat.NSSP;

import org.json.simple.JSONObject;

/**
 * basic response according to NSSP
 * 
 * @author sebschlicht
 * 
 */
public abstract class Response {

	// response JSON object
	protected final JSONObject json;

	/**
	 * create a new basic response
	 */
	public Response() {
		this.json = new JSONObject();
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

	@Override
	public String toString() {
		return this.json.toJSONString();
	}

}