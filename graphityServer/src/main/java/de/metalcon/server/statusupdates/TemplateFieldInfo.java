package de.metalcon.server.statusupdates;

import org.neo4j.graphdb.Node;
import org.neo4j.kernel.AbstractGraphDatabase;
import org.w3c.dom.Element;

import de.metalcon.socialgraph.Properties;

/**
 * field information within a status update template
 * 
 * @author sebschlicht
 * 
 */
public class TemplateFieldInfo extends TemplateItemInfo {

	/**
	 * template field type
	 */
	private final String type;

	/**
	 * create field information from a XML node
	 * 
	 * @param xmlNode
	 *            XML node element
	 * @throws IllegalArgumentException
	 *             if the XML node is not a valid template field node
	 */
	public TemplateFieldInfo(final Element xmlNode) {
		super(xmlNode);

		// TODO: remove type check
		this.type = xmlNode.getAttribute(TemplateXMLFields.FIELD_TYPE);
		if (this.type.equals(TemplateXMLFields.NO_VALUE)
				|| !(this.type.equals("String") || this.type.equals("Integer"))) {
			throw new IllegalArgumentException(
					"xml file malformed! template field items must contain attribute \""
							+ TemplateXMLFields.FIELD_TYPE + "\"!\n"
							+ "valid types: String, Integer");
		}
	}

	/**
	 * create field information from a neo4j node
	 * 
	 * @param node
	 *            neo4j template field item node
	 */
	public TemplateFieldInfo(final Node node) {
		super(node);

		this.type = (String) node.getProperty(Properties.Templates.FIELD_TYPE);
	}

	public String getType() {
		return this.type;
	}

	/**
	 * create a database node representing a template field information
	 * 
	 * @param graphDatabase
	 *            neo4j database to create the node in
	 * @param fieldInfo
	 *            template field information to be represented by the node
	 * @return neo4j node representing the template field information passed
	 */
	public static org.neo4j.graphdb.Node createTemplateFieldInfoNode(
			final AbstractGraphDatabase graphDatabase,
			final TemplateFieldInfo fieldInfo) {
		final org.neo4j.graphdb.Node fieldNode = TemplateItemInfo
				.createTemplateItemNode(graphDatabase, fieldInfo);
		fieldNode
				.setProperty(TemplateXMLFields.FIELD_TYPE, fieldInfo.getType());
		return fieldNode;
	}

}