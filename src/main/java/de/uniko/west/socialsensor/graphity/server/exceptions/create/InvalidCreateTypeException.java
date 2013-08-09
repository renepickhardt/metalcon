package de.uniko.west.socialsensor.graphity.server.exceptions.create;

/**
 * the type passed in the create request is invalid
 * 
 * @author sebschlicht
 * 
 */
public class InvalidCreateTypeException extends CreateFailedException {

	/**
	 * serialization information
	 */
	private static final long serialVersionUID = -5186497545492862439L;

	/**
	 * create a new invalid create type exception
	 * 
	 * @param salvationDescription
	 *            description of the problem supporting the salvation
	 */
	public InvalidCreateTypeException(final String salvationDescription) {
		super("type invalid", salvationDescription);
	}

}