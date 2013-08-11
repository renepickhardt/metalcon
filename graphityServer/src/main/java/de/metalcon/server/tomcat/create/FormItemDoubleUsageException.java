package de.metalcon.server.tomcat.create;

/**
 * exception thrown when form items use the same identifier
 * 
 * @author sebschlicht
 * 
 */
public class FormItemDoubleUsageException extends Exception {

	/**
	 * serialization information
	 */
	private static final long serialVersionUID = -8810934884185129326L;

	/**
	 * create a new form item double usage exception
	 * 
	 * @param message
	 *            message describing the occurrence
	 */
	public FormItemDoubleUsageException(final String message) {
		super(message);
	}

}