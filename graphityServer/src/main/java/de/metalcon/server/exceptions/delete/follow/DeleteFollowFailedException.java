package de.metalcon.server.exceptions.delete.follow;

import de.metalcon.server.exceptions.delete.DeleteFailedException;

/**
 * basic exception for all failures occurring when handling delete follow edge
 * requests
 * 
 * @author sebschlicht
 * 
 */
public abstract class DeleteFollowFailedException extends DeleteFailedException {

	/**
	 * serialization information
	 */
	private static final long serialVersionUID = -6944718633477317264L;

	/**
	 * create a new basic delete follow edge failed exception
	 * 
	 * @param statusMessage
	 *            status message defined within protocol <b>NSSP</b> in section
	 *            <b>Delete a follow edge</b>
	 * @param salvationDescription
	 *            description of the problem supporting the salvation
	 */
	public DeleteFollowFailedException(final String statusMessage,
			final String salvationDescription) {
		super(statusMessage, salvationDescription);
	}

}