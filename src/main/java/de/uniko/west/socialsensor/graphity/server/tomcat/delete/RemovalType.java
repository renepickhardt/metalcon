package de.uniko.west.socialsensor.graphity.server.tomcat.delete;


/**
 * type of a removal request
 * 
 * @author sebschlicht
 * 
 */
public enum RemovalType {

	/**
	 * remove a followship from one user to another
	 */
	FOLLOWSHIP("friendship"),

	/**
	 * remove a status update the user created
	 */
	STATUS_UPDATE("status_update");

	/**
	 * removal type identifier used as command parameter
	 */
	private final String identifier;

	/**
	 * create a new removal type
	 * 
	 * @param identifier
	 *            removal type identifier used as command parameter
	 */
	private RemovalType(final String identifier) {
		this.identifier = identifier;
	}

	/**
	 * access the identifier to switch between types when handling requests
	 * 
	 * @return removal type identifier used as command parameter
	 */
	public String getIdentifier() {
		return this.identifier;
	}

	/**
	 * get the removal type matching to the identifier passed
	 * 
	 * @param identifier
	 *            removal type identifier
	 * @return removal type
	 * @throws IllegalArgumentException
	 *             if the identifier is invalid
	 */
	public static RemovalType GetRemovalType(final String identifier) {
		if (FOLLOWSHIP.getIdentifier().equals(identifier)) {
			return FOLLOWSHIP;
		} else if (STATUS_UPDATE.getIdentifier().equals(identifier)) {
			return STATUS_UPDATE;
		}

		throw new IllegalArgumentException("\"" + identifier
				+ "\" is not a valid removal type identifier!");
	}

}