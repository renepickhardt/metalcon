package de.metalcon.musicStorageServer.protocol;

public class ProtocolConstants {

	/**
	 * response key for the status message
	 */
	public static final String STATUS_MESSAGE = "status_message";

	/**
	 * response key for a detailed description/solution
	 */
	public static final String SOLUTION = "solution";

	/**
	 * parameters for requests
	 * 
	 * @author sebschlicht
	 * 
	 */
	public class Parameter {

		/**
		 * parameters used in the create requests
		 * 
		 * @author sebschlicht
		 * 
		 */
		public class Create {

			public static final String MUSIC_ITEM_IDENTIFIER = "musicItemIdentifier";
			public static final String MUSIC_ITEM = "musicItem";
			public static final String META_DATA = "metaData";

		}

		/**
		 * parameters used in the read requests
		 * 
		 * @author sebschlicht
		 * 
		 */
		public class Read {

			public static final String MUSIC_ITEM_IDENTIFIER = "musicItemIdentifier";
			public static final String MUSIC_ITEM_VERSION = "musicItemVersion";

			/**
			 * read meta data of any number of music items
			 */
			public static final String MUSIC_ITEM_IDENTIFIERS = "musicItemIdentifiers";

		}

		/**
		 * parameters used in the update requests
		 * 
		 * @author sebschlicht
		 * 
		 */
		public class Update {

			public static final String MUSIC_ITEM_IDENTIFIER = "musicItemIdentifier";
			public static final String META_DATA = "metaData";

		}

		/**
		 * parameters used in the delete requests
		 * 
		 * @author sebschlicht
		 * 
		 */
		public class Delete {

			public static final String MUSIC_ITEM_IDENTIFIER = "musicItemIdentifier";

		}

	}

	/**
	 * status messages for responses
	 * 
	 * @author sebschlicht
	 * 
	 */
	public class StatusMessage {

		/**
		 * status messages for create responses
		 * 
		 * @author sebschlicht
		 * 
		 */
		public class Create {

			public static final String MUSIC_ITEM_IDENTIFIER_IN_USE = "music item identifier in use";
			public static final String MUSIC_ITEM_STREAM_INVALID = "music item stream invalid";
			public static final String META_DATA_MALFORMED = "meta data malformed";

		}

		/**
		 * status messages for read responses
		 * 
		 * @author sebschlicht
		 * 
		 */
		public class Read {

			public static final String MUSIC_ITEM_NOT_EXISTING = "music item not existing";
			public static final String MUSIC_ITEM_VERSION_INVALID = "music item version invalid";

		}

		/**
		 * status messages for update responses
		 * 
		 * @author sebschlicht
		 * 
		 */
		public class Update {

			public static final String MUSIC_ITEM_NOT_EXISTING = "music item not existing";
			public static final String META_DATA_MALFORMED = "meta data malformed";

		}

		/**
		 * status messages for delete responses
		 * 
		 * @author sebschlicht
		 * 
		 */
		public class Delete {

			public static final String MUSIC_ITEM_NOT_EXISTING = "music item not existing";

		}

	}

	/**
	 * server log messages
	 * 
	 * @author sebschlicht
	 * 
	 */
	public class LogMessage {

		public static final String HASH_COLLISION = "hash collision occurred!";

	}

}