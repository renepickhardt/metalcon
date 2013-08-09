package de.uniko.west.socialsensor.graphity.server.tomcat;

import java.util.Queue;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.neo4j.kernel.AbstractGraphDatabase;

import de.uniko.west.socialsensor.graphity.server.Server;
import de.uniko.west.socialsensor.graphity.socialgraph.operations.SocialGraphOperation;

/**
 * basic Tomcat HTTP serlevt for requests to the server
 * 
 * @author sebschlicht
 * 
 */
public abstract class GraphityHttpServlet extends HttpServlet {

	/**
	 * graph database to <b>read</b>
	 */
	protected AbstractGraphDatabase graphDB;

	/**
	 * command queue to stack commands created
	 */
	protected Queue<SocialGraphOperation> commandQueue;

	@Override
	public void init(final ServletConfig config) throws ServletException {
		super.init(config);
		final ServletContext context = this.getServletContext();
		final Server server = (Server) context.getAttribute("server");
		this.graphDB = server.getGraphDatabase();
		this.commandQueue = server.getCommandQueue();
	}

}