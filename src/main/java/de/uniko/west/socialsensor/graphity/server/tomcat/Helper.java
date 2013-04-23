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

	private static String getAttribute(final HttpServletRequest request,
			final String paramName) {
		final String param = request.getParameter(paramName);
		if (param != null) {
			return param;
		}

		throw new IllegalArgumentException("parameter \"" + paramName
				+ "\" is missing!");
	}

	public static int getInt(final HttpServletRequest request,
			final String paramName) {
		return Integer.valueOf(getAttribute(request, paramName));
	}

	public static long getLong(final HttpServletRequest request,
			final String paramName) {
		return Long.valueOf(getAttribute(request, paramName));
	}

	public static String getString(final HttpServletRequest request,
			final String paramName) {
		return getAttribute(request, paramName);
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