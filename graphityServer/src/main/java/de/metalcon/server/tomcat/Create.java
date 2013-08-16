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
import org.neo4j.graphdb.Node;

import de.metalcon.server.Configs;
import de.metalcon.server.Server;
import de.metalcon.server.exceptions.InvalidUserIdentifierException;
import de.metalcon.server.exceptions.RequestFailedException;
import de.metalcon.server.exceptions.create.statusupdate.InvalidStatusUpdateTypeException;
import de.metalcon.server.exceptions.create.statusupdate.StatusUpdateInstantiationFailedException;
import de.metalcon.server.statusupdates.StatusUpdate;
import de.metalcon.server.statusupdates.StatusUpdateManager;
import de.metalcon.server.statusupdates.StatusUpdateTemplate;
import de.metalcon.server.statusupdates.TemplateFileInfo;
import de.metalcon.server.tomcat.create.CreateType;
import de.metalcon.server.tomcat.create.FormFile;
import de.metalcon.server.tomcat.create.FormItemDoubleUsageException;
import de.metalcon.server.tomcat.create.FormItemList;
import de.metalcon.socialgraph.NeoUtils;
import de.metalcon.socialgraph.operations.ClientResponder;
import de.metalcon.socialgraph.operations.CreateFollow;
import de.metalcon.socialgraph.operations.CreateStatusUpdate;
import de.metalcon.socialgraph.operations.CreateUser;

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
	private final DiskFileItemFactory factory = new DiskFileItemFactory();

	@Override
	public void init(final ServletConfig config) throws ServletException {
		super.init(config);
		final ServletContext context = this.getServletContext();
		final Server server = (Server) context.getAttribute("server");
		this.config = server.getConfig();

		// load file factory in temporary directory
		final File tempDir = (File) context
				.getAttribute("javax.servlet.context.tempdir");
		this.factory.setRepository(tempDir);
	}

	@Override
	protected void doPost(final HttpServletRequest request,
			final HttpServletResponse response) throws IOException {
		// store response item for the server response creation
		final ClientResponder responder = new TomcatClientResponder(response);

		if (ServletFileUpload.isMultipartContent(request)) {
			try {
				// read multi-part form items
				final FormItemList items = this.extractFormItems(request);

				// read essential form fields
				final CreateType createType = CreateType.GetCreateType(items
						.getField(NSSProtocol.Create.TYPE));

				Node user = null;
				if (createType != CreateType.USER) {
					// TODO: OAuth, stop manual determining of user id
					final String userId = items.getField(NSSProtocol.USER_ID);
					user = NeoUtils.getUserNodeByIdentifier(this.graphDB,
							userId);
				}

				if (createType == CreateType.USER) {
					// read user specific fields
					final String userId = items
							.getField(NSSProtocol.Create.USER_ID);
					boolean existing = true;
					try {
						NeoUtils.getUserNodeByIdentifier(this.graphDB, userId);
					} catch (final InvalidUserIdentifierException e) {
						existing = false;
					}
					if (existing) {
						throw new InvalidUserIdentifierException(
								"the user identifier \"" + userId
										+ "\" is already in use.");
					}
					final String displayName = items
							.getField(NSSProtocol.Create.USER_DISPLAY_NAME);
					final String profilePicturePath = items
							.getField(NSSProtocol.Create.USER_PROFILE_PICTURE_PATH);

					// create user
					final CreateUser createUserCommand = new CreateUser(this,
							responder, userId, displayName, profilePicturePath);
					this.commandQueue.add(createUserCommand);
				} else if (createType == CreateType.FOLLOW) {
					// read followship specific fields
					final String followedId = items
							.getField(NSSProtocol.Create.FOLLOW_TARGET);
					final Node followed = NeoUtils.getUserNodeByIdentifier(
							this.graphDB, followedId);

					// create followship
					final CreateFollow createFriendshipCommand = new CreateFollow(
							this, responder, user, followed);
					this.commandQueue.add(createFriendshipCommand);
				} else {
					// read status update specific fields and files
					final String statusUpdateId = items
							.getField(NSSProtocol.Create.STATUS_UPDATE_ID);
					final String statusUpdateType = items
							.getField(NSSProtocol.Create.STATUS_UPDATE_TYPE);

					StatusUpdateTemplate template;
					try {
						template = StatusUpdateManager
								.getStatusUpdateTemplate(statusUpdateType);
					} catch (final IllegalArgumentException e) {
						throw new InvalidStatusUpdateTypeException(
								"there is no status update template named \""
										+ statusUpdateType + "\".");
					}

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
				}

				try {
					this.workingQueue.take();
					responder.finish();
				} catch (final InterruptedException e) {
					System.err.println("request status queue failed");
					e.printStackTrace();
				}
			} catch (final FormItemDoubleUsageException e) {
				responder.error(400, e.getMessage());
				e.printStackTrace();
			} catch (final FileUploadException e) {
				responder.error(500,
						"an error encountered while processing the request!");
				e.printStackTrace();
			} catch (final IllegalArgumentException e) {
				// a required form field is missing
				responder.error(500, e.getMessage());
				e.printStackTrace();
			} catch (final RequestFailedException e) {
				// the request contains errors
				responder.addLine(e.getMessage());
				responder.addLine(e.getSalvationDescription());
				responder.finish();
			}
		} else {
			// error - no multipart form
			responder
					.error(500, "create requests need to use multipart forms!");
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
		final ServletFileUpload upload = new ServletFileUpload(this.factory);
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