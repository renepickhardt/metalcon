package de.metalcon.server.exceptions.delete.statusupdate;

import de.metalcon.server.exceptions.delete.DeleteFailedException;

/**
 * basic exception for all failures occurring when handling delete status update
 * requests
 * 
 * @author sebschlicht
 * 
 */
public abstract class DeleteStatusUpdateFailedException extends
		DeleteFailedException {

	/**
	 * serialization information
	 */
	private static final long serialVersionUID = -7814272429956101877L;

	/**
	 * create a new basic delete status update failed exception
	 * 
	 * @param statusMessage
	 *            status message defined within protocol <b>NSSP</b> in section
	 *            <b>Delete a status update</b>
	 * @param salvationDescription
	 *            description of the problem supporting the salvation
	 */
	public DeleteStatusUpdateFailedException(final String statusMessage,
			final String salvationDescription) {
		super(statusMessage, salvationDescription);
	}

}