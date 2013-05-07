package de.uniko.west.socialsensor.graphity.server.statusupdates;

/**
 * status update template node (database)
 * 
 * @author Sebastian Schlicht
 * 
 */
public class StatusUpdateTemplateNode implements TemplateComparison {

	/**
	 * template identifier
	 */
	private String name;

	/**
	 * template version
	 */
	private String version;

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public String getVersion() {
		return this.version;
	}

}