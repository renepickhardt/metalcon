package de.uniko.west.socialsensor.graphity.server.statusupdates.templates;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class StatusUpdateManager {

	private static Map<String, Class<?>> STATUS_UPDATE_TEMPLATES = new HashMap<String, Class<?>>();

	public static void loadStatusUpdateTemplates() {
		// TODO
		// load XML files
		// check for existing versions in database
		// prompt for confirmation when overriding existing types
		// generate .class files of enabled types
		// load enabled types into hash map

		// DEBUG
		STATUS_UPDATE_TEMPLATES.put(PlainText.TYPE_IDENTIFIER, PlainText.class);
	}

	public static StatusUpdate instantiateStatusUpdate(
			final String typeIdentifier, final Map<String, String[]> values) {
		Class<?> statusUpdateClass = STATUS_UPDATE_TEMPLATES
				.get(typeIdentifier);

		if (statusUpdateClass != null) {
			try {
				StatusUpdate statusUpdate = (StatusUpdate) statusUpdateClass
						.newInstance();

				String[] object;
				for (Field field : statusUpdateClass.getFields()) {
					object = values.get(field.getName());
					if (object != null) {
						if (object.length == 1) {
							field.set(statusUpdate,
									field.getClass().cast(object[0]));
						}
					} else {
						throw new IllegalArgumentException("field \""
								+ field.getName() + "\" is missing!");
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