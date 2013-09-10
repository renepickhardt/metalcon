package de.metalcon.haveInCommons;

import java.net.UnknownHostException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {
	/**
	 * Create the test case
	 * 
	 * @param testName
	 *            name of the test case
	 */
	public AppTest(String testName) {
		super(testName);
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite(AppTest.class);
	}

	/**
	 * Rigourous Test :-)
	 */
	public void testApp() {
		assertTrue(true);
	}

	/**
	 * 
	 */
	public void queryMongoDB() {
		// init
		MongoDBStore mongoDB = null;
		try {
			mongoDB = new MongoDBStore("");
		} catch (UnknownHostException e) {
			e.printStackTrace();
			fail("Unknown Host");
		}

		mongoDB.insert(from, to, neighbours, collection);

	}
}
