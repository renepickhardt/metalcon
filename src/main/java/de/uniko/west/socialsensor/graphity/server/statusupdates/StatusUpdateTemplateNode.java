package de.uniko.west.socialsensor.graphity.server.statusupdates;

import org.neo4j.graphdb.Node;

import de.uniko.west.socialsensor.graphity.socialgraph.Properties;

/**
 * status update template node (database)
 * 
 * @author Sebastian Schlicht
 * 
 */
public class StatusUpdateTemplateNode implements TemplateComparison {

	/**
	 * template node
	 */
	private final Node node;

	/**
	 * template identifier
	 */
	private String identifier;

	/**
	 * template version
	 */
	private String version;

	/**
	 * template's java code
	 */
	private String code;

	/**
	 * create a new status update template node (database)
	 * 
	 * @param templateNode
	 *            neo4j node representing the template
	 */
	public StatusUpdateTemplateNode(final Node templateNode) {
		this.node = templateNode;
		this.identifier = (String) this.node
				.getProperty(Properties.Templates.IDENTIFIER);
		this.version = (String) this.node
				.getProperty(Properties.Templates.VERSION);
		this.code = (String) this.node.getProperty(Properties.Templates.CODE);
	}

	@Override
	public String getIdentifier() {
		return this.identifier;
	}

	@Override
	public String getVersion() {
		return this.version;
	}

	public String getCode() {
		return this.code;
	}

}