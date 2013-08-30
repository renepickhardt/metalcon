package de.metalcon.socialgraph.algorithms;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.kernel.AbstractGraphDatabase;
import org.xml.sax.SAXException;

import de.metalcon.server.statusupdates.StatusUpdateManager;
import de.metalcon.socialgraph.NeoUtils;
import de.metalcon.socialgraph.Properties;
import de.metalcon.socialgraph.TestConfiguration;

@RunWith(Suite.class)
@SuiteClasses({ WriteOptimizedGraphityTest.class,
		ReadOptimizedGraphityTest.class })
/**
 * test all social graph CRUD algorithms
 * 
 * @author Sebastian Schlicht
 *
 */
public class AlgorithmTests {

	/**
	 * database used flag
	 */
	private static boolean USED = false;

	/**
	 * test database
	 */
	public static AbstractGraphDatabase DATABASE;

	/**
	 * test user identifiers
	 */
	public static String[] USER_IDS;

	/**
	 * test user nodes
	 */
	public static Node[] USERS;

	/**
	 * create the test users in the database
	 * 
	 * @param graphDatabase
	 *            social graph database to operate on
	 */
	public static void createUsers(final AbstractGraphDatabase graphDatabase) {
		USER_IDS = new String[5];
		USERS = new Node[USER_IDS.length];
		final Transaction transaction = graphDatabase.beginTx();

		try {
			for (int i = 0; i < USER_IDS.length; i++) {
				USER_IDS[i] = String.valueOf(i);
				USERS[i] = NeoUtils.createUserNode(USER_IDS[i]);
				USERS[i].setProperty(Properties.User.DISPLAY_NAME, "Testy");
				USERS[i].setProperty(Properties.User.PROFILE_PICTURE_PATH,
						"google.de/somepic");
			}
			transaction.success();
		} finally {
			transaction.finish();
		}
	}

	public static void setUp() {
		final TestConfiguration config = new TestConfiguration();

		// delete existing test database
		final File databaseDir = new File(config.getDatabasePath());
		NeoUtils.deleteFile(databaseDir);

		// load the database from the test location
		DATABASE = NeoUtils.getSocialGraphDatabase(config);

		// fill the database with the test users
		createUsers(DATABASE);

		// assert the test user nodes to be existing
		for (int i = 0; i < USER_IDS.length; i++) {
			assertEquals(USERS[i], NeoUtils.getUserByIdentifier(USER_IDS[i]));
		}

		// load status update templates
		try {
			StatusUpdateManager.loadStatusUpdateTemplates("target/", config,
					DATABASE);
		} catch (ParserConfigurationException | SAXException | IOException e) {
			System.err.println("failed to load status update templates!");
			e.printStackTrace();
			fail();
		}

		USED = false;
	}

	public static void tearDown() {
		// close the test database
		DATABASE.shutdown();
	}

	/**
	 * check if the database has been used after initialization
	 * 
	 * @return database usage flag
	 */
	public static boolean wasUsed() {
		if (USED) {
			return true;
		} else {
			USED = true;
			return false;
		}
	}

}