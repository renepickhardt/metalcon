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
		 * creation type
		 */
		public static final String TYPE = "type";

		/**
		 * identifier of the user the follow edge shall be created to
		 */
		public static final String FOLLOW_TARGET = "followed_id";

		/**
		 * status update type identifier
		 */
		public static final String STATUS_UPDATE_TYPE = "status_update_type";

	}

	/**
	 * form field identifiers for read requests
	 * 
	 * @author sebschlicht
	 * 
	 */
	public class Read {

		/**
		 * identifier of the user whose status updates are requested
		 */
		public static final String POSTER_ID = "poster_id";

		/**
		 * number of status updates to be retrieved
		 */
		public static final String NUM_ITEMS = "num_items";

		/**
		 * own status updates/social network switch
		 */
		public static final String OWN_UPDATES = "own_updates";

	}

	/**
	 * form field identifiers for delete requests
	 * 
	 * @author sebschlicht
	 * 
	 */
	public class Delete {

		/**
		 * deletion type
		 */
		public static final String TYPE = "type";

		/**
		 * identifier of the user the follow edge shall be deleted from
		 */
		public static final String FOLLOWED = "followed_id";

		/**
		 * identifier of the status update that is to be deleted
		 */
		public static final String STATUS_UPDATE_ID = "status_update_id";

	}

}