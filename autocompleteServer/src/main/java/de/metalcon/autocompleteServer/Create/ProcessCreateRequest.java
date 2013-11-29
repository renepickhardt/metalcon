package de.metalcon.autocompleteServer.Create;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;

import de.metalcon.autocompleteServer.Helper.ContextListener;
import de.metalcon.autocompleteServer.Helper.ProtocolConstants;
import de.metalcon.utils.FormFile;
import de.metalcon.utils.FormItemList;

/**
 * 
 * @author Christian Schowalter
 * 
 */
public class ProcessCreateRequest {

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
	public static ProcessCreateResponse handleServlet(
			HttpServletRequest request, ServletContext context) {
		ProcessCreateResponse response = new ProcessCreateResponse(context);

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
	public static ProcessCreateResponse checkRequestParameter(
			FormItemList items, ProcessCreateResponse response,
			ServletContext context) {
		statusOk = true;
		CreateRequestContainer suggestTreeCreateRequestContainer = new CreateRequestContainer(
				context);

		// When Protocol requirements are not met, response is returned to show
		// error message and also inhibit creating corrupt entries.
		String suggestionString = checkSuggestionString(items, response);
		if (suggestionString == null) {
			suggestTreeCreateRequestContainer = null;
			return response;
		}
		response.addSuggestStringToContainer(suggestionString);
		suggestTreeCreateRequestContainer.getComponents().setSuggestString(
				suggestionString);

		String indexName = checkIndexName(context, items, response);
		if (indexName == null) {
			suggestTreeCreateRequestContainer = null;
			return response;
		}

		suggestTreeCreateRequestContainer.getComponents().setIndexName(
				indexName);

		String suggestionKey = checkSuggestionKey(items, response);
		suggestTreeCreateRequestContainer.getComponents().setKey(suggestionKey);

		Integer weight = checkWeight(items, response);
		if (weight == null) {
			suggestTreeCreateRequestContainer = null;
			return response;
		}
		suggestTreeCreateRequestContainer.getComponents().setWeight(weight);

		if (suggestionKey != null) {
			String image = checkImage(items, response);
			if (image == null) {
				statusOk = false;
				response.addNoImageWarning(CreateStatusCodes.NO_IMAGE);
			}

			suggestTreeCreateRequestContainer.getComponents().setImageBase64(
					image);
		}
		if (statusOk) {
			response.addStatusOk(CreateStatusCodes.STATUS_OK);
		}
		response.addContainer(suggestTreeCreateRequestContainer);

		return response;
	}

	private static String checkImage(FormItemList items,
			ProcessCreateResponse response) {
		FormFile image = null;
		String imageB64 = null;
		FileItem imageFileItem;
		try {
			image = items.getFile(ProtocolConstants.IMAGE);
			imageFileItem = image.getFormItem();

			BufferedImage bufferedImage = ImageIO.read(imageFileItem
					.getInputStream());
			if ((bufferedImage.getWidth() > ProtocolConstants.IMAGE_WIDTH)
					|| (bufferedImage.getHeight() > ProtocolConstants.IMAGE_HEIGHT)) {
				response.addImageGeometryTooBigWarning(CreateStatusCodes.IMAGE_GEOMETRY_TOO_BIG);
				return null;
			}

			if (imageFileItem.getSize() > ProtocolConstants.MAX_IMAGE_FILE_LENGTH) {
				response.addImageFileSizeTooBigWarning(CreateStatusCodes.IMAGE_FILE_TOO_LARGE);
				return null;
			}

		} catch (IllegalArgumentException e) {
			statusOk = false;
			return null;
		} catch (IOException e) {
			statusOk = false;

			return null;
		} catch (NullPointerException e) {
			statusOk = false;
			response.addNoImageWarning(CreateStatusCodes.NO_IMAGE);
			return null;
		}

		byte[] buffer = new byte[(int) imageFileItem.getSize()];
		byte[] tmp = null;
		try {
			int size = imageFileItem.getInputStream().read(buffer);
			if (size > 0) {
				tmp = new byte[size];
				for (int i = 0; i < size; i++) {
					tmp[i] = buffer[i];
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (tmp != null) {
			byte[] base64EncodedImage = Base64.encodeBase64(tmp);
			imageB64 = new String(base64EncodedImage);
		} else {
			statusOk = false;
			return null;
		}

		return "data:image/jpg;base64," + imageB64;
	}

	private static Integer checkWeight(FormItemList items,
			ProcessCreateResponse response) {
		String weight = null;
		try {
			weight = items.getField(ProtocolConstants.SUGGESTION_WEIGHT);
		} catch (IllegalArgumentException e) {
			response.addWeightNotGivenError(CreateStatusCodes.WEIGHT_NOT_GIVEN);
			statusOk = false;
			return null;
		}

		try {
			return Integer.parseInt(weight);
		} catch (NumberFormatException e) {
			response.addWeightNotANumberError(CreateStatusCodes.WEIGHT_NOT_A_NUMBER);
			statusOk = false;
			return null;
		}
	}

	private static String checkIndexName(ServletContext context,
			FormItemList items, ProcessCreateResponse response) {
		String indexName = null;
		try {
			indexName = items.getField(ProtocolConstants.INDEX_PARAMETER);
		} catch (IllegalArgumentException e) {
			response.addDefaultIndexWarning(CreateStatusCodes.INDEXNAME_NOT_GIVEN);
			indexName = ProtocolConstants.DEFAULT_INDEX_NAME;
			statusOk = false;
		}

		// check if the index we're going to write to exists
		if (ContextListener.getIndex(indexName, context) == null) {
			response.addIndexDoesNotExistError(indexName);
			statusOk = false;
			return null;
		}
		return indexName;
	}

	private static String checkSuggestionKey(FormItemList items,
			ProcessCreateResponse response) {
		String key = null;
		try {
			key = items.getField(ProtocolConstants.SUGGESTION_KEY);
		}

		catch (IllegalArgumentException e) {
			response.addNoKeyWarning(CreateStatusCodes.SUGGESTION_KEY_NOT_GIVEN);
			statusOk = false;
			return null;
		}
		//
		if (key.length() > ProtocolConstants.MAX_KEY_LENGTH) {
			response.addKeyTooLongWarning(CreateStatusCodes.KEY_TOO_LONG);
			statusOk = false;
			return null;
		}
		return key;
	}

	private static String checkSuggestionString(FormItemList items,
			ProcessCreateResponse response) {
		String suggestString = null;
		try {
			suggestString = items.getField(ProtocolConstants.SUGGESTION_STRING);
		} catch (IllegalArgumentException e) {
			response.addQueryNameMissingError(CreateStatusCodes.QUERYNAME_NOT_GIVEN);
			statusOk = false;
			return null;
		}
		if (suggestString.length() > ProtocolConstants.MAX_SUGGESTION_LENGTH) {
			response.addQueryNameTooLongError(CreateStatusCodes.SUGGESTION_STRING_TOO_LONG);
			statusOk = false;
			return null;
		}
		return suggestString;
	}

}
