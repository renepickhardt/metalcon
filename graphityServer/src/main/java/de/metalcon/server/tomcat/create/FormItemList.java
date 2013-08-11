package de.metalcon.server.tomcat.create;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.fileupload.FileItem;

/**
 * list for multi-part server form posts wrapping fields and files
 * 
 * @author sebschlicht
 * 
 */
public class FormItemList {

	/**
	 * form fields
	 */
	private final Map<String, String> fields;

	/**
	 * form files
	 */
	private final Map<String, FormFile> files;

	/**
	 * create an empty multi-part form wrapper
	 */
	public FormItemList() {
		this.fields = new HashMap<String, String>();
		this.files = new HashMap<String, FormFile>();
	}

	/**
	 * add a field to the form list
	 * 
	 * @param key
	 *            field identifier
	 * @param value
	 *            field value
	 * @throws FormItemDoubleUsageException
	 *             if there is a field having the same key
	 */
	public void addField(final String key, final String value)
			throws FormItemDoubleUsageException {
		if (this.fields.containsKey(key)) {
			throw new FormItemDoubleUsageException("field \"" + key
					+ "\" already existing!");
		}
		this.fields.put(key, value);
	}

	/**
	 * add a file to the form list
	 * 
	 * @param key
	 *            file identifier
	 * @param file
	 *            file item
	 * @throws FormItemDoubleUsageException
	 *             if there is a file having the same key
	 */
	public void addFile(final String key, final FileItem file)
			throws FormItemDoubleUsageException {
		if (this.files.containsKey(key)) {
			throw new FormItemDoubleUsageException("file \"" + key
					+ "\" already existing!");
		}
		this.files.put(key, new FormFile(file));
	}

	/**
	 * get a field from the form list
	 * 
	 * @param key
	 *            field identifier
	 * @return field value
	 * @throws IllegalArgumentException
	 *             if there is no field with such identifier
	 */
	public String getField(final String key) {
		if (this.fields.containsKey(key)) {
			return this.fields.get(key);
		}
		throw new IllegalArgumentException("field \"" + key + "\" is missing!");
	}

	/**
	 * get a file from the form list
	 * 
	 * @param key
	 *            file identifier
	 * @return form file
	 * @throws IllegalArgumentException
	 *             if there is no file with such identifier
	 */
	public FormFile getFile(final String key) {
		if (this.files.containsKey(key)) {
			return this.files.get(key);
		}
		throw new IllegalArgumentException("file \"" + key + "\" is missing!");
	}

	/**
	 * get a set containing all file identifiers
	 * 
	 * @return
	 */
	public Set<String> getFileIdentifiers() {
		return this.files.keySet();
	}

}