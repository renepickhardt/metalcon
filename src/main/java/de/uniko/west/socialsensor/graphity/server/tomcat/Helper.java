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
	 * @throws NumberFormatException
	 *             if the parameter is no integer that could be parsed
	 * @throws IllegalArgumentException
	 *             if the parameter is missing
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
	 * @throws NumberFormatException
	 *             if the parameter is no integer
	 * @throws IllegalArgumentException
	 *             if the parameter is missing
	 */
	public static int getInt(final HttpServletRequest request, final String name) {
		return Integer.valueOf(getFormParameter(request, name));
	}

	/**
	 * read a long parameter from the form of the Tomcat request
	 * 
	 * @param request
	 *            Tomcat request
	 * @param name
	 *            long parameter name
	 * @return long parameter value
	 * @throws NumberFormatException
	 *             if the parameter is no long
	 * @throws IllegalArgumentException
	 *             if the parameter is missing
	 */
	public static long getLong(final HttpServletRequest request,
			final String name) {
		return Long.valueOf(getFormParameter(request, name));
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