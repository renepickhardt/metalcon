package de.metalcon.server;

import java.util.concurrent.BlockingQueue;

import de.metalcon.socialgraph.SocialGraph;
import de.metalcon.socialgraph.operations.SocialGraphOperation;

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
	 * thread-safe social graph command queue being filled with commands
	 */
	private final BlockingQueue<SocialGraphOperation> commands;

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
	 *            thread-safe social graph command queue being filled with
	 *            commands
	 * @param graph
	 *            graph targeted by the commands
	 */
	public Worker(final BlockingQueue<SocialGraphOperation> commands,
			final SocialGraph graph) {
		this.commands = commands;
		this.graph = graph;
	}

	@Override
	public void run() {
		this.running = true;

		try {
			// exit execution loop if worker has been stopped
			SocialGraphOperation command = null;
			while (!this.stopping) {
				command = this.commands.take();
				command.run(this.graph);
			}
		} catch (final InterruptedException e) {
			// stopped by worker
			e.printStackTrace();
		} catch (final Exception e) {
			System.err.println("worker stopped unexcpectedly!");
			e.printStackTrace();
		}

		// reset worker flags
		this.running = false;
		this.stopping = false;
		System.out.println("worker has been stopped.");
	}

	/**
	 * start the worker thread
	 */
	public void start() {
		this.workerThread = new Thread(this);
		System.out.println("starting worker thread");
		this.workerThread.start();
		System.out.println("worker thread started");
	}

	/**
	 * stop the worker thread
	 */
	public void stop() {
		this.stopping = true;
		this.workerThread.interrupt();
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