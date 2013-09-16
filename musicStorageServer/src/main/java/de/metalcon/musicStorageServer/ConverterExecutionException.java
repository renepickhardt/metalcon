package de.metalcon.musicStorageServer;

/**
 * music item converting failed due to internal errors relating to the converter
 * 
 * @author sebschlicht
 * 
 */
public class ConverterExecutionException extends Exception {

	public ConverterExecutionException(final String message) {
		super(message);
	}

}