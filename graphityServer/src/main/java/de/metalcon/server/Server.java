package de.metalcon.server;

import java.io.File;
import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.neo4j.kernel.AbstractGraphDatabase;
import org.xml.sax.SAXException;

import de.metalcon.server.statusupdates.StatusUpdateManager;
import de.metalcon.server.tomcat.Create;
import de.metalcon.socialgraph.Algorithm;
import de.metalcon.socialgraph.NeoUtils;
import de.metalcon.socialgraph.SocialGraph;
import de.metalcon.socialgraph.algorithms.ReadOptimizedGraphity;
import de.metalcon.socialgraph.algorithms.WriteOptimizedGraphity;
import de.metalcon.socialgraph.operations.SocialGraphOperation;

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
	private final Configs config;

	/**
	 * social graph database
	 */
	private final AbstractGraphDatabase graphDatabase;

	/**
	 * social graph algorithm
	 */
	private SocialGraph graph;

	/**
	 * queued commands being executed by the command worker
	 */
	private final BlockingQueue<SocialGraphOperation> commands;

	/**
	 * server's social graph command worker
	 */
	private final Worker commandWorker;

	public Server() {
		// load server configuration
		this.config = Configs.get("/etc/graphity/graphity.conf");

		// load social graph database
		this.graphDatabase = NeoUtils.getSocialGraphDatabase(this.config);
		System.out.println("graph database created");

		// load social graph algorithm
		switch (this.config.getAlgorithm()) {

		case Algorithm.ReadOptimizedGraphity:
			this.graph = new ReadOptimizedGraphity(this.graphDatabase);
			break;

		case Algorithm.WriteOptimizedGraphity:
			this.graph = new WriteOptimizedGraphity(this.graphDatabase);
			break;

		default:
			throw new IllegalArgumentException(
					"invalid graph algorithm specified!");
		}

		// create command worker
		this.commands = new LinkedBlockingQueue<SocialGraphOperation>();
		this.commandWorker = new Worker(this.commands, this.graph);
	}

	/**
	 * start the server and wait for it to be running
	 */
	public void start() {
		this.commandWorker.start();
		while (!this.commandWorker.isRunning()) {
			// wait for the command worker to be ready
			System.out.println("waiting for worker to connect...");
			try {
				Thread.sleep(50);
			} catch (final InterruptedException e) {
				System.out.println("server thread could not wait for worker");
				e.printStackTrace();
			}
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
	 * access graph database to read users
	 * 
	 * @return social graph database
	 */
	public AbstractGraphDatabase getGraphDatabase() {
		return this.graphDatabase;
	}

	/**
	 * access command queue to add commands
	 * 
	 * @return queued commands being executed by the command worker
	 */
	public Queue<SocialGraphOperation> getCommandQueue() {
		return this.commands;
	}

	/**
	 * access server configuration
	 * 
	 * @return server configuration
	 */
	public Configs getConfig() {
		return this.config;
	}

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		this.graphDatabase.shutdown();
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		System.out.println("context initialized");
		this.start();

		final ServletContext context = arg0.getServletContext();
		context.setAttribute("server", this);

		// load status update types
		try {
			System.out.println("loading status update templates");
			StatusUpdateManager.loadStatusUpdateTemplates(
					context.getRealPath("/") + "WEB-INF/", this.config,
					this.graphDatabase);
			System.out.println("status update templates loaded");
		} catch (ParserConfigurationException | SAXException | IOException e) {
			System.err.println("failed to load status update templates!");
			e.printStackTrace();
		}

		final File tmpDir = (File) context
				.getAttribute("javax.servlet.context.tempdir");
		final DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setRepository(tmpDir);
		Create.setDiskFileItemFactory(factory);
	}
}