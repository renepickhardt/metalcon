package de.metalcon.server.tomcat.NSSP;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import java.lang.reflect.Field;

import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONObject;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import de.metalcon.socialgraph.algorithms.AlgorithmTests;

/**
 * basic NSSP request test
 * 
 * @author sebschlicht
 * 
 */
public abstract class RequestTest {

	/**
	 * NSSP prefix for missing parameters
	 */
	protected static final String MISSING_PARAM_BEFORE = "request incomplete: parameter \"";

	/**
	 * NSSP postfix for missing parameters
	 */
	protected static final String MISSING_PARAM_AFTER = "\" is missing";

	/**
	 * valid user identifier
	 */
	protected static String VALID_USER_IDENTIFIER;

	/**
	 * invalid user identifier
	 */
	protected static String INVALID_USER_IDENTIFIER;

	/**
	 * Tomcat request servlet
	 */
	protected HttpServletRequest request;

	/**
	 * JSON response from server
	 */
	protected JSONObject jsonResponse;

	@BeforeClass
	public static void beforeClass() {
		if (AlgorithmTests.wasUsed()) {
			// reset database
			AlgorithmTests.tearDown();
		}
		AlgorithmTests.setUp();

		// assert the database to be created successfully
		assertNotNull(AlgorithmTests.DATABASE);

		VALID_USER_IDENTIFIER = AlgorithmTests.USER_IDS[0];
		INVALID_USER_IDENTIFIER = "this_does_not_exists";
	}

	@Before
	public void setUp() {
		this.request = mock(HttpServletRequest.class);
	}

	@AfterClass
	public static void afterClass() {
		AlgorithmTests.tearDown();
	}

	/**
	 * extract the JSON object from the response, failing the test if this is
	 * not possible
	 * 
	 * @param response
	 *            NSSP response
	 * @return JSON object in the response passed
	 */
	protected static JSONObject extractJson(final Response response) {
		try {
			final Field field = Response.class.getDeclaredField("json");
			field.setAccessible(true);
			return (JSONObject) field.get(response);
		} catch (final Exception e) {
			fail("failed to extract the JSON object from class Response");
			return null;
		}
	}

}