package de.metalcon.autocompleteServer.Create;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;

import de.metalcon.autocompleteServer.Helper.ProtocolConstants;
import de.metalcon.utils.FormFile;
import de.metalcon.utils.FormItemList;

/**
 * 
 * @author Christian Schowalter
 * 
 */
public class ProcessCreateRequest {

	/**
	 * 
	 */
	private static final DiskFileItemFactory factory = new DiskFileItemFactory();

	public static ProcessCreateResponse handleServlet(
			HttpServletRequest request, ServletContext context) {
		ProcessCreateResponse response = new ProcessCreateResponse(context);

		FormItemList items = null;
		try {
			items = FormItemList.extractFormItems(request, factory);
		} catch (FileUploadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// items is == null, when request is not multipart
		if (items == null) {
			response.addHttpRequestError(CreateStatusCodes.REQUEST_MUST_BE_MULTIPART);
			// TODO double-check if return correct
			return response;
		}

		return checkRequestParameter(items, response, context);
	}

	public static ProcessCreateResponse checkRequestParameter(
			FormItemList items, ProcessCreateResponse response,
			ServletContext context) {
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

		String indexName = checkIndexName(items, response);
		// response.addIndexToContainer(indexName);
		suggestTreeCreateRequestContainer.getComponents().setIndexName(
				indexName);

		String suggestionKey = checkSuggestionKey(items, response);
		// response.addSuggestionKeyToContainer(suggestionKey);
		suggestTreeCreateRequestContainer.getComponents().setKey(suggestionKey);

		Integer weight = checkWeight(items, response);
		if (weight == null) {
			suggestTreeCreateRequestContainer = null;
			return response;
		}
		// response.addWeightToContainer(weight);
		suggestTreeCreateRequestContainer.getComponents().setWeight(weight);

		// Protocol forbids images for suggestions without keys
		// TODO: add this piece of information to nokey-Warning
		if (suggestionKey != null) {
			String image = checkImage(items, response);
			if (image == null) {
				response.addNoImageWarning(CreateStatusCodes.NO_IMAGE);
			}

			suggestTreeCreateRequestContainer.getComponents().setImageBase64(
					image);
		}
		response.addContainer(suggestTreeCreateRequestContainer);

		// TODO remove this line, when the queue is ready.
		// suggestTreeCreateRequestContainer.run(context);

		return response;
	}

	private static String checkImage(FormItemList items,
			ProcessCreateResponse response) {
		FormFile image = null;
		String imageB64 = null;
		FileItem imageFile;
		// FormItem imageFile = null;
		try {
			// TODO: double check, if it works that way on images
			image = items.getFile(ProtocolConstants.IMAGE);
			imageFile = image.getFormItem();
			// imageFile = image.get;
		} catch (IllegalArgumentException e) {
			// response.addNoImageWarning(CreateStatusCodes.NO_IMAGE);
			return null;
		}

		// TODO replace hard-coded value with file size of imageFile objects
		byte[] buffer = new byte[50000];
		byte[] tmp = null;
		try {
			int size = imageFile.getInputStream().read(buffer);
			if (size > 0) {
				// System.out.println(buffer);
				tmp = new byte[size];
				for (int i = 0; i < size; i++) {
					tmp[i] = buffer[i];
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (tmp != null) {
			byte[] base64EncodedImage = Base64.encodeBase64(tmp);
			imageB64 = new String(base64EncodedImage);
		} else {
			return null;
		}

		// BufferedImage bufferedImage = null;
		// FileReader fileReader = null;
		// try {
		// fileReader = new FileReader(imageFile);
		// } catch (IOException e2) {
		// // TODO Auto-generated catch block
		// e2.printStackTrace();
		// return null;
		// }
		// maybe there is a better way for verifying image encoding than MIME?
		// String mimeType = new
		// MimetypesFileTypeMap().getContentType(imageFile);
		// if (!(mimeType.equals("image/JPEG"))) {
		// try {
		// fileReader.close();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		// return null;
		// }
		//
		// long fileLength = imageFile.length();
		// char[] cbuf = null;
		// try {
		// fileReader.read(cbuf, 0, (int) fileLength);
		// fileReader.close();
		// } catch (IOException e1) {
		// e1.printStackTrace();
		// return null;
		// }
		// byte[] rawImageData = new byte[(int) fileLength];
		// for (int i = 0; i < fileLength; i++) {
		// rawImageData[i] = (byte) cbuf[i];
		// }
		// byte[] base64EncodedImage = Base64.encodeBase64(rawImageData);
		// String result = new String(base64EncodedImage);
		//
		// try {
		// ByteArrayInputStream bais = new ByteArrayInputStream(rawImageData);
		// bufferedImage = ImageIO.read(bais);
		// // TODO: maybe less strong
		// if ((ProtocolConstants.IMAGE_WIDTH < bufferedImage.getWidth()) ||
		// (ProtocolConstants.IMAGE_HEIGHT < bufferedImage
		// .getHeight())) {
		// return null;
		// }
		//
		// } catch (IOException e) {
		// e.printStackTrace();
		// return null;
		// }
		System.out.println(imageB64);
		return "data:image/jpg;base64," + imageB64;
	}

	private static Integer checkWeight(FormItemList items,
			ProcessCreateResponse response) {
		String weight = null;
		try {
			weight = items.getField(ProtocolConstants.SUGGESTION_WEIGHT);
		} catch (IllegalArgumentException e) {
			// TODO: RefactorName
			response.addWeightNotGivenError(CreateStatusCodes.WEIGHT_NOT_GIVEN);
			e.printStackTrace();
			return null;
		}

		try {
			return Integer.parseInt(weight);
		}
		// TODO: verify this is the right exception
		catch (NumberFormatException e) {
			response.addWeightNotANumberError(CreateStatusCodes.WEIGHT_NOT_A_NUMBER);
			return null;
		}
	}

	private static String checkIndexName(FormItemList items,
			ProcessCreateResponse response) {
		String index = null;
		try {
			index = items.getField(ProtocolConstants.INDEX_PARAMETER);
		} catch (IllegalArgumentException e) {
			response.addDefaultIndexWarning(CreateStatusCodes.INDEXNAME_NOT_GIVEN);
			index = ProtocolConstants.DEFAULT_INDEX_NAME;
		}
		return index;
	}

	private static String checkSuggestionKey(FormItemList items,
			ProcessCreateResponse response) {
		String key = null;
		try {
			key = items.getField(ProtocolConstants.SUGGESTION_KEY);
		}

		// this exception is no reason to abort processing!
		catch (IllegalArgumentException e) {
			response.addNoKeyWarning(CreateStatusCodes.SUGGESTION_KEY_NOT_GIVEN);
			return null;
		}
		//
		if (key.length() > ProtocolConstants.MAX_KEY_LENGTH) {
			response.addKeyTooLongWarning(CreateStatusCodes.KEY_TOO_LONG);
			return null;
		}
		return key;
	}

	private static String checkSuggestionString(FormItemList items,
			ProcessCreateResponse response) {
		String suggestString = null;
		// check is the field exists
		try {
			suggestString = items.getField(ProtocolConstants.SUGGESTION_STRING);
		} catch (IllegalArgumentException e) {
			// TODO: RefactorName
			response.addQueryNameMissingError(CreateStatusCodes.QUERYNAME_NOT_GIVEN);
			return null;
		}
		// checks if the suggestion String length matches ASTP requirements
		// FIXME: JUnit says the Error-Message is not added
		if (suggestString.length() > ProtocolConstants.MAX_SUGGESTION_LENGTH) {
			response.addQueryNameTooLongError(CreateStatusCodes.SUGGESTION_STRING_TOO_LONG);
			return null;
		}
		return suggestString;
	}

}
