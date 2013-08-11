package de.metalcon.server.exceptions.create.follow;

/**
 * the identifier of the followed user passed in the create follow edge request
 * is invalid
 * 
 * @author sebschlicht
 * 
 */
public class InvalidCreateFollowedIdentifier extends
		CreateFollowFailedException {

	/**
	 * serialization information
	 */
	private static final long serialVersionUID = 4632008486218827495L;

	/**
	 * create a new invalid followed identifier exception
	 * 
	 * @param salvationDescription
	 *            description of the problem supporting the salvation
	 */
	public InvalidCreateFollowedIdentifier(final String salvationDescription) {
		super("followed identifier invalid", salvationDescription);
	}

}