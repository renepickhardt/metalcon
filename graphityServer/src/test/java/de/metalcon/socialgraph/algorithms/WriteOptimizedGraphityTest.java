package de.metalcon.socialgraph.algorithms;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;

import de.metalcon.server.exceptions.StatusUpdateInstantiationFailedException;
import de.metalcon.server.statusupdates.StatusUpdate;
import de.metalcon.socialgraph.NeoUtils;
import de.metalcon.socialgraph.Properties;
import de.metalcon.socialgraph.SocialGraphRelationshipType;

/**
 * social graph first approach test
 * 
 * @author Sebastian Schlicht
 * 
 */
public class WriteOptimizedGraphityTest extends AlgorithmTest {

	private static boolean TESTING = false;

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

		// assert the test to get started by the algorithm tests suite
		assertNotNull(AlgorithmTests.DATABASE);
		this.loadUserNodes();

		// initialize the test transaction
		this.transaction = AlgorithmTests.DATABASE.beginTx();

		// load the Gravity read optimized algorithm
		this.algorithm = new WriteOptimizedGraphity(AlgorithmTests.DATABASE);
	}

	@After
	public void tearDown() {
		// finish the test transaction
		this.transaction.finish();
	}

	@Test
	/**
	 * assert the creation to success if providing valid user identifiers<br>creating the test scenario
	 */
	public void testCreateFriendship_Regular() {
		// A follows: B, C, D, E
		this.algorithm.createFriendship(this.USER_A, this.USER_B);
		this.algorithm.createFriendship(this.USER_A, this.USER_C);
		this.algorithm.createFriendship(this.USER_A, this.USER_D);
		this.algorithm.createFriendship(this.USER_A, this.USER_E);

		// B follows: A, D
		this.algorithm.createFriendship(this.USER_B, this.USER_A);
		this.algorithm.createFriendship(this.USER_B, this.USER_D);

		// C follows: A, E
		this.algorithm.createFriendship(this.USER_C, this.USER_A);
		this.algorithm.createFriendship(this.USER_C, this.USER_E);

		// D follows: C
		this.algorithm.createFriendship(this.USER_D, this.USER_C);

		// E follows none

		// prepare for relationship count check
		final Map<Node, int[]> sortedCounts = new HashMap<Node, int[]>();

		// A: 4 out, 2 in
		sortedCounts.put(this.USER_A, new int[] { 4, 2 });
		// B: 2 out, 1 in
		sortedCounts.put(this.USER_B, new int[] { 2, 1 });
		// C: 2 out, 2 in
		sortedCounts.put(this.USER_C, new int[] { 2, 2 });
		// D: 1 out, 2 in
		sortedCounts.put(this.USER_D, new int[] { 1, 2 });
		// E: 0 out, 2 in
		sortedCounts.put(this.USER_E, new int[] { 0, 2 });

		// check number of relationships for each user
		int[] relationshipCount;
		for (Node user : AlgorithmTests.USERS) {
			relationshipCount = sortedCounts.get(user);

			// assume matching relationship counts
			assertEquals(AlgorithmTest.getNumberOfRelationships(user
					.getRelationships(SocialGraphRelationshipType.FOLLOW,
							Direction.OUTGOING)), relationshipCount[0]);
			assertEquals(AlgorithmTest.getNumberOfRelationships(user
					.getRelationships(SocialGraphRelationshipType.FOLLOW,
							Direction.INCOMING)), relationshipCount[1]);
		}

		this.transaction.success();
	}

	@Test
	public void testCreateStatusUpdate_Regular() {
		// prepare for status update creation
		final List<StatusUpdateCreationItem> creates = new LinkedList<StatusUpdateCreationItem>();
		try {
			creates.add(new StatusUpdateCreationItem(this.USER_B, 4));
			creates.add(new StatusUpdateCreationItem(this.USER_D, 5));
			creates.add(new StatusUpdateCreationItem(this.USER_C, 6));
			creates.add(new StatusUpdateCreationItem(this.USER_C, 7));
			creates.add(new StatusUpdateCreationItem(this.USER_D, 8));
			creates.add(new StatusUpdateCreationItem(this.USER_A, 9));
			creates.add(new StatusUpdateCreationItem(this.USER_B, 10));
			creates.add(new StatusUpdateCreationItem(this.USER_D, 11));
			creates.add(new StatusUpdateCreationItem(this.USER_B, 12));
			creates.add(new StatusUpdateCreationItem(this.USER_C, 13));
			creates.add(new StatusUpdateCreationItem(this.USER_A, 14));
			creates.add(new StatusUpdateCreationItem(this.USER_A, 17));
		} catch (final StatusUpdateInstantiationFailedException e) {
			fail();
		}

		long timestamp;
		int statusUpdateIdInt = 0;
		StatusUpdate statusUpdate;
		Node statusUpdateNode;
		for (StatusUpdateCreationItem creationItem : creates) {
			// set status update identifier
			statusUpdate = creationItem.getStatusUpdate();
			statusUpdate.setId(String.valueOf(statusUpdateIdInt));
			statusUpdateIdInt += 1;

			// create status update
			timestamp = System.currentTimeMillis();
			this.algorithm.createStatusUpdate(timestamp,
					creationItem.getUser(), statusUpdate);

			// check if status update node exists and contains correct data
			statusUpdateNode = NeoUtils
					.getStatusUpdateByIdentifier(statusUpdate.getId());

			assertEquals(
					statusUpdateNode
							.getProperty(Properties.StatusUpdate.IDENTIFIER),
					statusUpdate.getId());
			assertEquals(
					statusUpdateNode
							.getProperty(Properties.StatusUpdate.TIMESTAMP),
					timestamp);
			assertEquals(
					statusUpdateNode
							.getProperty(Properties.StatusUpdate.CONTENT_TYPE),
					statusUpdate.getType());
			assertEquals(
					statusUpdateNode
							.getProperty(Properties.StatusUpdate.CONTENT),
					statusUpdate.toJSONObject().toJSONString());

			// prevent the time stamps from being equal
			AlgorithmTest.waitMs(2);
		}

		// sort creation items by users
		final Map<Node, LinkedList<StatusUpdateCreationItem>> sortedItems = new HashMap<Node, LinkedList<StatusUpdateCreationItem>>();
		for (Node user : AlgorithmTests.USERS) {
			sortedItems.put(user, new LinkedList<StatusUpdateCreationItem>());
		}
		for (StatusUpdateCreationItem creationItem : creates) {
			sortedItems.get(creationItem.getUser()).add(creationItem);
		}

		// compare status update node structure with the creation set
		LinkedList<StatusUpdateCreationItem> userItems;
		StatusUpdateCreationItem currentItem;
		for (Node user : AlgorithmTests.USERS) {
			userItems = sortedItems.get(user);
			statusUpdateNode = user;

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
	public void testReadStatusUpdates_Regular() {
		List<JSONObject> activities;

		// assert success if more status updates available than read
		activities = this.algorithm.readStatusUpdates(this.USER_A, this.USER_A,
				7, false);
		assertNotNull(activities);
		AlgorithmTest.compareValues(new long[] { 13, 12, 11, 10, 8, 7, 6 },
				activities);

		// assert success if reading from users without status updates
		activities = this.algorithm.readStatusUpdates(this.USER_A, this.USER_A,
				10, false);
		assertNotNull(activities);
		AlgorithmTest.compareValues(
				new long[] { 13, 12, 11, 10, 8, 7, 6, 5, 4 }, activities);

		// assert success if reading some status updates from one user in a row
		activities = this.algorithm.readStatusUpdates(this.USER_A, this.USER_B,
				2, false);
		assertNotNull(activities);
		AlgorithmTest.compareValues(new long[] { 17, 14 }, activities);

		// assert success if reading from one user directly
		activities = this.algorithm.readStatusUpdates(this.USER_A, this.USER_B,
				2, true);
		assertNotNull(activities);
		AlgorithmTest.compareValues(new long[] { 17, 14 }, activities);

		// assert success, even if reading too many status updates from multiple
		// nodes in ego network mode
		activities = this.algorithm.readStatusUpdates(this.USER_C, this.USER_C,
				4, false);
		assertNotNull(activities);
		AlgorithmTest.compareValues(new long[] { 17, 14, 9 }, activities);

		// assert success, even if reading too many status updates from one user
		// directly
		activities = this.algorithm.readStatusUpdates(this.USER_A, this.USER_C,
				4, true);
		assertNotNull(activities);
		AlgorithmTest.compareValues(new long[] { 17, 14, 9 }, activities);

		// assert success if reading from a single node in ego network mode
		activities = this.algorithm.readStatusUpdates(this.USER_D, this.USER_D,
				2, false);
		assertNotNull(activities);
		AlgorithmTest.compareValues(new long[] { 13, 7 }, activities);

		// assert success, even if reading too many status updates from a single
		// node in ego network mode
		activities = this.algorithm.readStatusUpdates(this.USER_D, this.USER_D,
				4, false);
		assertNotNull(activities);
		AlgorithmTest.compareValues(new long[] { 13, 7, 6 }, activities);

		// assert success, even if reading from an empty ego network
		activities = this.algorithm.readStatusUpdates(this.USER_E, this.USER_E,
				10, false);
		assertNotNull(activities);
		AlgorithmTest.compareValues(new long[] {}, activities);
	}

}