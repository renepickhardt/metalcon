package de.uniko.west.socialsensor.graphity.socialgraph.algorithms;

import static org.junit.Assert.assertFalse;

import java.io.File;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.kernel.AbstractGraphDatabase;

import de.uniko.west.socialsensor.graphity.server.Configs;
import de.uniko.west.socialsensor.graphity.socialgraph.NeoUtils;

@RunWith(Suite.class)
@SuiteClasses({ GraphityTest.class })
/**
 * test all social graph CRUD algorithms 
 * @author Sebastian Schlicht
 *
 */
public class AlgorithmTests {

	/**
	 * test database path
	 */
	private static final String DATABASE_PATH = "target/database/testlocation";

	/**
	 * test database
	 */
	public static AbstractGraphDatabase DATABASE;

	/**
	 * test user identifiers
	 */
	public static long USER_ID_A, USER_ID_B, USER_ID_C, USER_ID_D, USER_ID_E;

	@BeforeClass
	public static void setUp() {
		// delete existing test database
		final File databaseDir = new File(DATABASE_PATH);
		NeoUtils.deleteFile(databaseDir);

		// load the database from the test location
		final Configs config = new Configs();
		config.database_path = DATABASE_PATH;
		DATABASE = NeoUtils.getSocialGraphDatabase(config);

		// fill the database with the test users
		final Transaction transaction = DATABASE.beginTx();

		Node tmp = DATABASE.createNode();
		USER_ID_A = tmp.getId();

		tmp = DATABASE.createNode();
		USER_ID_B = tmp.getId();

		tmp = DATABASE.createNode();
		USER_ID_C = tmp.getId();

		tmp = DATABASE.createNode();
		USER_ID_D = tmp.getId();

		tmp = DATABASE.createNode();
		USER_ID_E = tmp.getId();

		transaction.success();
		transaction.finish();

		// assert the test user nodes to be existing
		assertFalse(USER_ID_A == 0);
		assertFalse(USER_ID_B == 0);
		assertFalse(USER_ID_C == 0);
		assertFalse(USER_ID_D == 0);
		assertFalse(USER_ID_E == 0);
	}

	@AfterClass
	public static void tearDown() {
		// close the test database
		DATABASE.shutdown();
	}

}