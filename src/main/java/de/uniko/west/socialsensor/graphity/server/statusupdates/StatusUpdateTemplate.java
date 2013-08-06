package de.uniko.west.socialsensor.graphity.server.statusupdates;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Relationship;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.uniko.west.socialsensor.graphity.socialgraph.Properties;
import de.uniko.west.socialsensor.graphity.socialgraph.SocialGraphRelationshipType;

/**
 * status update template
 * 
 * @author Sebastian Schlicht
 * 
 */
public class StatusUpdateTemplate {

	/**
	 * XML document builder
	 */
	private static DocumentBuilder DOC_BUILDER;

	/**
	 * template identifier
	 */
	private final String name;

	/**
	 * template version
	 */
	private final String version;

	/**
	 * template field information
	 */
	private Map<String, TemplateFieldInfo> fields;

	/**
	 * template file information
	 */
	private Map<String, TemplateFileInfo> files;

	/**
	 * create a new status update template from a XML file
	 * 
	 * @param xmlFile
	 *            XML file containing the template
	 * @throws ParserConfigurationException
	 *             - DocumentBuilder cannot match the current configuration
	 * @throws SAXException
	 *             - parsing error
	 * @throws IOException
	 *             - IO error
	 */
	public StatusUpdateTemplate(final File xmlFile)
			throws ParserConfigurationException, SAXException, IOException {
		if (DOC_BUILDER == null) {
			DOC_BUILDER = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
		}

		final Document doc = DOC_BUILDER.parse(xmlFile);
		this.fields = new HashMap<String, TemplateFieldInfo>();
		this.files = new HashMap<String, TemplateFileInfo>();

		final Element root = doc.getDocumentElement();
		root.normalize();

		if (!root.getNodeName().equals(TemplateXMLFields.TEMPLATE_ROOT)) {
			throw new IllegalArgumentException(
					"xml file malformed! root element must be \""
							+ TemplateXMLFields.TEMPLATE_ROOT + "\"!");
		}

		this.name = root.getAttribute(TemplateXMLFields.TEMPLATE_NAME);
		if (this.name.equals("")) {
			throw new IllegalArgumentException(
					"xml file malformed! root element must contain attribute \""
							+ TemplateXMLFields.TEMPLATE_NAME + "\"!");
		}

		this.version = root.getAttribute(TemplateXMLFields.TEMPLATE_VERSION);
		if (this.version.equals("")) {
			throw new IllegalArgumentException(
					"xml file malformed! root element must contain attribute \""
							+ TemplateXMLFields.TEMPLATE_VERSION + "\"!");
		}

		final NodeList fields = root.getChildNodes();
		Node node;
		Element element;
		String itemName, fieldType, fileContentType;
		for (int i = 0; i < fields.getLength(); i++) {
			node = fields.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				element = (Element) node;

				if (element.getNodeName().equals(TemplateXMLFields.FIELD)) {
					itemName = element
							.getAttribute(TemplateXMLFields.ITEM_NAME);
					if (itemName.equals("")) {
						throw new IllegalArgumentException(
								"xml file malformed! param element must contain attribute \""
										+ TemplateXMLFields.ITEM_NAME + "\"!");
					}

					fieldType = element
							.getAttribute(TemplateXMLFields.FIELD_TYPE);
					if (fieldType.equals("")
							|| !(fieldType.equals("String") || fieldType
									.equals("Integer"))) {
						throw new IllegalArgumentException(
								"xml file malformed! param element must contain attribute \""
										+ TemplateXMLFields.FIELD_TYPE
										+ "\"!\n"
										+ "valid types: String, Integer");
					}

					this.fields.put(itemName, new TemplateFieldInfo(itemName,
							fieldType));
				} else if (element.getNodeName().equals(TemplateXMLFields.FILE)) {
					itemName = element
							.getAttribute(TemplateXMLFields.ITEM_NAME);
					if (itemName.equals("")) {
						throw new IllegalArgumentException(
								"xml file malformed! file element must contain attribute \""
										+ TemplateXMLFields.ITEM_NAME + "\"!");
					}

					fileContentType = element
							.getAttribute(TemplateXMLFields.FILE_CONTENT_TYPE);
					if (fileContentType.equals("")) {
						throw new IllegalArgumentException(
								"xml file malformed! file element must contain attribute \""
										+ TemplateXMLFields.FILE_CONTENT_TYPE
										+ "\"!");
					}

					this.files.put(itemName, new TemplateFileInfo(itemName,
							fileContentType));
				}
			}
		}
	}

	public StatusUpdateTemplate(final org.neo4j.graphdb.Node templateNode) {
		this.name = (String) templateNode
				.getProperty(Properties.Templates.IDENTIFIER);
		this.version = (String) templateNode
				.getProperty(Properties.Templates.VERSION);

		org.neo4j.graphdb.Node fieldNode;
		String itemName, fieldType;
		for (Relationship field : templateNode.getRelationships(
				SocialGraphRelationshipType.TEMPLATE_FIELD, Direction.OUTGOING)) {
			fieldNode = field.getEndNode();

			itemName = (String) fieldNode
					.getProperty(Properties.Templates.ITEM_NAME);
			fieldType = (String) fieldNode
					.getProperty(Properties.Templates.FIELD_TYPE);
			this.fields.put(itemName,
					new TemplateFieldInfo(itemName, fieldType));
		}

		org.neo4j.graphdb.Node fileNode;
		String fileContentType;
		for (Relationship file : templateNode.getRelationships(
				SocialGraphRelationshipType.TEMPLATE_FILE, Direction.OUTGOING)) {
			fileNode = file.getEndNode();

			itemName = (String) fileNode
					.getProperty(Properties.Templates.ITEM_NAME);
			fileContentType = (String) fileNode
					.getProperty(Properties.Templates.FILE_CONTENT_TYPE);
			this.files.put(itemName, new TemplateFileInfo(itemName,
					fileContentType));
		}
	}

	public String getName() {
		return this.name;
	}

	public String getVersion() {
		return this.version;
	}

	/**
	 * generate the template's java code
	 * 
	 * @return java code representing a template instance
	 */
	public String generateJavaCode() {
		final StringBuilder builder = new StringBuilder();
		final String newLine = "\n";

		// package information
		builder.append(StatusUpdateTemplate.class.getPackage().toString());
		builder.append(".templates");
		builder.append(";");
		builder.append(newLine);
		builder.append(newLine);

		// imports
		builder.append("import java.util.Map;");
		builder.append(newLine);
		builder.append(newLine);
		builder.append("import ");
		builder.append(StatusUpdate.class.getName());
		builder.append(";");
		builder.append(newLine);
		builder.append(newLine);

		// class
		builder.append("public class ");
		builder.append(this.name);
		builder.append(" extends StatusUpdate {");
		builder.append(newLine);
		builder.append(newLine);

		// type identifier
		builder.append("public static final String TYPE_IDENTIFIER = \"");
		builder.append(this.name);
		builder.append("\";");
		builder.append(newLine);
		builder.append(newLine);

		// template fields
		TemplateFieldInfo field;
		for (String fieldName : this.fields.keySet()) {
			field = this.fields.get(fieldName);

			builder.append("public ");
			builder.append(field.getType());
			builder.append(" ");
			builder.append(field.getName());
			builder.append(";");
			builder.append(newLine);
			builder.append(newLine);
		}

		// template files
		TemplateFileInfo file;
		for (String fileName : this.files.keySet()) {
			file = this.files.get(fileName);

			builder.append("public String ");
			builder.append(file.getName());
			builder.append(";");
			builder.append(newLine);
			builder.append(newLine);
		}

		// constructor
		builder.append("public ");
		builder.append(this.name);
		builder.append("() {");
		builder.append(newLine);
		builder.append("super(TYPE_IDENTIFIER);");
		builder.append(newLine);
		builder.append("}");
		builder.append(newLine);
		builder.append(newLine);

		// JSON parsing
		builder.append("@Override");
		builder.append(newLine);
		builder.append("protected Map<String, Object> toObjectJSON() {");
		builder.append(newLine);
		builder.append("final Map<String, Object> objectJSON = super.toObjectJSON();");
		builder.append(newLine);
		for (String fieldName : this.fields.keySet()) {
			builder.append("objectJSON.put(\"");
			builder.append(fieldName);
			builder.append("\", this.");
			builder.append(fieldName);
			builder.append(");");
			builder.append(newLine);
		}
		builder.append("return objectJSON;");
		builder.append(newLine);
		builder.append("}");
		builder.append(newLine);
		builder.append(newLine);
		builder.append("}");

		return builder.toString();
	}

}