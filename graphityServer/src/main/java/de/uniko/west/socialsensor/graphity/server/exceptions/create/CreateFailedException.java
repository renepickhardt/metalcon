package de.uniko.west.socialsensor.graphity.server.exceptions.create;

import de.uniko.west.socialsensor.graphity.server.exceptions.RequestFailedException;

/**
 * basic exception for all failures occurring when handling create requests
 * 
 * @author sebschlicht
 * 
 */
public abstract class CreateFailedException extends RequestFailedException {

	/**
	 * serialization information
	 */
	private static final long serialVersionUID = -3453256420936646865L;

	/**
	 * create a new basic create failed exception
	 * 
	 * @param statusMessage
	 *            status message defined within protocol <b>NSSP</b> in section
	 *            <b>Create</b>
	 * @param salvationDescription
	 *            description of the problem supporting the salvation
	 */
	public CreateFailedException(final String statusMessage,
			final String salvationDescription) {
		super(statusMessage, salvationDescription);
	}

}