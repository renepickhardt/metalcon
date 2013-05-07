package de.uniko.west.socialsensor.graphity.socialgraph.algorithms;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Iterator;
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
import org.neo4j.graphdb.Transaction;

import de.uniko.west.socialsensor.graphity.server.statusupdates.templates.PlainText;
import de.uniko.west.socialsensor.graphity.socialgraph.NeoUtils;
import de.uniko.west.socialsensor.graphity.socialgraph.Properties;
import de.uniko.west.socialsensor.graphity.socialgraph.SocialGraph;
import de.uniko.west.socialsensor.graphity.socialgraph.SocialGraphRelationshipType;

/**
 * social graph algorithm Gravity test
 * 
 * @author Sebastian Schlicht
 * 
 */
public class GraphityTest {

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
	 * test user ego types
	 */
	private DynamicRelationshipType[] egoTypes;

	/**
	 * test transaction
	 */
	private Transaction transaction;

	@Before
	public void setUp() {
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
		this.egoTypes = new DynamicRelationshipType[this.userIds.length];
		for (int i = 0; i < this.userIds.length; i++) {
			this.users[i] = AlgorithmTests.DATABASE
					.getNodeById(this.userIds[i]);
			assertNotNull(this.users[i]);
			this.egoTypes[i] = Graphity.getEgoType(this.users[i]);
		}

		// load the Gravity algorithm
		this.graphity = new Graphity(AlgorithmTests.DATABASE);

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
		assertFalse(this.graphity.createFriendship(System.currentTimeMillis(),
				-1, AlgorithmTests.USER_ID_B));
		assertFalse(this.graphity.createFriendship(System.currentTimeMillis(),
				AlgorithmTests.USER_ID_A, -1));
		assertFalse(this.graphity.createFriendship(System.currentTimeMillis(),
				-1, -1));
	}

	/**
	 * count the number of relationships of an iterable
	 * 
	 * @param relationships
	 *            iterable relationships
	 * @return number of relationships available
	 */
	public static int getNumberOfRelationships(
			final Iterable<Relationship> relationships) {
		final Iterator<Relationship> iter = relationships.iterator();
		int result = 0;
		while (iter.hasNext()) {
			result += 1;
			iter.next();
		}
		return result;
	}

	@Test
	/**
	 * assert the creation to success if providing valid user identifiers<br>creating the test scenario
	 */
	public void testCreateFriendship_Regular() {
		// A follows: B, C, D, E
		assertTrue(this.graphity.createFriendship(System.currentTimeMillis(),
				AlgorithmTests.USER_ID_A, AlgorithmTests.USER_ID_B));
		assertTrue(this.graphity.createFriendship(System.currentTimeMillis(),
				AlgorithmTests.USER_ID_A, AlgorithmTests.USER_ID_C));
		assertTrue(this.graphity.createFriendship(System.currentTimeMillis(),
				AlgorithmTests.USER_ID_A, AlgorithmTests.USER_ID_D));
		assertTrue(this.graphity.createFriendship(System.currentTimeMillis(),
				AlgorithmTests.USER_ID_A, AlgorithmTests.USER_ID_E));

		// B follows: A, D
		assertTrue(this.graphity.createFriendship(System.currentTimeMillis(),
				AlgorithmTests.USER_ID_B, AlgorithmTests.USER_ID_A));
		assertTrue(this.graphity.createFriendship(System.currentTimeMillis(),
				AlgorithmTests.USER_ID_B, AlgorithmTests.USER_ID_D));

		// C follows: A, E
		assertTrue(this.graphity.createFriendship(System.currentTimeMillis(),
				AlgorithmTests.USER_ID_C, AlgorithmTests.USER_ID_A));
		assertTrue(this.graphity.createFriendship(System.currentTimeMillis(),
				AlgorithmTests.USER_ID_C, AlgorithmTests.USER_ID_E));

		// D follows: C
		assertTrue(this.graphity.createFriendship(System.currentTimeMillis(),
				AlgorithmTests.USER_ID_D, AlgorithmTests.USER_ID_C));

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

			// ego type: 1 (or 0 if not following any users)
			assertEquals(
					getNumberOfRelationships(this.users[i].getRelationships(
							this.egoTypes[i], Direction.OUTGOING)),
					(relationshipCount[0] != 0) ? 1 : 0);

			// assume matching relationship counts
			assertEquals(
					getNumberOfRelationships(this.users[i].getRelationships(
							SocialGraphRelationshipType.FOLLOW,
							Direction.OUTGOING)), relationshipCount[0]);
			assertEquals(
					getNumberOfRelationships(this.users[i].getRelationships(
							SocialGraphRelationshipType.FOLLOW,
							Direction.INCOMING)), relationshipCount[1]);
		}

		this.transaction.success();
	}

	@Test
	public void testCreateStatusUpdate_NotFoundException() {
		// assert the creation to fail if providing invalid user identifier
		final PlainText statusUpdate = new PlainText();
		statusUpdate.message = "this is not the reason why!";
		assertEquals(this.graphity.createStatusUpdate(
				System.currentTimeMillis(), -1, statusUpdate), 0);
	}

	/**
	 * wait the number of milliseconds specified
	 * 
	 * @param ms
	 *            delay in milliseconds
	 */
	public static void waitMs(final long ms) {
		final long targetMs = System.currentTimeMillis() + ms;
		while (System.currentTimeMillis() < targetMs) {
			// wait
		}
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
			statusUpdateNode = AlgorithmTests.DATABASE.getNodeById(nodeId);
			assertNotNull(statusUpdateNode);

			assertEquals(statusUpdateNode.getProperty(Properties.Timestamp),
					timestamp);
			assertEquals(statusUpdateNode.getProperty(Properties.ContentType),
					creationItem.getStatusUpdate().getType());
			assertEquals(statusUpdateNode.getProperty(Properties.Content),
					creationItem.getStatusUpdate().toJSONString());

			// prevent the time stamps from being equal
			waitMs(2);
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
						statusUpdateNode.getProperty(Properties.Timestamp),
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

	/**
	 * extract the messages of the status updates
	 * 
	 * @param activities
	 *            list of status updates in Activity JSON format
	 * @return list containing the status update messages
	 */
	private static List<Long> extractStatusUpdateMessages(
			final List<String> activities) {
		final List<Long> statusUpdateMessages = new LinkedList<Long>();

		int index;
		String message;
		for (String activity : activities) {
			index = activity.indexOf("message");
			message = activity.substring(index + 10,
					activity.indexOf("}", index) - 1);
			statusUpdateMessages.add(Long.valueOf(message));
		}

		return statusUpdateMessages;
	}

	/**
	 * compare the expected messages with the ones in the activities
	 * 
	 * @param messages
	 *            status update messages expected
	 * @param activities
	 *            list of status updates in Activity JSON format
	 */
	public static void compareValues(final long[] messages,
			final List<String> activities) {
		final List<Long> statusUpdateNodeMessages = extractStatusUpdateMessages(activities);
		assertEquals(statusUpdateNodeMessages.size(), messages.length);

		int i = 0;
		for (long message : statusUpdateNodeMessages) {
			assertEquals(message, messages[i]);
			i += 1;
		}
	}

	@Test
	public void testReadStatusUpdates_Regular() {
		List<String> activities;

		// assert success if more status updates available than read
		activities = this.graphity.readStatusUpdates(AlgorithmTests.USER_ID_A,
				AlgorithmTests.USER_ID_A, 7, false);
		assertNotNull(activities);
		compareValues(new long[] { 13, 12, 11, 10, 8, 7, 6 }, activities);

		// assert success if reading from users without status updates
		activities = this.graphity.readStatusUpdates(AlgorithmTests.USER_ID_A,
				AlgorithmTests.USER_ID_A, 10, false);
		assertNotNull(activities);
		compareValues(new long[] { 13, 12, 11, 10, 8, 7, 6, 5, 4 }, activities);

		// assert success if reading some status updates from one user in a row
		activities = this.graphity.readStatusUpdates(AlgorithmTests.USER_ID_A,
				AlgorithmTests.USER_ID_B, 2, false);
		assertNotNull(activities);
		compareValues(new long[] { 17, 14 }, activities);

		// assert success if reading from one user directly
		activities = this.graphity.readStatusUpdates(AlgorithmTests.USER_ID_A,
				AlgorithmTests.USER_ID_B, 2, true);
		assertNotNull(activities);
		compareValues(new long[] { 17, 14 }, activities);

		// assert success, even if reading too many status updates from multiple
		// nodes in ego network mode
		activities = this.graphity.readStatusUpdates(AlgorithmTests.USER_ID_C,
				AlgorithmTests.USER_ID_C, 4, false);
		assertNotNull(activities);
		compareValues(new long[] { 17, 14, 9 }, activities);

		// assert success, even if reading too many status updates from one user
		// directly
		activities = this.graphity.readStatusUpdates(AlgorithmTests.USER_ID_A,
				AlgorithmTests.USER_ID_C, 4, true);
		assertNotNull(activities);
		compareValues(new long[] { 17, 14, 9 }, activities);

		// assert success if reading from a single node in ego network mode
		activities = this.graphity.readStatusUpdates(AlgorithmTests.USER_ID_D,
				AlgorithmTests.USER_ID_D, 2, false);
		assertNotNull(activities);
		compareValues(new long[] { 13, 7 }, activities);

		// assert success, even if reading too many status updates from a single
		// node in ego network mode
		activities = this.graphity.readStatusUpdates(AlgorithmTests.USER_ID_D,
				AlgorithmTests.USER_ID_D, 4, false);
		assertNotNull(activities);
		compareValues(new long[] { 13, 7, 6 }, activities);

		// assert success, even if reading from an empty ego network
		activities = this.graphity.readStatusUpdates(AlgorithmTests.USER_ID_E,
				AlgorithmTests.USER_ID_E, 10, false);
		assertNotNull(activities);
		compareValues(new long[] {}, activities);
	}
}