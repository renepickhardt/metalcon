package de.metalcon.server.tomcat.NSSP.create;


/**
 * create request type
 * 
 * @author sebschlicht
 * 
 */
public enum CreateRequestType {

	/**
	 * create a user
	 */
	USER("user"),

	/**
	 * create a follow edge from one user to another
	 */
	FOLLOW("follow"),

	/**
	 * create a status update for the user requesting
	 */
	STATUS_UPDATE("status_update");

	/**
	 * create request type identifier used as command parameter
	 */
	private final String identifier;

	/**
	 * create a new create request type
	 * 
	 * @param identifier
	 *            create request type identifier used as command parameter
	 */
	private CreateRequestType(final String identifier) {
		this.identifier = identifier;
	}

	/**
	 * access the identifier to switch between types when handling requests
	 * 
	 * @return create request type identifier used as command parameter
	 */
	public String getIdentifier() {
		return this.identifier;
	}

}