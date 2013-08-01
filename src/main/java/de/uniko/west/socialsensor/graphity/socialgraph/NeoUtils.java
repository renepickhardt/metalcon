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

/**
 * collection of useful functions for neo4j
 * 
 * @author Jonas Kunze, Rene Pickhardt, Sebastian Schlicht
 * 
 */
public class NeoUtils {

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
	 * search for a status update of a user
	 * 
	 * @param user
	 *            identifier of the user owning the status update
	 * @param statusUpdateId
	 *            identifier of the status update
	 * @return status update node if ownership has been proved<br>
	 *         <b>null</b> - otherwise
	 */
	public static Node getStatusUpdate(final Node user,
			final long statusUpdateId) {
		Node statusUpdate = user;
		do {
			statusUpdate = NeoUtils.getNextSingleNode(statusUpdate,
					SocialGraphRelationshipType.UPDATE);
		} while ((statusUpdate != null)
				&& (statusUpdate.getId() != statusUpdateId));

		return statusUpdate;
	}

}