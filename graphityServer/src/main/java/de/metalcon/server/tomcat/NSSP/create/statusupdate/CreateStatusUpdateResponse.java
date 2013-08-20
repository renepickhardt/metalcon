package de.metalcon.server.tomcat.NSSP.create.statusupdate;

import de.metalcon.server.tomcat.NSSP.ProtocolConstants;
import de.metalcon.server.tomcat.NSSP.create.CreateResponse;

/**
 * response to create status update requests according to NSSP
 * 
 * @author sebschlicht
 * 
 */
public class CreateStatusUpdateResponse extends CreateResponse {

	/**
	 * add status message: user identifier missing
	 */
	public void userIdentifierMissing() {
		this.parameterMissing(
				ProtocolConstants.Parameters.Create.StatusUpdate.USER_IDENTIFIER,
				"Please provide a user identifier matching to an existing user.");
	}

	/**
	 * add status message: status update identifier missing
	 */
	public void statusUpdateIdentifierMissing() {
		this.parameterMissing(
				ProtocolConstants.Parameters.Create.StatusUpdate.STATUS_UPDATE_IDENTIFIER,
				"Please provide a status update identifier that is not used by an existing status update.");
	}

	/**
	 * add status message: status update type missing
	 */
	public void statusUpdateTypeMissing() {
		this.parameterMissing(
				ProtocolConstants.Parameters.Create.StatusUpdate.STATUS_UPDATE_TYPE,
				"Please provide a valid status update template that you are using to create the status update.");
	}

	/**
	 * add status message: user identifier invalid
	 * 
	 * @param userId
	 *            user identifier passed
	 */
	public void userIdentifierInvalid(final String userId) {
		this.addStatusMessage(
				ProtocolConstants.StatusCodes.Create.StatusUpdate.USER_NOT_EXISTING,
				"there is no user with the identifier \"" + userId
						+ "\". Please provide a valid user identifier.");
	}

	/**
	 * add status message: status update identifier invalid
	 * 
	 * @param statusUpdateId
	 *            status update identifier passed
	 */
	public void statusUpdateIdentifierInvalid(final String statusUpdateId) {
		this.addStatusMessage(
				ProtocolConstants.StatusCodes.Create.StatusUpdate.STATUS_UPDATE_EXISTING,
				"there is already a status update with the identifier \""
						+ statusUpdateId
						+ "\" existing. Please provide a valid status update identifier.");
	}

	/**
	 * add status message: status update type invalid
	 * 
	 * @param statusUpdateType
	 *            status update type passed
	 */
	public void statusUpdateTypeInvalid(final String statusUpdateType) {
		this.addStatusMessage(
				ProtocolConstants.StatusCodes.Create.StatusUpdate.STATUS_UPDATE_TYPE_NOT_EXISTING,
				"there is no status update template called \""
						+ statusUpdateType
						+ "\" existing. Please provide a valid status update type.");
	}

	/**
	 * add status message: status update instantiation failed
	 * 
	 * @param solution
	 *            detailed description and solution
	 */
	public void statusUpdateInstantiationFailed(final String solution) {
		this.addStatusMessage(
				ProtocolConstants.StatusCodes.Create.StatusUpdate.STATUS_UPDATE_INSTANTIATION_FAILED,
				solution);
	}

	/**
	 * add status message: create status update succeeded
	 */
	public void createStatusUpdateSucceeded() {
		this.addStatusMessage(
				ProtocolConstants.StatusCodes.Create.StatusUpdate.SUCCEEDED, "");
	}

}