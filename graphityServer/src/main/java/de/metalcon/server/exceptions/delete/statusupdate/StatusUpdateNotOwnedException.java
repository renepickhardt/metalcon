package de.metalcon.server.exceptions.delete.statusupdate;

/**
 * the status update is not owned by the user executing the delete status update
 * request
 * 
 * @author sebschlicht
 * 
 */
public class StatusUpdateNotOwnedException extends
		DeleteStatusUpdateFailedException {

	/**
	 * serialization information
	 */
	private static final long serialVersionUID = 8139327440971060134L;

	/**
	 * create a new status update not owned exception
	 * 
	 * @param salvationDescription
	 *            description of the problem supporting the salvation
	 */
	public StatusUpdateNotOwnedException(final String salvationDescription) {
		super("status update not owned", salvationDescription);
	}

}