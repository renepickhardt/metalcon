package de.metalcon.utils.messages;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;

public class Messages {
	static {
		Messages.initialize(MethodHandles.lookup().lookupClass());
	}

	/**
	 * This method is used to load all Strings from a resource properties file
	 * into the associated class variables
	 * 
	 * @param c
	 *            The class to be initialized
	 */
	protected static void initialize(Class c) {
		/*
		 * Strings devinde in the poperties file
		 */
		ResourceBundle labels = ResourceBundle.getBundle(c.getName(),
				Locale.ENGLISH);

		Enumeration<String> bundleKeys = labels.getKeys();
		int numberOfStringInResourceFile = 0;
		while (bundleKeys.hasMoreElements()) {
			bundleKeys.nextElement();
			numberOfStringInResourceFile++;
		}

		/*
		 * Fields defined in c
		 */
		Field[] fields = c.getDeclaredFields();
		if (fields.length != numberOfStringInResourceFile) {
			System.err
					.println("Unable to initialize "
							+ c.getName()
							+ ": The number of class fields and entries in the resource bundle file must be equal.");
			System.exit(1);
		}
		for (Field f : fields) {
			try {
				f.set(null, labels.getString(f.getName()));
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
				System.exit(1);
			}

		}
	}

	public static void main(String[] args) {

	}
}
