package de.uniko.west.socialsensor.graphity.server.tomcat;

/**
 * form field identifier for server requests
 * 
 * @author sebschlicht
 * 
 */
public class FormFields {

	/**
	 * user identifier (TODO: delete)
	 */
	public static final String USER_ID = "user_id";

	/**
	 * form field identifies for create requests
	 * 
	 * @author sebschlicht
	 * 
	 */
	public class Create {

		/**
		 * creation type (followship/status update)
		 */
		public static final String CREATE_TYPE = "createType";

		/**
		 * identifier of the user a followship shall be created to
		 */
		public static final String FOLLOWSHIP_TARGET = "targetId";

		/**
		 * status update type identifier
		 */
		public static final String STATUS_UPDATE_TYPE = "type";

	}

}