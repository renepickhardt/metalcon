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
import java.net.URL;
import java.net.URLClassLoader;
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
	 * compiler working directory
	 */
	private static String WORKING_DIR;

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
		final String javaPath = WORKING_DIR + templateNode.getIdentifier()
				+ ".java";
		final File javaFile = new File(javaPath);
		final String[] optionsAndSources = { "-proc:none", "-source", "1.7",
				"-target", "1.7", javaPath };

		// write java file
		final FileOutputStream javaOutput = new FileOutputStream(javaFile);
		final PrintWriter writer = new PrintWriter(javaOutput);
		writer.write(templateNode.getCode());
		writer.flush();
		writer.close();

		// TODO: delete
		final FileInputStream javaInput = new FileInputStream(javaFile);

		// delete previous class file
		final File classFile = new File(WORKING_DIR
				+ templateNode.getIdentifier() + ".class");
		classFile.delete();

		// compile java file to class file
		final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		compiler.run(javaInput, System.out, System.err, optionsAndSources);

		// delete java file
		javaFile.delete();
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
		// set working directory
		final String targetPackage = StatusUpdate.class.getPackage().getName()
				+ ".templates.";
		WORKING_DIR = "target/classes/" + targetPackage.replace(".", "/");

		// create class loader
		final URLClassLoader loader = new URLClassLoader(new URL[] { new File(
				WORKING_DIR).toURI().toURL() });

		// load / create status update template manager node
		NODE = new StatusUpdateTemplateManagerNode(graphDatabase);

		// crawl XML files
		StatusUpdateTemplateFile templateFile;
		StatusUpdateTemplateNode templateNode;
		final File[] xmlFiles = loadXmlFiles(new File(config.templatesPath()));
		for (File xmlFile : xmlFiles) {
			templateFile = new StatusUpdateTemplateFile(xmlFile);
			templateNode = NODE.getStatusUpdateTemplateNode(templateFile
					.getIdentifier());

			// check for previous template versions
			if (templateNode != null) {
				if (!templateFile.getVersion()
						.equals(templateNode.getVersion())) {
					// TODO: ask which template shall be used, continue with
					// next file if ignoring the XML file
				}
			}

			// create the new template (referring to previous versions)
			templateNode = NODE.createStatusUpdateTemplateNode(templateFile);

			// generate class file
			generateJavaFiles(templateNode);

			// register the new template
			try {
				STATUS_UPDATE_TYPES.put(
						templateNode.getIdentifier(),
						loader.loadClass(targetPackage
								+ templateNode.getIdentifier()));
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		loader.close();
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