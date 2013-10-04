package de.metalcon.autocompleteServer.Create;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;

import de.metalcon.autocompleteServer.Helper.ContextListener;
import de.metalcon.autocompleteServer.Helper.ProtocolConstants;
import de.metalcon.utils.FormItemList;

public class NewIndexRequest {

	private static boolean statusOk = true;

	private static final DiskFileItemFactory factory = new DiskFileItemFactory();

	/**
	 * 
	 * processes HTTP-Requests by extracting the form-data. If extraction is
	 * successful, data is passed to checkRequestParameter() which also takes on
	 * generating a response. If extraction fails a response containing an
	 * error-message is returned.
	 * 
	 * @param request
	 * @param context
	 * @author Christian Schowalter
	 */
	public static NewIndexResponse handleServlet(HttpServletRequest request,
			ServletContext context) {
		NewIndexResponse response = new NewIndexResponse(context);

		FormItemList items = null;
		try {
			items = FormItemList.extractFormItems(request, factory);
		} catch (FileUploadException e) {
			e.printStackTrace();
		}

		// items is == null, when request Content-Type is not multipart
		if (items == null) {
			response.addHttpRequestError(CreateStatusCodes.REQUEST_MUST_BE_MULTIPART);
			return response;
		}

		return checkRequestParameter(items, response, context);
	}

	/**
	 * 
	 * Checks extracted form-items for ASTP-compliance. Also generates
	 * Status-Message for the response, which tells if there are any problems
	 * with the given data.
	 * 
	 * @param items
	 * @param response
	 * @param context
	 * @return response
	 */
	public static NewIndexResponse checkRequestParameter(FormItemList items,
			NewIndexResponse response, ServletContext context) {
		statusOk = true;
		NewIndexContainer newIndexContainer = new NewIndexContainer(context);

		// When Protocol requirements are not met, response is returned to show
		// error message and also inhibit creating corrupt entries.
		String indexName = checkIndexName(context, items, response);
		if (indexName != null) {

			response.addIndexNotGivenError(indexName);
			return response;
		}

		if (statusOk) {
			response.addStatusOk(CreateStatusCodes.STATUS_OK);
		}

		response.addContainer(newIndexContainer);

		return response;
	}

	private static String checkIndexName(ServletContext context,
			FormItemList items, NewIndexResponse response) {
		String indexName = null;
		try {
			indexName = items.getField(ProtocolConstants.INDEX_PARAMETER);
		} catch (IllegalArgumentException e) {
			response.addIndexNotGivenError(CreateStatusCodes.NEW_INDEXNAME_NOT_GIVEN);
			indexName = ProtocolConstants.DEFAULT_INDEX_NAME;
			statusOk = false;
		}

		// check if the index we're going to write to exists
		if (ContextListener.getIndex(indexName, context) != null) {
			response.addIndexAlreadyExistsError(indexName);
			statusOk = false;
			return null;
		}
		return indexName;
	}
}
