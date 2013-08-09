package de.uniko.west.socialsensor.graphity.server.exceptions.read;

/**
 * the poster identifier passed in the read request is invalid
 * 
 * @author sebschlicht
 * 
 */
public class InvalidPosterIdentifierException extends ReadFailedException {

	/**
	 * serialization information
	 */
	private static final long serialVersionUID = -7378370093078971431L;

	/**
	 * create a new invalid poster identifier exception
	 * 
	 * @param salvationDescription
	 *            description of the problem supporting the salvation
	 */
	public InvalidPosterIdentifierException(final String salvationDescription) {
		super("poster identifier invalid", salvationDescription);
	}

}