package de.uniko.west.socialsensor.graphity.socialgraph;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.kernel.AbstractGraphDatabase;

import de.uniko.west.socialsensor.graphity.server.Configs;
import de.uniko.west.socialsensor.graphity.socialgraph.algorithms.Graphity;

public class SocialGraphTest {

	/**
	 * test database path
	 */
	private static final String databasePath = "target/database/testlocation";

	/**
	 * test database
	 */
	private AbstractGraphDatabase graphDatabase;

	/**
	 * social graph implementation Graphity being tested
	 */
	private SocialGraph gravity;

	/**
	 * test user
	 */
	private long user1, user2;

	/**
	 * test transaction
	 */
	private Transaction transaction;

	@Before
	public void setUp() {
		// TODO: delete existing test database

		// load the database from the test location
		final Configs config = new Configs();
		config.database_path = databasePath;
		this.graphDatabase = NeoUtils.getSocialGraphDatabase(config);
		this.gravity = new Graphity(this.graphDatabase);

		// fill the database with the test scenario
		this.transaction = this.graphDatabase.beginTx();

		Node tmp = this.graphDatabase.createNode();
		this.user1 = tmp.getId();

		tmp = this.graphDatabase.createNode();
		this.user2 = tmp.getId();

		this.transaction.success();
		this.transaction.finish();

		// initialize test transaction
		this.transaction = this.graphDatabase.beginTx();
	}

	@After
	public void shutdown() {
		// finish the running test transaction and shut down neo4j
		this.transaction.finish();
		this.graphDatabase.shutdown();
	}

	@Test
	public void testCreateFriendship_NotFoundException() {
		// assert the creation to fail if providing invalid user identifiers
		assertFalse(this.gravity.createFriendship(0, this.user1 + 1,
				this.user2 + 1));
	}

	@Test
	public void testCreateFriendship_Regular() {
		// assert the creation to success if providing valid user identifiers
		assertTrue(this.gravity.createFriendship(123, this.user1, this.user2));
		this.transaction.success();
	}

}