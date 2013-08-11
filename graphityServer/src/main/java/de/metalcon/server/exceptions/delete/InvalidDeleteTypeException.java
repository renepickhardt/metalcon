package de.metalcon.server.exceptions.delete;

/**
 * the type passed in the delete request is invalid
 * 
 * @author sebschlicht
 * 
 */
public class InvalidDeleteTypeException extends DeleteFailedException {

	/**
	 * serialization information
	 */
	private static final long serialVersionUID = 3962688566539476222L;

	/**
	 * create a new invalid delete type exception
	 * 
	 * @param salvationDescription
	 *            description of the problem supporting the salvation
	 */
	public InvalidDeleteTypeException(final String salvationDescription) {
		super("type invalid", salvationDescription);
	}

}