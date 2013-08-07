package de.uniko.west.socialsensor.graphity.server.tomcat;

import javax.servlet.http.HttpServletRequest;

/**
 * helper class for Tomcat requests/responses
 * 
 * @author Sebastian Schlicht
 * 
 */
public class Helper {

	/**
	 * read a form parameter from the Tomcat request
	 * 
	 * @param request
	 *            Tomcat request
	 * @param name
	 *            parameter name
	 * @return parameter value
	 * @throws IllegalArgumentException
	 *             if the parameter is missing
	 */
	private static String getFormParameter(final HttpServletRequest request,
			final String name) {
		final String attribute = request.getParameter(name);
		if (attribute != null) {
			return attribute;
		}

		throw new IllegalArgumentException("parameter \"" + name
				+ "\" is missing!");
	}

	/**
	 * read a boolean parameter from the form of the Tomcat request
	 * 
	 * @param request
	 *            Tomcat request
	 * @param name
	 *            boolean parameter name
	 * @return boolean parameter value
	 * @throws IllegalArgumentException
	 *             if the parameter is missing or no integer that could be
	 *             parsed
	 */
	public static boolean getBool(final HttpServletRequest request,
			final String name) {
		int value = getInt(request, name);
		return (value != 0);
	}

	/**
	 * read an integer parameter from the form of the Tomcat request
	 * 
	 * @param request
	 *            Tomcat request
	 * @param name
	 *            integer parameter name
	 * @return integer parameter value
	 * @throws IllegalArgumentException
	 *             if the parameter is missing or no integer
	 */
	public static int getInt(final HttpServletRequest request, final String name) {
		try {
			return Integer.valueOf(getFormParameter(request, name));
		} catch (final NumberFormatException e) {
			throw new IllegalArgumentException("parameter \"" + name
					+ "\" is not a valid integer!");
		}
	}

	/**
	 * read a long parameter from the form of the Tomcat request
	 * 
	 * @param request
	 *            Tomcat request
	 * @param name
	 *            long parameter name
	 * @return long parameter value
	 * @throws IllegalArgumentException
	 *             if the parameter is missing or no long
	 */
	public static long getLong(final HttpServletRequest request,
			final String name) {
		try {
			return Long.valueOf(getFormParameter(request, name));
		} catch (final NumberFormatException e) {
			throw new IllegalArgumentException("parameter \"" + name
					+ "\" is not a valid long!");
		}
	}

	/**
	 * read a String parameter from the form of the Tomcat request
	 * 
	 * @param request
	 *            Tomcat request
	 * @param name
	 *            String parameter value
	 * @return String parameter value
	 * @throws IllegalArgumentException
	 *             if the parameter is missing
	 */
	public static String getString(final HttpServletRequest request,
			final String name) {
		return getFormParameter(request, name);
	}

}