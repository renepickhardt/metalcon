package de.metalcon.server.exceptions.create.statusupdate;

import de.metalcon.server.exceptions.create.CreateFailedException;

/**
 * basic exception for all failures occurring when handling create status update
 * requests
 * 
 * @author sebschlicht
 * 
 */
public abstract class CreateStatusUpdateFailedException extends
		CreateFailedException {

	/**
	 * serialization information
	 */
	private static final long serialVersionUID = -506215818060441530L;

	/**
	 * create a new basic create status update failed exception
	 * 
	 * @param statusMessage
	 *            status message defined within protocol <b>NSSP</b> in section
	 *            <b>Create a status update</b>
	 * @param salvationDescription
	 *            description of the problem supporting the salvation
	 */
	public CreateStatusUpdateFailedException(final String statusMessage,
			final String salvationDescription) {
		super(statusMessage, salvationDescription);
	}

}