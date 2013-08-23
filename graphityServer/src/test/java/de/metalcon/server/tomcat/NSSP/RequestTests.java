package de.metalcon.server.tomcat.NSSP;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.metalcon.server.tomcat.NSSP.create.CreateFollowRequestTest;
import de.metalcon.server.tomcat.NSSP.create.CreateStatusUpdateRequestTest;
import de.metalcon.server.tomcat.NSSP.create.CreateUserRequestTest;
import de.metalcon.server.tomcat.NSSP.delete.DeleteFollowRequestTest;
import de.metalcon.server.tomcat.NSSP.delete.DeleteStatusUpdateRequestTest;
import de.metalcon.server.tomcat.NSSP.delete.DeleteUserRequestTest;
import de.metalcon.server.tomcat.NSSP.read.ReadRequestTest;

@RunWith(Suite.class)
@SuiteClasses({ ReadRequestTest.class, DeleteUserRequestTest.class,
		DeleteFollowRequestTest.class, DeleteStatusUpdateRequestTest.class,
		CreateUserRequestTest.class, CreateFollowRequestTest.class,
		CreateStatusUpdateRequestTest.class })
/**
 * test suite to run all request test cases
 * @author sebschlicht
 *
 */
public class RequestTests {

}