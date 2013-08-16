package de.metalcon.socialgraph.operations;

import java.util.List;

import org.neo4j.graphdb.Node;

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
	 * owner of the stream targeted
	 */
	private final Node poster;

	/**
	 * number of items to be read
	 */
	private final int numItems;

	/**
	 * single stream flag
	 */
	private final boolean ownUpdates;

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
		this.poster = readRequest.getPoster();
		this.numItems = readRequest.getNumItems();
		this.ownUpdates = readRequest.getOwnUpdates();
	}

	@Override
	protected boolean execute(final SocialGraph graph) {
		final List<String> activities = graph.readStatusUpdates(this.poster,
				this.user, this.numItems, this.ownUpdates);

		// add news stream to the client response
		this.response.addActivityStream(activities);

		return false;
	}

}