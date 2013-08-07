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
		public static final String CREATE_TYPE = "creation_type";

		/**
		 * identifier of the user a followship shall be created to
		 */
		public static final String FOLLOWSHIP_TARGET = "target_id";

		/**
		 * status update type identifier
		 */
		public static final String STATUS_UPDATE_TYPE = "type";

	}

	/**
	 * form field identifiers for read requests
	 * 
	 * @author sebschlicht
	 * 
	 */
	public class Read {

		/**
		 * number of status updates to be retrieved
		 */
		public static final String NUM_ITEMS = "numItems";

		/**
		 * own status updates/social network switch
		 */
		public static final String OWN_UPDATES = "ownUpdates";

	}

	/**
	 * form field identifiers for delete requests
	 * 
	 * @author sebschlicht
	 * 
	 */
	public class Delete {

		/**
		 * removal type (followship/status update)
		 */
		public static final String REMOVAL_TYPE = "removal_type";

		/**
		 * identifier of the user the followship to shall be removed from
		 */
		public static final String FOLLOWSHIP_REMOVAL_TARGET = "target_id";

		/**
		 * identifier of the status update that is to be deleted
		 */
		public static final String STATUS_UPDATE_ID = "object_id";

	}

}