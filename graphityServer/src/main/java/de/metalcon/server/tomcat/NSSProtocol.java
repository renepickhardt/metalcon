package de.metalcon.server.tomcat;

/**
 * NewsStreamServerProtocol constants
 * 
 * @author sebschlicht
 * 
 */
public class NSSProtocol {

	/**
	 * JSON field name for the status message
	 */
	public static final String STATUS_MESSAGE = "statusMessage";

	/**
	 * JSON field name for the detailed description and the solution
	 */
	public static final String SOLUTION = "solution";

	/**
	 * status codes for NSSP requests
	 * 
	 * @author sebschlicht
	 * 
	 */
	public class StatusCodes {

		/**
		 * status codes for read requests
		 * 
		 * @author sebschlicht
		 * 
		 */
		public class Read {

			/**
			 * there is no user with the identifier of the user requesting
			 */
			public static final String USER_NOT_EXISTING = "user identifier invalid";

			/**
			 * there is no user with the identifier of the user posted
			 */
			public static final String POSTER_NOT_EXISTING = "poster identifier invalid";

			/**
			 * the number of items is invalid (no number | number <= 0)
			 */
			public static final String NUM_ITEMS_INVALID = "number of items invalid";

			/**
			 * the retrieval flag is invalid (no number)
			 */
			public static final String OWN_UPDATES_INVALID = "retrieval flag invalid";

		}

		/**
		 * status codes for delete requests
		 * 
		 * @author sebschlicht
		 * 
		 */
		public class Delete {

			/**
			 * the delete request type is invalid
			 */
			public static final String TYPE_INVALID = "type invalid";

			/**
			 * status codes for delete user requests
			 * 
			 * @author sebschlicht
			 * 
			 */
			public class User {

				/**
				 * there is no user with the identifier of the user requesting
				 */
				public static final String USER_NOT_EXISTING = "user identifier invalid";

			}

			/**
			 * status codes for delete follow edge requests
			 * 
			 * @author sebschlicht
			 * 
			 */
			public class Follow {

				/**
				 * there is no user with the identifier of the user requesting
				 */
				public static final String USER_NOT_EXISTING = "user identifier invalid";

				/**
				 * there is no user with the identifier of the user followed
				 */
				public static final String FOLLOWED_NOT_EXISTING = "followed identifier invalid";

			}

			/**
			 * status codes for delete status update requests
			 * 
			 * @author sebschlicht
			 * 
			 */
			public class StatusUpdate {

				/**
				 * there is no user with the identifier of the user requesting
				 */
				public static final String USER_NOT_EXISTING = "user identifier invalid";

				/**
				 * there is no status update with the identifier passed
				 */
				public static final String STATUS_UPDATE_NOT_EXISTING = "status update identifier invalid";

			}

		}

	}

	/**
	 * parameters for NSSP requests
	 * 
	 * @author sebschlicht
	 * 
	 */
	public class Parameters {

		/**
		 * parameters for read requests
		 * 
		 * @author sebschlicht
		 * 
		 */
		public class Read {

			/**
			 * identifier of the user requesting
			 */
			public static final String USER_IDENTIFIER = "user_id";

			/**
			 * identifier of the user posted
			 */
			public static final String POSTER_IDENTIFIER = "poster_id";

			/**
			 * number of items to retrieve
			 */
			public static final String NUM_ITEMS = "num_items";

			/**
			 * retrieval flag
			 */
			public static final String OWN_UPDATES = "own_updates";

		}

		/**
		 * parameters for delete requests
		 * 
		 * @author sebschlicht
		 * 
		 */
		public class Delete {

			/**
			 * delete request type
			 */
			public static final String TYPE = "type";

			/**
			 * parameters for delete user requests
			 * 
			 * @author sebschlicht
			 * 
			 */
			public class User {

				/**
				 * identifier of the user to be deleted
				 */
				public static final String USER_IDENTIFIER = "user_id";

			}

			/**
			 * parameters for delete follow edge requests
			 * 
			 * @author sebschlicht
			 * 
			 */
			public class Follow {

				/**
				 * identifier of the user requesting
				 */
				public static final String USER_IDENTIFIER = "user_id";

				/**
				 * identifier if the user followed
				 */
				public static final String FOLLOWED_IDENTIFIER = "followed_id";

			}

			/**
			 * parameters for delete status update requests
			 * 
			 * @author sebschlicht
			 * 
			 */
			public class StatusUpdate {

				/**
				 * identifier of the user requesting
				 */
				public static final String USER_IDENTIFIER = "user_id";

				/**
				 * identifier of the status update to be deleted
				 */
				public static final String STATUS_UPDATE_IDENTIFIER = "status_update_id";

			}

		}

	}

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
		 * identifier of the new user
		 */
		public static final String USER_ID = "user_id";

		/**
		 * display name of the new user
		 */
		public static final String USER_DISPLAY_NAME = "user_display_name";

		/**
		 * path to the profile picture of the new user
		 */
		public static final String USER_PROFILE_PICTURE_PATH = "user_profile_picture_path";

		/**
		 * identifier of the user the follow edge shall be created to
		 */
		public static final String FOLLOW_TARGET = "followed_id";

		/**
		 * identifier of the status update that is to be created
		 */
		public static final String STATUS_UPDATE_ID = "status_update_id";

		/**
		 * status update type identifier
		 */
		public static final String STATUS_UPDATE_TYPE = "status_update_type";

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
		 * identifier of the user that shall be deleted
		 */
		public static final String USER_ID = "user_id";

		/**
		 * identifier of the status update that is to be deleted
		 */
		public static final String STATUS_UPDATE_ID = "status_update_id";

	}

}