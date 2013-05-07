package de.uniko.west.socialsensor.graphity.server.statusupdates.templates;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import org.neo4j.kernel.AbstractGraphDatabase;

/**
 * status update manager to load
 * 
 * @author Sebastian Schlicht
 * 
 */
public class StatusUpdateManager {

	/**
	 * status update classes allowed
	 */
	private static Map<String, Class<?>> STATUS_UPDATE_TYPES = new HashMap<String, Class<?>>();

	/**
	 * load the status update allowed
	 * 
	 * @param graphDatabase
	 *            graph database to store status update version control
	 */
	public static void loadStatusUpdateTemplates(
			final AbstractGraphDatabase graphDatabase) {
		// TODO
		// load XML files
		// check for existing versions in database
		// prompt for confirmation when overriding existing types
		// generate .class files of enabled types
		// load enabled types into hash map

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