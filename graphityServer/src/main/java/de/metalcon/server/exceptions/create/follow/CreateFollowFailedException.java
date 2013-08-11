package de.metalcon.server.exceptions.create.follow;

import de.metalcon.server.exceptions.create.CreateFailedException;

/**
 * basic exception for all failures occurring when handling create follow edge
 * requests
 * 
 * @author sebschlicht
 * 
 */
public abstract class CreateFollowFailedException extends CreateFailedException {

	/**
	 * serialization information
	 */
	private static final long serialVersionUID = 4850922823107224380L;

	/**
	 * create a new basic create follow edge failed exception
	 * 
	 * @param statusMessage
	 *            status message defined within protocol <b>NSSP</b> in section
	 *            <b>Create a follow edge</b>
	 * @param salvationDescription
	 *            description of the problem supporting the salvation
	 */
	public CreateFollowFailedException(final String statusMessage,
			final String salvationDescription) {
		super(statusMessage, salvationDescription);
	}

}