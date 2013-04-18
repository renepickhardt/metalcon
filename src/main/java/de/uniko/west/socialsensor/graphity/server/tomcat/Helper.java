package de.uniko.west.socialsensor.graphity.server.tomcat;

import javax.servlet.http.HttpServletRequest;

/**
 * helper class for Tomcat requests/responses
 * 
 * @author Sebastian Schlicht
 * 
 */
public class Helper {

	private static Object getAttribute(final HttpServletRequest request,
			final String attributeName) {
		final Object object = request.getAttribute(attributeName);
		if (object != null) {
			return object;
		}

		throw new IllegalArgumentException("attribute \"" + attributeName
				+ "\" is missing!");
	}

	public static boolean getBoolean(final HttpServletRequest request,
			final String attributeName) {
		return (Boolean) getAttribute(request, attributeName);
	}

	public static int getInt(final HttpServletRequest request,
			final String attributeName) {
		return (Integer) getAttribute(request, attributeName);
	}

	public static long getLong(final HttpServletRequest request,
			final String attributeName) {
		return (Long) getAttribute(request, attributeName);
	}

	public static void sendErrorMessage(final int errorCode,
			final String message) {
		// TODO
	}

}