package de.uniko.west.socialsensor.graphity.server.exceptions;

/**
 * the user identifier passed in the request is invalid<br>
 * TODO: use OAuth and remove user identifier
 * 
 * @author sebschlicht
 * 
 */
public class InvalidUserIdentifierException extends RequestFailedException {

	/**
	 * serialization information
	 */
	private static final long serialVersionUID = -947996333609513197L;

	/**
	 * create a new invalid user identifier exception
	 * 
	 * @param salvationDescription
	 *            description of the problem supporting the salvation
	 */
	public InvalidUserIdentifierException(final String salvationDescription) {
		super("user identifier invalid", salvationDescription);
	}

}