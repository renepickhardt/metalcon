package de.uniko.west.socialsensor.graphity.socialgraph.algorithms;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;

import de.uniko.west.socialsensor.graphity.socialgraph.NeoUtils;
import de.uniko.west.socialsensor.graphity.socialgraph.Properties;
import de.uniko.west.socialsensor.graphity.socialgraph.SocialGraph;
import de.uniko.west.socialsensor.graphity.socialgraph.SocialGraphRelationshipType;
import de.uniko.west.socialsensor.graphity.socialgraph.operations.ReadStatusUpdates;
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
	 * test user identifiers
	 */
	private long[] userIds;

	/**
	 * test user nodes
	 */
	private Node[] users;

	/**
	 * test user ego types
	 */
	private DynamicRelationshipType[] egoTypes;

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
		// assert the test to get started by the algorithm tests
		assertNotNull(AlgorithmTests.DATABASE);

		// load all user nodes
		this.userIds = new long[] { AlgorithmTests.USER_ID_A,
				AlgorithmTests.USER_ID_B, AlgorithmTests.USER_ID_C,
				AlgorithmTests.USER_ID_D, AlgorithmTests.USER_ID_E };
		this.users = new Node[this.userIds.length];
		this.egoTypes = new DynamicRelationshipType[this.userIds.length];
		for (int i = 0; i < this.userIds.length; i++) {
			this.users[i] = AlgorithmTests.DATABASE
					.getNodeById(this.userIds[i]);
			assertNotNull(this.users[i]);
			this.egoTypes[i] = Graphity.getEgoType(this.users[i]);
		}

		// load the Gravity algorithm
		this.gravity = new Graphity(AlgorithmTests.DATABASE);

		// initialize test transaction
		this.transaction = AlgorithmTests.DATABASE.beginTx();
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
				AlgorithmTests.USER_ID_B));
		assertFalse(this.gravity.createFriendship(0, AlgorithmTests.USER_ID_A,
				-1));
		assertFalse(this.gravity.createFriendship(0, -1, -1));
	}

	private static int getNumberOfRelationships(
			final Iterable<Relationship> relationships) {
		int result = 0;
		for (Relationship relationship : relationships) {
			result += 1;
		}
		return result;
	}

	@Test
	/**
	 * assert the creation to success if providing valid user identifiers<br>creating the test scenario
	 */
	public void testCreateFriendship_Regular() {
		// A follows: B, C, D, E
		assertTrue(this.gravity.createFriendship(System.currentTimeMillis(),
				AlgorithmTests.USER_ID_A, AlgorithmTests.USER_ID_B));
		assertTrue(this.gravity.createFriendship(System.currentTimeMillis(),
				AlgorithmTests.USER_ID_A, AlgorithmTests.USER_ID_C));
		assertTrue(this.gravity.createFriendship(System.currentTimeMillis(),
				AlgorithmTests.USER_ID_A, AlgorithmTests.USER_ID_D));
		assertTrue(this.gravity.createFriendship(System.currentTimeMillis(),
				AlgorithmTests.USER_ID_A, AlgorithmTests.USER_ID_E));

		// B follows: A, D
		assertTrue(this.gravity.createFriendship(System.currentTimeMillis(),
				AlgorithmTests.USER_ID_B, AlgorithmTests.USER_ID_A));
		assertTrue(this.gravity.createFriendship(System.currentTimeMillis(),
				AlgorithmTests.USER_ID_B, AlgorithmTests.USER_ID_D));

		// C follows: A, E
		assertTrue(this.gravity.createFriendship(System.currentTimeMillis(),
				AlgorithmTests.USER_ID_C, AlgorithmTests.USER_ID_A));
		assertTrue(this.gravity.createFriendship(System.currentTimeMillis(),
				AlgorithmTests.USER_ID_C, AlgorithmTests.USER_ID_E));

		// D follows: C
		assertTrue(this.gravity.createFriendship(System.currentTimeMillis(),
				AlgorithmTests.USER_ID_D, AlgorithmTests.USER_ID_C));

		// E follows none

		// check relationships for A: 1 ego out, 4 out, 2 in
		assertEquals(getNumberOfRelationships(this.users[0].getRelationships(
				this.egoTypes[0], Direction.OUTGOING)), 1);
		assertEquals(getNumberOfRelationships(this.users[0].getRelationships(
				SocialGraphRelationshipType.FOLLOW, Direction.OUTGOING)), 4);
		assertEquals(getNumberOfRelationships(this.users[0].getRelationships(
				SocialGraphRelationshipType.FOLLOW, Direction.INCOMING)), 2);

		// check relationships for B: 1 ego out, 2 out, 1 in
		assertEquals(getNumberOfRelationships(this.users[1].getRelationships(
				this.egoTypes[1], Direction.OUTGOING)), 1);
		assertEquals(getNumberOfRelationships(this.users[1].getRelationships(
				SocialGraphRelationshipType.FOLLOW, Direction.OUTGOING)), 2);
		assertEquals(getNumberOfRelationships(this.users[1].getRelationships(
				SocialGraphRelationshipType.FOLLOW, Direction.INCOMING)), 1);

		// check relationships for C: 1 ego out, 2 out, 2 in
		assertEquals(getNumberOfRelationships(this.users[2].getRelationships(
				this.egoTypes[2], Direction.OUTGOING)), 1);
		assertEquals(getNumberOfRelationships(this.users[2].getRelationships(
				SocialGraphRelationshipType.FOLLOW, Direction.OUTGOING)), 2);
		assertEquals(getNumberOfRelationships(this.users[2].getRelationships(
				SocialGraphRelationshipType.FOLLOW, Direction.INCOMING)), 2);

		// check relationships for D: 1 ego out, 1 out, 2 in
		assertEquals(getNumberOfRelationships(this.users[3].getRelationships(
				this.egoTypes[3], Direction.OUTGOING)), 1);
		assertEquals(getNumberOfRelationships(this.users[3].getRelationships(
				SocialGraphRelationshipType.FOLLOW, Direction.OUTGOING)), 1);
		assertEquals(getNumberOfRelationships(this.users[3].getRelationships(
				SocialGraphRelationshipType.FOLLOW, Direction.INCOMING)), 2);

		// check relationships for E: 0 ego out, 0 out, 2 in
		assertEquals(getNumberOfRelationships(this.users[4].getRelationships(
				this.egoTypes[4], Direction.OUTGOING)), 0);
		assertEquals(getNumberOfRelationships(this.users[4].getRelationships(
				SocialGraphRelationshipType.FOLLOW, Direction.OUTGOING)), 0);
		assertEquals(getNumberOfRelationships(this.users[4].getRelationships(
				SocialGraphRelationshipType.FOLLOW, Direction.INCOMING)), 2);

		this.transaction.success();
	}

	@Test
	public void testCreateStatusUpdate_NotFoundException() {
		// assert the creation to fail if providing invalid user identifier
		assertEquals(this.gravity.createStatusUpdate(
				System.currentTimeMillis(), -1, this.statusUpdate), 0);
	}

	private static int getLength(final Node node,
			final RelationshipType relationshipType) {
		int length = 0;
		Node tmp = node;
		while ((tmp = NeoUtils.getNextSingleNode(tmp, relationshipType)) != null) {
			length += 1;
		}
		return length;
	}

	@Test
	public void testCreateStatusUpdate_Regular() {
		// prepare for status update creation
		final List<StatusUpdateCreationItem> creates = new LinkedList<StatusUpdateCreationItem>();
		creates.add(new StatusUpdateCreationItem(this.userIds[1], 4)); // B: 4
		creates.add(new StatusUpdateCreationItem(this.userIds[3], 5)); // D: 5
		creates.add(new StatusUpdateCreationItem(this.userIds[2], 6)); // C: 6
		creates.add(new StatusUpdateCreationItem(this.userIds[2], 7)); // C: 7
		creates.add(new StatusUpdateCreationItem(this.userIds[3], 8)); // D: 8
		creates.add(new StatusUpdateCreationItem(this.userIds[0], 9)); // A: 9
		creates.add(new StatusUpdateCreationItem(this.userIds[1], 10));// B: 10
		creates.add(new StatusUpdateCreationItem(this.userIds[3], 11));// D: 11
		creates.add(new StatusUpdateCreationItem(this.userIds[1], 12));// B: 12
		creates.add(new StatusUpdateCreationItem(this.userIds[2], 13));// C: 13
		creates.add(new StatusUpdateCreationItem(this.userIds[0], 14));// A: 14
		creates.add(new StatusUpdateCreationItem(this.userIds[0], 17));// A: 17

		long timestamp;
		long nodeId;
		Node statusUpdateNode;
		for (StatusUpdateCreationItem creationItem : creates) {
			// assume a success of the creation
			timestamp = System.currentTimeMillis();
			nodeId = this.gravity.createStatusUpdate(timestamp,
					creationItem.getUserId(), creationItem.getStatusUpdate());
			assertTrue(nodeId != 0);

			// check if status update node exists and contains correct data
			statusUpdateNode = AlgorithmTests.DATABASE.getNodeById(nodeId);
			assertNotNull(statusUpdateNode);

			assertEquals(statusUpdateNode.getProperty(Properties.Timestamp),
					timestamp);
			assertEquals(statusUpdateNode.getProperty(Properties.ContentType),
					creationItem.getStatusUpdate().getType());
			assertEquals(statusUpdateNode.getProperty(Properties.Content),
					creationItem.getStatusUpdate().toJSONString());
		}

		// sort creation items by user identifiers
		final Map<Long, LinkedList<StatusUpdateCreationItem>> userSorted = new HashMap<Long, LinkedList<StatusUpdateCreationItem>>();
		for (long userId : this.userIds) {
			userSorted.put(userId, new LinkedList<StatusUpdateCreationItem>());
		}
		for (StatusUpdateCreationItem creationItem : creates) {
			userSorted.get(creationItem.getUserId()).add(creationItem);
		}

		// compare status update node structure with the creation set
		int userIndex = 0;
		LinkedList<StatusUpdateCreationItem> userItems;
		StatusUpdateCreationItem currentItem;
		for (long userId : this.userIds) {
			userItems = userSorted.get(userId);
			statusUpdateNode = this.users[userIndex];

			// loop through existing status update nodes
			while ((statusUpdateNode = NeoUtils.getNextSingleNode(
					statusUpdateNode, SocialGraphRelationshipType.UPDATE)) != null) {
				// assume there is a creation item corresponding to the node
				currentItem = userItems.removeLast();
				assertNotNull(currentItem);

				assertEquals(
						statusUpdateNode.getProperty(Properties.Timestamp),
						currentItem.getStatusUpdate().getTimestamp());
			}

			// assume that the list is empty now, too
			assertTrue(userItems.isEmpty());

			userIndex += 1;
		}

		this.transaction.success();
	}

	@Test
	public void testReadStatusUpdates_NotFoundException() {
		// assert failure if providing invalid poster identifier
		assertNull(this.gravity.readStatusUpdates(-1, AlgorithmTests.USER_ID_A,
				15, true));
	}

	@Test
	public void testReadStatusUpdates_Regular() {
		// assert success even if trying to gain more status updates than
		// available
		List<String> activities = this.gravity.readStatusUpdates(
				AlgorithmTests.USER_ID_A, AlgorithmTests.USER_ID_B, 15, false);
		assertNotNull(activities);

		// display complete Activity stream
		final String activityStream = ReadStatusUpdates
				.getActivityStream(activities);
		System.out.println(activityStream);
	}
}