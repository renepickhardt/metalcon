package de.metalcon.server.tomcat.NSSP;

/**
 * NewsStreamServerProtocol constants
 * 
 * @author sebschlicht
 * 
 */
public class ProtocolConstants {

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
		 * status codes for create requests
		 * 
		 * @author sebschlicht
		 * 
		 */
		public class Create {

			/**
			 * the create request is not using a multipart form
			 */
			public static final String NO_MULTIPART_REQUEST = "create requests have to be multipart requests";

			/**
			 * the create request type is invalid
			 */
			public static final String TYPE_INVALID = "type invalid";

			/**
			 * status codes for create user requests
			 * 
			 * @author sebschlicht
			 * 
			 */
			public class User {

				/**
				 * everything went fine
				 */
				public static final String SUCCEEDED = "ok";

				/**
				 * there is already a user with the identifier existing
				 */
				public static final String USER_EXISTING = "user identifier invalid";

			}

			/**
			 * status codes for create follow edge requests
			 * 
			 * @author sebschlicht
			 * 
			 */
			public class Follow {

				/**
				 * everything went fine
				 */
				public static final String SUCCEEDED = "ok";

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
			 * status codes for create status update requests
			 * 
			 * @author sebschlicht
			 * 
			 */
			public class StatusUpdate {

				/**
				 * everything went fine
				 */
				public static final String SUCCEEDED = "ok";

				/**
				 * there is no user with the identifier of the user requesting
				 */
				public static final String USER_NOT_EXISTING = "user identifier invalid";

				/**
				 * there is already a status update with the identifier existing
				 */
				public static final String STATUS_UPDATE_EXISTING = "status update identifier invalid";

				/**
				 * the status update template is unknown
				 */
				public static final String STATUS_UPDATE_TYPE_NOT_EXISTING = "status update type invalid";

				/**
				 * the parameters passed did not match the status update
				 * template specified
				 */
				public static final String STATUS_UPDATE_INSTANTIATION_FAILED = "status update instantiation failed";

			}

		}

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
				 * everything went fine
				 */
				public static final String SUCCEEDED = "ok";

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
				 * everything went fine
				 */
				public static final String SUCCEEDED = "ok";

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
				 * everything went fine
				 */
				public static final String SUCCEEDED = "ok";

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
		 * parameters for create requests
		 * 
		 * @author sebschlicht
		 * 
		 */
		public class Create {

			/**
			 * create request type
			 */
			public static final String TYPE = "type";

			/**
			 * parameters for create user requests
			 * 
			 * @author sebschlicht
			 * 
			 */
			public class User {

				/**
				 * identifier of the user to be created
				 */
				public static final String USER_IDENTIFIER = "user_id";

				/**
				 * display name of the new user
				 */
				public static final String DISPLAY_NAME = "user_display_name";

				/**
				 * path to the profile picture of the new user
				 */
				public static final String PROFILE_PICTURE_PATH = "user_profile_picture_path";

			}

			/**
			 * parameters for create follow edge requests
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
				 * identifier of the user followed
				 */
				public static final String FOLLOWED_IDENTIFIER = "followed_id";

			}

			/**
			 * parameters for create status update requests
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
				 * identifier of the status update to be created
				 */
				public static final String STATUS_UPDATE_IDENTIFIER = "status_update_id";

				/**
				 * status update template used
				 */
				public static final String STATUS_UPDATE_TYPE = "status_update_type";

			}

		}

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
				 * identifier of the user followed
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

}