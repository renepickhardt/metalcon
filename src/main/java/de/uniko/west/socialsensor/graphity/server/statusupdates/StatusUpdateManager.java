package de.uniko.west.socialsensor.graphity.server.statusupdates;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import org.neo4j.kernel.AbstractGraphDatabase;

import de.uniko.west.socialsensor.graphity.server.Configs;
import de.uniko.west.socialsensor.graphity.server.statusupdates.templates.PlainText;

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

	/**
	 * load the status update allowed
	 * 
	 * @param config
	 *            server configuration
	 * @param graphDatabase
	 *            graph database to store status update version control
	 */
	public static void loadStatusUpdateTemplates(final Configs config,
			final AbstractGraphDatabase graphDatabase) {
		// load / create status update template manager node
		NODE = new StatusUpdateTemplateManagerNode(graphDatabase);

		// load XML files
		StatusUpdateTemplateFile templateFile;
		StatusUpdateTemplateNode templateNode;
		final File[] xmlFiles = loadXmlFiles(new File(config.templatesPath()));
		for (File xmlFile : xmlFiles) {
			templateFile = new StatusUpdateTemplateFile(xmlFile);
			templateNode = NODE.getStatusUpdateTemplateNode(templateFile
					.getName());

			if (templateNode != null) {
				if (!templateFile.getVersion()
						.equals(templateNode.getVersion())) {
					// TODO: ask which template shall be used, continue with
					// next file if ignoring the XML file
				}
			} else {
				templateNode = NODE
						.createStatusUpdateTemplateNode(templateFile);
			}

			// TODO: create java code from XML file, store code in node
			// TODO: create java class file from code for enabled types
			// TODO: add enabled classes to hash map
		}

		// DEBUG
		STATUS_UPDATE_TYPES.put(PlainText.TYPE_IDENTIFIER, PlainText.class);
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