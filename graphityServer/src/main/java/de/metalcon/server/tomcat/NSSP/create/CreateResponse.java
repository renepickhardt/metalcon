package de.metalcon.server.tomcat.NSSP.create;

import de.metalcon.server.tomcat.NSSP.ProtocolConstants;
import de.metalcon.server.tomcat.NSSP.Response;

/**
 * basic response to create requests according to NSSP
 * 
 * @author sebschlicht
 * 
 */
public class CreateResponse extends Response {

	/**
	 * add status message: no multipart request
	 */
	public void noMultipartRequest() {
		this.addStatusMessage(
				ProtocolConstants.StatusCodes.Create.NO_MULTIPART_REQUEST,
				"Please use a multipart form to execute your request.");
	}

	/**
	 * add status message: create request type missing
	 */
	public void typeMissing() {
		this.parameterMissing(
				ProtocolConstants.Parameters.Create.TYPE,
				"Please provide the type of the create request. Use \""
						+ CreateRequestType.USER.getIdentifier()
						+ "\" to create a user, \""
						+ CreateRequestType.FOLLOW.getIdentifier()
						+ "\" to create a follow edge or \""
						+ CreateRequestType.STATUS_UPDATE.getIdentifier()
						+ "\" to create a status update.");
	}

	/**
	 * add status message: create request type invalid
	 * 
	 * @param type
	 *            create request type passed
	 */
	public void typeInvalid(final String type) {
		this.addStatusMessage(
				ProtocolConstants.StatusCodes.Create.TYPE_INVALID,
				"\"" + type + "\" is not a valid create request type. Use \""
						+ CreateRequestType.USER.getIdentifier()
						+ "\" to create a user, \""
						+ CreateRequestType.FOLLOW.getIdentifier()
						+ "\" to create a follow edge or \""
						+ CreateRequestType.STATUS_UPDATE.getIdentifier()
						+ "\" to create a status update.");
	}

}