package de.metalcon.server.exceptions.create.statusupdate;

/**
 * the status update identifier passed in the create status update request is
 * invalid
 * 
 * @author sebschlicht
 * 
 */
public class InvalidCreateStatusUpdateIdentifierException extends
		CreateStatusUpdateFailedException {

	/**
	 * serialization information
	 */
	private static final long serialVersionUID = 4795183167531928985L;

	/**
	 * create a new invalid status update identifier exception
	 * 
	 * @param salvationDescription
	 *            description of the problem supporting the salvation
	 */
	public InvalidCreateStatusUpdateIdentifierException(
			final String salvationDescription) {
		super("status update identifier invalid", salvationDescription);
	}

}