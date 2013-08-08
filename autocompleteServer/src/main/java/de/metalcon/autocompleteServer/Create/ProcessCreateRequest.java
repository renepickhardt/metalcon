package de.metalcon.autocompleteServer.Create;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 * 
 * @author Christian Schowalter
 * 
 */
public class ProcessCreateRequest {

	public void checkRequestParameter(HttpServletRequest request) {
		// checkContentType(); Accept must be text/x-json.

		// Uncomment this check for more paranoid behavior (yet to be
		// specified!)
		// boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		// if (isMultipart) {}

	}

	private final DiskFileItemFactory factory = new DiskFileItemFactory();

	private FormItemList extractFormItems(final HttpServletRequest request) {
		final ServletFileUpload upload = new ServletFileUpload(this.factory);
		final FormItemList formItems = new FormItemList();

		try {
			for (FileItem item : upload.parseRequest(request)) {
				if (item.isFormField()) {
					formItems.addField(item.getFieldName(), item.getString());
				} else {
					formItems.addFile(item.getFieldName(), item);
				}
			}
		} catch (FileUploadException e) {
			// TODO make status message instead of STrace
			e.printStackTrace();
		} catch (FormItemDoubleUsageException e) {
			// TODO make status message instead of STrace
			e.printStackTrace();
		}

		return formItems;
	}

	private static void checkImage(HttpServletRequest request) {
		// This method needs a library to analyze JPEGs concerning their size.

	}

	private static void checkIndex(HttpServletRequest request) {
		// Check if index is provided. If not, use default and respond
	}

	private static void checkKey(HttpServletRequest request) {
		// Check if key is provided. Wait, is this even necessary?
	}

	private static void checkTerm(HttpServletRequest request) {
		// Check if term is provided. If not, abort and respond

	}

}
