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

	private final File file;

	public ImageFileItem(File file) {
		super();
		this.file = file;
	}

	// private final FileInputStream fileInputStream;

	@Override
	public FileItemHeaders getHeaders() {
		return null;
	}

	@Override
	public void setHeaders(FileItemHeaders arg0) {

	}

	@Override
	public void delete() {

	}

	@Override
	public byte[] get() {
		return null;
	}

	@Override
	public String getContentType() {
		return null;
	}

	@Override
	public String getFieldName() {
		return null;
	}

	@Override
	public InputStream getInputStream() throws IOException {

		return new FileInputStream(this.file);
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		return null;
	}

	@Override
	public long getSize() {
		return 0;
	}

	@Override
	public String getString() {
		return null;
	}

	@Override
	public String getString(String arg0) throws UnsupportedEncodingException {
		return null;
	}

	@Override
	public boolean isFormField() {
		return false;
	}

	@Override
	public boolean isInMemory() {
		return false;
	}

	@Override
	public void setFieldName(String arg0) {

	}

	@Override
	public void setFormField(boolean arg0) {

	}

	@Override
	public void write(File arg0) throws Exception {

	}

}
