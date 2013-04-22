package de.uniko.west.socialsensor.graphity.server;

import java.util.LinkedList;
import java.util.Queue;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.neo4j.kernel.AbstractGraphDatabase;

import de.uniko.west.socialsensor.graphity.socialgraph.Algorithm;
import de.uniko.west.socialsensor.graphity.socialgraph.NeoUtils;
import de.uniko.west.socialsensor.graphity.socialgraph.SocialGraph;
import de.uniko.west.socialsensor.graphity.socialgraph.algorithms.Graphity;
import de.uniko.west.socialsensor.graphity.socialgraph.operations.SocialGraphOperation;

/**
 * graphity server
 * 
 * @author sebschlicht
 * 
 */
public class Server implements ServletContextListener {

	/**
	 * server configuration
	 */
	private Configs config;

	/**
	 * social graph database
	 */
	private AbstractGraphDatabase graphDatabase;

	/**
	 * social graph algorithm
	 */
	private SocialGraph graph;

	/**
	 * queued commands being executed by the command worker
	 */
	private Queue<SocialGraphOperation> commands;

	/**
	 * server's social graph command worker
	 */
	private Worker commandWorker;

	public Server() {
		// load server configuration
		this.config = Configs
				.get("/usr/local/apache-tomcat-7.0.39/webapps/Graphity-Server-0.0.1-SNAPSHOT/config.txt");

		// load social graph database
		this.graphDatabase = NeoUtils.getSocialGraphDatabase(this.config);

		// load social graph algorithm
		switch (this.config.algorithm()) {

		case Algorithm.Graphity:
			this.graph = new Graphity(this.graphDatabase);
			break;

		default:
			throw new IllegalArgumentException(
					"invalid graph algorithm specified!");
		}

		// create and start command worker
		this.commands = new LinkedList<SocialGraphOperation>();
		this.commandWorker = new Worker(this.commands, this.graph);
	}

	/**
	 * start the server and wait for it to be running
	 */
	public void start() {
		this.commandWorker.start();
		while (!this.commandWorker.isRunning()) {
			// wait for the command worker to be ready
		}
	}

	/**
	 * stop the server and wait for it to be stopped<br>
	 * <b>leaves commands that have not been executed yet</b>
	 */
	public void stop() {
		this.commandWorker.stop();
		this.commandWorker.waitForShutdown();
	}

	/**
	 * access command queue to add commands
	 * 
	 * @return queued commands being executed by the command worker
	 */
	public Queue<SocialGraphOperation> getCommandQueue() {
		return this.commands;
	}

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		this.graphDatabase.shutdown();
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		final ServletContext context = arg0.getServletContext();
		context.setAttribute("server", this);
	}

}