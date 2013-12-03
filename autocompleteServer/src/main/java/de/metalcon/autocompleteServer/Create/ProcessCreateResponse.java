package de.metalcon.autocompleteServer.Create;

import javax.servlet.ServletContext;

import org.json.simple.JSONObject;

import de.metalcon.autocompleteServer.Helper.ProtocolConstants;

public class ProcessCreateResponse {

	private final JSONObject jsonResponse;
	private CreateRequestContainer container;

	public ProcessCreateResponse(ServletContext context) {
		this.jsonResponse = new JSONObject();

	}

	/**
	 * Adds a queryName-Error to the container object. Expects the String to be
	 * not NULL and correctly formatted.
	 * 
	 * @param querynameNotGiven
	 */
	@SuppressWarnings("unchecked")
	public void addQueryNameMissingError(String querynameNotGiven) {
		this.jsonResponse.put(CreateStatusCodes.STATUS_NO_QUERY,
				querynameNotGiven);
	}

	@SuppressWarnings("unchecked")
	public void addQueryNameTooLongError(String querynameTooLong) {
		this.jsonResponse.put(CreateStatusCodes.STATUS_QUERY_TOO_LONG,
				querynameTooLong);
	}

	/**
	 * Adds the suggestion String to the container object. Expects the String to
	 * be not NULL and correctly formatted.
	 * 
	 * @param suggestionString
	 */
	@SuppressWarnings("unchecked")
	public void addSuggestStringToContainer(String suggestionString) {
		this.jsonResponse.put(ProtocolConstants.QUERY_PARAMETER,
				suggestionString);
	}

	/**
	 * Adds a DefaultIndex-Warning to the container object. Expects the String
	 * to be not NULL and correctly formatted.
	 * 
	 * @param indexnameNotGiven
	 */
	@SuppressWarnings("unchecked")
	public void addDefaultIndexWarning(String indexnameNotGiven) {
		this.jsonResponse.put(CreateStatusCodes.STATUS_DEFAULT_INDEX,
				indexnameNotGiven);
	}

	/**
	 * Adds a NoImage-Warning to the container object. Expects the String to be
	 * not NULL and correctly formatted.
	 * 
	 * @param noImage
	 */
	@SuppressWarnings("unchecked")
	public void addNoImageWarning(String noImage) {
		this.jsonResponse.put(CreateStatusCodes.STATUS_NO_IMAGE, noImage);
	}

	/**
	 * Adds a requestMustBeMultipart-Error to the container object. Expects the
	 * String to be not NULL and correctly formatted.
	 * 
	 * @param noImage
	 */
	@SuppressWarnings("unchecked")
	public void addHttpRequestError(String requestMustBeMultipart) {
		this.jsonResponse.put(CreateStatusCodes.STATUS_REQUEST_NOT_MULTIPART,
				requestMustBeMultipart);
	}

	/**
	 * Adds a WeightNotGiven-Error to the container object. Expects the String
	 * to be not NULL and correctly formatted.
	 * 
	 * @param noImage
	 */
	@SuppressWarnings("unchecked")
	public void addWeightNotGivenError(String weightNotGiven) {
		this.jsonResponse.put(CreateStatusCodes.STATUS_NO_WEIGHT,
				weightNotGiven);

	}

	/**
	 * Adds a WeightNotANumber-Error to the container object. Expects the String
	 * to be not NULL and correctly formatted.
	 * 
	 * @param noImage
	 */
	@SuppressWarnings("unchecked")
	public void addWeightNotANumberError(String weightNotANumber) {
		this.jsonResponse.put(CreateStatusCodes.STATUS_WEIGHT_NOT_A_NUMBER,
				weightNotANumber);
	}

	/**
	 * Adds an IndexDoesNotExist-Error to the container object. Expects the
	 * String to be not NULL and correctly formatted.
	 * 
	 * @param indexName
	 */
	@SuppressWarnings("unchecked")
	public void addIndexDoesNotExistError(String indexName) {
		this.jsonResponse
				.put(CreateStatusCodes.STATUS_INDEX_DOES_NOT_EXIST, indexName);

	}

	/**
	 * Adds a WeightNotANumber-Error to the container object. Expects the String
	 * to be not NULL and correctly formatted.
	 * 
	 * @param noImage
	 */
	@SuppressWarnings("unchecked")
	public void addNoKeyWarning(String keyNotGiven) {
		this.jsonResponse.put(CreateStatusCodes.STATUS_NO_KEY, keyNotGiven);
	}

	/**
	 * Adds a KeyTooLong-Warning to the container object. Expects the String to
	 * be not NULL and correctly formatted.
	 * 
	 * @param noImage
	 */
	@SuppressWarnings("unchecked")
	public void addKeyTooLongWarning(String keyTooLong) {
		this.jsonResponse
				.put(CreateStatusCodes.STATUS_KEY_TOO_LONG, keyTooLong);
	}

	/**
	 * Adds Status OK to the container object. Expects the String to be not NULL
	 * and correctly formatted. To stay conform to the ASTP, Status OK should
	 * only be added, if the Response does not have and will get any Warning or
	 * Error.
	 * 
	 * @param noImage
	 */
	@SuppressWarnings("unchecked")
	public void addStatusOk(String statusOK) {
		this.jsonResponse.put(CreateStatusCodes.STATUS_OK, statusOK);
	}

	/**
	 * Adds a ImageGeometryTooBig-Warning to the container object.Expects the
	 * String to be not NULL and correctly formatted.
	 * 
	 * @param noImage
	 */
	@SuppressWarnings("unchecked")
	public void addImageGeometryTooBigWarning(String imageGeometryTooBig) {
		this.jsonResponse.put(CreateStatusCodes.IMAGE_GEOMETRY_TOO_BIG,
				imageGeometryTooBig);
	}

	/**
	 * Adds a ImageFileSizeTooBig-Warning to the container object.Expects the
	 * String to be not NULL and correctly formatted.
	 * 
	 * @param noImage
	 */
	@SuppressWarnings("unchecked")
	public void addImageFileSizeTooBigWarning(String imageFileTooLarge) {
		this.jsonResponse.put(CreateStatusCodes.IMAGE_FILE_TOO_LARGE,
				imageFileTooLarge);
	}

	/**
	 * Getter for the response as JSONObject.
	 * 
	 * @return JSONObject
	 */
	public JSONObject getResponse() {
		return this.jsonResponse;
	}

	public CreateRequestContainer getContainer() {
		return this.container;
	}

	public void addContainer(
			CreateRequestContainer suggestTreeCreateRequestContainer) {
		this.container = suggestTreeCreateRequestContainer;

	}

}
