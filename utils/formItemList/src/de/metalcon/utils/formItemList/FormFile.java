package de.metalcon.utils.formItemList;

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
	 * form item extracted
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

	/**
	 * @return form item extracted
	 */
	public FileItem getFormItem() {
		return this.formItem;
	}

	/**
	 * @return file handle for local file
	 */
	public File getFile() {
		return this.file;
	}

	/**
	 * @param file
	 *            file handle for local file
	 */
	public void setFile(final File file) {
		this.file = file;
	}

	/**
	 * @return content type of the file sent
	 */
	public String getContentType() {
		return this.formItem.getContentType();
	}

	/**
	 * @return original file name (file name on client machine)
	 */
	public String getOriginalFileName() {
		return this.formItem.getName();
	}

	/**
	 * @return absolute path of the local file
	 */
	public String getAbsoluteFilePath() {
		return this.file.getAbsolutePath();
	}

}