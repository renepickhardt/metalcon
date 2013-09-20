package de.metalcon.musicStorageServer.protocol;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;

import org.json.simple.JSONObject;

/**
 * basic request test class providing essential functionality for protocol tests
 * 
 * @author sebschlicht
 * 
 */
public class RequestTest {

	/**
	 * MSSP prefix for missing parameters
	 */
	private static final String MISSING_PARAM_BEFORE = "request incomplete: parameter \"";

	/**
	 * MSSP postfix for missing parameters
	 */
	private static final String MISSING_PARAM_AFTER = "\" is missing";

	/**
	 * JSON response from server
	 */
	protected JSONObject jsonResponse;

	/**
	 * extract the JSON object from the response, failing the test if this is
	 * not possible
	 * 
	 * @param response
	 *            MSSP response
	 */
	protected void extractJson(final Response response) {
		try {
			final Field field = Response.class.getDeclaredField("json");
			field.setAccessible(true);
			this.jsonResponse = (JSONObject) field.get(response);
		} catch (final Exception e) {
			fail("failed to extract the JSON object from class Response");
		}
	}

	/**
	 * check if the JSON response contains a certain status message<br>
	 * <b>fails</b> the test if the status message is not contained
	 * 
	 * @param statusMessage
	 *            status message the response should contain
	 */
	protected void checkForStatusMessage(final String statusMessage) {
		assertEquals(statusMessage,
				this.jsonResponse.get(ProtocolConstants.STATUS_MESSAGE));
	}

	/**
	 * check if the JSON response contains a certain missing parameter status
	 * message<br>
	 * <b>fails</b> the test if the status message is not contained
	 * 
	 * @param parameter
	 *            parameter the missing parameter status message is expected for
	 */
	protected void checkForMissingParameter(final String parameter) {
		this.checkForStatusMessage(MISSING_PARAM_BEFORE + parameter
				+ MISSING_PARAM_AFTER);
	}

}