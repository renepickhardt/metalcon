package de.metalcon.socialgraph;

import java.io.File;
import java.nio.channels.NonWritableChannelException;
import java.util.HashMap;
import java.util.Map;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.index.Index;
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
	 * active graph database
	 */
	private static AbstractGraphDatabase DATABASE;

	/**
	 * lucene index identifier
	 */
	private static final String IDENTIFIER = "id";

	/**
	 * lucene status update template node index
	 */
	private static Index<Node> INDEX_STATUS_UPDATE_TEMPLATES;

	/**
	 * lucene user node index
	 */
	private static Index<Node> INDEX_USERS;

	/**
	 * lucene status update node index
	 */
	private static Index<Node> INDEX_STATUS_UPDATES;

	/**
	 * load the database configuration map for neo4j databases
	 * 
	 * @param config
	 *            Graphity configuration containing the configuration values
	 * @return neo4j database configuration map
	 */
	private static Map<String, String> loadDatabaseConfig(
			final Configuration config) {
		// create database configuration
		final Map<String, String> databaseConfig = new HashMap<String, String>();

		// fill database configuration
		databaseConfig.put("cache_type", config.getCacheType());
		databaseConfig.put("use_memory_mapped_buffers",
				config.getUseMemoryMappedBuffers());

		return databaseConfig;
	}

	/**
	 * load the lucene indices for a database
	 * 
	 * @param graphDatabase
	 *            social graph database to operate on
	 */
	private static void loadLuceneIndices(
			final AbstractGraphDatabase graphDatabase) {
		INDEX_STATUS_UPDATE_TEMPLATES = graphDatabase.index().forNodes("tmpl");
		INDEX_USERS = graphDatabase.index().forNodes("user");
		INDEX_STATUS_UPDATES = graphDatabase.index().forNodes("stup");
	}

	/**
	 * open the neo4j graph database
	 * 
	 * @param config
	 *            Graphity configuration
	 * @return abstract graph database
	 */
	public static AbstractGraphDatabase getSocialGraphDatabase(
			final Configuration config) {
		// prepare neo4j graph configuration
		final Map<String, String> graphConfig = loadDatabaseConfig(config);

		// load database from path specified
		AbstractGraphDatabase database;
		if (config.getReadOnly()) {
			database = new EmbeddedReadOnlyGraphDatabase(
					config.getDatabasePath(), graphConfig);
		} else {
			database = new EmbeddedGraphDatabase(config.getDatabasePath(),
					graphConfig);
		}

		// load lucene indices
		DATABASE = database;
		loadLuceneIndices(database);

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
		} catch (final NonWritableChannelException e) {
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
	 * create a user node in the active database
	 * 
	 * @param userId
	 *            user identifier
	 * @return user node having its identifier stored
	 * @throws IllegalArgumentException
	 *             if the identifier is already in use
	 */
	public static Node createUserNode(final String userId) {
		if (INDEX_USERS.get(IDENTIFIER, userId).getSingle() == null) {
			final Node user = DATABASE.createNode();
			user.setProperty(Properties.User.IDENTIFIER, userId);
			INDEX_USERS.add(user, IDENTIFIER, userId);
			return user;
		}

		throw new IllegalArgumentException("user node with identifier \""
				+ userId + "\" already existing!");
	}

	/**
	 * create a status update node in the active database
	 * 
	 * @param statusUpdateId
	 *            status update identifier
	 * @return status update node having its identifier stored
	 * @throws IllegalArgumentException
	 *             if the identifier is already in use
	 */
	public static Node createStatusUpdateNode(final String statusUpdateId) {
		if (INDEX_STATUS_UPDATES.get(IDENTIFIER, statusUpdateId).getSingle() == null) {
			final Node statusUpdate = DATABASE.createNode();
			statusUpdate.setProperty(Properties.StatusUpdate.IDENTIFIER,
					statusUpdateId);
			INDEX_STATUS_UPDATES.add(statusUpdate, IDENTIFIER, statusUpdateId);
			return statusUpdate;
		}

		throw new IllegalArgumentException(
				"status update node with identifier \"" + statusUpdateId
						+ "\" already existing!");
	}

	/**
	 * get a user node from the active database
	 * 
	 * @param userId
	 *            user node identifier
	 * @return user node with the identifier passed<br>
	 *         <b>null</b> if there is no user with such identifier
	 */
	public static Node getUserByIdentifier(final String userId) {
		return INDEX_USERS.get(IDENTIFIER, userId).getSingle();
	}

	/**
	 * get a status update node from the active database
	 * 
	 * @param statusUpdateId
	 *            status update node identifier
	 * @return status update node with the identifier passed<br>
	 *         <b>null</b> if there is no status update with such identifier
	 */
	public static Node getStatusUpdateByIdentifier(final String statusUpdateId) {
		return INDEX_STATUS_UPDATES.get(IDENTIFIER, statusUpdateId).getSingle();
	}

	/**
	 * store a status update template node in the index replacing previous
	 * occurrences
	 * 
	 * @param graphDatabase
	 *            graph database to operate on
	 * @param templateId
	 *            template identifier
	 * @param templateNode
	 *            template node that shall be stored
	 * @param previousTemplateNode
	 *            node of the latest previous template version
	 */
	public static void storeStatusUpdateTemplateNode(
			final AbstractGraphDatabase graphDatabase, final String templateId,
			final Node templateNode, final Node previousTemplateNode) {
		// remove node of the latest previous template version
		if (previousTemplateNode != null) {
			INDEX_STATUS_UPDATE_TEMPLATES.remove(previousTemplateNode,
					IDENTIFIER, templateId);
		}

		// add the new template node
		INDEX_STATUS_UPDATE_TEMPLATES.add(templateNode, IDENTIFIER, templateId);
	}

	/**
	 * get a status update template node from the active database via its
	 * identifier
	 * 
	 * @param templateId
	 *            status update template node identifier
	 * @return status update template node with the identifier passed<br>
	 *         <b>null</b>if there is no template with such identifier
	 */
	public static Node getStatusUpdateTemplateByIdentifier(
			final String templateId) {
		return INDEX_STATUS_UPDATE_TEMPLATES.get(IDENTIFIER, templateId)
				.getSingle();
	}

}