package de.uniko.west.socialsensor.graphity.server.tomcat;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
	 * generated serial version UID
	 */
	private static final long serialVersionUID = 4435622251859808192L;
	/**
	 * command queue to stack commands created
	 */
	private Queue<SocialGraphOperation> commandQueue;

	/**
	 * server configuration containing file paths
	 */
	private Configs config;

	@Override
	public void init(final ServletConfig config) throws ServletException {
		super.init(config);
		final ServletContext context = this.getServletContext();
		final Server server = (Server) context.getAttribute("server");
		this.commandQueue = server.getCommandQueue();
		this.config = server.getConfig();
	}

	@Override
	protected void doPost(final HttpServletRequest request,
			final HttpServletResponse response) throws IOException {
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);

		// TODO: check if always when posting multi-data forms
		if (isMultipart) {
			// create file factory in a temporary directory
			final DiskFileItemFactory factory = new DiskFileItemFactory();
			final ServletContext context = this.getServletConfig()
					.getServletContext();
			final File tempDir = (File) context
					.getAttribute("javax.servlet.context.tempdir");
			factory.setRepository(tempDir);

			final ServletFileUpload upload = new ServletFileUpload(factory);
			List<FileItem> items = null;

			try {
				items = upload.parseRequest(request);
			} catch (FileUploadException e) {
				e.printStackTrace();
			}

			// store response item for the server response creation
			final ClientResponder responder = new TomcatClientResponder(
					response);

			// create the map for status update instantiation
			if (items != null) {
				long timestamp = System.currentTimeMillis();
				final Map<String, String> fields = new HashMap<String, String>();
				final Map<String, FileItem> files = new HashMap<String, FileItem>();

				for (FileItem item : items) {
					if (item.isFormField()) {
						// add form value to fields map
						if (!fields.containsKey(item.getFieldName())) {
							fields.put(item.getFieldName(), item.getString());
						}
					} else {
						// add file item to files map
						if (!files.containsKey(item.getFieldName())) {
							files.put(item.getFieldName(), item);
						}
					}
				}

				CreateType type;
				String temp = fields.get("createType");
				if (temp != null) {
					try {
						type = CreateType.GetCreateType(temp);
						if (type == null) {
							throw new ClassCastException();
						}
					} catch (final ClassCastException invalid) {
						// send error - field type invalid
						Helper.sendErrorMessage(
								response,
								400,
								"the field \"createType\" is corrupted!<br>please type in a valid create type identifier.");
						return;
					}
				} else {
					// send error - field missing
					Helper.sendErrorMessage(
							response,
							400,
							"the field \"createType\" is missing!<br>please specify what you want to create.");
					return;
				}

				// TODO: OAuth
				long userId;
				temp = fields.get("user_id");
				if (temp != null) {
					try {
						userId = Long.parseLong(temp);
					} catch (NumberFormatException e) {
						// send error - field type invalid
						Helper.sendErrorMessage(
								response,
								400,
								"the field \"user_id\" is corrupted!<br>please type in a valid identification number.");
						return;
					}
				} else {
					// send error - field missing
					Helper.sendErrorMessage(
							response,
							400,
							"the field \"user_id\" is missing!<br>please specify your identification number.");
					return;
				}

				// create friendship command
				if (type == CreateType.FRIENDSHIP) {
					long targetId;
					temp = fields.get("targetId");
					if (temp != null) {
						try {
							targetId = Long.parseLong(temp);
						} catch (NumberFormatException e) {
							// send error - field type invalid
							Helper.sendErrorMessage(
									response,
									400,
									"the field \"targetId\" is corrupted!<br>please provide a number greater than zero.");

							return;
						}
					} else {
						// send error - field missing
						Helper.sendErrorMessage(
								response,
								400,
								"the field \"targetId\" is missing!<br>please specify the user you want to follow.");
						return;
					}

					final CreateFriendship createFriendshipCommand = new CreateFriendship(
							responder, timestamp, userId, targetId);
					this.commandQueue.add(createFriendshipCommand);
				} else {
					String statusUpdateType;
					temp = fields.get("type");
					if (temp != null) {
						statusUpdateType = temp;
					} else {
						// send error - field missing
						Helper.sendErrorMessage(
								response,
								400,
								"the field \"type\" is missing!<br>please specify the status update type you want to create.");
						return;
					}

					// parse files
					final List<File> fileList = new LinkedList<File>();
					String fileDir;
					File file;
					FileItem fileItem;
					for (String fieldName : files.keySet()) {
						// get the target directory
						fileItem = files.get(fieldName);
						fileDir = getFileDir(this.config,
								fileItem.getContentType());

						file = new File(fileDir + userId + "-" + timestamp
								+ "-" + fileItem.getName());
						fileList.add(file);

						// create the file
						try {
							fileItem.write(file);

							// store file path in fields map
							fields.put(fieldName, file.getAbsolutePath());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					// create a new status update of the type specified
					try {
						final StatusUpdate statusUpdate = StatusUpdateManager
								.instantiateStatusUpdate(statusUpdateType,
										fields);

						final CreateStatusUpdate createStatusUpdateCommand = new CreateStatusUpdate(
								responder, timestamp, userId, statusUpdate);
						this.commandQueue.add(createStatusUpdateCommand);
					} catch (IllegalArgumentException e) {
						// remove the files
						for (File tempFile : fileList) {
							tempFile.delete();
						}

						Helper.sendErrorMessage(response, 400, e.getMessage());
					}
				}
			}
		}
	}

	/**
	 * get the directory for a file of the content type specified
	 * 
	 * @param config
	 *            server configuration
	 * @param contentType
	 *            file item content type
	 * @return directory for this content type
	 */
	private static String getFileDir(final Configs config,
			final String contentType) {
		return config.picture_path;
	}
}