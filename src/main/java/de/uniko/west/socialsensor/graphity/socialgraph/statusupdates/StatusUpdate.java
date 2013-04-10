package de.uniko.west.socialsensor.graphity.socialgraph.statusupdates;

/**
 * basic status update class
 * 
 * @author sebschlicht
 * 
 */
public abstract class StatusUpdate {

	/**
	 * status update type identifier
	 */
	protected final String type;

	/**
	 * create a basic status update
	 * 
	 * @param type
	 *            status update type identifier
	 */
	public StatusUpdate(final String type) {
		this.type = type;
	}

	/**
	 * get status update type
	 * 
	 * @return status update type identifier
	 */
	public String getType() {
		return this.type;
	}

}