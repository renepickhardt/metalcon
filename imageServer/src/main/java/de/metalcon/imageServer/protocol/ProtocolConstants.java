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

	public class StatusMessage {
		public class Create {

			public static final String IMAGE_IDENTIFIER_MISSING = "No Image Identifier given";
			public static final String IMAGESTREAM_MISSING = "No ImageStream found";
			public static final String IMAGE_METADATA_MISSING = "No meta data given";
			public static final String AUTOROTATE_FLAG_MISSING = "Autorotate flag not found";
			public static final String AUTOROTATE_FLAG_MALFORMED = "Autorotate flag malformed";
		}

		public class Read {

			public static final String IMAGE_IDENTIFIER_MISSING = "Image identifier misssing";

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

		public class Delete {

			public static final String IMAGE_IDENTIFIER = "imageIdentifier";
		}
	}

}