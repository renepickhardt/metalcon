package de.uniko.west.socialsensor.graphity.socialgraph.algorithms;

import static org.junit.Assert.assertEquals;
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
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

import de.uniko.west.socialsensor.graphity.server.exceptions.InvalidUserIdentifierException;
import de.uniko.west.socialsensor.graphity.server.exceptions.create.follow.InvalidCreateFollowedIdentifier;
import de.uniko.west.socialsensor.graphity.server.statusupdates.StatusUpdate;
import de.uniko.west.socialsensor.graphity.server.statusupdates.StatusUpdateManager;
import de.uniko.west.socialsensor.graphity.server.tomcat.create.FormItemDoubleUsageException;
import de.uniko.west.socialsensor.graphity.server.tomcat.create.FormItemList;
import de.uniko.west.socialsensor.graphity.socialgraph.NeoUtils;
import de.uniko.west.socialsensor.graphity.socialgraph.Properties;
import de.uniko.west.socialsensor.graphity.socialgraph.SocialGraph;
import de.uniko.west.socialsensor.graphity.socialgraph.SocialGraphRelationshipType;

/**
 * social graph first approach test
 * 
 * @author Sebastian Schlicht
 * 
 */
public class BaselineTest {

	/**
	 * class testing flag
	 */
	private static boolean TESTING = false;

	/**
	 * social graph implementation Graphity being tested
	 */
	private SocialGraph graphity;

	/**
	 * test user identifiers
	 */
	private long[] userIds;

	/**
	 * test user nodes
	 */
	private Node[] users;

	/**
	 * test transaction
	 */
	private Transaction transaction;

	@Before
	public void setUp() throws Exception {
		if (!TESTING) {
			if (AlgorithmTests.wasUsed()) {
				// reset database
				AlgorithmTests.tearDown();
				AlgorithmTests.setUp();
			}

			TESTING = true;
		}

		// assert the test to get started by the algorithm tests
		assertNotNull(AlgorithmTests.DATABASE);

		// load all user nodes
		this.userIds = new long[] { AlgorithmTests.USER_ID_A,
				AlgorithmTests.USER_ID_B, AlgorithmTests.USER_ID_C,
				AlgorithmTests.USER_ID_D, AlgorithmTests.USER_ID_E };
		this.users = new Node[this.userIds.length];
		for (int i = 0; i < this.userIds.length; i++) {
			this.users[i] = AlgorithmTests.DATABASE
					.getNodeById(this.userIds[i]);
			assertNotNull(this.users[i]);
		}

		// load the Gravity algorithm
		this.graphity = new Baseline(AlgorithmTests.DATABASE);

		// initialize test transaction
		this.transaction = AlgorithmTests.DATABASE.beginTx();
	}

	@After
	public void tearDown() throws Exception {
		// finish the running test transaction
		this.transaction.finish();
	}

	@Test(expected = InvalidUserIdentifierException.class)
	public void testCreateFriendship_InvalidFollowingIdentifier() {
		// assert the creation to fail if providing invalid following identifier
		this.graphity.createFriendship(System.currentTimeMillis(), -1,
				AlgorithmTests.USER_ID_B);
	}

	@Test(expected = InvalidCreateFollowedIdentifier.class)
	public void testCreateFriendship_InvalidFollowedIdentifier() {
		// assert the creation to fail if providing invalid followed identifier
		this.graphity.createFriendship(System.currentTimeMillis(),
				AlgorithmTests.USER_ID_A, -1);
	}

	@Test
	/**
	 * assert the creation to success if providing valid user identifiers<br>creating the test scenario
	 */
	public void testCreateFriendship_Regular() {
		// A follows: B, C, D, E
		this.graphity.createFriendship(System.currentTimeMillis(),
				AlgorithmTests.USER_ID_A, AlgorithmTests.USER_ID_B);
		this.graphity.createFriendship(System.currentTimeMillis(),
				AlgorithmTests.USER_ID_A, AlgorithmTests.USER_ID_C);
		this.graphity.createFriendship(System.currentTimeMillis(),
				AlgorithmTests.USER_ID_A, AlgorithmTests.USER_ID_D);
		this.graphity.createFriendship(System.currentTimeMillis(),
				AlgorithmTests.USER_ID_A, AlgorithmTests.USER_ID_E);

		// B follows: A, D
		this.graphity.createFriendship(System.currentTimeMillis(),
				AlgorithmTests.USER_ID_B, AlgorithmTests.USER_ID_A);
		this.graphity.createFriendship(System.currentTimeMillis(),
				AlgorithmTests.USER_ID_B, AlgorithmTests.USER_ID_D);

		// C follows: A, E
		this.graphity.createFriendship(System.currentTimeMillis(),
				AlgorithmTests.USER_ID_C, AlgorithmTests.USER_ID_A);
		this.graphity.createFriendship(System.currentTimeMillis(),
				AlgorithmTests.USER_ID_C, AlgorithmTests.USER_ID_E);

		// D follows: C
		this.graphity.createFriendship(System.currentTimeMillis(),
				AlgorithmTests.USER_ID_D, AlgorithmTests.USER_ID_C);

		// E follows none

		// prepare for relationship count check
		final Map<Long, int[]> sortedCounts = new HashMap<Long, int[]>();

		// A: 4 out, 2 in
		sortedCounts.put(AlgorithmTests.USER_ID_A, new int[] { 4, 2 });
		// B: 2 out, 1 in
		sortedCounts.put(AlgorithmTests.USER_ID_B, new int[] { 2, 1 });
		// C: 2 out, 2 in
		sortedCounts.put(AlgorithmTests.USER_ID_C, new int[] { 2, 2 });
		// D: 1 out, 2 in
		sortedCounts.put(AlgorithmTests.USER_ID_D, new int[] { 1, 2 });
		// E: 0 out, 2 in
		sortedCounts.put(AlgorithmTests.USER_ID_E, new int[] { 0, 2 });

		// check number of relationships for each user
		int[] relationshipCount;
		for (int i = 0; i < this.userIds.length; i++) {
			relationshipCount = sortedCounts.get(this.userIds[i]);

			// assume matching relationship counts
			assertEquals(GraphityTest.getNumberOfRelationships(this.users[i]
					.getRelationships(SocialGraphRelationshipType.FOLLOW,
							Direction.OUTGOING)), relationshipCount[0]);
			assertEquals(GraphityTest.getNumberOfRelationships(this.users[i]
					.getRelationships(SocialGraphRelationshipType.FOLLOW,
							Direction.INCOMING)), relationshipCount[1]);
		}

		this.transaction.success();
	}

	@Test
	public void testCreateStatusUpdate_NotFoundException() {
		// assert the creation to fail if providing invalid user identifier
		final FormItemList values = new FormItemList();
		try {
			values.addField("message", "this is not the reason why!");
		} catch (final FormItemDoubleUsageException e) {
			e.printStackTrace();
		}
		final StatusUpdate statusUpdate = StatusUpdateManager
				.instantiateStatusUpdate("PlainText", values);
		assertEquals(this.graphity.createStatusUpdate(
				System.currentTimeMillis(), -1, statusUpdate), 0);
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
			nodeId = this.graphity.createStatusUpdate(timestamp,
					creationItem.getUserId(), creationItem.getStatusUpdate());
			assertTrue(nodeId != 0);

			// check if status update node exists and contains correct data
			statusUpdateNode = NeoUtils.getNodeByIdentifier(
					AlgorithmTests.DATABASE, nodeId);
			assertNotNull(statusUpdateNode);

			assertEquals(
					statusUpdateNode
							.getProperty(Properties.StatusUpdate.TIMESTAMP),
					timestamp);
			assertEquals(
					statusUpdateNode
							.getProperty(Properties.StatusUpdate.CONTENT_TYPE),
					creationItem.getStatusUpdate().getType());
			assertEquals(
					statusUpdateNode
							.getProperty(Properties.StatusUpdate.CONTENT),
					creationItem.getStatusUpdate().toJSONString());

			// prevent the time stamps from being equal
			GraphityTest.waitMs(2);
		}

		// sort creation items by user identifiers
		final Map<Long, LinkedList<StatusUpdateCreationItem>> sortedItems = new HashMap<Long, LinkedList<StatusUpdateCreationItem>>();
		for (long userId : this.userIds) {
			sortedItems.put(userId, new LinkedList<StatusUpdateCreationItem>());
		}
		for (StatusUpdateCreationItem creationItem : creates) {
			sortedItems.get(creationItem.getUserId()).add(creationItem);
		}

		// compare status update node structure with the creation set
		LinkedList<StatusUpdateCreationItem> userItems;
		StatusUpdateCreationItem currentItem;
		for (int i = 0; i < this.userIds.length; i++) {
			userItems = sortedItems.get(this.userIds[i]);
			statusUpdateNode = this.users[i];

			// loop through existing status update nodes
			while ((statusUpdateNode = NeoUtils.getNextSingleNode(
					statusUpdateNode, SocialGraphRelationshipType.UPDATE)) != null) {
				// assume there is a creation item corresponding to the node
				currentItem = userItems.removeLast();
				assertNotNull(currentItem);

				assertEquals(
						statusUpdateNode
								.getProperty(Properties.StatusUpdate.TIMESTAMP),
						currentItem.getStatusUpdate().getTimestamp());
			}

			// assume that the list is empty now, too
			assertTrue(userItems.isEmpty());
		}

		this.transaction.success();
	}

	@Test
	public void testReadStatusUpdates_NotFoundException() {
		// assert failure if providing invalid poster identifier
		assertNull(this.graphity.readStatusUpdates(-1,
				AlgorithmTests.USER_ID_A, 15, true));
	}

	@Test
	public void testReadStatusUpdates_Regular() {
		List<String> activities;

		// assert success if more status updates available than read
		activities = this.graphity.readStatusUpdates(AlgorithmTests.USER_ID_A,
				AlgorithmTests.USER_ID_A, 7, false);
		assertNotNull(activities);
		GraphityTest.compareValues(new long[] { 13, 12, 11, 10, 8, 7, 6 },
				activities);

		// assert success if reading from users without status updates
		activities = this.graphity.readStatusUpdates(AlgorithmTests.USER_ID_A,
				AlgorithmTests.USER_ID_A, 10, false);
		assertNotNull(activities);
		GraphityTest.compareValues(
				new long[] { 13, 12, 11, 10, 8, 7, 6, 5, 4 }, activities);

		// assert success if reading some status updates from one user in a row
		activities = this.graphity.readStatusUpdates(AlgorithmTests.USER_ID_A,
				AlgorithmTests.USER_ID_B, 2, false);
		assertNotNull(activities);
		GraphityTest.compareValues(new long[] { 17, 14 }, activities);

		// assert success if reading from one user directly
		activities = this.graphity.readStatusUpdates(AlgorithmTests.USER_ID_A,
				AlgorithmTests.USER_ID_B, 2, true);
		assertNotNull(activities);
		GraphityTest.compareValues(new long[] { 17, 14 }, activities);

		// assert success, even if reading too many status updates from multiple
		// nodes in ego network mode
		activities = this.graphity.readStatusUpdates(AlgorithmTests.USER_ID_C,
				AlgorithmTests.USER_ID_C, 4, false);
		assertNotNull(activities);
		GraphityTest.compareValues(new long[] { 17, 14, 9 }, activities);

		// assert success, even if reading too many status updates from one user
		// directly
		activities = this.graphity.readStatusUpdates(AlgorithmTests.USER_ID_A,
				AlgorithmTests.USER_ID_C, 4, true);
		assertNotNull(activities);
		GraphityTest.compareValues(new long[] { 17, 14, 9 }, activities);

		// assert success if reading from a single node in ego network mode
		activities = this.graphity.readStatusUpdates(AlgorithmTests.USER_ID_D,
				AlgorithmTests.USER_ID_D, 2, false);
		assertNotNull(activities);
		GraphityTest.compareValues(new long[] { 13, 7 }, activities);

		// assert success, even if reading too many status updates from a single
		// node in ego network mode
		activities = this.graphity.readStatusUpdates(AlgorithmTests.USER_ID_D,
				AlgorithmTests.USER_ID_D, 4, false);
		assertNotNull(activities);
		GraphityTest.compareValues(new long[] { 13, 7, 6 }, activities);

		// assert success, even if reading from an empty ego network
		activities = this.graphity.readStatusUpdates(AlgorithmTests.USER_ID_E,
				AlgorithmTests.USER_ID_E, 10, false);
		assertNotNull(activities);
		GraphityTest.compareValues(new long[] {}, activities);
	}

}