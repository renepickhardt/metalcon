package de.metalcon.socialgraph.operations;

import org.neo4j.graphdb.Node;

import de.metalcon.server.tomcat.GraphityHttpServlet;
import de.metalcon.socialgraph.SocialGraph;

/**
 * remove command: friendship
 * 
 * @author Sebastian Schlicht
 * 
 */
public class RemoveFriendship extends SocialGraphOperation {

	/**
	 * followed user
	 */
	private final Node followed;

	/**
	 * create a new delete follow edge command
	 * 
	 * @param servlet
	 *            response servlet
	 * @param responder
	 *            client responder
	 * @param following
	 *            following user
	 * @param followed
	 *            followed user
	 */
	public RemoveFriendship(final GraphityHttpServlet servlet,
			final ClientResponder responder, final Node following,
			final Node followed) {
		super(servlet, responder, following);
		this.followed = followed;
	}

	@Override
	protected boolean execute(final SocialGraph graph) {
		graph.removeFriendship(this.user, this.followed);
		this.responder.addLine("ok");

		return true;
	}

}