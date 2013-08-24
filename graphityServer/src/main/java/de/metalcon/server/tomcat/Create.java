package de.metalcon.server.tomcat;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;

import de.metalcon.server.exceptions.StatusUpdateInstantiationFailedException;
import de.metalcon.server.statusupdates.StatusUpdate;
import de.metalcon.server.statusupdates.StatusUpdateManager;
import de.metalcon.server.statusupdates.StatusUpdateTemplate;
import de.metalcon.server.statusupdates.TemplateFileInfo;
import de.metalcon.server.tomcat.NSSP.create.CreateRequest;
import de.metalcon.server.tomcat.NSSP.create.CreateResponse;
import de.metalcon.server.tomcat.NSSP.create.follow.CreateFollowRequest;
import de.metalcon.server.tomcat.NSSP.create.follow.CreateFollowResponse;
import de.metalcon.server.tomcat.NSSP.create.statusupdate.CreateStatusUpdateRequest;
import de.metalcon.server.tomcat.NSSP.create.statusupdate.CreateStatusUpdateResponse;
import de.metalcon.server.tomcat.NSSP.create.user.CreateUserRequest;
import de.metalcon.server.tomcat.NSSP.create.user.CreateUserResponse;
import de.metalcon.socialgraph.operations.CreateFollow;
import de.metalcon.socialgraph.operations.CreateStatusUpdate;
import de.metalcon.socialgraph.operations.CreateUser;
import de.metalcon.utils.FormFile;
import de.metalcon.utils.FormItemList;

/**
 * Tomcat create operation handler
 * 
 * @author Sebastian Schlicht
 * 
 */
public class Create extends GraphityHttpServlet {

	/**
	 * serialization information
	 */
	private static final long serialVersionUID = 752658118858186708L;

	/**
	 * file item factory
	 */
	private static DiskFileItemFactory FACTORY;

	/**
	 * set the file item factory<br>
	 * <b>must be called before initialization of servlets</b>
	 * 
	 * @param factory
	 *            file item factory
	 */
	public static void setDiskFileItemFactory(final DiskFileItemFactory factory) {
		FACTORY = factory;
	}

	@Override
	protected void doPost(final HttpServletRequest request,
			final HttpServletResponse response) throws IOException {
		// store response item for the server response creation
		final TomcatClientResponder responder = new TomcatClientResponder(
				response);
		response.setHeader("Access-Control-Allow-Origin",
				this.config.getHeaderAccessControl());
		CreateResponse createResponse = new CreateResponse();
		FormItemList formItemList = null;
		try {
			formItemList = FormItemList.extractFormItems(request, FACTORY);
		} catch (final FileUploadException e) {
			responder.error(500,
					"errors encountered while processing the request");
			return;
		}

		if (formItemList != null) {
			final CreateRequest createRequest = CreateRequest.checkRequest(
					formItemList, createResponse);

			boolean commandStacked = false;
			if (createRequest != null) {
				switch (createRequest.getType()) {

				// create a user
				case USER:
					final CreateUserResponse createUserResponse = new CreateUserResponse();
					final CreateUserRequest createUserRequest = CreateUserRequest
							.checkRequest(formItemList, createRequest,
									createUserResponse);
					createResponse = createUserResponse;

					if (createUserRequest != null) {
						// create user
						final CreateUser createUserCommand = new CreateUser(
								this, createUserResponse, createUserRequest);
						this.commandQueue.add(createUserCommand);

						commandStacked = true;
					}
					break;

				// create a follow edge
				case FOLLOW:
					final CreateFollowResponse createFollowResponse = new CreateFollowResponse();
					final CreateFollowRequest createFollowRequest = CreateFollowRequest
							.checkRequest(formItemList, createRequest,
									createFollowResponse);
					createResponse = createFollowResponse;

					if (createFollowRequest != null) {
						// create follow edge
						final CreateFollow createFollowCommand = new CreateFollow(
								this, createFollowResponse, createFollowRequest);
						this.commandQueue.add(createFollowCommand);

						commandStacked = true;
					}
					break;

				// create a status update
				default:
					final CreateStatusUpdateResponse createStatusUpdateResponse = new CreateStatusUpdateResponse();
					final CreateStatusUpdateRequest createStatusUpdateRequest = CreateStatusUpdateRequest
							.checkRequest(formItemList, createRequest,
									createStatusUpdateResponse);
					createResponse = createStatusUpdateResponse;

					if (createStatusUpdateRequest != null) {
						try {
							this.writeFiles(createStatusUpdateRequest
									.getStatusUpdateTemplate(), formItemList);

							// create a new status update of the type specified
							final StatusUpdate statusUpdate = StatusUpdateManager
									.instantiateStatusUpdate(
											createStatusUpdateRequest
													.getStatusUpdateTemplate()
													.getName(), formItemList);
							statusUpdate.setId(createStatusUpdateRequest
									.getStatusUpdateId());
							createStatusUpdateRequest
									.setStatusUpdate(statusUpdate);

							// create status update
							final CreateStatusUpdate createStatusUpdateCommand = new CreateStatusUpdate(
									this, createStatusUpdateResponse,
									createStatusUpdateRequest);
							this.commandQueue.add(createStatusUpdateCommand);

							commandStacked = true;
						} catch (final StatusUpdateInstantiationFailedException e) {
							// remove the files
							FormFile fileItem;
							File file;
							for (String fileIdentifier : formItemList
									.getFileIdentifiers()) {
								fileItem = formItemList.getFile(fileIdentifier);
								file = fileItem.getFile();

								if (file != null) {
									file.delete();
								}
							}

							createStatusUpdateResponse
									.statusUpdateInstantiationFailed(e
											.getMessage());
						} catch (final Exception e) {
							responder.error(500,
									"errors encountered while writing files");
						}
					}
					break;

				}

				if (commandStacked) {
					try {
						this.workingQueue.take();
					} catch (final InterruptedException e) {
						System.err
								.println("request status queue failed due to create request");
						e.printStackTrace();
					}
				}
			}
		} else {
			createResponse.noMultipartRequest();
		}

		responder.addLine(createResponse.toString());
		responder.finish();
	}

	/**
	 * write the files posted if they are matching to the template targeted
	 * 
	 * @param template
	 *            status update template targeted
	 * @param items
	 *            form item list
	 * @throws StatusUpdateInstantiationFailedException
	 *             if a file is not matching the content type set
	 * @throws Exception
	 *             if a file could not be written
	 */
	private void writeFiles(final StatusUpdateTemplate template,
			final FormItemList items) throws Exception {
		FormFile fileItem;
		TemplateFileInfo fileInfo;
		for (String fileIdentifier : items.getFileIdentifiers()) {
			fileItem = items.getFile(fileIdentifier);
			fileInfo = template.getFiles().get(fileIdentifier);

			if (fileInfo.getContentType().equals(fileItem.getContentType())) {
				// write the file and store it within the instantiation item
				final File file = this.writeFile(fileItem);
				fileItem.setFile(file);
			} else {
				throw new StatusUpdateInstantiationFailedException("file \""
						+ fileIdentifier + "\" must have content type "
						+ fileInfo.getContentType());
			}
		}
	}

	/**
	 * write a file to the directory matching its content type
	 * 
	 * @param fileItem
	 *            form file
	 * @return instance of the file written
	 * @throws Exception
	 *             if the file could not be written
	 */
	private File writeFile(final FormFile fileItem) throws Exception {
		final String directory = this.getFileDir(fileItem.getContentType());

		// TODO: ensure unique file names
		final File file = new File(directory + System.currentTimeMillis() + "-"
				+ fileItem.getOriginalFileName());
		fileItem.getFormItem().write(file);
		return file;
	}

	/**
	 * get the directory for a file of the content type specified
	 * 
	 * @param contentType
	 *            file item content type
	 * @return directory for this content type
	 */
	private String getFileDir(final String contentType) {
		return this.config.picture_path;
	}
}