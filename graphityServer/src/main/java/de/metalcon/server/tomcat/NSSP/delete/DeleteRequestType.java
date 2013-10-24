package de.metalcon.server.tomcat.NSSP.delete;


/**
 * delete request type
 * 
 * @author sebschlicht
 * 
 */
public enum DeleteRequestType {

	/**
	 * delete a user
	 */
	USER("user"),

	/**
	 * delete a follow edge from one user to another
	 */
	FOLLOW("follow"),

	/**
	 * delete a status update the user created
	 */
	STATUS_UPDATE("status_update");

	/**
	 * delete request type identifier used as command parameter
	 */
	private final String identifier;

	/**
	 * create a new delete request type
	 * 
	 * @param identifier
	 *            delete request type identifier used as command parameter
	 */
	private DeleteRequestType(final String identifier) {
		this.identifier = identifier;
	}

	/**
	 * access the identifier to switch between types when handling requests
	 * 
	 * @return delete request type identifier used as command parameter
	 */
	public String getIdentifier() {
		return this.identifier;
	}

}