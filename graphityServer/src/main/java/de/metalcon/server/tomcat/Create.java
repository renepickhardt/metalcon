package de.metalcon.server.tomcat;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import de.metalcon.server.Configs;
import de.metalcon.server.Server;
import de.metalcon.server.exceptions.create.statusupdate.StatusUpdateInstantiationFailedException;
import de.metalcon.server.statusupdates.StatusUpdate;
import de.metalcon.server.statusupdates.StatusUpdateManager;
import de.metalcon.server.statusupdates.StatusUpdateTemplate;
import de.metalcon.server.statusupdates.TemplateFileInfo;
import de.metalcon.server.tomcat.NSSP.create.CreateRequest;
import de.metalcon.server.tomcat.NSSP.create.CreateResponse;
import de.metalcon.server.tomcat.NSSP.create.user.CreateUserRequest;
import de.metalcon.server.tomcat.NSSP.create.user.CreateUserResponse;
import de.metalcon.server.tomcat.create.FormFile;
import de.metalcon.server.tomcat.create.FormItemDoubleUsageException;
import de.metalcon.socialgraph.operations.ClientResponder;
import de.metalcon.socialgraph.operations.CreateFollow;
import de.metalcon.socialgraph.operations.CreateStatusUpdate;
import de.metalcon.socialgraph.operations.CreateUser;
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
	 * server configuration containing file paths
	 */
	private Configs config;

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
	public void init(final ServletConfig config) throws ServletException {
		super.init(config);
		final ServletContext context = this.getServletContext();
		final Server server = (Server) context.getAttribute("server");
		this.config = server.getConfig();
	}

	@Override
	protected void doPost(final HttpServletRequest request,
			final HttpServletResponse response) throws IOException {
		// store response item for the server response creation
		final ClientResponder responder = new TomcatClientResponder(response);

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

				default:
					// TODO
					try {
						this.writeFiles(template, items);

						// create a new status update of the type specified
						final StatusUpdate statusUpdate = StatusUpdateManager
								.instantiateStatusUpdate(statusUpdateType,
										items);
						statusUpdate.setId(statusUpdateId);

						final CreateStatusUpdate createStatusUpdateCommand = new CreateStatusUpdate(
								this, responder, System.currentTimeMillis(),
								user, statusUpdate);
						this.commandQueue.add(createStatusUpdateCommand);
					} catch (final StatusUpdateInstantiationFailedException e) {
						// remove the files
						FormFile fileItem;
						File file;
						for (String fileIdentifier : items.getFileIdentifiers()) {
							fileItem = items.getFile(fileIdentifier);
							file = fileItem.getFile();

							if (file != null) {
								file.delete();
							}
						}

						throw e;
					} catch (final Exception e) {
						throw new IllegalArgumentException(
								"file writing failed!");
					}
					break;

				}
			}
		} else {
			createResponse.noMultipartRequest();
		}
	}

	/**
	 * extract fields and files from the multi-part form in the request
	 * 
	 * @param request
	 *            HTTP servlet request
	 * @return form fields and files wrapped in a form item list
	 * @throws FormItemDoubleUsageException
	 *             if form items use the same identifier
	 * @throws FileUploadException
	 *             if the form item parsing fails
	 */
	private FormItemList extractFormItems(final HttpServletRequest request)
			throws FileUploadException, FormItemDoubleUsageException {
		final ServletFileUpload upload = new ServletFileUpload(this.FACTORY);
		final FormItemList formItems = new FormItemList();

		for (FileItem item : upload.parseRequest(request)) {
			if (item.isFormField()) {
				formItems.addField(item.getFieldName(), item.getString());
			} else {
				formItems.addFile(item.getFieldName(), item);
			}
		}

		return formItems;
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