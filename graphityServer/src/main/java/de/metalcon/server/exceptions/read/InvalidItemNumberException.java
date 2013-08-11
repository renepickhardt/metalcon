package de.metalcon.server.exceptions.read;

/**
 * the number of items to be retrieved passed in the read request
 * 
 * @author sebschlicht
 * 
 */
public class InvalidItemNumberException extends ReadFailedException {

	/**
	 * serialization information
	 */
	private static final long serialVersionUID = 6898080924327938745L;

	/**
	 * create a new invalid item number exception
	 * 
	 * @param salvationDescription
	 *            description of the problem supporting the salvation
	 */
	public InvalidItemNumberException(final String salvationDescription) {
		super("number of items invalid", salvationDescription);
	}

}