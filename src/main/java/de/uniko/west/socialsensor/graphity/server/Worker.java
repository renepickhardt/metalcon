package de.uniko.west.socialsensor.graphity.server;

import java.util.Queue;

import de.uniko.west.socialsensor.graphity.socialgraph.SocialGraph;
import de.uniko.west.socialsensor.graphity.socialgraph.operations.SocialGraphOperation;

/**
 * command worker
 * 
 * @author sebschlicht
 * 
 */
public class Worker implements Runnable {

	/**
	 * worker thread
	 */
	private Thread workerThread;

	/**
	 * social graph command queue being filled with commands
	 */
	private final Queue<SocialGraphOperation> commands;

	/**
	 * graph targeted by the commands
	 */
	private final SocialGraph graph;

	/**
	 * worker status flag
	 */
	private boolean running;

	/**
	 * worker stopping flag
	 */
	private boolean stopping;

	/**
	 * create a new command worker for a social graph
	 * 
	 * @param commands
	 *            social graph command queue being filled with commands
	 * @param graph
	 *            graph targeted by the commands
	 */
	public Worker(final Queue<SocialGraphOperation> commands,
			final SocialGraph graph) {
		this.commands = commands;
		this.graph = graph;
	}

	@Override
	public void run() {
		this.running = true;

		// exit execution loop if worker has been stopped
		SocialGraphOperation command = null;
		while (!this.stopping) {
			command = this.commands.poll();

			// run first command (if existing)
			if (command != null) {
				command.run(this.graph);
			}
		}

		// reset worker flags
		this.running = false;
		this.stopping = false;
	}

	/**
	 * start the worker thread
	 */
	public void start() {
		this.workerThread = new Thread(this);
		this.workerThread.start();
	}

	/**
	 * stop the worker thread
	 */
	public void stop() {
		this.stopping = true;
	}

	/**
	 * wait until the worker has shut down
	 */
	public void waitForShutdown() {
		if (this.stopping) {
			while (this.running) {
				// wait for the worker thread to finish
			}
		}
	}

	/**
	 * access worker status flag
	 * 
	 * @return worker status flag
	 */
	public boolean isRunning() {
		return this.running;
	}

}