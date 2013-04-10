package de.uniko.west.socialsensor.graphity.socialgraph.statusupdates;

/**
 * plain text status update
 * 
 * @author Sebastian Schlicht
 * 
 */
public class PlainText extends StatusUpdate {

	/**
	 * status update type identifier
	 */
	public static final String TYPE_IDENTIFIER = "plain";

	/**
	 * user message
	 */
	private String message;

	/**
	 * create a new plain text status update
	 * 
	 * @param message
	 *            user message
	 */
	public PlainText(final String message) {
		super(TYPE_IDENTIFIER);
		this.message = message;
	}

}