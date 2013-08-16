package de.metalcon.server.tomcat.NSSP.create;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.servlet.ServletFileUpload;

import de.metalcon.server.tomcat.create.FormItemList;

/**
 * basic create requests according to NSSP
 * 
 * @author sebschlicht
 * 
 */
public class CreateRequest {

	/**
	 * create request type
	 */
	private final CreateRequestType type;

	/**
	 * create a new basic create request according to NSSP
	 * 
	 * @param type
	 *            create request type
	 */
	public CreateRequest(final CreateRequestType type) {
		this.type = type;
	}

	/**
	 * 
	 * @return create request type
	 */
	public CreateRequestType getType() {
		return this.type;
	}

	public static CreateRequest checkRequest(final HttpServletRequest request,
			final CreateResponse createResponse) {
		if (ServletFileUpload.isMultipartContent(request)) {
			final FormItemList formItemList = new FormItemList(request);
			final CreateRequestType type = checkType(request, createResponse);
		}

		return null;
	}

	private static CreateRequestType checkType(
			final HttpServletRequest request,
			final CreateResponse createResponse) {
		return null;
	}

}