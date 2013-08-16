package de.metalcon.socialgraph.operations;

import org.neo4j.graphdb.Node;

import de.metalcon.server.tomcat.GraphityHttpServlet;
import de.metalcon.socialgraph.SocialGraph;

/**
 * create command: follow edge
 * 
 * @author Sebastian Schlicht
 * 
 */
public class CreateFollow extends SocialGraphOperation {

	/**
	 * followed user
	 */
	private final Node followed;

	/**
	 * create a new create follow edge command
	 * 
	 * @param servlet
	 *            request servlet
	 * @param responder
	 *            client responder
	 * @param following
	 *            following user
	 * @param followed
	 *            followed user
	 */
	public CreateFollow(final GraphityHttpServlet servlet,
			final ClientResponder responder, final Node following,
			final Node followed) {
		super(servlet, following);
		this.followed = followed;
	}

	@Override
	protected boolean execute(final SocialGraph graph) {
		graph.createFriendship(this.user, this.followed);
		// TODO: createFollowSucceeded();
		return true;
	}

}