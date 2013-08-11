package de.metalcon.server.exceptions.read;

/**
 * the retrieval flag passed in the read request is invalid
 * 
 * @author sebschlicht
 * 
 */
public class InvalidRetrievalFlag extends ReadFailedException {

	/**
	 * serialization information
	 */
	private static final long serialVersionUID = -3452930068698206115L;

	/**
	 * create a new invalid retrieval flag exception
	 * 
	 * @param salvationDescription
	 *            description of the problem supporting the salvation
	 */
	public InvalidRetrievalFlag(final String salvationDescription) {
		super("retrieval flag invalid", salvationDescription);
	}

}