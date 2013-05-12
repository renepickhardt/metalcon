package de.uniko.west.socialsensor.graphity.server.statusupdates;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import javax.xml.parsers.ParserConfigurationException;

import org.neo4j.kernel.AbstractGraphDatabase;
import org.xml.sax.SAXException;

import de.uniko.west.socialsensor.graphity.server.Configs;

/**
 * status update manager to load
 * 
 * @author Sebastian Schlicht
 * 
 */
public class StatusUpdateManager {

	/**
	 * status update template manager node
	 */
	private static StatusUpdateTemplateManagerNode NODE;

	/**
	 * status update classes allowed
	 */
	private static Map<String, Class<?>> STATUS_UPDATE_TYPES = new HashMap<String, Class<?>>();

	/**
	 * load all XML files listed in the directory specified
	 * 
	 * @param directory
	 *            status update templates directory
	 * @return array of status update templates (XML files)
	 */
	private static File[] loadXmlFiles(final File directory) {
		return directory.listFiles(new FileFilter() {

			@Override
			public boolean accept(File file) {
				return file.getName().endsWith(".xml");
			}

		});
	}

	private static void generateJavaFiles(
			final StatusUpdateTemplateNode templateNode)
			throws FileNotFoundException {
		final File file = new File(templateNode.getIdentifier() + ".java");
		final String[] optionsAndSources = { "-source", "1.7", "-target",
				"1.7", templateNode.getIdentifier() + ".java" };

		// write java file
		final FileOutputStream outputStream = new FileOutputStream(file);
		PrintWriter writer = new PrintWriter(outputStream);
		writer.write(templateNode.getCode());
		writer.flush();
		writer.close();

		final FileInputStream reader = new FileInputStream(file);

		// compile to class file
		writer = new PrintWriter(templateNode.getIdentifier() + ".cass");
		final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		compiler.run(reader, outputStream, System.err, optionsAndSources);
	}

	/**
	 * load the status update allowed
	 * 
	 * @param config
	 *            server configuration
	 * @param graphDatabase
	 *            graph database to store status update version control
	 * @throws IOException
	 *             - IO error
	 * @throws SAXException
	 *             - parse error
	 * @throws ParserConfigurationException
	 *             - DocumentBuilder cannot match the current configuration
	 */
	public static void loadStatusUpdateTemplates(final Configs config,
			final AbstractGraphDatabase graphDatabase)
			throws ParserConfigurationException, SAXException, IOException {
		// load / create status update template manager node
		NODE = new StatusUpdateTemplateManagerNode(graphDatabase);

		// load XML files
		StatusUpdateTemplateFile templateFile;
		StatusUpdateTemplateNode templateNode;
		final File[] xmlFiles = loadXmlFiles(new File(config.templatesPath()));
		for (File xmlFile : xmlFiles) {
			templateFile = new StatusUpdateTemplateFile(xmlFile);
			templateNode = NODE.getStatusUpdateTemplateNode(templateFile
					.getIdentifier());

			if (templateNode != null) {
				if (!templateFile.getVersion()
						.equals(templateNode.getVersion())) {
					// TODO: ask which template shall be used, continue with
					// next file if ignoring the XML file
				}
			} else {
				// create the new template
				templateNode = NODE
						.createStatusUpdateTemplateNode(templateFile);
			}

			generateJavaFiles(templateNode);
			try {
				STATUS_UPDATE_TYPES.put(
						templateNode.getIdentifier(),
						ClassLoader.getSystemClassLoader().loadClass(
								templateNode.getIdentifier()));
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}

			// TODO: create java class file from code for enabled types
			// TODO: add enabled classes to hash map
		}

		// DEBUG
		// STATUS_UPDATE_TYPES.put(PlainText.TYPE_IDENTIFIER, PlainText.class);
	}

	/**
	 * instantiate a status update
	 * 
	 * @param typeIdentifier
	 *            status update type identifier
	 * @param values
	 *            parameter map containing all status update fields necessary
	 * @return status update instance of type specified
	 */
	public static StatusUpdate instantiateStatusUpdate(
			final String typeIdentifier, final Map<String, String[]> values) {
		Class<?> statusUpdateClass = STATUS_UPDATE_TYPES.get(typeIdentifier);

		// check if status update type is existing and allowed
		if (statusUpdateClass != null) {
			try {
				// instantiate status update
				StatusUpdate statusUpdate = (StatusUpdate) statusUpdateClass
						.newInstance();

				// set status update fields
				String[] object;
				for (Field field : statusUpdateClass.getFields()) {
					if (!Modifier.isFinal(field.getModifiers())) {
						object = values.get(field.getName());
						if (object != null) {
							if (object.length == 1) {
								field.set(statusUpdate, object[0]);
							} else {
								throw new IllegalArgumentException("field \""
										+ field.getName() + "\" is corrupted!");
							}
						} else {
							throw new IllegalArgumentException("field \""
									+ field.getName() + "\" is missing!");
						}
					}
				}

				return statusUpdate;
			} catch (SecurityException e) {
				throw new IllegalArgumentException(
						"failed to load the status update type specified!");
			} catch (InstantiationException e) {
				throw new IllegalArgumentException(
						"failed to load the status update type specified!");
			} catch (IllegalAccessException e) {
				throw new IllegalArgumentException(
						"failed to load the status update type specified!");
			}
		} else {
			throw new IllegalArgumentException(
					"status update type not existing or allowed!");
		}
	}

}