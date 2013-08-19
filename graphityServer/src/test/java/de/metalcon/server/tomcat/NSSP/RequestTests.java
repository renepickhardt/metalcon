package de.metalcon.server.tomcat.NSSP;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.metalcon.server.tomcat.NSSP.delete.DeleteFollowRequestTest;
import de.metalcon.server.tomcat.NSSP.delete.DeleteStatusUpdateRequestTest;
import de.metalcon.server.tomcat.NSSP.delete.DeleteUserRequestTest;
import de.metalcon.server.tomcat.NSSP.read.ReadRequestTest;

@RunWith(Suite.class)
@SuiteClasses({ ReadRequestTest.class, DeleteUserRequestTest.class,
		DeleteFollowRequestTest.class, DeleteStatusUpdateRequestTest.class })
/**
 * test suite to run all request test cases
 * @author sebschlicht
 *
 */
public class RequestTests {

	/**
	 * valid user identifier
	 */
	public static final String USER_IDENTIFIER = "0";

}