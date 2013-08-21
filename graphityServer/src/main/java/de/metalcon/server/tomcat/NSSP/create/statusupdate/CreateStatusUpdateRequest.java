package de.metalcon.server.tomcat.NSSP.create.statusupdate;

import org.neo4j.graphdb.Node;

import de.metalcon.server.statusupdates.StatusUpdate;
import de.metalcon.server.statusupdates.StatusUpdateManager;
import de.metalcon.server.statusupdates.StatusUpdateTemplate;
import de.metalcon.server.tomcat.NSSP.ProtocolConstants;
import de.metalcon.server.tomcat.NSSP.create.CreateRequest;
import de.metalcon.server.tomcat.NSSP.create.CreateRequestType;
import de.metalcon.socialgraph.NeoUtils;
import de.metalcon.utils.FormItemList;

/**
 * create status update requests according to NSSP
 * 
 * @author sebschlicht
 * 
 */
public class CreateStatusUpdateRequest extends CreateRequest {

	/**
	 * timestamp of the status update creation
	 */
	private final long timestamp;

	/**
	 * user requesting
	 */
	private final Node user;

	/**
	 * identifier of the status update that shall be created
	 */
	private final String statusUpdateId;

	/**
	 * status update template used
	 */
	private final StatusUpdateTemplate statusUpdateTemplate;

	/**
	 * status update created
	 */
	private StatusUpdate statusUpdate;

	/**
	 * create a new create status update request
	 * 
	 * @param type
	 *            create request type
	 * @param timestamp
	 *            timestamp of the status update creation
	 * @param user
	 *            user requesting
	 * @param statusUpdateId
	 *            identifier of the status update that shall be created
	 * @param statusUpdateTemplate
	 *            status update template used
	 */
	public CreateStatusUpdateRequest(final CreateRequestType type,
			final long timestamp, final Node user, final String statusUpdateId,
			final StatusUpdateTemplate statusUpdateTemplate) {
		super(type);
		this.timestamp = timestamp;
		this.user = user;
		this.statusUpdateId = statusUpdateId;
		this.statusUpdateTemplate = statusUpdateTemplate;
	}

	/**
	 * 
	 * @return timestamp of the status update creation
	 */
	public long getTimestamp() {
		return this.timestamp;
	}

	/**
	 * 
	 * @return user requesting
	 */
	public Node getUser() {
		return this.user;
	}

	/**
	 * 
	 * @return identifier of the status update that shall be created
	 */
	public String getStatusUpdateId() {
		return this.statusUpdateId;
	}

	/**
	 * 
	 * @return status update template used
	 */
	public StatusUpdateTemplate getStatusUpdateTemplate() {
		return this.statusUpdateTemplate;
	}

	/**
	 * 
	 * @return status update created
	 */
	public StatusUpdate getStatusUpdate() {
		return this.statusUpdate;
	}

	/**
	 * 
	 * @param statusUpdate
	 *            status update created
	 */
	public void setStatusUpdate(final StatusUpdate statusUpdate) {
		this.statusUpdate = statusUpdate;
	}

	/**
	 * check a create status update request for validity concerning NSSP
	 * 
	 * @param formItemList
	 *            form item list extracted
	 * @param createRequest
	 *            basic create request object
	 * @param createStatusUpdateResponse
	 *            create status update response object
	 * @return create status update request object<br>
	 *         <b>null</b> if the create status update request is invalid
	 */
	public static CreateStatusUpdateRequest checkRequest(
			final FormItemList formItemList, final CreateRequest createRequest,
			final CreateStatusUpdateResponse createStatusUpdateResponse) {
		final Node user = checkUserIdentifier(formItemList,
				createStatusUpdateResponse);
		if (user != null) {
			final String statusUpdateId = checkStatusUpdateIdentifier(
					formItemList, createStatusUpdateResponse);
			if (statusUpdateId != null) {
				final StatusUpdateTemplate statusUpdateTemplate = checkStatusUpdateType(
						formItemList, createStatusUpdateResponse);
				if (statusUpdateTemplate != null) {
					return new CreateStatusUpdateRequest(
							createRequest.getType(),
							System.currentTimeMillis(), user, statusUpdateId,
							statusUpdateTemplate);
				}
			}
		}

		return null;
	}

	/**
	 * check if the request contains a valid user identifier
	 * 
	 * @param formItemList
	 *            form item list extracted
	 * @param createFollowResponse
	 *            response object
	 * @return user node with the identifier passed<br>
	 *         <b>null</b> if the identifier is invalid
	 */
	private static Node checkUserIdentifier(final FormItemList formItemList,
			final CreateStatusUpdateResponse createStatusUpdateResponse) {
		final String userId = formItemList
				.getField(ProtocolConstants.Parameters.Create.StatusUpdate.USER_IDENTIFIER);
		if (userId != null) {
			final Node user = NeoUtils.getUserByIdentifier(userId);
			if (user != null) {
				return user;
			}

			createStatusUpdateResponse.userIdentifierInvalid(userId);
		} else {
			createStatusUpdateResponse.userIdentifierMissing();
		}

		return null;
	}

	/**
	 * check if the request contains a valid status update identifier
	 * 
	 * @param formItemList
	 *            form item list extracted
	 * @param createStatusUpdateResponse
	 *            response object
	 * @return status update identifier<br>
	 *         <b>null</b> if the identifier is invalid
	 */
	private static String checkStatusUpdateIdentifier(
			final FormItemList formItemList,
			final CreateStatusUpdateResponse createStatusUpdateResponse) {
		final String statusUpdateId = formItemList
				.getField(ProtocolConstants.Parameters.Create.StatusUpdate.STATUS_UPDATE_IDENTIFIER);
		if (statusUpdateId != null) {
			final Node statusUpdate = NeoUtils
					.getStatusUpdateByIdentifier(statusUpdateId);
			if (statusUpdate == null) {
				return statusUpdateId;
			}

			createStatusUpdateResponse
					.statusUpdateIdentifierInvalid(statusUpdateId);
		} else {
			createStatusUpdateResponse.statusUpdateIdentifierMissing();
		}

		return null;
	}

	/**
	 * check if the request contains a valid status update type
	 * 
	 * @param formItemList
	 *            form item list extracted
	 * @param createStatusUpdateResponse
	 *            response object
	 * @return status update template<br>
	 *         <b>null</b> if the type is invalid
	 */
	private static StatusUpdateTemplate checkStatusUpdateType(
			final FormItemList formItemList,
			final CreateStatusUpdateResponse createStatusUpdateResponse) {
		final String statusUpdateType = formItemList
				.getField(ProtocolConstants.Parameters.Create.StatusUpdate.STATUS_UPDATE_TYPE);
		if (statusUpdateType != null) {
			final StatusUpdateTemplate statusUpdateTemplate = StatusUpdateManager
					.getStatusUpdateTemplate(statusUpdateType);
			if (statusUpdateTemplate != null) {
				return statusUpdateTemplate;
			}

			createStatusUpdateResponse
					.statusUpdateTypeInvalid(statusUpdateType);
		} else {
			createStatusUpdateResponse.statusUpdateTypeMissing();
		}

		return null;
	}

}