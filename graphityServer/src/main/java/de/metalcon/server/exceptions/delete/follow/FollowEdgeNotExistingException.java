package de.metalcon.server.exceptions.delete.follow;

/**
 * the follow edge that was requested to be deleted in the delete follow edge
 * request is not existing
 * 
 * @author sebschlicht
 * 
 */
public class FollowEdgeNotExistingException extends DeleteFollowFailedException {

	/**
	 * serialization information
	 */
	private static final long serialVersionUID = -3297282297326788029L;

	/**
	 * create a new follow edge not existing exception
	 * 
	 * @param salvationDescription
	 *            description of the problem supporting the salvation
	 */
	public FollowEdgeNotExistingException(final String salvationDescription) {
		super("follow edge not existing", salvationDescription);
	}

}