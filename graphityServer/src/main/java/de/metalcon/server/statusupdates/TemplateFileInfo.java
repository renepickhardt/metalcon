package de.metalcon.server.statusupdates;

import org.neo4j.graphdb.Node;
import org.neo4j.kernel.AbstractGraphDatabase;
import org.w3c.dom.Element;

import de.metalcon.socialgraph.Properties;

/**
 * file information within a status update template
 * 
 * @author sebschlicht
 * 
 */
public class TemplateFileInfo extends TemplateItemInfo {

	/**
	 * template file content type (MIME)
	 */
	private final String contentType;

	/**
	 * create file information from a XML node
	 * 
	 * @param xmlNode
	 *            XML node element
	 * @throws IllegalArgumentException
	 *             if the XML node is not a valid template file node
	 */
	public TemplateFileInfo(final Element xmlNode) {
		super(xmlNode);

		this.contentType = xmlNode
				.getAttribute(TemplateXMLFields.FILE_CONTENT_TYPE);
		if (this.contentType.equals(TemplateXMLFields.NO_VALUE)) {
			throw new IllegalArgumentException(
					"xml file malformed! template file items must contain attribute \""
							+ TemplateXMLFields.FILE_CONTENT_TYPE + "\"!");
		}
	}

	/**
	 * create file information from a neo4j node
	 * 
	 * @param node
	 *            neo4j template file item node
	 */
	public TemplateFileInfo(final Node node) {
		super(node);

		this.contentType = (String) node
				.getProperty(Properties.Templates.FILE_CONTENT_TYPE);
	}

	public String getContentType() {
		return this.contentType;
	}

	/**
	 * create a database node representing a template file information
	 * 
	 * @param graphDatabase
	 *            neo4j database to create the node in
	 * @param fileInfo
	 *            template file information to be represented by the node
	 * @return neo4j node representing the template file information passed
	 */
	public static org.neo4j.graphdb.Node createTemplateFileInfoNode(
			final AbstractGraphDatabase graphDatabase,
			final TemplateFileInfo fileInfo) {
		final org.neo4j.graphdb.Node fileNode = TemplateItemInfo
				.createTemplateItemNode(graphDatabase, fileInfo);
		fileNode.setProperty(TemplateXMLFields.FILE_CONTENT_TYPE,
				fileInfo.getContentType());
		return fileNode;
	}

}