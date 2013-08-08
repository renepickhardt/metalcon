package de.metalcon.autocompleteServer.Create;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import de.metalcon.autocompleteServer.Helper.ProtocolConstants;

public class CreateServlet extends HttpServlet {

	public CreateServlet() {
		// TODO Auto-generated constructor stub
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
		} catch (final FileUploadException e) {
			// TODO make status message instead of STrace
			e.printStackTrace();
		} catch (final FormItemDoubleUsageException e) {
			// TODO make status message instead of STrace
			e.printStackTrace();
		}

		return formItems;
	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException {

		try {
			final ClientResponder responder = new TomcatClientResponder(
					response);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// this check can be removed, if the protocol is optimistic (yet to be
		// specified!)
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		if (isMultipart) {
			try {
				final FormItemList items = this.extractFormItems(request);

				final String term = items
						.getField(ProtocolConstants.QUERY_PARAMETER);

				// } catch (final FileUploadException e) {
				// TODO: append status message, continue processing
				// } catch (final FormItemDoubleUsageException e) {
				// TODO: append error message, abort processing
			} catch (final IllegalArgumentException e) {
				// TODO: append error message, abort processing
			}
		} else {
			// TODO no-Multipart warning, abort request processing
		}
	}
}
