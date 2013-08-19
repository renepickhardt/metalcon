package de.metalcon.socialgraph.operations;

import de.metalcon.server.tomcat.GraphityHttpServlet;
import de.metalcon.server.tomcat.NSSP.create.statusupdate.CreateStatusUpdateRequest;
import de.metalcon.server.tomcat.NSSP.create.statusupdate.CreateStatusUpdateResponse;
import de.metalcon.socialgraph.SocialGraph;

/**
 * create command: status update
 * 
 * @author Sebastian Schlicht
 * 
 */
public class CreateStatusUpdate extends SocialGraphOperation {

	/**
	 * create status update response object
	 */
	private final CreateStatusUpdateResponse response;

	/**
	 * create status update request object
	 */
	private final CreateStatusUpdateRequest request;

	/**
	 * create a new create status update command
	 * 
	 * @param servlet
	 *            request servlet
	 * @param createStatusUpdateResponse
	 *            create status update response object
	 * @param createStatusUpdateRequest
	 *            create status update request object
	 */
	public CreateStatusUpdate(final GraphityHttpServlet servlet,
			final CreateStatusUpdateResponse createStatusUpdateResponse,
			final CreateStatusUpdateRequest createStatusUpdateRequest) {
		super(servlet, createStatusUpdateRequest.getUser());
		this.response = createStatusUpdateResponse;
		this.request = createStatusUpdateRequest;
	}

	@Override
	protected boolean execute(final SocialGraph graph) {
		graph.createStatusUpdate(this.request.getTimestamp(), this.user,
				this.request.getStatusUpdate());
		this.response.createStatusUpdateSucceeded();
		return true;
	}

}