package de.metalcon.server.exceptions;

/**
 * basic class for all failures occurring when handling requests
 * 
 * @author sebschlicht
 * 
 */
public abstract class RequestFailedException extends RuntimeException {

	/**
	 * serialization information
	 */
	private static final long serialVersionUID = -4802214548980004110L;

	/**
	 * description of the problem supporting the salvation
	 */
	private final String salvationDescription;

	/**
	 * create a new basic request failed exception
	 * 
	 * @param statusMessage
	 *            status message defined within protocol <b>NSSP</b>
	 * @param salvationDescription
	 *            description of the problem supporting the salvation
	 */
	public RequestFailedException(final String statusMessage,
			final String salvationDescription) {
		super(statusMessage);
		this.salvationDescription = salvationDescription;
	}

	/**
	 * 
	 * @return description of the problem supporting the salvation
	 */
	public String getSalvationDescription() {
		return this.salvationDescription;
	}

}