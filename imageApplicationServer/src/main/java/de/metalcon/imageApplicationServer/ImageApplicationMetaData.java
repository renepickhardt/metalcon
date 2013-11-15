package de.metalcon.imageApplicationServer;

public class ImageApplicationMetaData {

	public static final String IMAGES = "images";

	public class Image {

		/**
		 * image identifier
		 */
		public static final String IDENTIFIER = "identifier";

		/**
		 * image title (optional)
		 */
		public static final String TITLE = "title";

		/**
		 * image description (optional)
		 */
		public static final String DESCRIPTION = "description";

		/**
		 * image width after the scaling requested (optional)
		 */
		public static final String WIDTH = "width";

		/**
		 * information about the user uploaded the image
		 */
		public static final String UPLOADER = "uploader";

		public class Uploader {

			/**
			 * real name
			 */
			public static final String NAME = "name";

			/**
			 * metalcon user identifier (optional)
			 */
			public static final String IDENTIFIER = "identifier";

			// TODO: user URL in IAS?
			// TODO: user image URL in IAS? (if constant URL)

		}

		/**
		 * image license
		 */
		public static final String LICENSE = "license";

		/**
		 * original image size
		 */
		public static final String SIZE = "size";

		public class Size {

			/**
			 * original image width
			 */
			public static final String WIDTH = "width";

			/**
			 * original image height
			 */
			public static final String HEIGHT = "height";

		}

		/**
		 * geographical data of the creation (optional)
		 */
		public static final String GEODATA = "geodata";

		public class Geodata {

			public static final String LONGITUDE = "longitude";
			public static final String LATITUDE = "latitude";

		}

		/**
		 * author of the image (serverside if equal to uploader)
		 */
		public static final String AUTHOR = "author";

		/**
		 * timestamp of the upload (serverside)
		 */
		public static final String TIMESTAMP = "timestamp";

		/**
		 * file size (serverside)
		 */
		public static final String FILESIZE = "filesize";

		// TODO: basis image URL in IAS? (may include source URL)

	}

}