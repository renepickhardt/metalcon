package de.uniko.west.socialsensor.graphity.server.tomcat;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

	public static String getString(final HttpServletRequest request,
			final String attributeName) {
		return (String) getAttribute(request, attributeName);
	}

	public static void sendErrorMessage(final HttpServletResponse response,
			final int errorCode, final String message) {
		try {
			response.sendError(errorCode, message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}