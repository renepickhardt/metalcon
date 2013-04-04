package de.uniko.west.socialsensor.graphity.socialgraph;

/**
 * configuration for social graph databases
 * 
 * @author sebschlicht
 * 
 */
public interface NeoGraphConfiguration {

	/**
	 * access social graph database path
	 * 
	 * @return social graph database path
	 */
	String databasePath();

	/**
	 * access database read only flag
	 * 
	 * @return database read only flag
	 */
	boolean readOnly();

	/**
	 * access cache type
	 * 
	 * @return cache type
	 */
	String cacheType();

	/**
	 * access use memory mapped buffers flag
	 * 
	 * @return use memory mapped buffers flag
	 */
	String useMemoryMappedBuffers();

}