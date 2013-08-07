package de.uniko.west.socialsensor.graphity.server.tomcat;

import java.io.File;
import java.io.IOException;
import java.util.Queue;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import de.uniko.west.socialsensor.graphity.server.Configs;
import de.uniko.west.socialsensor.graphity.server.Server;
import de.uniko.west.socialsensor.graphity.server.statusupdates.StatusUpdate;
import de.uniko.west.socialsensor.graphity.server.statusupdates.StatusUpdateManager;
import de.uniko.west.socialsensor.graphity.server.statusupdates.StatusUpdateTemplate;
import de.uniko.west.socialsensor.graphity.server.statusupdates.TemplateFileInfo;
import de.uniko.west.socialsensor.graphity.server.tomcat.create.CreationType;
import de.uniko.west.socialsensor.graphity.server.tomcat.create.FormFile;
import de.uniko.west.socialsensor.graphity.server.tomcat.create.FormItemDoubleUsageException;
import de.uniko.west.socialsensor.graphity.server.tomcat.create.FormItemList;
import de.uniko.west.socialsensor.graphity.socialgraph.operations.ClientResponder;
import de.uniko.west.socialsensor.graphity.socialgraph.operations.CreateFriendship;
import de.uniko.west.socialsensor.graphity.socialgraph.operations.CreateStatusUpdate;
import de.uniko.west.socialsensor.graphity.socialgraph.operations.SocialGraphOperation;

/**
 * Tomcat create operation handler
 * 
 * @author Sebastian Schlicht
 * 
 */
public class Create extends HttpServlet {

	/**
	 * command queue to stack commands created
	 */
	private Queue<SocialGraphOperation> commandQueue;

	/**
	 * server configuration containing file paths
	 */
	private Configs config;

	/**
	 * file item factory
	 */
	private DiskFileItemFactory factory = new DiskFileItemFactory();

	@Override
	public void init(final ServletConfig config) throws ServletException {
		super.init(config);
		final ServletContext context = this.getServletContext();
		final Server server = (Server) context.getAttribute("server");
		this.commandQueue = server.getCommandQueue();
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

		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		// TODO: check if always (automatically) when posting multi-data forms
		if (isMultipart) {
			try {
				// read multi-part form items
				final FormItemList items = this.extractFormItems(request);

				// TODO: OAuth, stop manual determining of user id
				final long userId = Long.parseLong(items
						.getField(FormFields.USER_ID));

				// read essential form fields
				final long timestamp = System.currentTimeMillis();
				final CreationType createType = CreationType.GetCreationType(items
						.getField(FormFields.Create.CREATE_TYPE));

				if (createType == CreationType.FRIENDSHIP) {
					// read followship specific fields
					final long followedId = Long.parseLong(items
							.getField(FormFields.Create.FOLLOWSHIP_TARGET));

					// create followship
					final CreateFriendship createFriendshipCommand = new CreateFriendship(
							responder, timestamp, userId, followedId);
					this.commandQueue.add(createFriendshipCommand);
				} else {
					// read status update specific fields and files
					final String statusUpdateType = items
							.getField(FormFields.Create.STATUS_UPDATE_TYPE);
					final StatusUpdateTemplate template = StatusUpdateManager
							.getStatusUpdateTemplate(statusUpdateType);

					try {
						this.writeFiles(template, items);

						// create a new status update of the type specified
						final StatusUpdate statusUpdate = StatusUpdateManager
								.instantiateStatusUpdate(statusUpdateType,
										items);

						final CreateStatusUpdate createStatusUpdateCommand = new CreateStatusUpdate(
								responder, timestamp, userId, statusUpdate);
						this.commandQueue.add(createStatusUpdateCommand);
					} catch (final IllegalArgumentException e) {
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

						// delegate the error to show error message to the user
						throw e;
					} catch (final Exception e) {
						throw new IllegalArgumentException(
								"file writing failed!");
					}
				}
			} catch (final FileUploadException e) {
				// TODO: error log target management
				e.printStackTrace();
			} catch (final FormItemDoubleUsageException e) {
				// TODO: error log target management
				System.out.println(e.getMessage());
			} catch (final IllegalArgumentException e) {
				responder.error(500, e.getMessage());
				e.printStackTrace();
			}
		} else {
			// TODO: error - no multi-part form
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
	 * @throws IllegalArgumentException
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
				throw new IllegalArgumentException("file \"" + fileIdentifier
						+ "\" must have content type "
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