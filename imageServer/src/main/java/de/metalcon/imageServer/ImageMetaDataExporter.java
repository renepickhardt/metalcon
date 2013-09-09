package de.metalcon.imageServer;

import magick.MagickException;
import magick.MagickImage;

import org.json.simple.JSONObject;

/**
 * image meta data exporter
 * 
 * @author sebschlicht
 * 
 */
public class ImageMetaDataExporter {

	/**
	 * prefix for EXIF data
	 */
	private static final String EXIF = "exif";

	/**
	 * EXIF image author
	 */
	private static final String EXIF_AUTHOR = "Artist";

	/**
	 * EXIF date and time of the image creation
	 */
	private static final String EXIF_DATE_TIME = "DateTime";

	/**
	 * EXIF orientation of the image <b>usable for auto-rotation</b>
	 */
	private static final String EXIF_ORIENTATION = "Orientation";

	/**
	 * EXIF GPS latitude reference (N: North, S:South)
	 */
	private static final String EXIF_GPS_LATITUDE_REF = "GPSLatitudeRef";

	/**
	 * EXIF GPS latitude value
	 */
	private static final String EXIF_GPS_LATITUDE = "GPSLatitude";

	/**
	 * EXIF GPS longitude reference (E: East, W:West)
	 */
	private static final String EXIF_GPS_LONGITUDE_REF = "GPSLongitudeRef";

	/**
	 * EXIF GPS longitude value
	 */
	private static final String EXIF_GPS_LONGITUDE = "GPSLongitude";

	/**
	 * EXIF GPS altitude reference (0: above Sea Level, 1: below Sea Level)
	 */
	private static final String EXIF_GPS_ALTITUDE_REF = "GPSAltitudeRef";

	/**
	 * EXIF GPS altitude value
	 */
	private static final String EXIF_GPS_ALTITUDE = "GPSAltitude";

	/**
	 * EXIF GPS precision TODO: how and what?
	 */
	private static final String EXIF_GPS_PRECISION = "GPSDOP";

	/**
	 * export the EXIF data of an image
	 * 
	 * @param image
	 *            magick image containing the EXIF data
	 * @return JSON object containing the EXIF data exported
	 */
	@SuppressWarnings("unchecked")
	public static JSONObject exportExifData(final MagickImage image) {
		final JSONObject exifData = new JSONObject();

		final String author = exportExifAttribute(EXIF_AUTHOR, image);
		if (author != null) {
			exifData.put(EXIF_AUTHOR, author);
		}

		final String dateTime = exportExifAttribute(EXIF_DATE_TIME, image);
		if (dateTime != null) {
			exifData.put(EXIF_DATE_TIME, dateTime);
		}

		final String orientation = exportExifAttribute(EXIF_ORIENTATION, image);
		if (orientation != null) {
			exifData.put(EXIF_ORIENTATION, orientation);
		}

		final String latituteRef = exportExifAttribute(EXIF_GPS_LATITUDE_REF,
				image);
		if (latituteRef != null) {
			exifData.put(EXIF_GPS_LATITUDE_REF, latituteRef);
		}

		final String latitute = exportExifAttribute(EXIF_GPS_LATITUDE, image);
		if (latitute != null) {
			exifData.put(EXIF_GPS_LATITUDE, latitute);
		}

		final String longitudeRef = exportExifAttribute(EXIF_GPS_LONGITUDE_REF,
				image);
		if (longitudeRef != null) {
			exifData.put(EXIF_GPS_LONGITUDE_REF, longitudeRef);
		}

		final String longitude = exportExifAttribute(EXIF_GPS_LONGITUDE, image);
		if (longitude != null) {
			exifData.put(EXIF_GPS_LONGITUDE, longitude);
		}

		final String altitudeRef = exportExifAttribute(EXIF_GPS_ALTITUDE_REF,
				image);
		if (altitudeRef != null) {
			exifData.put(EXIF_GPS_ALTITUDE_REF, altitudeRef);
		}

		final String altitude = exportExifAttribute(EXIF_GPS_ALTITUDE, image);
		if (altitude != null) {
			exifData.put(EXIF_GPS_ALTITUDE, altitude);
		}

		final String precision = exportExifAttribute(EXIF_GPS_PRECISION, image);
		if (precision != null) {
			exifData.put(EXIF_GPS_PRECISION, precision);
		}

		return exifData;
	}

	public static JSONObject mergeExifData(final JSONObject higher,
			final JSONObject lower) {
		// TODO
		return null;
	}

	/**
	 * export the value of an single EXIF attribute
	 * 
	 * @param attribute
	 *            EXIF attribute name
	 * @param image
	 *            magick image containing the EXIF attribute
	 * @return value of the EXIF attribute named
	 */
	private static String exportExifAttribute(final String attribute,
			final MagickImage image) {
		try {
			return image.getImageAttribute(EXIF + ":" + attribute);
		} catch (final MagickException e) {
			// failed to export the attribute
			return null;
		}
	}

}