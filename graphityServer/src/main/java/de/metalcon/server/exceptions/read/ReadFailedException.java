package de.metalcon.server.exceptions.read;

import de.metalcon.server.exceptions.RequestFailedException;

/**
 * basic exception for all failures occurring when handling read requests
 * 
 * @author sebschlicht
 * 
 */
public abstract class ReadFailedException extends RequestFailedException {

	/**
	 * serialization information
	 */
	private static final long serialVersionUID = -2912871535267532104L;

	/**
	 * create a new basic read failed exception
	 * 
	 * @param statusMessage
	 *            status message defined within protocol <b>NSSP</b> in section
	 *            <b>Read</b>
	 * @param salvationDescription
	 *            description of the problem supporting the salvation
	 */
	public ReadFailedException(final String statusMessage,
			final String salvationDescription) {
		super(statusMessage, salvationDescription);
	}

}