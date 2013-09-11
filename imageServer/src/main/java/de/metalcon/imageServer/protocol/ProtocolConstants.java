package de.metalcon.imageServer.protocol;

/**
 * constants used in the image server protocol
 */
public class ProtocolConstants {

	// TODO: JavaDoc

	/**
	 * JSON field name for the status message
	 */
	public static final String STATUS_MESSAGE = "statusMessage";

	/**
	 * JSON field name for the detailed description and the solution
	 */
	public static final String SOLUTION = "solution";

	public class LogMessage {
		// TODO: improve log messages
		public static final String HASH_COLLISION = "Hash collision occured";
		public static final String CROPPING_FAILURE = "Cropping failed";
		public static final String SCALING_FAILURE = "Scaling failed";
		public static final String READ_PROCESS_FAILED = "Read Process failed";
		public static final String FILE_NOT_FOUND = "File not found";
		public static final String ARCHIVE_STREAM_WRITING_FAILED = "Writing the archive stream failed";
	}

	public class StatusMessage {

		public static final String INTERNAL_SERVER_ERROR = "An internal server error has occured";

		public class Create {

			public static final String IMAGE_IDENTIFIER_IN_USE = "image identifier passed is already in use";
			public static final String META_DATA_MALFORMED = "meta data passed is malformed";
			public static final String IMAGE_STREAM_INVALID = "image stream passed is invalid";
			public static final String IMAGE_URL_MALFORMED = "image URL passed is malformed";
			public static final String IMAGE_URL_INVALID = "image URL passed is invalid";
			public static final String AUTOROTATE_FLAG_MALFORMED = "auto rotation flag passed is malformed";
		}

		// TODO
		public class Read {

			public static final String NO_IMAGE_FOUND = "No image found";
			public static final String GEOMETRY_BIGGER_THAN_ORIGINAL = "Requested geometry bigger than original size";
		}

		public class Update {

			public static final String IMAGE_NOT_EXISTING = "image identifier passed is not in use";
			public static final String META_DATA_MALFORMED = "meta data passed is malformed";
		}

		public class Delete {

			public static final String IMAGE_NOT_EXISTING = "image identifier passed is not in use";
		}
	}

	public class Parameters {

		public class Create {

			public static final String IMAGE_IDENTIFIER = "imageIdentifier";
			public static final String IMAGESTREAM = "imageStream";
			public static final String META_DATA = "metaData";
			public static final String AUTOROTATE_FLAG = "autoRotateFlag";
		}

		public class Read {

			public static final String IMAGE_IDENTIFIER = "imageIdentifier";
			public static final String ORIGINAL_FLAG = "originalFlag";
			public static final String IMAGE_WIDTH = "imageWidth";
			public static final String IMAGE_HEIGHT = "imageHeight";
		}

		public class Update {

			public static final String IMAGE_IDENTIFIER = "imageIdentifier";
			public static final String META_DATA = "metaData";
		}

		public class Delete {

			public static final String IMAGE_IDENTIFIER = "imageIdentifier";
		}
	}

}