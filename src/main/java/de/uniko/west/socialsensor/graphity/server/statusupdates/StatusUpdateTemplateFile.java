package de.uniko.west.socialsensor.graphity.server.statusupdates;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
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
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public String getVersion() {
		return this.version;
	}

}