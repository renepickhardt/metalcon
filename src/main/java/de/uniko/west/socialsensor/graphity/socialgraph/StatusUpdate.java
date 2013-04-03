package de.uniko.west.socialsensor.graphity.socialgraph;

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
	protected String type;

	/**
	 * get status update type
	 * 
	 * @return status update type identifier
	 */
	public String getType() {
		return this.type;
	}

}