package de.uniko.west.socialsensor.graphity.server.statusupdates;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
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
import de.uniko.west.socialsensor.graphity.server.tomcat.create.FormItemList;

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
	 * status update templates loaded
	 */
	private static Map<String, StatusUpdateTemplate> STATUS_UPDATE_TYPES = new HashMap<String, StatusUpdateTemplate>();

	/**
	 * working directory for template generation
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

	/**
	 * generate a java class file for the template specified
	 * 
	 * @param template
	 *            status update template
	 * @throws FileNotFoundException
	 */
	private static void generateJavaFiles(final StatusUpdateTemplate template,
			final Configs config) throws FileNotFoundException {
		final String javaPath = WORKING_DIR + template.getName() + ".java";
		final File javaFile = new File(javaPath);
		final String[] optionsAndSources = { "-classpath",
				config.workingPath(), "-proc:none", "-source", "1.7",
				"-target", "1.7", javaPath };

		// write java file
		final PrintWriter writer = new PrintWriter(javaFile);
		writer.write(template.getJavaCode());
		writer.flush();
		writer.close();

		// delete previous class file
		final File classFile = new File(WORKING_DIR + template.getName()
				+ ".class");
		classFile.delete();

		// compile java file to class file
		final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		compiler.run(System.in, System.out, System.err, optionsAndSources);

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
		WORKING_DIR = config.workingPath() + targetPackage.replace(".", "/");
		final File dirWorking = new File(WORKING_DIR);
		if (!dirWorking.exists()) {
			dirWorking.mkdir();
		}
		System.out.println("WORKING_DIR:" + WORKING_DIR);

		// create class loader
		final ClassLoader classLoader = StatusUpdate.class.getClassLoader();
		final URLClassLoader loader = new URLClassLoader(new URL[] { new File(
				config.workingPath()).toURI().toURL() }, classLoader);

		// load / create status update template manager node
		NODE = new StatusUpdateTemplateManagerNode(graphDatabase);

		// crawl XML files
		StatusUpdateTemplate template;
		final File[] xmlFiles = loadXmlFiles(new File(config.templatesPath()));
		System.out.println("TEMPLATES:" + xmlFiles.length);
		for (File xmlFile : xmlFiles) {
			template = new StatusUpdateTemplate(xmlFile);
			final StatusUpdateTemplate previousTemplate = NODE
					.getStatusUpdateTemplate(template.getName());

			// check for previous template versions
			if (previousTemplate != null) {
				if (!template.getVersion()
						.equals(previousTemplate.getVersion())) {
					// TODO: ask which template shall be used, for the moment
					// overwrite previous versions
				}
			}

			// create the new template (referring to previous versions)
			NODE.storeStatusUpdateTemplate(template);

			// generate class file
			generateJavaFiles(template, config);

			// register the new template
			try {
				template.setInstantiationClass(loader.loadClass(targetPackage
						+ template.getName()));
				STATUS_UPDATE_TYPES.put(template.getName(), template);
			} catch (final ClassNotFoundException e) {
				e.printStackTrace();
			}
		}

		loader.close();
	}

	/**
	 * get the status update template with the name passed
	 * 
	 * @param templateName
	 *            name of status update template being searched
	 * @return status update template
	 * @throws IllegalArgumentException
	 *             if there is no status update having the name passed
	 */
	public static StatusUpdateTemplate getStatusUpdateTemplate(
			final String templateName) {
		final StatusUpdateTemplate template = STATUS_UPDATE_TYPES
				.get(templateName);
		if (template != null) {
			return template;
		}

		throw new IllegalArgumentException("status update type \""
				+ templateName + "\" not existing or allowed!");
	}

	/**
	 * instantiate a status update
	 * 
	 * @param typeIdentifier
	 *            status update type identifier
	 * @param values
	 *            form item list containing all status update items necessary
	 * @return status update instance of type specified
	 */
	public static StatusUpdate instantiateStatusUpdate(
			final String typeIdentifier, final FormItemList values) {
		final StatusUpdateTemplate template = getStatusUpdateTemplate(typeIdentifier);
		final Class<?> templateInstantiationClass = template
				.getInstantiationClass();

		try {
			// instantiate status update
			final StatusUpdate statusUpdate = (StatusUpdate) templateInstantiationClass
					.newInstance();

			// set status update fields
			String value;
			for (String fieldName : template.getFields().keySet()) {
				value = values.getField(fieldName);
				templateInstantiationClass.getField(fieldName).set(
						statusUpdate, value);
			}

			// set status update file paths
			for (String fileName : template.getFiles().keySet()) {
				value = values.getFile(fileName).getAbsoluteFilePath();
				templateInstantiationClass.getField(fileName).set(statusUpdate,
						value);
			}

			return statusUpdate;
		} catch (final SecurityException e) {
			throw new IllegalArgumentException(
					"failed to load the status update type specified, SecurityException occurred!");
		} catch (final InstantiationException e) {
			throw new IllegalArgumentException(
					"failed to load the status update type specified, InstantiationException occurred!");
		} catch (final IllegalAccessException e) {
			throw new IllegalArgumentException(
					"failed to load the status update type specified, IllegalAccessException occurred!");
		} catch (final NoSuchFieldException e) {
			throw new IllegalArgumentException(
					"failed to load the status update type specified, NoSuchFieldException occurred!");
		}
	}

}