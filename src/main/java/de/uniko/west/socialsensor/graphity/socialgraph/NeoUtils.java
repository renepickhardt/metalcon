package de.uniko.west.socialsensor.graphity.socialgraph;

import java.io.File;
import java.nio.channels.NonWritableChannelException;
import java.util.HashMap;
import java.util.Map;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.kernel.AbstractGraphDatabase;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.neo4j.kernel.EmbeddedReadOnlyGraphDatabase;

import de.uniko.west.socialsensor.graphity.server.exceptions.InvalidUserIdentifierException;
import de.uniko.west.socialsensor.graphity.server.exceptions.delete.statusupdate.InvalidStatusUpdateIdentifierException;

/**
 * collection of useful functions for neo4j
 * 
 * @author Jonas Kunze, Rene Pickhardt, Sebastian Schlicht
 * 
 */
public class NeoUtils {

	/**
	 * lucene user node index name
	 */
	private static final String INDEX_USER = "user";

	/**
	 * lucene status update node index name
	 */
	private static final String INDEX_STATUS_UPDATE = "stup";

	/**
	 * create the database configuration map for neo4j databases
	 * 
	 * @param config
	 *            neo4j graph configuration containing the configuration values
	 * @return neo4j database configuration map
	 */
	private static Map<String, String> createDatabaseConfig(
			final NeoGraphConfiguration config) {
		// create database configuration
		final Map<String, String> databaseConfig = new HashMap<String, String>();

		// fill database configuration
		databaseConfig.put("cache_type", config.cacheType());
		databaseConfig.put("use_memory_mapped_buffers",
				config.useMemoryMappedBuffers());

		return databaseConfig;
	}

	/**
	 * open the neo4j graph database
	 * 
	 * @param config
	 *            neo4j graph configuration
	 * @return abstract graph database
	 */
	public static AbstractGraphDatabase getSocialGraphDatabase(
			final NeoGraphConfiguration config) {
		// prepare neo4j graph configuration
		final Map<String, String> graphConfig = createDatabaseConfig(config);

		// load database from path specified
		AbstractGraphDatabase database;
		if (config.readOnly()) {
			database = new EmbeddedReadOnlyGraphDatabase(config.databasePath(),
					graphConfig);
		} else {
			database = new EmbeddedGraphDatabase(config.databasePath(),
					graphConfig);
		}

		return database;
	}

	/**
	 * delete file or directory
	 * 
	 * @param file
	 *            file path
	 * @return true - if the file/directory has been deleted<br>
	 *         false otherwise
	 */
	public static boolean deleteFile(final File file) {
		// delete directory content recursively
		if (file.isDirectory()) {
			final File[] children = file.listFiles();
			for (File child : children) {
				if (!deleteFile(child)) {
					return false;
				}
			}
		}

		// delete file/empty directory
		return file.delete();
	}

	/**
	 * find an incoming relation from the user passed of the specified type
	 * 
	 * @param user
	 *            destination user node
	 * @param relationshipType
	 *            relationship type of the relation being searched
	 * @return source node of the relation<br>
	 *         null - if there is no relation of the type specified
	 */
	public static Node getPrevSingleNode(final Node user,
			RelationshipType relationshipType) {
		// find an incoming relation of the type specified
		final Relationship rel = user.getSingleRelationship(relationshipType,
				Direction.INCOMING);

		return (rel == null) ? (null) : (rel.getStartNode());
	}

	/**
	 * find an outgoing relation from the user passed of the specified type
	 * 
	 * @param user
	 *            source user node
	 * @param relType
	 *            relationship type of the relation being searched
	 * @return node targeted by the relation<br>
	 *         null - if there is no relation of the type specified
	 */
	public static Node getNextSingleNode(final Node user,
			RelationshipType relationshipType) {
		// find an outgoing relation of the type specified
		Relationship rel = null;
		try {
			rel = user.getSingleRelationship(relationshipType,
					Direction.OUTGOING);
		} catch (NonWritableChannelException e) {
			// TODO: why is this here? Bug for read-only databases in previous
			// version?
		}

		return (rel == null) ? (null) : (rel.getEndNode());
	}

	/**
	 * Search for a relationship between two nodes
	 * 
	 * @param source
	 *            source node to loop through relationships
	 * @param target
	 *            target node of the relationship
	 * @param relationshipType
	 *            relationship type of the relation being searched
	 * @param direction
	 *            direction of the relationship being searched
	 * @return relationship instance matching the passed arguments if existing<br>
	 *         <b>null</b> - otherwise
	 */
	public static Relationship getRelationshipBetween(final Node source,
			final Node target, final RelationshipType relationshipType,
			final Direction direction) {
		for (Relationship relationship : source.getRelationships(
				relationshipType, direction)) {
			if (relationship.getEndNode().equals(target)) {
				return relationship;
			}
		}

		return null;
	}

	/**
	 * create a user node in a database
	 * 
	 * @param graphDB
	 *            social graph database to operate on
	 * @param userId
	 *            user identifier
	 * @return user node having its identifier stored
	 * @throws IllegalArgumentException
	 *             if the identifier is already in use
	 */
	public static Node createUserNode(final AbstractGraphDatabase graphDB,
			final String userId) {
		if (graphDB.index().forNodes(INDEX_USER).get("id", userId).getSingle() == null) {
			final Node user = graphDB.createNode();
			user.setProperty(Properties.User.IDENTIFIER, userId);
			return user;
		}

		throw new IllegalArgumentException("user node with identifier \""
				+ userId + "\" already existing!");
	}

	/**
	 * create a status update node in a database
	 * 
	 * @param graphDB
	 *            social graph database to operate on
	 * @param statusUpdateId
	 *            status update identifier
	 * @return status update node having its identifier stored
	 * @throws IllegalArgumentException
	 *             if the identifier is already in use
	 */
	public static Node createStatusUpdateNode(
			final AbstractGraphDatabase graphDB, final String statusUpdateId) {
		if (graphDB.index().forNodes(INDEX_STATUS_UPDATE)
				.get("id", statusUpdateId).getSingle() == null) {
			final Node statusUpdate = graphDB.createNode();
			statusUpdate.setProperty(Properties.StatusUpdate.IDENTIFIER,
					statusUpdateId);
			return statusUpdate;
		}

		throw new IllegalArgumentException(
				"status update node with identifier \"" + statusUpdateId
						+ "\" already existing!");
	}

	/**
	 * get a user node from a database via its identifier
	 * 
	 * @param graphDB
	 *            social graph database to search in
	 * @param userId
	 *            user node identifier
	 * @return user node with the identifier passed
	 * @throws InvalidUserIdentifierException
	 *             if there is no user node with the identifier passed
	 */
	public static Node getUserNodeByIdentifier(
			final AbstractGraphDatabase graphDB, final String userId) {
		final Node user = graphDB.index().forNodes(INDEX_USER)
				.get("id", userId).getSingle();

		if (user != null) {
			return user;
		}

		throw new InvalidUserIdentifierException("user with identifier \""
				+ userId + "\" not existing!");
	}

	/**
	 * get a status update node from a database via its identifier
	 * 
	 * @param graphDB
	 *            social graph database to search in
	 * @param statusUpdateId
	 *            status update node identifier
	 * @return status update node with the identifier passed
	 * @throws InvalidStatusUpdateIdentifierException
	 *             if there is no status update node with the identifier passed
	 */
	public static Node getStatusUpdateNodeByIdentifier(
			final AbstractGraphDatabase graphDB, final String statusUpdateId) {
		final Node statusUpdate = graphDB.index().forNodes(INDEX_STATUS_UPDATE)
				.get("id", statusUpdateId).getSingle();

		if (statusUpdate != null) {
			return statusUpdate;
		}

		throw new InvalidStatusUpdateIdentifierException(
				"status update with identifier \"" + statusUpdateId
						+ "\" not existing!");
	}

	/**
	 * fixed identifiers for special nodes
	 * 
	 * @author sebschlicht
	 * 
	 */
	public class NodeIdentifiers {

		/**
		 * database root node
		 */
		public static final long ROOT = 0;

	}

}