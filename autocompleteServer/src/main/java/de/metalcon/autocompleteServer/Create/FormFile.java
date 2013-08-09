package de.metalcon.autocompleteServer.Create;

import java.io.File;

import org.apache.commons.fileupload.FileItem;

/**
 * status update creation form file wrapper
 * 
 * @author sebschlicht
 * 
 */
public class FormFile {

	/**
	 * form item
	 */
	private final FileItem formItem;

	/**
	 * physical file on disc
	 */
	private File file;

	/**
	 * create a new form file wrapper
	 * 
	 * @param formItem
	 *            form item
	 */
	public FormFile(final FileItem formItem) {
		this.formItem = formItem;
	}

	public FileItem getFormItem() {
		return this.formItem;
	}

	public File getFile() {
		return this.file;
	}

	public void setFile(final File file) {
		this.file = file;
	}

	public String getContentType() {
		return this.formItem.getContentType();
	}

	public String getOriginalFileName() {
		return this.formItem.getName();
	}

	public String getAbsoluteFilePath() {
		return this.file.getAbsolutePath();
	}

}