package de.metalcon.server.statusupdates;

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

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.kernel.AbstractGraphDatabase;
import org.xml.sax.SAXException;

import de.metalcon.server.exceptions.StatusUpdateInstantiationFailedException;
import de.metalcon.socialgraph.Configuration;
import de.metalcon.socialgraph.NeoUtils;
import de.metalcon.socialgraph.SocialGraphRelationshipType;
import de.metalcon.utils.FormItemList;

/**
 * status update manager to load
 * 
 * @author Sebastian Schlicht
 * 
 */
public class StatusUpdateManager {

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
	 * @param workingPath
	 *            working path including JAVA and/or CLASS-files
	 * @throws FileNotFoundException
	 */
	private static void generateJavaFiles(final StatusUpdateTemplate template,
			final String workingPath) throws FileNotFoundException {
		final String javaPath = WORKING_DIR + template.getName() + ".java";
		final File javaFile = new File(javaPath);
		final String[] optionsAndSources = { "-classpath", workingPath,
				"-proc:none", "-source", "1.7", "-target", "1.7", javaPath };

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
	 * store the status update template linking to the latest previous version
	 * 
	 * @param graphDatabase
	 *            social graph database to store the template in
	 * @param template
	 *            status update template
	 * @param previousTemplateNode
	 *            template node of the latest previous version
	 */
	private static void storeStatusUpdateTemplate(
			final AbstractGraphDatabase graphDatabase,
			final StatusUpdateTemplate template, final Node previousTemplateNode) {
		final Transaction transaction = graphDatabase.beginTx();
		try {
			// create template node
			final Node templateNode = StatusUpdateTemplate.createTemplateNode(
					graphDatabase, template);

			// link to the latest previous version
			if (previousTemplateNode != null) {
				templateNode.createRelationshipTo(previousTemplateNode,
						SocialGraphRelationshipType.Templates.PREVIOUS);
			}

			// store the template node
			NeoUtils.storeStatusUpdateTemplateNode(graphDatabase,
					template.getName(), templateNode, previousTemplateNode);

			transaction.success();
		} finally {
			transaction.finish();
		}
	}

	/**
	 * load the status update allowed
	 * 
	 * @param rootDir
	 *            classes directory parent
	 * @param config
	 *            Graphity configuration
	 * @param graphDatabase
	 *            graph database to store status update version control
	 * @throws IOException
	 *             - IO error
	 * @throws SAXException
	 *             - parse error
	 * @throws ParserConfigurationException
	 *             - DocumentBuilder cannot match the current configuration
	 */
	public static void loadStatusUpdateTemplates(final String rootDir,
			final Configuration config,
			final AbstractGraphDatabase graphDatabase)
			throws ParserConfigurationException, SAXException, IOException {
		// set working directory
		final String workingPath = rootDir + "classes/";
		final String targetPackage = StatusUpdate.class.getPackage().getName()
				+ ".templates.";
		WORKING_DIR = workingPath + targetPackage.replace(".", "/");
		final File dirWorking = new File(WORKING_DIR);
		if (!dirWorking.exists()) {
			dirWorking.mkdir();
		}
		System.out.println("WORKING_DIR:" + WORKING_DIR);

		// create class loader
		final ClassLoader classLoader = StatusUpdate.class.getClassLoader();
		final URLClassLoader loader = new URLClassLoader(new URL[] { new File(
				workingPath).toURI().toURL() }, classLoader);

		// crawl XML files
		StatusUpdateTemplate template, previousTemplate;
		final File templatesDir = new File(config.getTemplatesPath());
		if (!templatesDir.exists()) {
			templatesDir.mkdir();
		}
		File[] xmlFiles = loadXmlFiles(templatesDir);

		// create basic status update template
		if (xmlFiles.length == 0) {
			final File plainTemplate = new File(config.getTemplatesPath()
					+ "Plain.xml");
			final PrintWriter writer = new PrintWriter(plainTemplate);
			writer.println("<class name=\"Plain\" version=\"1.0\">");
			writer.println("<param name=\"message\" type=\"String\" />");
			writer.println("</class>");
			writer.flush();
			writer.close();
			xmlFiles = loadXmlFiles(new File(config.getTemplatesPath()));
		}

		System.out.println("TEMPLATES:" + xmlFiles.length);
		for (File xmlFile : xmlFiles) {
			template = new StatusUpdateTemplate(xmlFile);
			final Node previousTemplateNode = NeoUtils
					.getStatusUpdateTemplateByIdentifier(template.getName());

			if (previousTemplateNode != null) {
				previousTemplate = new StatusUpdateTemplate(
						previousTemplateNode);
			} else {
				previousTemplate = null;
			}

			// check for previous template versions
			if (previousTemplate != null) {
				if (!template.getVersion()
						.equals(previousTemplate.getVersion())) {
					// TODO: ask which template shall be used, for the moment
					// overwrite previous versions
				}
			}

			// store the new template
			storeStatusUpdateTemplate(graphDatabase, template,
					previousTemplateNode);

			// generate class file
			generateJavaFiles(template, workingPath);

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
	 * @return status update template<br>
	 *         <b>null</b> if there is no template with such name
	 */
	public static StatusUpdateTemplate getStatusUpdateTemplate(
			final String templateName) {
		return STATUS_UPDATE_TYPES.get(templateName);
	}

	/**
	 * instantiate a status update
	 * 
	 * @param typeIdentifier
	 *            status update type identifier
	 * @param values
	 *            form item list containing all status update items necessary
	 * @return status update instance of type specified
	 * @throws StatusUpdateInstantiationFailedException
	 *             if the status update could not be instantiated with the
	 *             parameters passed
	 * @throws IllegalArgumentException
	 *             if template internal errors occur
	 */
	public static StatusUpdate instantiateStatusUpdate(
			final String typeIdentifier, final FormItemList values)
			throws StatusUpdateInstantiationFailedException {
		final StatusUpdateTemplate template = getStatusUpdateTemplate(typeIdentifier);
		final Class<?> templateInstantiationClass = template
				.getInstantiationClass();

		try {
			// instantiate status update
			final StatusUpdate statusUpdate = (StatusUpdate) templateInstantiationClass
					.newInstance();

			// set status update fields
			String value;
			try {
				for (String fieldName : template.getFields().keySet()) {
					try {
						value = values.getField(fieldName);
					} catch (final IllegalArgumentException e) {
						throw new StatusUpdateInstantiationFailedException(
								e.getMessage());
					}
					templateInstantiationClass.getField(fieldName).set(
							statusUpdate, value);
				}

				// set status update file paths
				for (String fileName : template.getFiles().keySet()) {
					try {
						value = values.getFile(fileName).getAbsoluteFilePath();
					} catch (final IllegalArgumentException e) {
						throw new StatusUpdateInstantiationFailedException(
								e.getMessage());
					}
					templateInstantiationClass.getField(fileName).set(
							statusUpdate, value);
				}
			} catch (final IllegalArgumentException e) {
				throw new StatusUpdateInstantiationFailedException(
						"The types of the parameters passed do not match the status update template.");
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