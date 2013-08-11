package de.metalcon.server.exceptions.delete;

import de.metalcon.server.exceptions.RequestFailedException;

/**
 * basic exception for all failures occurring when handling delete requests
 * 
 * @author sebschlicht
 * 
 */
public abstract class DeleteFailedException extends RequestFailedException {

	/**
	 * serialization information
	 */
	private static final long serialVersionUID = -6493862090077942362L;

	/**
	 * create a new basic delete failed exception
	 * 
	 * @param statusMessage
	 *            status message defined within protocol <b>NSSP</b> in section
	 *            <b>Delete</b>
	 * @param salvationDescription
	 *            description of the problem supporting the salvation
	 */
	public DeleteFailedException(final String statusMessage,
			final String salvationDescription) {
		super(statusMessage, salvationDescription);
	}

}