package de.metalcon.server.exceptions.create.statusupdate;

/**
 * the status update type passed in the create status update request is invalid
 * 
 * @author sebschlicht
 * 
 */
public class InvalidStatusUpdateTypeException extends
		CreateStatusUpdateFailedException {

	/**
	 * serialization information
	 */
	private static final long serialVersionUID = 6208661989810303159L;

	/**
	 * create a new invalid status update type exception
	 * 
	 * @param salvationDescription
	 *            description of the problem supporting the salvation
	 */
	public InvalidStatusUpdateTypeException(final String salvationDescription) {
		super("status update type invalid", salvationDescription);
	}

}