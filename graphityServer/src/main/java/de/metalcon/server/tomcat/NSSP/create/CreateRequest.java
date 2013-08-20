package de.metalcon.server.tomcat.NSSP.create;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;

import de.metalcon.server.tomcat.NSSP.ProtocolConstants;
import de.metalcon.utils.FormItemList;

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

	/**
	 * check a create request for validity concerning NSSP
	 * 
	 * @param request
	 *            Tomcat servlet request
	 * @param factory
	 *            disk file item factory
	 * @param createResponse
	 *            create response object
	 * @return create request object<br>
	 *         <b>null</b> if the create request is invalid
	 * @throws FileUploadException
	 *             if errors encountered while processing the request
	 */
	public static CreateRequest checkRequest(final HttpServletRequest request,
			final DiskFileItemFactory factory,
			final CreateResponse createResponse) throws FileUploadException {
		final FormItemList formItemList = FormItemList.extractFormItems(
				request, factory);
		return checkRequest(formItemList, createResponse);
	}

	/**
	 * check a create request for validity concerning NSSP
	 * 
	 * @param formItemList
	 *            form item list extracted
	 * @param createResponse
	 *            create response object
	 * @return create request object<br>
	 *         <b>null</b> if the create request is invalid
	 */
	public static CreateRequest checkRequest(final FormItemList formItemList,
			final CreateResponse createResponse) {
		if (formItemList != null) {
			final CreateRequestType type = checkType(formItemList,
					createResponse);
			if (type != null) {
				return new CreateRequest(type);
			}
		}

		return null;
	}

	/**
	 * check if the request contains a valid create request type
	 * 
	 * @param formItemList
	 *            form item list
	 * @param createResponse
	 *            create response object
	 * @return create request type<br>
	 *         <b>null</b> if the create request type is invalid
	 */
	private static CreateRequestType checkType(final FormItemList formItemList,
			final CreateResponse createResponse) {
		final String sType = formItemList
				.getField(ProtocolConstants.Parameters.Create.TYPE);
		if (sType != null) {
			if (CreateRequestType.USER.getIdentifier().equals(sType)) {
				return CreateRequestType.USER;
			} else if (CreateRequestType.FOLLOW.getIdentifier().equals(sType)) {
				return CreateRequestType.FOLLOW;
			} else if (CreateRequestType.STATUS_UPDATE.getIdentifier().equals(
					sType)) {
				return CreateRequestType.STATUS_UPDATE;
			}

			createResponse.typeInvalid(sType);
		} else {
			createResponse.typeMissing();
		}

		return null;
	}

}