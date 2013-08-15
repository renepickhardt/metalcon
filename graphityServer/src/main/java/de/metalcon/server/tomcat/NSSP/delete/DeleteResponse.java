package de.metalcon.server.tomcat.NSSP.delete;

import de.metalcon.server.tomcat.NSSProtocol;
import de.metalcon.server.tomcat.NSSP.Response;

/**
 * basic response to delete requests according to NSSP
 * 
 * @author sebschlicht
 * 
 */
public class DeleteResponse extends Response {

	/**
	 * add status message: delete request type missing
	 */
	public void typeMissing() {
		this.parameterMissing(
				NSSProtocol.Parameters.Delete.TYPE,
				"Please provide the type of the delete request. Use \"user\" to delete a user, \"follow\" to delete a follow edge or \"status_update\" to delete a status update.");
	}

	/**
	 * add status message: delete request type invalid
	 * 
	 * @param type
	 *            delete request type passed
	 */
	public void typeInvalid(final String type) {
		this.addStatusMessage(
				NSSProtocol.StatusCodes.Delete.TYPE_INVALID,
				"\""
						+ type
						+ "\" is not a valid delete request type. Use \"user\" to delete a user, \"follow\" to delete a follow edge or \"status_update\" to delete a status update.");
	}

}