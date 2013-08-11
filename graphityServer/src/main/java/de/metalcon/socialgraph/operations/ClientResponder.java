package de.metalcon.socialgraph.operations;

/**
 * interface to be implemented to respond to HTTP clients
 * 
 * @author Sebastian Schlicht
 * 
 */
public interface ClientResponder {

	/**
	 * add a line to the client response
	 * 
	 * @param line
	 *            line to be sent to the client
	 */
	void addLine(final String line);

	/**
	 * abort the request due to an error occurred
	 * 
	 * @param errorCode
	 *            error code
	 * @param message
	 *            message describing the error
	 */
	void error(final int errorCode, final String message);

	/**
	 * finish the response
	 */
	void finish();

}