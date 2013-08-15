package de.metalcon.server.tomcat.NSSP;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ ReadRequestTest.class })
/**
 * test suite to run all request test cases
 * @author sebschlicht
 *
 */
public class RequestTests {

	/**
	 * valid user identifier
	 */
	public static final String USER_IDENTIFIER = "1";

}