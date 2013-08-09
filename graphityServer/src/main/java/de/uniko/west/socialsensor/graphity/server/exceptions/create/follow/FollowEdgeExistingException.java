package de.uniko.west.socialsensor.graphity.server.exceptions.create.follow;

/**
 * the follow edge that was requested to be created in the create follow edge
 * request is already existing
 * 
 * @author sebschlicht
 * 
 */
public class FollowEdgeExistingException extends CreateFollowFailedException {

	/**
	 * serialization information
	 */
	private static final long serialVersionUID = -3497493507345187708L;

	/**
	 * create a new follow edge existing exception
	 * 
	 * @param salvationDescription
	 *            description of the problem supporting the salvation
	 */
	public FollowEdgeExistingException(final String salvationDescription) {
		super("follow edge existing", salvationDescription);
	}

}