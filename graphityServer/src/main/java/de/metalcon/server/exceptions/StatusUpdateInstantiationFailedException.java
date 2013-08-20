package de.metalcon.server.exceptions;

/**
 * the status update could not be instantiated with the parameters passed in the
 * create status update request
 * 
 * @author sebschlicht
 * 
 */
public class StatusUpdateInstantiationFailedException extends Exception {

	/**
	 * serialization information
	 */
	private static final long serialVersionUID = 7189973749442061201L;

	/**
	 * create a new status update instantiation failed exception
	 */
	public StatusUpdateInstantiationFailedException(
			final String salvationDescription) {
		super(salvationDescription);
	}

}
