package de.metalcon.socialgraph.operations;

import java.util.List;

import org.json.simple.JSONObject;

import de.metalcon.server.tomcat.GraphityHttpServlet;
import de.metalcon.server.tomcat.NSSP.read.ReadRequest;
import de.metalcon.server.tomcat.NSSP.read.ReadResponse;
import de.metalcon.socialgraph.SocialGraph;

/**
 * read command: status updates
 * 
 * @author Sebastian Schlicht
 * 
 */
public class ReadStatusUpdates extends SocialGraphOperation {

	/**
	 * read response object
	 */
	private final ReadResponse response;

	/**
	 * read request object
	 */
	private final ReadRequest request;

	/**
	 * create a new read status updates command
	 * 
	 * @param servlet
	 *            response servlet
	 * @param readResponse
	 *            read response object
	 * @param readRequest
	 *            read request object
	 */
	public ReadStatusUpdates(final GraphityHttpServlet servlet,
			final ReadResponse readResponse, final ReadRequest readRequest) {
		super(servlet, readRequest.getUser());
		this.response = readResponse;
		this.request = readRequest;
	}

	@Override
	protected boolean execute(final SocialGraph graph) {
		final List<JSONObject> activities = graph.readStatusUpdates(
				this.request.getPoster(), this.user,
				this.request.getNumItems(), this.request.getOwnUpdates());

		// add news stream to the client response
		this.response.addActivityStream(activities);

		return false;
	}

}