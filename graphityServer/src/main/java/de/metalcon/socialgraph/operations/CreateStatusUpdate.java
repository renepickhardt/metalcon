package de.metalcon.socialgraph.operations;

import org.neo4j.graphdb.Node;

import de.metalcon.server.statusupdates.StatusUpdate;
import de.metalcon.server.tomcat.GraphityHttpServlet;
import de.metalcon.socialgraph.SocialGraph;

/**
 * create command: status update
 * 
 * @author Sebastian Schlicht
 * 
 */
public class CreateStatusUpdate extends SocialGraphOperation {

	/**
	 * time stamp of the operation
	 */
	private final long timestamp;

	/**
	 * status update content object
	 */
	private final StatusUpdate content;

	/**
	 * create a new create status update command
	 * 
	 * @param servlet
	 *            request servlet
	 * @param responder
	 *            client responder
	 * @param timestamp
	 *            time stamp of the status update
	 * @param poster
	 *            posting user
	 * @param content
	 *            status update content object
	 */
	public CreateStatusUpdate(final GraphityHttpServlet servlet,
			final ClientResponder responder, final long timestamp,
			final Node poster, final StatusUpdate content) {
		super(servlet, poster);
		this.timestamp = timestamp;
		this.content = content;
	}

	@Override
	protected boolean execute(final SocialGraph graph) {
		graph.createStatusUpdate(this.timestamp, this.user, this.content);
		// TODO: createStatusUpdateSucceeded();
		return true;
	}

}