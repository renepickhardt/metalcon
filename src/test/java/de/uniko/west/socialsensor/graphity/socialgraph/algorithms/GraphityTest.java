package de.uniko.west.socialsensor.graphity.socialgraph.algorithms;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

import de.uniko.west.socialsensor.graphity.socialgraph.Properties;
import de.uniko.west.socialsensor.graphity.socialgraph.SocialGraph;
import de.uniko.west.socialsensor.graphity.socialgraph.statusupdates.PlainText;
import de.uniko.west.socialsensor.graphity.socialgraph.statusupdates.StatusUpdate;

/**
 * social graph algorithm Gravity test
 * 
 * @author Sebastian Schlicht
 * 
 */
public class GraphityTest {

	/**
	 * social graph implementation Graphity being tested
	 */
	private SocialGraph gravity;

	/**
	 * test transaction
	 */
	private Transaction transaction;

	/**
	 * test status update
	 */
	private StatusUpdate statusUpdate;

	@Before
	public void setUp() {
		// load the Gravity algorithm
		this.gravity = new Graphity(AlgorithmTests.DATABASE);

		// initialize test transaction
		this.transaction = AlgorithmTests.DATABASE.beginTx();

		// create test status update
		this.statusUpdate = new PlainText("testy");

		// assert the test user nodes to be existing
		assertFalse(AlgorithmTests.USER_ID1 == 0);
		assertFalse(AlgorithmTests.USER_ID2 == 0);
	}

	@After
	public void tearDown() {
		// finish the running test transaction
		this.transaction.finish();
	}

	@Test
	public void testCreateFriendship_NotFoundException() {
		// assert the creation to fail if providing invalid user identifier(s)
		assertFalse(this.gravity.createFriendship(0, -1,
				AlgorithmTests.USER_ID2));
		assertFalse(this.gravity.createFriendship(0, AlgorithmTests.USER_ID1,
				-1));
		assertFalse(this.gravity.createFriendship(0, -1, -1));
	}

	@Test
	public void testCreateFriendship_Regular() {
		// assert the creation to success if providing valid user identifiers
		assertTrue(this.gravity.createFriendship(System.currentTimeMillis(),
				AlgorithmTests.USER_ID1, AlgorithmTests.USER_ID2));

		// check if relationship has been created successfully
		final Node user1 = AlgorithmTests.DATABASE
				.getNodeById(AlgorithmTests.USER_ID1);
		final DynamicRelationshipType egoType = Graphity.getEgoType(user1);
		assertTrue(user1.hasRelationship(egoType, Direction.OUTGOING));

		this.transaction.success();
	}

	@Test
	public void testCreateStatusUpdate_NotFoundException() {
		// assert the creation to fail if providing invalid user identifier
		assertFalse(this.gravity.createStatusUpdate(0, -1, null));
	}

	@Test
	public void testCreateStatusUpdate_Regular() {
		// TODO: let createStatusUpdate return node identifier
		final long timestamp = System.currentTimeMillis();
		assertTrue(this.gravity.createStatusUpdate(timestamp,
				AlgorithmTests.USER_ID1, this.statusUpdate));

		// TODO: replace node identifier with method result
		final Node statusUpdateNode = AlgorithmTests.DATABASE.getNodeById(3);
		assertEquals((long) statusUpdateNode.getProperty(Properties.Timestamp),
				timestamp);
		assertEquals(statusUpdateNode.getProperty(Properties.ContentType),
				this.statusUpdate.getType());
		final String activityJSON = (String) statusUpdateNode
				.getProperty(Properties.Content);
		System.out.println(activityJSON);

		this.transaction.success();
	}
}