package de.metalcon.musicStorageServer.converting.exceptions;

/**
 * music item converting process failed
 * 
 * @author sebschlicht
 * 
 */
public class ConvertingFailedException extends Exception {

	/**
	 * serialization information
	 */
	private static final long serialVersionUID = 7745081711404679571L;

	public ConvertingFailedException(final String message) {
		super(message);
	}

	@Override
	public String toString() {
		return "converting process failed: " + this.getMessage() + "!";
	}

}