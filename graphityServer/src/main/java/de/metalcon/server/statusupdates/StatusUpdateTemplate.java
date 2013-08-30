package de.metalcon.server.statusupdates;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Relationship;
import org.neo4j.kernel.AbstractGraphDatabase;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.metalcon.socialgraph.Properties;
import de.metalcon.socialgraph.SocialGraphRelationshipType;

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
	 * java code representing the status update template
	 */
	private final String javaCode;

	/**
	 * template field information
	 */
	private final Map<String, TemplateFieldInfo> fields;

	/**
	 * template file information
	 */
	private final Map<String, TemplateFileInfo> files;

	/**
	 * template instantiation class
	 */
	private Class<?> instantiationClass;

	/**
	 * create a new status update template from a XML file
	 * 
	 * @param xmlFile
	 *            XML file containing the template
	 * @throws IllegalArgumentException
	 *             if the XML file contains errors
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

		// read template information
		if (!root.getNodeName().equals(TemplateXMLFields.TEMPLATE_ROOT)) {
			throw new IllegalArgumentException(
					"xml file malformed! template root element must be \""
							+ TemplateXMLFields.TEMPLATE_ROOT + "\"!");
		}

		this.name = root.getAttribute(TemplateXMLFields.TEMPLATE_NAME);
		if (this.name.equals("")) {
			throw new IllegalArgumentException(
					"xml file malformed! template root element must contain attribute \""
							+ TemplateXMLFields.TEMPLATE_NAME + "\"!");
		}

		this.version = root.getAttribute(TemplateXMLFields.TEMPLATE_VERSION);
		if (this.version.equals("")) {
			throw new IllegalArgumentException(
					"xml file malformed! template root element must contain attribute \""
							+ TemplateXMLFields.TEMPLATE_VERSION + "\"!");
		}

		// read template items
		final NodeList fields = root.getChildNodes();
		Node node;
		Element element;
		TemplateFieldInfo fieldInfo;
		TemplateFileInfo fileInfo;
		for (int i = 0; i < fields.getLength(); i++) {
			node = fields.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				element = (Element) node;

				if (element.getNodeName().equals(TemplateXMLFields.FIELD)) {
					fieldInfo = new TemplateFieldInfo(element);
					this.fields.put(fieldInfo.getName(), fieldInfo);
				} else if (element.getNodeName().equals(TemplateXMLFields.FILE)) {
					fileInfo = new TemplateFileInfo(element);
					this.files.put(fileInfo.getName(), fileInfo);
				}
			}
		}

		this.javaCode = this.generateJavaCode();
	}

	/**
	 * create a new status update template from a neo4j node
	 * 
	 * @param templateNode
	 *            neo4j template node
	 */
	public StatusUpdateTemplate(final org.neo4j.graphdb.Node templateNode) {
		// read template information
		this.name = (String) templateNode
				.getProperty(Properties.Templates.IDENTIFIER);
		this.version = (String) templateNode
				.getProperty(Properties.Templates.VERSION);
		this.javaCode = (String) templateNode
				.getProperty(Properties.Templates.CODE);
		this.fields = new HashMap<String, TemplateFieldInfo>();
		this.files = new HashMap<String, TemplateFileInfo>();

		// read template field items
		TemplateFieldInfo fieldInfo;
		for (Relationship field : templateNode
				.getRelationships(SocialGraphRelationshipType.Templates.FIELD,
						Direction.OUTGOING)) {
			fieldInfo = new TemplateFieldInfo(field.getEndNode());
			this.fields.put(fieldInfo.getName(), fieldInfo);
		}

		// read template file items
		TemplateFileInfo fileInfo;
		for (Relationship file : templateNode.getRelationships(
				SocialGraphRelationshipType.Templates.FILE, Direction.OUTGOING)) {
			fileInfo = new TemplateFileInfo(file.getEndNode());
			this.files.put(fileInfo.getName(), fileInfo);
		}
	}

	public String getName() {
		return this.name;
	}

	public String getVersion() {
		return this.version;
	}

	public String getJavaCode() {
		return this.javaCode;
	}

	public Map<String, TemplateFieldInfo> getFields() {
		return this.fields;
	}

	public Map<String, TemplateFileInfo> getFiles() {
		return this.files;
	}

	public Class<?> getInstantiationClass() {
		return this.instantiationClass;
	}

	public void setInstantiationClass(final Class<?> instantiationClass) {
		this.instantiationClass = instantiationClass;
	}

	/**
	 * generate the template's java code
	 * 
	 * @return java code representing a template instance
	 */
	private String generateJavaCode() {
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
		for (String fileName : this.files.keySet()) {
			builder.append("objectJSON.put(\"");
			builder.append(fileName);
			builder.append("\", this.");
			builder.append(fileName);
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

	/**
	 * create a database node representing a status update template
	 * 
	 * @param graphDatabase
	 *            neo4j database to create the node in
	 * @param template
	 *            status update template to be represented by the node
	 * @return neo4j node representing the status update template passed
	 */
	public static org.neo4j.graphdb.Node createTemplateNode(
			final AbstractGraphDatabase graphDatabase,
			final StatusUpdateTemplate template) {
		final org.neo4j.graphdb.Node templateNode = graphDatabase.createNode();

		// write template information
		templateNode.setProperty(Properties.Templates.IDENTIFIER,
				template.getName());
		templateNode.setProperty(Properties.Templates.VERSION,
				template.getVersion());
		templateNode.setProperty(Properties.Templates.CODE,
				template.getJavaCode());

		org.neo4j.graphdb.Node itemNode;

		// write template field items
		TemplateFieldInfo fieldInfo;
		for (String fieldName : template.getFields().keySet()) {
			fieldInfo = template.getFields().get(fieldName);
			itemNode = TemplateFieldInfo.createTemplateFieldInfoNode(
					graphDatabase, fieldInfo);
			templateNode.createRelationshipTo(itemNode,
					SocialGraphRelationshipType.Templates.FIELD);
		}

		// write template file items
		TemplateFileInfo fileInfo;
		for (String fileName : template.getFiles().keySet()) {
			fileInfo = template.getFiles().get(fileName);
			itemNode = TemplateFileInfo.createTemplateFileInfoNode(
					graphDatabase, fileInfo);
			templateNode.createRelationshipTo(itemNode,
					SocialGraphRelationshipType.Templates.FILE);
		}

		return templateNode;
	}

}