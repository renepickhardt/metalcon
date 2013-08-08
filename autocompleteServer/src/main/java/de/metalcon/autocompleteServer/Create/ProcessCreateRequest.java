package de.metalcon.autocompleteServer.Create;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import de.metalcon.autocompleteServer.Helper.ProtocolConstants;

/**
 * 
 * @author Christian Schowalter
 * 
 */
public class ProcessCreateRequest {
	private static final DiskFileItemFactory factory = new DiskFileItemFactory();

	public static ProcessCreateResponse checkRequestParameter(
			HttpServletRequest request) {
		ProcessCreateResponse response = new ProcessCreateResponse();
		if (!checkIsMultiPart(request, response)) {
			return response;
		}
		final FormItemList items = extractFormItems(request);
		String suggestionString = checkSuggestionString(items, response);
		if (suggestionString == null) {
			return response;
		}
		response.addSuggestStringToContainer(suggestionString);

		String indexName = checkIndexName(items, response);

		String suggestionKey = checkSuggestionKey(items, response);

		Integer weight = checkWeight(items, response);
		return response;
	}

	private static Integer checkWeight(FormItemList items,
			ProcessCreateResponse response) {
		String weight = items.getField(ProtocolConstants.SUGGESTION_WEIGHT);
		// TODO Auto-generated method stub
		return null;
	}

	private static String checkIndexName(FormItemList items,
			ProcessCreateResponse response) {
		// TODO Auto-generated method stub
		return null;
	}

	private static String checkSuggestionKey(FormItemList items,
			ProcessCreateResponse response) {
		String key = items.getField(ProtocolConstants.SUGGESTION_KEY);
		// TODO Auto-generated method stub
		return null;
	}

	private static String checkSuggestionString(FormItemList items,
			ProcessCreateResponse response) {
		String suggestString = null;
		// check is the field exists
		try {
			suggestString = items.getField(ProtocolConstants.SUGGESTION_STRING);
		} catch (IllegalArgumentException e) {
			// TODO: RefactorName
			response.addError(CreateStatusCodes.QUERYNAME_NOT_GIVEN);
			return null;
		}
		// check if the length matches ASTP requirements
		// TODO: add Strings!
		if (suggestString.length() > ProtocolConstants.SUGGESTION_LENGTH) {
			response.addError(null);
		}
		return null;
	}

	// checkContentType(); Accept must be text/x-json.

	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	private static boolean checkIsMultiPart(HttpServletRequest request,
			ProcessCreateResponse response) {
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		if (isMultipart) {
			return true;
		}
		response.addError(CreateStatusCodes.REQUEST_MUST_BE_MULTIPART);
		return false;
	}

	private static FormItemList extractFormItems(
			final HttpServletRequest request) {
		final ServletFileUpload upload = new ServletFileUpload(factory);
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

}
