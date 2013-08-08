package de.uniko.west.socialsensor.graphity.server;

import java.io.IOException;
import java.util.HashMap;
import java.util.Queue;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.xml.parsers.ParserConfigurationException;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.kernel.AbstractGraphDatabase;
import org.neo4j.tooling.GlobalGraphOperations;
import org.xml.sax.SAXException;

import de.uniko.west.socialsensor.graphity.server.statusupdates.StatusUpdateManager;
import de.uniko.west.socialsensor.graphity.server.tomcat.create.FormItemDoubleUsageException;
import de.uniko.west.socialsensor.graphity.server.tomcat.create.FormItemList;
import de.uniko.west.socialsensor.graphity.socialgraph.Algorithm;
import de.uniko.west.socialsensor.graphity.socialgraph.NeoUtils;
import de.uniko.west.socialsensor.graphity.socialgraph.SocialGraph;
import de.uniko.west.socialsensor.graphity.socialgraph.algorithms.Baseline;
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
	private BlockingQueue<SocialGraphOperation> commands;

	/**
	 * server's social graph command worker
	 */
	private Worker commandWorker;

	public Server() {
		// load server configuration
		this.config = Configs
				.get("/usr/local/apache-tomcat-7.0.39/databases/graphity-server.txt");

		// load social graph database
		this.graphDatabase = NeoUtils.getSocialGraphDatabase(this.config);

		// load social graph algorithm
		switch (this.config.algorithm()) {

		case Algorithm.Graphity:
			this.graph = new Graphity(this.graphDatabase);
			break;

		case Algorithm.Baseline:
			this.graph = new Baseline(this.graphDatabase);
			break;

		default:
			throw new IllegalArgumentException(
					"invalid graph algorithm specified!");
		}

		// create and start command worker
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
		this.start();

		boolean isEmpty = true;
		final GlobalGraphOperations globalGraphOps = GlobalGraphOperations
				.at(this.graphDatabase);
		for (Node node : globalGraphOps.getAllNodes()) {
			isEmpty = false;
			break;
		}

		// DEBUG
		// load test case scenario
		if (isEmpty) {
			Transaction transaction = this.graphDatabase.beginTx();

			// create 5 users
			final long[] userIds = new long[5];
			for (int i = 0; i < userIds.length; i++) {
				userIds[i] = this.graphDatabase.createNode().getId();
			}

			transaction.success();
			transaction.finish();
			transaction = this.graphDatabase.beginTx();

			// create friendships
			final HashMap<Long, long[]> friendships = new HashMap<Long, long[]>();
			friendships.put(userIds[0], new long[] { userIds[1], userIds[2],
					userIds[3], userIds[4] });
			friendships.put(userIds[1], new long[] { userIds[0], userIds[3] });
			friendships.put(userIds[2], new long[] { userIds[0], userIds[4] });
			friendships.put(userIds[3], new long[] { userIds[2] });
			friendships.put(userIds[4], new long[] {});

			for (long userId : userIds) {
				for (long followed : friendships.get(userId)) {
					this.graph.createFriendship(System.currentTimeMillis(),
							userId, followed);
				}
			}

			transaction.success();
			transaction.finish();
			transaction = this.graphDatabase.beginTx();

			// create status updates
			final SortedMap<Integer, Long> statusUpdates = new TreeMap<Integer, Long>();
			// A
			statusUpdates.put(17, userIds[0]);
			statusUpdates.put(14, userIds[0]);
			statusUpdates.put(9, userIds[0]);
			// B
			statusUpdates.put(12, userIds[1]);
			statusUpdates.put(10, userIds[1]);
			statusUpdates.put(4, userIds[1]);
			// C
			statusUpdates.put(13, userIds[2]);
			statusUpdates.put(7, userIds[2]);
			statusUpdates.put(6, userIds[2]);
			// D
			statusUpdates.put(11, userIds[3]);
			statusUpdates.put(8, userIds[3]);
			statusUpdates.put(5, userIds[3]);

			long timestamp;
			int message;
			final FormItemList values = new FormItemList();
			while (!statusUpdates.isEmpty()) {
				timestamp = System.currentTimeMillis();
				message = statusUpdates.firstKey();
				try {
					values.addField("message", String.valueOf(message));
				} catch (final FormItemDoubleUsageException e) {
					e.printStackTrace();
				}

				this.graph.createStatusUpdate(timestamp, statusUpdates
						.remove(message), StatusUpdateManager
						.instantiateStatusUpdate("PlainText", values));

				while (System.currentTimeMillis() <= timestamp) {
				}
			}

			transaction.success();
			transaction.finish();
		}

		// load status update types
		try {
			StatusUpdateManager.loadStatusUpdateTemplates(this.config,
					this.graphDatabase);
		} catch (ParserConfigurationException | SAXException | IOException e) {
			System.err.println("failed to load status update templates!");
			e.printStackTrace();
		}

		final ServletContext context = arg0.getServletContext();
		context.setAttribute("server", this);
	}
}