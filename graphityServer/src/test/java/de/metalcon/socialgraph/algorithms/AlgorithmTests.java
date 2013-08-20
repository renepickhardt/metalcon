package de.metalcon.socialgraph.algorithms;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.kernel.AbstractGraphDatabase;
import org.xml.sax.SAXException;

import de.metalcon.server.Configs;
import de.metalcon.server.statusupdates.StatusUpdateManager;
import de.metalcon.socialgraph.NeoUtils;

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
	 * test database path
	 */
	private static final String DATABASE_PATH = "target/database/testlocation";

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
			}
			transaction.success();
		} finally {
			transaction.finish();
		}
	}

	@BeforeClass
	public static void setUp() {
		// delete existing test database
		final File databaseDir = new File(DATABASE_PATH);
		NeoUtils.deleteFile(databaseDir);

		// load the database from the test location
		final Configs config = new Configs("config.txt");
		config.database_path = DATABASE_PATH;
		DATABASE = NeoUtils.getSocialGraphDatabase(config);

		// fill the database with the test users
		createUsers(DATABASE);

		// assert the test user nodes to be existing
		for (int i = 0; i < USER_IDS.length; i++) {
			assertEquals(USERS[i], NeoUtils.getUserByIdentifier(USER_IDS[i]));
		}

		// load status update templates
		try {
			StatusUpdateManager.loadStatusUpdateTemplates(config, DATABASE);
		} catch (ParserConfigurationException | SAXException | IOException e) {
			System.err.println("failed to load status update templates!");
			e.printStackTrace();
			fail();
		}

		USED = false;
	}

	@AfterClass
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