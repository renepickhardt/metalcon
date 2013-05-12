package de.uniko.west.socialsensor.graphity.server.statusupdates.templates;

import java.util.Map;

import de.uniko.west.socialsensor.graphity.server.statusupdates.StatusUpdate;

/**
 * plain text status update
 * 
 * @author Sebastian Schlicht
 * 
 */
public class PlainText_Cmpl extends StatusUpdate {

	/**
	 * status update type identifier
	 */
	public static final String TYPE_IDENTIFIER = PlainText_Cmpl.class.getName();

	/**
	 * user message
	 */
	public String message;

	/**
	 * create a new plain text status update
	 */
	public PlainText_Cmpl() {
		super(TYPE_IDENTIFIER);
	}

	@Override
	protected Map<String, Object> toObjectJSON() {
		final Map<String, Object> objectJSON = super.toObjectJSON();
		objectJSON.put("message", this.message);
		return objectJSON;
	}

}