package de.metalcon.socialgraph.operations;

import org.neo4j.graphdb.Node;

import de.metalcon.server.tomcat.GraphityHttpServlet;
import de.metalcon.socialgraph.SocialGraph;

/**
 * delete command: user
 * 
 * @author sebschlicht
 * 
 */
public class DeleteUser extends SocialGraphOperation {

	/**
	 * user that shall be deleted
	 */
	private final Node user;

	/**
	 * create a new delete user command
	 * 
	 * @param servlet
	 *            response servlet
	 * @param responder
	 *            client responder
	 * @param user
	 *            user that shall be deleted
	 */
	public DeleteUser(final GraphityHttpServlet servlet,
			final ClientResponder responder, final Node user) {
		super(servlet, responder, null);
		this.user = user;
	}

	@Override
	protected boolean execute(final SocialGraph graph) {
		graph.deleteUser(this.user);
		this.responder.addLine("ok");
		return true;
	}

}
