package de.metalcon.server.statusupdates;

import org.neo4j.graphdb.Node;
import org.neo4j.kernel.AbstractGraphDatabase;
import org.w3c.dom.Element;

import de.metalcon.socialgraph.Properties;

/**
 * basic item information for all status update template items
 * 
 * @author sebschlicht
 * 
 */
public abstract class TemplateItemInfo {

	/**
	 * item name
	 */
	private final String name;

	/**
	 * create basic status update item information
	 * 
	 * @param name
	 */
	public TemplateItemInfo(final String name) {
		this.name = name;
	}

	/**
	 * create basic status update item information from a XML node
	 * 
	 * @param xmlNode
	 *            XML node element
	 * @throws IllegalArgumentException
	 *             if the XML node is not a valid template item node
	 */
	public TemplateItemInfo(final Element xmlNode) {
		this.name = xmlNode.getAttribute(TemplateXMLFields.ITEM_NAME);

		if (this.name.equals(TemplateXMLFields.NO_VALUE)) {
			throw new IllegalArgumentException(
					"xml file malformed! template items must contain attribute \""
							+ TemplateXMLFields.ITEM_NAME + "\"!");
		}
	}

	/**
	 * create basic status update item information from a neo4j node
	 * 
	 * @param node
	 *            neo4j template item node
	 */
	public TemplateItemInfo(final Node node) {
		this.name = (String) node.getProperty(Properties.Templates.ITEM_NAME);
	}

	public String getName() {
		return this.name;
	}

	/**
	 * create a database node representing a template item information
	 * 
	 * @param graphDatabase
	 *            neo4j database to create the node in
	 * @param itemInfo
	 *            template item information to be represented by the node
	 * @return neo4j node representing the template item information passed
	 */
	protected static org.neo4j.graphdb.Node createTemplateItemNode(
			final AbstractGraphDatabase graphDatabase,
			final TemplateItemInfo itemInfo) {
		final org.neo4j.graphdb.Node itemNode = graphDatabase.createNode();
		itemNode.setProperty(TemplateXMLFields.ITEM_NAME, itemInfo.getName());
		return itemNode;
	}

}