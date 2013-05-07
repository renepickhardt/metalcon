package de.uniko.west.socialsensor.graphity.server.statusupdates;

/**
 * interface to compare two template instances regardless of their type
 * 
 * @author Sebastian Schlicht
 * 
 */
public interface TemplateComparison {

	/**
	 * get the unique identifier
	 * 
	 * @return status update template identifier
	 */
	String getName();

	/**
	 * get the template version
	 * 
	 * @return status update template version
	 */
	String getVersion();

}