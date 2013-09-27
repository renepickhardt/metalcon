package de.metalcon.imageStorageServer.protocol;

/**
 * constants used in the image server protocol
 */
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
	 */
	// TODO: this should be called "Parameter"
	public class Parameters {

		/**
		 * parameters used in the create requests
		 */
		public class Create {

			public static final String IMAGE_IDENTIFIER = "imageIdentifier";
			public static final String IMAGE_STREAM = "imageStream";
			public static final String META_DATA = "metaData";
			public static final String AUTOROTATE_FLAG = "autoRotationFlag";
			public static final String CROP_LEFT = "left";
			public static final String CROP_TOP = "top";
			public static final String CROP_HEIGHT = "height";
			public static final String CROP_WIDTH = "width";
			public static final String IMAGE_ITEM = "imageItem";
			public static final String URL = "imageUrl";

		}

		/**
		 * parameters used in the read requests
		 */
		public class Read {

			public static final String IMAGE_IDENTIFIER = "imageIdentifier";
			public static final String ORIGINAL_FLAG = "originalFlag";
			public static final String IMAGE_WIDTH = "width";
			public static final String IMAGE_HEIGHT = "height";
			public static final String IMAGE_IDENTIFIER_LIST = "imageIdentifierList";

		}

		/**
		 * parameters used in the update requests
		 */
		public class Update {

			public static final String IMAGE_IDENTIFIER = "imageIdentifier";
			public static final String META_DATA = "metaData";

		}

		/**
		 * parameters used in the delete requests
		 */
		public class Delete {

			public static final String IMAGE_IDENTIFIER = "imageIdentifier";

		}
	}

	/**
	 * status messages for responses
	 */
	public class StatusMessage {

		public static final String INTERNAL_SERVER_ERROR = "An internal server error has occured";

		/**
		 * status messages for create responses
		 */
		public class Create {

			public static final String IMAGE_IDENTIFIER_IN_USE = "image identifier in use";
			public static final String META_DATA_MALFORMED = "meta data malformed";
			public static final String IMAGE_STREAM_INVALID = "image stream invalid";

			public static final String IMAGE_URL_MALFORMED = "image URL passed is malformed";
			public static final String IMAGE_URL_INVALID = "image URL passed is invalid";

			public static final String AUTOROTATE_FLAG_MALFORMED = "auto rotation flag malformed";
			public static final String CROP_LEFT_INVALID = "left-hand side cropping coordinate invalid";
			public static final String CROP_TOP_INVALID = "top side cropping coordinate invalid";
			public static final String CROP_HEIGHT_INVALID = "cropping height invalid";
			public static final String CROP_WIDTH_INVALID = "cropping width invalid";

			public static final String CROP_LEFT_MALFORMED = "left-hand side cropping coordinate malformed";
			public static final String CROP_TOP_MALFORMED = "top side cropping coordinate malformed";
			public static final String CROP_HEIGHT_MALFORMED = "cropping height malformed";
			public static final String CROP_WIDTH_MALFORMED = "cropping width malformed";

		}

		/**
		 * status messages for read responses
		 */
		public class Read {

			public static final String NO_IMAGE_FOUND = "No image found";
			public static final String GEOMETRY_BIGGER_THAN_ORIGINAL = "Requested geometry bigger than original size";
			public static final String IDENTIFIER_LIST_CONTAINS_EMPTY_PARTS = "Identifier list contains empty parts";
			public static final String SCALING_WIDTH_MALFORMED = "scaling width malformed";
			public static final String SCALING_HEIGHT_MALFORMED = "scaling height malformed";
			public static final String SCALING_WIDTH_INVALID = "scaling width invalid";
			public static final String SCALING_HEIGHT_INVALID = "scaling height invalid";

		}

		/**
		 * status messages for update responses
		 */
		public class Update {

			public static final String IMAGE_NOT_EXISTING = "image not existing";
			public static final String META_DATA_MALFORMED = "meta data malformed";

		}

		/**
		 * status messages for delete responses
		 */
		public class Delete {

			public static final String IMAGE_NOT_EXISTING = "image not existing";

		}
	}

	/**
	 * server log messages
	 */
	public class LogMessage {

		// TODO: improve log messages
		public static final String HASH_COLLISION = "Hash collision occured";
		public static final String CROPPING_FAILURE = "Cropping failed";
		public static final String SCALING_FAILURE = "Scaling failed";
		public static final String ARCHIVE_STREAM_WRITING_FAILED = "Writing the archive stream failed";

	}

}
