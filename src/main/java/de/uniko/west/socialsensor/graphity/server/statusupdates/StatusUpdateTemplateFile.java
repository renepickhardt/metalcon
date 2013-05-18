package de.uniko.west.socialsensor.graphity.server.statusupdates;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * status update template file (XML)
 * 
 * @author Sebastian Schlicht
 * 
 */
public class StatusUpdateTemplateFile implements TemplateComparison {

	/**
	 * XML document builder
	 */
	private static DocumentBuilder DOC_BUILDER;

	/**
	 * XML document
	 */
	private Document doc;

	/**
	 * template identifier
	 */
	private String name;

	/**
	 * template version
	 */
	private String version;

	/**
	 * template fields
	 */
	private Map<String, String> fields;

	/**
	 * create a new status update template file (XML)
	 * 
	 * @param xmlFile
	 *            XML file containing the template
	 * @throws ParserConfigurationException
	 *             - DocumentBuilder cannot match the current configuration
	 * @throws SAXException
	 *             - parse error
	 * @throws IOException
	 *             - IO error
	 */
	public StatusUpdateTemplateFile(final File xmlFile)
			throws ParserConfigurationException, SAXException, IOException {
		if (DOC_BUILDER == null) {
			DOC_BUILDER = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
		}

		this.doc = DOC_BUILDER.parse(xmlFile);
		this.fields = new HashMap<String, String>();

		final Element root = this.doc.getDocumentElement();
		root.normalize();

		if (!root.getNodeName().equals("class")) {
			throw new IllegalArgumentException(
					"xml file malformed! root element must be \"class\"!");
		}

		this.name = root.getAttribute("name");
		if (this.name.equals("")) {
			throw new IllegalArgumentException(
					"xml file malformed! root element must contain attribute \"name\"!");
		}

		this.version = root.getAttribute("version");
		if (this.version.equals("")) {
			throw new IllegalArgumentException(
					"xml file malformed! root element must contain attribute \"version\"!");
		}

		final NodeList fields = root.getChildNodes();
		Node node;
		Element element;
		String fieldName, fieldType;
		for (int i = 0; i < fields.getLength(); i++) {
			node = fields.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				element = (Element) node;

				if (element.getNodeName().equals("param")) {
					fieldName = element.getAttribute("name");
					if (fieldName.equals("")) {
						throw new IllegalArgumentException(
								"xml file malformed! param element must contain attribute \"name\"!");
					}

					fieldType = element.getAttribute("type");
					if (fieldType.equals("")
							|| !(fieldType.equals("String") || fieldType
									.equals("Integer"))) {
						throw new IllegalArgumentException(
								"xml file malformed! param element must contain attribute \"type\"!\n"
										+ "valid types: String, Integer");
					}

					this.fields.put(fieldName, fieldType);
				} else if (element.getNodeName().equals("file")) {
					fieldName = element.getAttribute("name");
					if (fieldName.equals("")) {
						throw new IllegalArgumentException(
								"xml file malformed! file element must contain attribute \"name\"!");
					}

					this.fields.put(fieldName, "String");
				}
			}
		}
	}

	@Override
	public String getIdentifier() {
		return this.name;
	}

	@Override
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
		builder.append(StatusUpdateTemplateFile.class.getPackage().toString());
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
		for (String fieldName : this.fields.keySet()) {
			builder.append("public ");
			builder.append(this.fields.get(fieldName));
			builder.append(" ");
			builder.append(fieldName);
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