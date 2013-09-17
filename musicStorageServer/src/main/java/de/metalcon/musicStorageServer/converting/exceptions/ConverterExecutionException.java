package de.metalcon.musicStorageServer.converting.exceptions;

/**
 * music item converting failed due to internal errors relating to the converter
 * 
 * @author sebschlicht
 * 
 */
public class ConverterExecutionException extends Exception {

	/**
	 * serialization information
	 */
	private static final long serialVersionUID = -8276350627961815813L;

	public ConverterExecutionException(final String message) {
		super(message);
	}

	@Override
	public String toString() {
		return "the converting failed due to an internal error: "
				+ this.getMessage();
	}

}