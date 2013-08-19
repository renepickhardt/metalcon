package de.metalcon.socialgraph.operations;

import de.metalcon.server.tomcat.GraphityHttpServlet;
import de.metalcon.server.tomcat.NSSP.delete.statusupdate.DeleteStatusUpdateRequest;
import de.metalcon.server.tomcat.NSSP.delete.statusupdate.DeleteStatusUpdateResponse;
import de.metalcon.socialgraph.SocialGraph;

/**
 * delete command: status update
 * 
 * @author Sebastian Schlicht
 * 
 */
public class DeleteStatusUpdate extends SocialGraphOperation {

	/**
	 * delete status update response object
	 */
	private final DeleteStatusUpdateResponse response;

	/**
	 * delete status update request object
	 */
	private final DeleteStatusUpdateRequest request;

	/**
	 * create a new delete status update command
	 * 
	 * @param servlet
	 *            request servlet
	 * @param responder
	 *            client responder
	 * @param poster
	 *            posting user
	 * @param statusUpdate
	 *            status update
	 */
	public DeleteStatusUpdate(final GraphityHttpServlet servlet,
			final DeleteStatusUpdateResponse deleteStatusUpdateResponse,
			final DeleteStatusUpdateRequest deleteStatusUpdateRequest) {
		super(servlet, deleteStatusUpdateRequest.getUser());
		this.response = deleteStatusUpdateResponse;
		this.request = deleteStatusUpdateRequest;
	}

	@Override
	protected boolean execute(final SocialGraph graph) {
		graph.deleteStatusUpdate(this.user, this.request.getStatusUpdate());
		this.response.deleteStatusUpdateSucceeded();
		return true;
	}

}