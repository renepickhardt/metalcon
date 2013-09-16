package de.metalcon.musicStorageServer;

/**
 * music item converting process failed
 * 
 * @author sebschlicht
 * 
 */
public class ConvertingFailedException extends Exception {

	public ConvertingFailedException(final String message) {
		super(message);
	}

}