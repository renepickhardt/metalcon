package de.metalcon.imageServer.protocol;

/**
 * constants used in the image server protocol
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

	public class LogMessage {
		// TODO: improve log messages
		public static final String HASH_COLLISION = "Hash collision occured";
		public static final String CROPPING_FAILURE = "Cropping failed";
		public static final String SCALING_FAILURE = "Scaling failed";
		public static final String READ_PROCESS_FAILED = "Read Process failed";
	}

	public class Solution {
		// TODO: improve solution hints

		public class Create {
			public static final String IMAGE_METADATA_MALFORMED = "Please check the data format of the meta data";
			public static final String IMAGE_INVALID = "The image provided can't be processed. Please check if your file is an image encoded in a format supported by ImageMagick";
			public static final String IMAGE_IDENTIFIER_ALREADY_EXISTS = "Please provide a unique identifier for each image. You should consider checking your identifier identifier management system";
			public static final String URL_MALFORMED = "Please check if the provided URL is valid";
		}

		public class Read {
			public static final String NO_IMAGE_FOUND = "Please check if the identifier is correct";
			public static final String GEMOTRY_BIGGER_THAN_ORIGINAL = "The requested image geometry should not be greater than the original size. Otherwise you will only receive the original image scale but never an upscaled version. If you need to request a bigger image, please provide one first.";
		}
	}

	public class StatusMessage {
		public class Create {

			public static final String IMAGE_IDENTIFIER_MISSING = "No Image Identifier given";
			public static final String IMAGESTREAM_MISSING = "No ImageStream found";
			public static final String IMAGE_METADATA_MISSING = "No meta data given";
			public static final String AUTOROTATE_FLAG_MISSING = "Autorotate flag not found";
			public static final String AUTOROTATE_FLAG_MALFORMED = "Autorotate flag malformed";
			public static final String REQUEST_BROKEN_RESPONSE_BEGIN = "request corrupt: ";
			public static final String REQUEST_BROKEN_RESPONSE_END = "is malformed";
			public static final String IMAGE_IDENTIFIER_ALREADY_EXISTS = "Image identifier already exists";
			public static final String PROBLEM_WITH_REQUEST = "Problem occured while processing the request: ";
		}

		public class Read {

			public static final String IMAGE_IDENTIFIER_MISSING = "Image identifier missing";
			public static final String NO_IMAGE_FOUND = "No image found";
			public static final String GEOMETRY_BIGGER_THAN_ORIGINAL = "Requested geometry bigger than original size";

		}

		public static final String INTERNAL_SERVER_ERROR = "An internal server error has occured";
		public static final String INTERNAL_SERVER_ERROR_SOLUTION = "Please check the server logs";
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