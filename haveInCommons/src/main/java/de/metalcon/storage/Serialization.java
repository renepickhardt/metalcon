package de.metalcon.storage;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

/**
 * @author Jonas Kunze
 */
public class Serialization {

	/**
	 * Deserializes the content of the given file and returns the deserialized
	 * object
	 * 
	 * @param fileName
	 *            The pathname to the file to be deserialized
	 * @return The deserialized object or null if the file was not found
	 */
	public static Object readObjectFromFile(final String fileName) {
		try (InputStream file = new FileInputStream(fileName);
				InputStream buffer = new BufferedInputStream(file);
				ObjectInput input = new ObjectInputStream(buffer);) {
			return input.readObject();
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
		}
		return null;
	}

	/**
	 * Serializes obj and writes it into the file with the pathname given by
	 * <code>fileName</code>
	 * 
	 * @param obj
	 *            The object to be serialized
	 * @param fileName
	 *            The file obj should be written to
	 * @return true in case of success
	 */
	public static boolean writeObjectToFile(final Object obj,
			final String fileName) {
		try (OutputStream file = new FileOutputStream(fileName);
				OutputStream buffer = new BufferedOutputStream(file);
				ObjectOutput output = new ObjectOutputStream(buffer);) {
			output.writeObject(obj);
			return true;
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return false;
	}
}
