package de.metalcon.server.exceptions.delete.follow;

/**
 * the identifier of the followed user passed in the delete follow edge request
 * is invalid
 * 
 * @author sebschlicht
 * 
 */
public class InvalidDeleteFollowedIdentifier extends
		DeleteFollowFailedException {

	/**
	 * serialization information
	 */
	private static final long serialVersionUID = -2412001732546429292L;

	/**
	 * create a new invalid followed identifier exception
	 * 
	 * @param salvationDescription
	 *            description of the problem supporting the salvation
	 */
	public InvalidDeleteFollowedIdentifier(final String salvationDescription) {
		super("followed identifier invalid", salvationDescription);
	}

}