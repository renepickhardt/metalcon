package de.metalcon.autocompleteServer.Create;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemHeaders;

public class ImageFileItem implements FileItem {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8355853836800944661L;
	private final File file;

	public ImageFileItem(File file) {
		this.file = file;
	}

	// private final FileInputStream fileInputStream;

	@Override
	public FileItemHeaders getHeaders() {
		System.out.println("getHeaders");
		return null;
	}

	@Override
	public void setHeaders(FileItemHeaders arg0) {
		System.out.println("setHeaders");
	}

	@Override
	public void delete() {
		System.out.println("delete");
	}

	@Override
	public byte[] get() {
		System.out.println("get");
		return null;
	}

	@Override
	public String getContentType() {
		System.out.println("getContentType");
		return null;
	}

	@Override
	public String getFieldName() {
		System.out.println("getFieldName");
		return null;
	}

	@Override
	public InputStream getInputStream() throws IOException {

		return new FileInputStream(this.file);
	}

	@Override
	public String getName() {
		System.out.println("getName");
		return null;
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		System.out.println("getOutputStream");
		return null;
	}

	@Override
	public long getSize() {
		return this.file.length();
	}

	@Override
	public String getString() {
		System.out.println("getStrin");
		return null;
	}

	@Override
	public String getString(String arg0) throws UnsupportedEncodingException {
		System.out.println("fetString");
		return null;
	}

	@Override
	public boolean isFormField() {
		System.out.println("isFormField");
		return false;
	}

	@Override
	public boolean isInMemory() {
		System.out.println("isInMem");
		return false;
	}

	@Override
	public void setFieldName(String arg0) {
		System.out.println("setFieldName");
	}

	@Override
	public void setFormField(boolean arg0) {
		System.out.println("setFormField");
	}

	@Override
	public void write(File arg0) throws Exception {
		System.out.println("write");
	}

}
