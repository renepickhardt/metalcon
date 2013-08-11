package de.metalcon.server.exceptions.delete.statusupdate;

/**
 * the status update identifier passed in the delete status update request is
 * invalid
 * 
 * @author sebschlicht
 * 
 */
public class InvalidStatusUpdateIdentifierException extends
		DeleteStatusUpdateFailedException {

	/**
	 * serialization information
	 */
	private static final long serialVersionUID = 8475426856101752797L;

	/**
	 * create a new invalid status update identifier exception
	 * 
	 * @param salvationDescription
	 *            description of the problem supporting the salvation
	 */
	public InvalidStatusUpdateIdentifierException(
			final String salvationDescription) {
		super("status update identifier invalid", salvationDescription);
	}

}