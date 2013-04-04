package de.uniko.west.socialsensor.graphity.socialgraph;

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
			RelationshipType relType) {
		// find an outgoing relation of the type specified
		Relationship rel = null;
		try {
			rel = user.getSingleRelationship(relType, Direction.OUTGOING);
		} catch (NonWritableChannelException e) {
			// TODO: why is this here? Bug for read-only databases in previous
			// version?
		}

		return (rel == null) ? (null) : (rel.getEndNode());
	}

}