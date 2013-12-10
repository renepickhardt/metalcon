package de.metalcon.socialgraph;

/**
 * Graphity configuration
 * 
 * @author sebschlicht
 * 
 */
public interface Configuration {

	/**
	 * @return path to the Neo4j graph database
	 */
	String getDatabasePath();

	/**
	 * @return path to status update template files
	 */
	String getTemplatesPath();

	/**
	 * @return database read-only flag
	 */
	boolean getReadOnly();

	/**
	 * @return Graphity algorithm used
	 */
	String getAlgorithm();

	/**
	 * @return Neo4j configuration field: use memory mapped buffers flag
	 */
	String getUseMemoryMappedBuffers();

	/**
	 * @return Neo4j configuration field: cache type
	 */
	String getCacheType();

}