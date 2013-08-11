package de.metalcon.server.exceptions.create.statusupdate;

/**
 * the status update could not be instantiated with the parameters passed in the
 * create status update request
 * 
 * @author sebschlicht
 * 
 */
public class StatusUpdateInstantiationFailedException extends
		CreateStatusUpdateFailedException {

	/**
	 * serialization information
	 */
	private static final long serialVersionUID = 7189973749442061201L;

	/**
	 * create a new status update instantiation failed exception
	 * 
	 * @param salvationDescription
	 *            description of the problem supporting the salvation
	 */
	public StatusUpdateInstantiationFailedException(
			final String salvationDescription) {
		super("status update instantiation failed", salvationDescription);
	}

}
