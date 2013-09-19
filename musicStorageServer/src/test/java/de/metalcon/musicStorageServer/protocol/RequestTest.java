package de.metalcon.musicStorageServer.protocol;

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

}